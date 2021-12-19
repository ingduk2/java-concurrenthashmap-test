package com.test.threadsafehashmap.test.atomic.nousesyncro.conhashmap;

import com.test.threadsafehashmap.soju.SoJuType;
import com.test.threadsafehashmap.soju.threadsafe.SoJuDto_Atomic;
import com.test.threadsafehashmap.soju.threadsafe.SoJuManageService_Atomic;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ## v15. atomic dto, ConcurrentHashMap, getAndPut
 */
@Slf4j
public class V15Test {

    private SoJuManageService_Atomic service_atomic;
    private Map<String, SoJuDto_Atomic> concurrentHashMap;

    @BeforeEach
    void setUp() {
        concurrentHashMap = new HashMap<>();
        service_atomic = new SoJuManageService_Atomic(concurrentHashMap);
    }

    @RepeatedTest(10)
    void testV1() {
        int loopSize = 300;
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
                    for (int j = 0; j < loopSize; j++) {
                        service_atomic.sumGetAndPut(soJuDto);
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
                .isEqualTo(loopSize * loopSize / 3);
        Assertions.assertThat(
                concurrentHashMap.get(SoJuType.JINRO_IS_BACK.name()).getPrice())
                .isEqualTo(loopSize * loopSize / 3 * SoJuType.JINRO_IS_BACK.getPrice());

        Assertions.assertThat(
                concurrentHashMap.get(SoJuType.CHAM_ISLE.name()).getCount())
                .isEqualTo(loopSize * loopSize / 3);
        Assertions.assertThat(
                concurrentHashMap.get(SoJuType.CHAM_ISLE.name()).getPrice())
                .isEqualTo(loopSize * loopSize / 3 * SoJuType.CHAM_ISLE.getPrice());

        Assertions.assertThat(
                concurrentHashMap.get(SoJuType.CHUEM_CHURUM.name()).getCount())
                .isEqualTo(loopSize * loopSize / 3);
        Assertions.assertThat(
                concurrentHashMap.get(SoJuType.CHUEM_CHURUM.name()).getPrice())
                .isEqualTo(loopSize * loopSize / 3 * SoJuType.CHUEM_CHURUM.getPrice());
    }
}
