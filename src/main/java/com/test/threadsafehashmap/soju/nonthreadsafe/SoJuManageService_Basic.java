package com.test.threadsafehashmap.soju.nonthreadsafe;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SoJuManageService_Basic {

    private final Map<String, SoJuDto_Basic> soJuSumMap;

    public void sumComputeIfAbsent(SoJuDto_Basic soJuDto) {
        String key = soJuDto.getName();
        SoJuDto_Basic baseZeroDto = SoJuDto_Basic.getZeroDto(soJuDto);
        soJuSumMap.computeIfAbsent(key, value -> baseZeroDto)
                .updateCountAndPrice(soJuDto);
    }

    public void sumGetAndPut(SoJuDto_Basic soJuDto) {
        String key = soJuDto.getName();
        SoJuDto_Basic savedDto = soJuSumMap.get(key);
        if (ObjectUtils.isEmpty(savedDto)) {
            soJuSumMap.put(key, SoJuDto_Basic.deepCopy(soJuDto));
        } else {
            savedDto.updateCountAndPrice(soJuDto);
        }
    }
}
