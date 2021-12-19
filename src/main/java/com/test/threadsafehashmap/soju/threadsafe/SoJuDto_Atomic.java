package com.test.threadsafehashmap.soju.threadsafe;

import lombok.*;

import java.util.concurrent.atomic.LongAdder;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SoJuDto_Atomic {

    private String name;
    private LongAdder count = new LongAdder();
    private LongAdder price = new LongAdder();

    @Builder
    public SoJuDto_Atomic(String name, long count, long price) {
        this.name = name;
        this.count.add(count);
        this.price.add(price);
    }

    public static SoJuDto_Atomic getZeroDto(SoJuDto_Atomic soJuDto) {
        return SoJuDto_Atomic.builder()
                .name(soJuDto.getName())
                .count(0)
                .price(0)
                .build();
    }

    public static SoJuDto_Atomic deepCopy(SoJuDto_Atomic soJuDto) {
        return SoJuDto_Atomic.builder()
                .name(soJuDto.getName())
                .count(soJuDto.getCount())
                .price(soJuDto.getPrice())
                .build();
    }

    public void updateCountAndPrice(SoJuDto_Atomic soJuDto) {
        this.count.add(soJuDto.getCount());
        this.price.add(soJuDto.getPrice());
    }

    public long getCount() {
        return this.count.longValue();
    }

    public long getPrice() {
        return this.price.longValue();
    }
}
