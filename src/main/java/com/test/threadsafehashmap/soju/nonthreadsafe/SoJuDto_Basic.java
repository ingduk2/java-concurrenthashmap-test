package com.test.threadsafehashmap.soju.nonthreadsafe;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SoJuDto_Basic {

    private String name;
    private long count;
    private long price;

    @Builder
    public SoJuDto_Basic(String name, long count, long price) {
        this.name = name;
        this.count = count;
        this.price = price;
    }

    public static SoJuDto_Basic getZeroDto(SoJuDto_Basic soJuDto) {
        return SoJuDto_Basic.builder()
                .name(soJuDto.getName())
                .count(0)
                .price(0)
                .build();
    }

    public static SoJuDto_Basic deepCopy(SoJuDto_Basic soJuDto) {
        return SoJuDto_Basic.builder()
                .name(soJuDto.getName())
                .count(soJuDto.getCount())
                .price(soJuDto.getPrice())
                .build();
    }

    public void updateCountAndPrice(SoJuDto_Basic soJuDto) {
        this.count += soJuDto.getCount();
        this.price += soJuDto.getPrice();
    }
}
