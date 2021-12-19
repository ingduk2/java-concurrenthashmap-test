생각없이 만들었더니 이래됨.... 어떤 상황에서는 괜찮지만 또 어떤 상황에서는 문제가 생김
```java
    public synchronized void sumGetAndPut(SoJuDto_Atomic soJuDto) {
        String key = soJuDto.getName();
        SoJuDto_Atomic savedDto = soJuSumMap.get(key);
        if (ObjectUtils.isEmpty(savedDto)) {
            soJuSumMap.put(key, soJuDto);
        } else {
            savedDto.updateCountAndPrice(soJuDto);
        }
    }
```
여기서는 성공함. 쓰레드 스택 끝나서 문제 없는듯
```java
        for(int i = 0; i < loopSize; i++) {
            executorService.submit(() -> {
                try {
                    service_atomic_synchronized.sumGetAndPut(soJuDto);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
```
부하 제곱으로 더 줄려고 내부에 for문 추가하니 값이 뻥튀기가 됨. loopSize도는동안 threadStack 살아있어서 값이 뻥튀기되는듯?
```java
        for(int i = 0; i < loopSize; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < loopSize; j++) {
                        service_atomic_synchronized.sumGetAndPut(soJuDto);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
```
내부에서 참조 못하게 새로 생성함
```java
    public synchronized void sumGetAndPut(SoJuDto_Atomic soJuDto) {
        String key = soJuDto.getName();
        SoJuDto_Atomic savedDto = soJuSumMap.get(key);
        if (ObjectUtils.isEmpty(savedDto)) {
            soJuSumMap.put(key, SoJuDto_Atomic.deepCopy(soJuDto));
//            soJuSumMap.put(key, soJuDto);
        } else {
            savedDto.updateCountAndPrice(soJuDto);
        }
    }
```
