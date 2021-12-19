package com.test.threadsafehashmap.test.basic.nousesyncro.hashmap;

import com.test.threadsafehashmap.soju.SoJuType;
import com.test.threadsafehashmap.soju.nonthreadsafe.SoJuDto_Basic;
import com.test.threadsafehashmap.soju.nonthreadsafe.SoJuManageService_Basic;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ## v1. int dto, hashMap, computeIfAbsent
 */
@Slf4j
public class V1Test {

    private SoJuManageService_Basic service;
    private Map<String, SoJuDto_Basic> hashMap;

    @BeforeEach
    void setUp() {
        hashMap = new HashMap<>();
        service = new SoJuManageService_Basic(hashMap);
    }

    @RepeatedTest(1000)
    void testV1Test() {
        int loopSize = 300;
        int innerLoopSize = 1;
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
                    for (int j = 0; j < innerLoopSize; j++) {
                        service.sumComputeIfAbsent(soJuDto);
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

        log.info("{}", hashMap.get(SoJuType.JINRO_IS_BACK.name()));
        log.info("{}", hashMap.get(SoJuType.CHAM_ISLE.name()));
        log.info("{}", hashMap.get(SoJuType.CHUEM_CHURUM.name()));

        Assertions.assertThat(
                hashMap.get(SoJuType.JINRO_IS_BACK.name()).getCount())
                .isEqualTo(loopSize * innerLoopSize / 3);
        Assertions.assertThat(
                hashMap.get(SoJuType.JINRO_IS_BACK.name()).getPrice())
                .isEqualTo(loopSize * innerLoopSize / 3 * SoJuType.JINRO_IS_BACK.getPrice());

        Assertions.assertThat(
                hashMap.get(SoJuType.CHAM_ISLE.name()).getCount())
                .isEqualTo(loopSize * innerLoopSize / 3);
        Assertions.assertThat(
                hashMap.get(SoJuType.CHAM_ISLE.name()).getPrice())
                .isEqualTo(loopSize * innerLoopSize / 3 * SoJuType.CHAM_ISLE.getPrice());

        Assertions.assertThat(
                hashMap.get(SoJuType.CHUEM_CHURUM.name()).getCount())
                .isEqualTo(loopSize * innerLoopSize / 3);
        Assertions.assertThat(
                hashMap.get(SoJuType.CHUEM_CHURUM.name()).getPrice())
                .isEqualTo(loopSize * innerLoopSize / 3 * SoJuType.CHUEM_CHURUM.getPrice());
    }
}
