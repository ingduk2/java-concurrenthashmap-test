package com.test.threadsafehashmap.soju;

import lombok.Getter;

@Getter
public enum SoJuType {
    JINRO_IS_BACK(1, 3),
    CHAM_ISLE(1, 6),
    CHUEM_CHURUM(1, 9);

    private int count;
    private long price;

    SoJuType(int count, long price) {
        this.count = count;
        this.price = price;
    }

}
