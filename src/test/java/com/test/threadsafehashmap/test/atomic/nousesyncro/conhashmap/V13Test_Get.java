package com.test.threadsafehashmap.test.atomic.nousesyncro.conhashmap;

import com.test.threadsafehashmap.soju.SoJuType;
import com.test.threadsafehashmap.soju.threadsafe.SoJuDto_Atomic;
import com.test.threadsafehashmap.soju.threadsafe.SoJuManageService_Atomic;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * ## v13. atomic dto, ConcurrentHashMap, computeIfAbsent Get
 * 1. remove 로 지우면서 가져와서 합치기 - 실패 - 최근의 값을 가져온다고하는데 지우면서 update되면서 지워지면
 * 몇개씩 손실이 일어남.
 * 2. get해서 계속 set -> 성공함. 그러나 지우는 부분이 애매해지고 한없이 커질 수 있음
 * 3. get해서 차이만큼 쌓는거 할려했는데 복잡해서 하다가 맘..
 * 4. compute 로 하면서 0 만들고 return 나오는걸로 계산도 했던것 같은데 안됨. ㅜ
 *
 * -> 이게 get 하는 동안에도 계속 update가 되고, remove 하는 동안에도 update 됨.
 * -> concurrentHashmap 이 쓰기에만 lock 이 있음
 * -> 검색 작업(포함 get) 은 차단되지 않음, update와 겹칠 수 있음
 * -> remove 가장 최근에 완료된 업데이트 작업의 시작을 보류 한 결과를 반영
 * */
@Slf4j
public class V13Test_Get {

    private SoJuManageService_Atomic service_atomic;
    private SoJuManageService_Atomic service_atomic2;
    private Map<String, SoJuDto_Atomic> concurrentHashMap;
    private Map<String, SoJuDto_Atomic> concurrentHashMap2;
    private ReentrantLock lock;

    @BeforeEach
    void setUp() {
        concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap2 = new ConcurrentHashMap<>();
        service_atomic = new SoJuManageService_Atomic(concurrentHashMap);
        service_atomic2 = new SoJuManageService_Atomic(concurrentHashMap2);
        lock = new ReentrantLock();
    }

    @RepeatedTest(10)
    void testV13Test() throws InterruptedException {
//        log.info("test Start----");
        int loopSize = 30000;
        int innerLoopSize = 30;
        CountDownLatch countDownLatch = new CountDownLatch(loopSize);
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        ExecutorService singleService = Executors.newSingleThreadExecutor();

        SoJuDto_Atomic chamIsle = SoJuDto_Atomic.getZeroDto(SoJuDto_Atomic.builder()
                .name(SoJuType.CHAM_ISLE.name())
                .build());
        SoJuDto_Atomic chuemchurum = SoJuDto_Atomic.getZeroDto(SoJuDto_Atomic.builder()
                .name(SoJuType.CHUEM_CHURUM.name())
                .build());
        SoJuDto_Atomic jinroIsBack = SoJuDto_Atomic.getZeroDto(SoJuDto_Atomic.builder()
                .name(SoJuType.JINRO_IS_BACK.name())
                .build());

        singleService.submit(() ->{
            while (true) {
                //remove 하면 하는동안 겹쳐지는애들 손실.
                List<SoJuDto_Atomic> collect = concurrentHashMap.keySet().stream()
                        .map(concurrentHashMap::remove)
                        .collect(Collectors.toList());

                for (SoJuDto_Atomic soJuDto_atomic : collect) {
                    switch (soJuDto_atomic.getName()) {
                        case "JINRO_IS_BACK":
                            jinroIsBack.updateCountAndPrice(soJuDto_atomic);
                            break;
                        case "CHAM_ISLE":
                            chamIsle.updateCountAndPrice(soJuDto_atomic);
                            break;
                        case "CHUEM_CHURUM":
                            chuemchurum.updateCountAndPrice(soJuDto_atomic);
                            break;
                    }
                }
                //get 하면 멀쩡함

//                SoJuDto_Atomic saved1 = concurrentHashMap.remove(SoJuType.CHAM_ISLE.name());
//                chamIsle.updateCountAndPrice(saved1);
//                SoJuDto_Atomic saved2 = concurrentHashMap.remove(SoJuType.CHUEM_CHURUM.name());
//                chuemchurum.updateCountAndPrice(saved2);
//                SoJuDto_Atomic saved3 = concurrentHashMap.remove(SoJuType.JINRO_IS_BACK.name());
//                jinroIsBack.updateCountAndPrice(saved3);
                Thread.sleep(10);

            }
        });

        for(int i = 0; i < loopSize; i++) {
            int selector = (i % 3);
            SoJuType soJu = SoJuType.values()[selector];
            SoJuDto_Atomic soJuDto = SoJuDto_Atomic.builder()
                    .name(soJu.name())
                    .count(soJu.getCount())
                    .price(soJu.getPrice())
                    .build();

            //publisher
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < innerLoopSize; j++) {
                        service_atomic.computeIfAbsent(soJuDto);
                        service_atomic2.computeIfAbsent(soJuDto);
                    }
//                    lock.unlock();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        //countDown 다떨어질때까지 기다림.
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        Thread.sleep(100);
        singleService.shutdown();

        log.info("{}", concurrentHashMap);
        log.info("{}", concurrentHashMap2);
        log.info("-------------------------------------------------------------");
        log.info("{}", jinroIsBack);
        log.info("{}", chamIsle);
        log.info("{}", chuemchurum);

        Assertions.assertThat(
                        jinroIsBack.getCount())
                .isEqualTo(loopSize * innerLoopSize / 3);
        Assertions.assertThat(
                        jinroIsBack.getPrice())
                .isEqualTo(loopSize * innerLoopSize / 3 * SoJuType.JINRO_IS_BACK.getPrice());

        Assertions.assertThat(
                        chamIsle.getCount())
                .isEqualTo(loopSize * innerLoopSize / 3);
        Assertions.assertThat(
                        chamIsle.getPrice())
                .isEqualTo(loopSize * innerLoopSize / 3 * SoJuType.CHAM_ISLE.getPrice());

        Assertions.assertThat(
                        chuemchurum.getCount())
                .isEqualTo(loopSize * innerLoopSize / 3);
        Assertions.assertThat(
                        chuemchurum.getPrice())
                .isEqualTo(loopSize * innerLoopSize / 3 * SoJuType.CHUEM_CHURUM.getPrice());
    }
}
