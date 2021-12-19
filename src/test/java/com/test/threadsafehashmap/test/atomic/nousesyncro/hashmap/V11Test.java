package com.test.threadsafehashmap.test.atomic.nousesyncro.hashmap;

import com.test.threadsafehashmap.soju.SoJuType;
import com.test.threadsafehashmap.soju.threadsafe.SoJuDto_Atomic;
import com.test.threadsafehashmap.soju.threadsafe.SoJuManageService_Atomic;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ## v11. atomic dto, hashMap, getAndPut
 */
@Slf4j
public class V11Test {

    private SoJuManageService_Atomic service_atomic;
    private Map<String, SoJuDto_Atomic> hashMap;

    @BeforeEach
    void setUp() {
        hashMap = new HashMap<>();
        service_atomic = new SoJuManageService_Atomic(hashMap);
    }

    @RepeatedTest(10)
    void testV11Test() {
        int loopSize = 30;
        int innerLoopSize = 30;
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
                .isEqualTo(loopSize * innerLoopSize/ 3 * SoJuType.CHUEM_CHURUM.getPrice());
    }
}
