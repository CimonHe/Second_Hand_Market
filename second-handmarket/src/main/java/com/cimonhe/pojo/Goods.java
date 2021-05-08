package com.cimonhe.pojo;

import com.cimonhe.constant.PendingStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Goods {

    int id;

    String goodsInfo;

    double price;

    int sellerId;

    boolean isOnSale;

    int pendingStatus;

    String sellerPhone;

    public Goods(String goodsInfo, double price, int sellerId, String sellerPhone) {
        this.goodsInfo = goodsInfo;
        this.price = price;
        this.sellerId = sellerId;
        this.sellerPhone = sellerPhone;

        this.isOnSale = true;
        this.pendingStatus = PendingStatus.PENDING;
    }
}
