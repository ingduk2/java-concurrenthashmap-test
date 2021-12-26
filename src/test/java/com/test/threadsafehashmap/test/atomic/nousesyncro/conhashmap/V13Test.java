package com.test.threadsafehashmap.test.atomic.nousesyncro.conhashmap;

import com.test.threadsafehashmap.soju.SoJuType;
import com.test.threadsafehashmap.soju.threadsafe.SoJuDto_Atomic;
import com.test.threadsafehashmap.soju.threadsafe.SoJuManageService_Atomic;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ## v13. atomic dto, ConcurrentHashMap, computeIfAbsent
 */
@Slf4j
public class V13Test {

    private SoJuManageService_Atomic service_atomic;
    private Map<String, SoJuDto_Atomic> concurrentHashMap;
    private ReentrantLock lock;
    @BeforeEach
    void setUp() {
        concurrentHashMap = new ConcurrentHashMap<>();
        service_atomic = new SoJuManageService_Atomic(concurrentHashMap);
        lock = new ReentrantLock();
    }

    @RepeatedTest(1)
    void testV13Test() throws InterruptedException {
        int loopSize = 3000000;
        int innerLoopSize = 1;
        CountDownLatch countDownLatch = new CountDownLatch(loopSize);
        ExecutorService executorService = Executors.newFixedThreadPool(300);
        ExecutorService singleService = Executors.newSingleThreadExecutor();

        SoJuDto_Atomic dto1 = SoJuDto_Atomic.getZeroDto(SoJuDto_Atomic.builder()
                .name(SoJuType.CHAM_ISLE.name())
                .build());
        SoJuDto_Atomic dto2 = SoJuDto_Atomic.getZeroDto(SoJuDto_Atomic.builder()
                .name(SoJuType.CHUEM_CHURUM.name())
                .build());
        SoJuDto_Atomic dto3 = SoJuDto_Atomic.getZeroDto(SoJuDto_Atomic.builder()
                .name(SoJuType.JINRO_IS_BACK.name())
                .build());
        singleService.submit(() ->{
            while (true) {
                SoJuDto_Atomic saved1 = concurrentHashMap.remove(SoJuType.CHAM_ISLE.name());
                dto1.updateCountAndPrice(saved1);
                SoJuDto_Atomic saved2 = concurrentHashMap.remove(SoJuType.CHUEM_CHURUM.name());
                dto2.updateCountAndPrice(saved2);
                SoJuDto_Atomic saved3 = concurrentHashMap.remove(SoJuType.JINRO_IS_BACK.name());
                dto3.updateCountAndPrice(saved3);
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

            executorService.submit(() -> {
                lock.lock();
                try {
                    for (int j = 0; j < innerLoopSize; j++) {
                        service_atomic.computeIfAbsent(soJuDto);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
                lock.unlock();
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

        log.info("{}", concurrentHashMap.get(SoJuType.JINRO_IS_BACK.name()));
        log.info("{}", concurrentHashMap.get(SoJuType.CHAM_ISLE.name()));
        log.info("{}", concurrentHashMap.get(SoJuType.CHUEM_CHURUM.name()));

        Assertions.assertThat(
                concurrentHashMap.get(SoJuType.JINRO_IS_BACK.name()).getCount())
                .isEqualTo(loopSize * innerLoopSize / 3);
        Assertions.assertThat(
                concurrentHashMap.get(SoJuType.JINRO_IS_BACK.name()).getPrice())
                .isEqualTo(loopSize * innerLoopSize / 3 * SoJuType.JINRO_IS_BACK.getPrice());

        Assertions.assertThat(
                concurrentHashMap.get(SoJuType.CHAM_ISLE.name()).getCount())
                .isEqualTo(loopSize * innerLoopSize / 3);
        Assertions.assertThat(
                concurrentHashMap.get(SoJuType.CHAM_ISLE.name()).getPrice())
                .isEqualTo(loopSize * innerLoopSize / 3 * SoJuType.CHAM_ISLE.getPrice());

        Assertions.assertThat(
                concurrentHashMap.get(SoJuType.CHUEM_CHURUM.name()).getCount())
                .isEqualTo(loopSize * innerLoopSize / 3);
        Assertions.assertThat(
                concurrentHashMap.get(SoJuType.CHUEM_CHURUM.name()).getPrice())
                .isEqualTo(loopSize * innerLoopSize / 3 * SoJuType.CHUEM_CHURUM.getPrice());
    }
}
