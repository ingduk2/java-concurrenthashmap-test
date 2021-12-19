# ConcurrentHashMap count sum Test
#### concurrentHashMap 을 써도 ThreadSafe 하지 않아서 여러가지 노가다로 테스트 해봄
#### spring bean singleton 이기 때문에 Class에 상태값을 가지면 동기화 문제가 발생 할 수밖에 없음
#### 뇌피셜이 많이 들어가 있으며, 틀린 부분이 있을 수도 있고, 저만 모르는 걸수도 있음..
* #### 결론 1. : synchronized 쓰면 걍 해결됨! 그러나 트래픽이 오를수록, synchronized 부분에서 **시간이 소요하는 만큼** 배로 성능 저하(db call, restapi call 등이 있을듯). concurrentHashMap의 의미가 없음..
* #### 결론 2. : atomic Dto, ConcurrentHashMap, computeXXX 연산 사용(get and put X)
* #### 더 좋은 방법이 있을 것 같은데 모르겠음....
---
Java Map 구현체 비교
#### HashMap
* No Thread Safe
#### HashTable
* Thread Safe
* 주요 method 들이 synchronized 되어있음 그래서 성능 이슈가 생김, 그러나 확실히 동기화 될듯. 
#### ConcurrentHashMap
* Thread Safe(조건부) **여러개의 Action은 보장하지 않음**(get후 없을 경우 put), 복합작업 메서드 **computeXXX** 지원
* dto, vo update 할때는 concurrentHashMap과 관계가 없다. (그래서 atomic 필요한듯)
* 빈 해시 버킷에 삽입하는 경우 lock 사용 X, Compare and Swap 사용
* 이미 노드가 있는 경우 synchronized를 이용해 동기화
---
Test
#### int, long 등 primitive(기본형 타입) Dto
* hashmap
  * no synchronized
     * v1. int dto, hashMap, computeIfAbsent **실패함**
     * v3. int dto, hashMap, getAndPut **실패**
* **concurrentHashMap**
  * no synchronized
    * v5. int dto, concurrentHashMap, computeIfAbsent **실패 ?????**
    * v7. int dto, concurrentHashMap, getAndPut **실패 ?????**
      
#### java.util.concurrent.atomic 패키지 사용 Dto
* hashmap
  * no synchronized
    * v9. atomic dto, hashMap, computeIfAbsent **실패** (1번 여러번 돌리면 잘 실패하는데, repeated하면 1~2개빼고 거의 성공하는데 이유는 못찾음..)
    * v11. atomic dto, hashMap, getAndPut **실패** (1번 여러번 돌리면 잘 실패하는데, repeated하면 1~2개빼고 거의 성공하는데 이유는 못찾음..)
* **concurrentHashMap**
  * no synchronized
    * v13. atomic dto, ConcurrentHashMap, computeIfAbsent **성공!!!!**
    * v15. atomic dto, ConcurrentHashMap, getAndPut **실패 (복합작업이라서 보장을 못해주는듯)** (1번 여러번 돌리면 잘 실패하는데, repeated하면 1~2개빼고 거의 성공하는데 이유는 못찾음..)

#### synchronized 사용
* v2. int dto, hashMap, **synchronized method** computeIfAbsent **성공**
* v4. int dto, hashMap, **synchronized method** getAndPut **성공**
* v6. int dto, concurrentHashMap, **synchronized method**, computeIfAbsent **성공**
* v8. int dto, concurrentHashMap, **synchronized method**, getAndPut **성공**
* v10. atomic dto, hashMap, **synchronized method** computeIfAbsent **성공**
* v12. atomic dto, hashMap, **synchronized method** getAndPut **성공**
* v14. atomic dto, ConcurrentHashMap, **synchronized method** computeIfAbsent **성공**
* v16. atomic dto, ConcurrentHashMap, **synchronized method** getAndPut **성공**

[삽질만 안하면 성공함.](https://github.com/ingduk2/java-concurrenthashmap-test/blob/master/vain.md)

* reference
  * [Java의 Synchronized 키워드](https://sup2is.github.io/2020/11/02/java-synchroinzed-keyword.html)
  * [Testing Multi-Threaded Code in Java](https://www.baeldung.com/java-testing-multithreaded)
  * [concurrentHashMap 예제 및 주의점](http://blog.breakingthat.com/2019/04/04/java-collection-map-concurrenthashmap/)
  * [concurrentHashMap 동기화 방식](https://pplenty.tistory.com/17)
  * [HashMap, Hashtable, ConcurrentHashMap 동기화 처리 방식](https://tomining.tistory.com/169)
  * [atomic](https://acet.pe.kr/809)
  * [atomic](https://bestugi.tistory.com/41)
