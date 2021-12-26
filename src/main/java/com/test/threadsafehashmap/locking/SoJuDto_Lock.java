package com.test.threadsafehashmap.locking;

import lombok.*;

import java.util.concurrent.atomic.LongAdder;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SoJuDto_Lock {

    private String name;
    private LongAdder count = new LongAdder();
    private LongAdder price = new LongAdder();

    @Builder
    public SoJuDto_Lock(String name, int count, long price) {
        this.name = name;
        this.count.add(count);
        this.price.add(price);
    }

    public static SoJuDto_Lock getZeroDto(SoJuDto_Lock soJuDto) {
        return SoJuDto_Lock.builder()
                .name(soJuDto.getName())
                .count(0)
                .price(0)
                .build();
    }

    public static SoJuDto_Lock deepCopy(SoJuDto_Lock soJuDto) {
        return SoJuDto_Lock.builder()
                .name(soJuDto.getName())
                .count(soJuDto.getCount())
                .price(soJuDto.getPrice())
                .build();
    }

    public void updateCountAndPrice(SoJuDto_Lock soJuDto) {
        this.count.add(soJuDto.getCount());
        this.price.add(soJuDto.getPrice());
    }

    public int getCount() {
        return this.count.intValue();
    }

    public long getPrice() {
        return this.price.longValue();
    }
}
