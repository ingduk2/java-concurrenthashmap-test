package com.test.threadsafehashmap.test.basic.usesyncro.conhashmap;

import com.test.threadsafehashmap.soju.SoJuType;
import com.test.threadsafehashmap.soju.nonthreadsafe.SoJuDto_Basic;
import com.test.threadsafehashmap.soju.nonthreadsafe.SoJuManageService_Basic_Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ## v8. int dto, concurrentHashMap, synchronized method, getAndPut
 */
@Slf4j
public class V8Test {

    private SoJuManageService_Basic_Synchronized synchronizedService;
    private Map<String, SoJuDto_Basic> concurrentHashMap;

    @BeforeEach
    void setUp() {
        concurrentHashMap = new ConcurrentHashMap<>();
        synchronizedService = new SoJuManageService_Basic_Synchronized(concurrentHashMap);
    }

    @RepeatedTest(1000)
    void testV1() {
        int loopSize = 300;
        CountDownLatch countDownLatch = new CountDownLatch(loopSize);
        ExecutorService executorService = Executors.newFixedThreadPool(30);

        for(int i = 0; i < loopSize; i++) {
            int selector = (i % 3);
            SoJuType soJu = SoJuType.values()[selector];
            SoJuDto_Basic soJuDto = SoJuDto_Basic.builder()
                    .name(soJu.name())
                    .count(soJu.getCount())
                    .price(soJu.getPrice())
                    .build();

            executorService.submit(() -> {
                try {
                    synchronizedService.sumGetAndPut(soJuDto);
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

        log.info("{}", concurrentHashMap.get(SoJuType.JINRO_IS_BACK.name()));
        log.info("{}", concurrentHashMap.get(SoJuType.CHAM_ISLE.name()));
        log.info("{}", concurrentHashMap.get(SoJuType.CHUEM_CHURUM.name()));

        Assertions.assertThat(
                concurrentHashMap.get(SoJuType.JINRO_IS_BACK.name()).getCount())
                .isEqualTo(loopSize / 3);
        Assertions.assertThat(
                concurrentHashMap.get(SoJuType.JINRO_IS_BACK.name()).getPrice())
                .isEqualTo(loopSize / 3 * SoJuType.JINRO_IS_BACK.getPrice());

        Assertions.assertThat(
                concurrentHashMap.get(SoJuType.CHAM_ISLE.name()).getCount())
                .isEqualTo(loopSize / 3);
        Assertions.assertThat(
                concurrentHashMap.get(SoJuType.CHAM_ISLE.name()).getPrice())
                .isEqualTo(loopSize / 3 * SoJuType.CHAM_ISLE.getPrice());

        Assertions.assertThat(
                concurrentHashMap.get(SoJuType.CHUEM_CHURUM.name()).getCount())
                .isEqualTo(loopSize / 3);
        Assertions.assertThat(
                concurrentHashMap.get(SoJuType.CHUEM_CHURUM.name()).getPrice())
                .isEqualTo(loopSize / 3 * SoJuType.CHUEM_CHURUM.getPrice());
    }
}
