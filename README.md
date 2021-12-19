# ConcurrentHashMap count sum Test
## concurrentHashMap 을 써도 ThreadSafe 하지 않아서 여러가지 노가다로 테스트 해봄
## 뇌피셜이 많이 들어가 있으며, 틀린 부분이 있을 수도 있음..
## 결론 1. : 트래픽 적으면 synchronized 쓰면 걍 해결됨!! 그러나 트래픽이 더 늘어나거나 synchronized 걸린 부분에서 시간이 소요하는 만큼 배로 성능이 저하
## 결론 2. : atomic Dto, ConcurrentHashMap, computeXXX 연산 사용(get, put X)
## 더 좋은 방법이 있을 것 같은데 모르겠음....
#### HashMap : 멀티쓰레드 동기화 안됨
#### HashTable : 주요 method 들이 synchronized 되어있음 그래서 성능이 많이 깎임, 그러나 확실히 동기화 될듯. 
#### ConcurrentHashMap : 

## v1. int dto, hashMap, computeIfAbsent
## -> 당연히 실패
## v2. int dto, hashMap, synchronized method computeIfAbsent
## -> 당연히 성공
## v3. int dto, hashMap, getAndPut
## -> 당연히 실패
## v4. int dto, hashMap, synchronized method getAndPut
## -> 당연히 성공

## v5. int dto, concurrentHashMap, computeIfAbsent
## -> 실패함.. ?????? 
## v6. int dto, concurrentHashMap, synchronized method, computeIfAbsent
## -> 당연히 성공
## v7. int dto, concurrentHashMap, getAndPut
## -> 실패함.. ??????
## v8. int dto, concurrentHashMap, synchronized method, getAndPut
## -> 당연히 성공

## v9. atomic dto, hashMap, computeIfAbsent
## -> 실패
## v10. atomic dto, hashMap, synchronized method computeIfAbsent
## -> 당연히 성공
## v11. atomic dto, hashMap, getAndPut
## -> 실패
## v12. atomic dto, hashMap, synchronized method getAndPut
## -> 당연히 성공

## v13. atomic dto, ConcurrentHashMap, computeIfAbsent
## -> 성공!!!!
## v14. atomic dto, ConcurrentHashMap, synchronized method computeIfAbsent
## -> 당연히 성공
## v15. atomic dto, ConcurrentHashMap, getAndPut
## -> 실패
## v16. atomic dto, ConcurrentHashMap, synchronized method getAndPut
## -> 당연히 성공

* reference
  * https://sup2is.github.io/2020/11/02/java-synchroinzed-keyword.html
  * https://www.baeldung.com/java-testing-multithreaded
  * http://blog.breakingthat.com/2019/04/04/java-collection-map-concurrenthashmap/
  * https://pplenty.tistory.com/17
  * https://tomining.tistory.com/169