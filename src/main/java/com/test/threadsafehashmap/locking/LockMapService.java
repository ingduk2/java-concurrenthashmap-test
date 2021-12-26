package com.test.threadsafehashmap.locking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class LockMapService {

    private final Map<String, SoJuDto_Lock> lockMap;
    private final ReentrantLock lock = new ReentrantLock();

    public void sumLockMap(SoJuDto_Lock dto_lock) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            lockMap.computeIfAbsent(dto_lock.getName(), value -> SoJuDto_Lock.getZeroDto(dto_lock))
                    .updateCountAndPrice(dto_lock);
        } finally {
            lock.unlock();
        }
    }

    public List<SoJuDto_Lock> removeAndGetLockMap() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            return lockMap.keySet().stream()
                    .map(lockMap::remove)
                    .collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }
}
