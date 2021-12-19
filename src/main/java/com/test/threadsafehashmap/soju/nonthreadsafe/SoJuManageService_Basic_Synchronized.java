package com.test.threadsafehashmap.soju.nonthreadsafe;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SoJuManageService_Basic_Synchronized {

    private final Map<String, SoJuDto_Basic> soJuSumMap;

    public synchronized void sumComputeIfAbsent(SoJuDto_Basic soJuDto) {
        String key = soJuDto.getName();
        SoJuDto_Basic baseZeroDto = SoJuDto_Basic.getZeroDto(soJuDto);
        soJuSumMap.computeIfAbsent(key, value -> baseZeroDto)
                .updateCountAndPrice(soJuDto);
    }

    public synchronized void sumGetAndPut(SoJuDto_Basic soJuDto) {
        String key = soJuDto.getName();
        SoJuDto_Basic savedDto = soJuSumMap.get(key);
        if (ObjectUtils.isEmpty(savedDto)) {
            soJuSumMap.put(key, SoJuDto_Basic.deepCopy(soJuDto));
        } else {
            savedDto.updateCountAndPrice(soJuDto);
        }
    }
}
