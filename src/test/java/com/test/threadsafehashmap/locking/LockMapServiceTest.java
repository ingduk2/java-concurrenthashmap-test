package com.test.threadsafehashmap.locking;

import com.test.threadsafehashmap.soju.SoJuType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class LockMapServiceTest {

    private LockMapService lockMapService;
    private Map<String, SoJuDto_Lock> lockMap;
    private List<SoJuDto_Lock> readList;

    @BeforeEach
    void setUp() {
        lockMap = new ConcurrentHashMap<>();
        lockMapService = new LockMapService(lockMap);
        readList = new ArrayList<>();
    }

    @Test
    void lockMapServiceTest() {
        int loopSize = 3000000;
        int innerLoopSize = 1;
        CountDownLatch countDownLatch = new CountDownLatch(loopSize);
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        ScheduledExecutorService singleService = Executors.newScheduledThreadPool(1);

        singleService.scheduleAtFixedRate(
                new ReadThread(readList),
                1000,
                1000,
                TimeUnit.MILLISECONDS);

        for (int i = 0; i < loopSize; i++) {
            int selector = (i % 3);
            SoJuDto_Lock soJuDto_lock = getSoJuDto_lock(selector);

            executorService.submit(
                    new WriteThread(lockMapService, soJuDto_lock, countDownLatch, innerLoopSize));
        }

        //countDown 다떨어질때까지 기다림.
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        singleService.shutdown();

        log.info("{}", lockMap);
        log.info("{}", readList);

        Map<String, SoJuDto_Lock> jinroMap = new ConcurrentHashMap<>();
        Map<String, SoJuDto_Lock> chamMap = new ConcurrentHashMap<>();
        Map<String, SoJuDto_Lock> chuemMap = new ConcurrentHashMap<>();
        getResult(jinroMap, chamMap, chuemMap);
        log.info("==================================");
        log.info("{}", jinroMap);
        log.info("{}", chamMap);
        log.info("{}", chuemMap);
    }

    private void getResult(Map<String, SoJuDto_Lock> jinroMap,
                           Map<String, SoJuDto_Lock> chamMap,
                           Map<String, SoJuDto_Lock> chuemMap) {
        //1 map에 남은 개수
        String jinroKey = SoJuType.JINRO_IS_BACK.name();
        jinroMap.put(jinroKey, lockMap.get(jinroKey));

        String chamKey = SoJuType.CHAM_ISLE.name();
        chamMap.put(chamKey, lockMap.get(chamKey));

        String chuemKey = SoJuType.CHUEM_CHURUM.name();
        chuemMap.put(chuemKey, lockMap.get(chuemKey));

        //2 list에 남은 개수
        for (SoJuDto_Lock soJuDto_lock : readList) {
            String name = soJuDto_lock.getName();
            if (name.equals(jinroKey)) {
//                jinroMap.computeIfPresent(name, (k, v) -> {
//                    v.updateCountAndPrice(soJuDto_lock);
//                    return v;
//                });
                jinroMap.computeIfAbsent(name, v -> SoJuDto_Lock.getZeroDto(soJuDto_lock))
                        .updateCountAndPrice(soJuDto_lock);
            } else if (name.equals(chamKey)) {
//                chamMap.computeIfPresent(name, (k, v) -> {
//                    v.updateCountAndPrice(soJuDto_lock);
//                    return v;
//                });
                chamMap.computeIfAbsent(name, v -> SoJuDto_Lock.getZeroDto(soJuDto_lock))
                        .updateCountAndPrice(soJuDto_lock);
            } else if (name.equals(chuemKey)) {
//                chuemMap.computeIfPresent(name, (k, v) -> {
//                    v.updateCountAndPrice(soJuDto_lock);
//                    return v;
//                });
                chuemMap.computeIfAbsent(name, v -> SoJuDto_Lock.getZeroDto(soJuDto_lock))
                        .updateCountAndPrice(soJuDto_lock);
            }
        }
    }

    private SoJuDto_Lock getSoJuDto_lock(int selector) {
        SoJuType soJu = SoJuType.values()[selector];
        return SoJuDto_Lock.builder()
                .name(soJu.name())
                .count(soJu.getCount())
                .price(soJu.getPrice())
                .build();
    }

    @RequiredArgsConstructor
    private class ReadThread implements Runnable {

        private final List<SoJuDto_Lock> readList;

        @Override
        public void run() {
            try {
                List<SoJuDto_Lock> read = lockMapService.removeAndGetLockMap();
                readList.addAll(read);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @RequiredArgsConstructor
    private class WriteThread implements Runnable {

        private final LockMapService lockMapService;
        private final SoJuDto_Lock soJuDto_lock;
        private final CountDownLatch countDownLatch;
        private final int innerLoopSize;

        @Override
        public void run() {
            try {
                for (int i = 0; i < innerLoopSize; i++) {
                    lockMapService.sumLockMap(soJuDto_lock);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        }
    }

}