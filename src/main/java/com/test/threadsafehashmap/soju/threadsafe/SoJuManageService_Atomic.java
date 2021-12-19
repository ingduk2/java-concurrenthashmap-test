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
public class SoJuManageService_Atomic {

    private final Map<String, SoJuDto_Atomic> soJuSumMap;

    public void computeIfAbsent(SoJuDto_Atomic soJuDto) {
        String key = soJuDto.getName();
        SoJuDto_Atomic baseZeroDto = SoJuDto_Atomic.getZeroDto(soJuDto);
        soJuSumMap.computeIfAbsent(key, value -> baseZeroDto)
                .updateCountAndPrice(soJuDto);
    }

    public void sumGetAndPut(SoJuDto_Atomic soJuDto) {
        String key = soJuDto.getName();
        SoJuDto_Atomic savedDto = soJuSumMap.get(key);
        if (ObjectUtils.isEmpty(savedDto)) {
            soJuSumMap.put(key, SoJuDto_Atomic.deepCopy(soJuDto));
        } else {
            savedDto.updateCountAndPrice(soJuDto);
        }
    }
}
