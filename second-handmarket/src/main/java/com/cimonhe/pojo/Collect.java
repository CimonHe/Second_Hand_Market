package com.cimonhe.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Collect {

    int id;
    int collectorId;
    int goodsId;

    public Collect(int collectorId, int goodsId) {
        this.collectorId = collectorId;
        this.goodsId = goodsId;
    }
}
