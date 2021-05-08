package com.cimonhe.service;

import com.cimonhe.pojo.Goods;

import java.util.List;
import java.util.Map;

public interface GoodsService {

    List<Goods> queryOnSaleGoods();

    List<Goods> queryPendingGoods();

    List<Goods> queryByInfoLike_IOS_PS(String info);

    List<Goods> queryByTagId(int tagId);

    int cntByTagId(int tagId);

    int cntOnSaleGoods();

    int cntPendingGoods();

    int cntOnSaleByInfoLike(String info);

    int cntBySellerId(int sellerId);

    int addGoods(Goods goods);

    int cntOnSaleGoodsBySellerId(int sellerId);

    Goods queryById(int goodsId);

    int pendingGoodsPass(int goodsId);

    int pendingGoodsNotPass(int goodsId);

    int updateIOSbyId(int id);

    List<Goods> queryBySellerId(int sellerId);

    int deleteIsOnSaleGoodsBySellerId(int sellerId,int goodsId);


    List<Goods> queryOnSaleGoodsBySellerId(int sellerId);

}
