package com.test.threadsafehashmap.reference;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
public class ConcurrentHashMapGetAndPutTest {

    @RepeatedTest(3000)
    void exampleTestLongAdderGetAndPut() {
        int totalCount = 0;
        int loopSize = 300;
        CountDownLatch countDownLatch = new CountDownLatch(loopSize);
        ExecutorService tp = Executors.newFixedThreadPool(30);

        Map<String, LongAdder> testMap = new ConcurrentHashMap<>();

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
        Assertions.assertThat(testMap.get("HR").intValue()).isEqualTo(loopSize / 3 * 10);
        Assertions.assertThat(testMap.get("SALES").intValue()).isEqualTo(loopSize / 3 * 10);
        Assertions.assertThat(testMap.get("IT").intValue()).isEqualTo(loopSize / 3 * 10);
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
                for (int i = 0; i < 10; i++) {
//                    testMap.computeIfAbsent(runnerNo, value -> new LongAdder()).increment();
                    //아니 왜 처음에는 무조건 안맞지? 중간에는 가끔 안맞음. 흠
                    LongAdder savedAdder = testMap.get(runnerNo);
                    if (ObjectUtils.isEmpty(savedAdder)) {
                        LongAdder longAdder = new LongAdder();
                        longAdder.increment();
                        testMap.put(runnerNo, longAdder);
                    } else {
                        savedAdder.increment();
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }

        }
    }
}
