package com.test.threadsafehashmap.test.atomic.usesyncro.conhashmap;

import com.test.threadsafehashmap.soju.SoJuType;
import com.test.threadsafehashmap.soju.threadsafe.SoJuDto_Atomic;
import com.test.threadsafehashmap.soju.threadsafe.SoJuManageService_Atomic_Synchronized;
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

/**
 * ## v14. atomic dto, ConcurrentHashMap, synchronized method computeIfAbsent
 */
@Slf4j
public class V14Test {

    private SoJuManageService_Atomic_Synchronized service_atomic_synchronized;
    private Map<String, SoJuDto_Atomic> concurrentHashMap;

    @BeforeEach
    void setUp() {
        concurrentHashMap = new ConcurrentHashMap<>();
        service_atomic_synchronized = new SoJuManageService_Atomic_Synchronized(concurrentHashMap);
    }

    @RepeatedTest(1000)
    void testV14Test() {
        int loopSize = 300;
        int innerLoopSize = 10;
        CountDownLatch countDownLatch = new CountDownLatch(loopSize);
        ExecutorService executorService = Executors.newFixedThreadPool(30);

        for(int i = 0; i < loopSize; i++) {
            int selector = (i % 3);
            SoJuType soJu = SoJuType.values()[selector];
            SoJuDto_Atomic soJuDto = SoJuDto_Atomic.builder()
                    .name(soJu.name())
                    .count(soJu.getCount())
                    .price(soJu.getPrice())
                    .build();

            executorService.submit(() -> {
                try {
                    for (int j = 0; j < innerLoopSize; j++) {
                        service_atomic_synchronized.computeIfAbsent(soJuDto);
                    }
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
                .isEqualTo(loopSize * innerLoopSize / 3);
        Assertions.assertThat(
                        concurrentHashMap.get(SoJuType.JINRO_IS_BACK.name()).getPrice())
                .isEqualTo(loopSize * innerLoopSize / 3 * SoJuType.JINRO_IS_BACK.getPrice());

        Assertions.assertThat(
                        concurrentHashMap.get(SoJuType.CHAM_ISLE.name()).getCount())
                .isEqualTo(loopSize * innerLoopSize/ 3);
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
