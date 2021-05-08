package com.cimonhe.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tag_Goods {

    int id;
    int tagId;
    int goodsId;

    public Tag_Goods(int tagId, int goodsId) {
        this.tagId = tagId;
        this.goodsId = goodsId;
    }
}
