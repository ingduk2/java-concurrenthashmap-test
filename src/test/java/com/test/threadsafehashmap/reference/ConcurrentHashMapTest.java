package com.test.threadsafehashmap.reference;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
public class ConcurrentHashMapTest {

    @RepeatedTest(1000)
    void exampleTestLongAdder() {
        int totalCount = 0;
        int loopSize = 30;
        CountDownLatch countDownLatch = new CountDownLatch(loopSize);
        ExecutorService tp = Executors.newFixedThreadPool(30);

        Map<String, LongAdder> testMap = new HashMap<>();

        for(int i = 0; i < loopSize; i++) {
            int selector = (i % 3);
            String type = (selector == 0) ? "HR" : (selector == 1) ? "SALES" : "IT";
            totalCount++;
            tp.submit(new Runner(type , countDownLatch, testMap));
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("{}", totalCount);
        log.info("{}", testMap);
        tp.shutdown();
        Assertions.assertThat(testMap.get("HR").intValue()).isEqualTo(loopSize / 3);
        Assertions.assertThat(testMap.get("SALES").intValue()).isEqualTo(loopSize / 3);
        Assertions.assertThat(testMap.get("IT").intValue()).isEqualTo(loopSize / 3);
    }

    static class Runner implements Runnable {
        private final String runnerNo;
        private final CountDownLatch countDownLatch;
        private Map<String, LongAdder> testMap;
        Runner(String runnerNo, CountDownLatch countDownLatch, Map<String, LongAdder> testMap){
            this.runnerNo = runnerNo;
            this.countDownLatch = countDownLatch;
            this.testMap = testMap;
        }
        @Override
        public void run() {
            try {
                testMap.computeIfAbsent(runnerNo, (value) -> new LongAdder()).increment();
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }

        }
    }
}
