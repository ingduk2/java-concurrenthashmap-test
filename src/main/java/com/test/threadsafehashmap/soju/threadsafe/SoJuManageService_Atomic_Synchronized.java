package com.test.threadsafehashmap.soju.threadsafe;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SoJuManageService_Atomic_Synchronized {

    private final Map<String, SoJuDto_Atomic> soJuSumMap;

    public synchronized void computeIfAbsent(SoJuDto_Atomic soJuDto) {
        log.info("{}", soJuDto);
        String key = soJuDto.getName();
        SoJuDto_Atomic baseZeroDto = SoJuDto_Atomic.getZeroDto(soJuDto);
        soJuSumMap.computeIfAbsent(key, value -> baseZeroDto)
                .updateCountAndPrice(soJuDto);
    }

    public synchronized void sumGetAndPut(SoJuDto_Atomic soJuDto) {
        log.info("{}", soJuDto);
        String key = soJuDto.getName();
        SoJuDto_Atomic savedDto = soJuSumMap.get(key);
        if (ObjectUtils.isEmpty(savedDto)) {
            log.info("put");
            soJuSumMap.put(key, SoJuDto_Atomic.deepCopy(soJuDto));
            //븅신짓함.. 상황에 따라서 참조 공유해서 꼬임
//            soJuSumMap.put(key, soJuDto);
        } else {
            log.info("update");
            savedDto.updateCountAndPrice(soJuDto);
        }
    }
}
