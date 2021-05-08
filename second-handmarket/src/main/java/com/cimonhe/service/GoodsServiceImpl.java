package com.cimonhe.service;

import com.cimonhe.constant.PendingStatus;
import com.cimonhe.mapper.GoodsMapper;
import com.cimonhe.pojo.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoodsServiceImpl implements GoodsService{

    @Autowired
    GoodsMapper goodsMapper;

    @Override
    public List<Goods> queryOnSaleGoods() {
        Map<String,Object> map = new HashMap<>();
        map.put("pendingStatus",PendingStatus.PASS);
        map.put("isOnSale",true);
        return goodsMapper.queryByIOSandPS(map);
    }

    @Override
    public List<Goods> queryPendingGoods() {
        Map<String,Object> map = new HashMap<>();
        map.put("pendingStatus",PendingStatus.PENDING);
        map.put("isOnSale",true);
        return goodsMapper.queryByIOSandPS(map);
    }

    @Override
    public List<Goods> queryByInfoLike_IOS_PS(String info) {
        Map<String,Object> map = new HashMap<>();
        map.put("pendingStatus",PendingStatus.PASS);
        map.put("isOnSale",true);
        map.put("info","%"+info+"%");
        return goodsMapper.queryByInfoLike_IOS_PS(map);
    }

    @Override
    public List<Goods> queryByTagId(int tagId) {
        return goodsMapper.queryByTagId(tagId);
    }

    @Override
    public int cntByTagId(int tagId) {
        return goodsMapper.cntByTagId(tagId);
    }


    @Override
    public int cntOnSaleGoods() {
        Map<String,Object> map = new HashMap<>();
        map.put("pendingStatus",PendingStatus.PASS);
        map.put("isOnSale",true);
        return goodsMapper.cntByIOS_PS(map);
    }

    @Override
    public int cntPendingGoods() {
        Map<String,Object> map = new HashMap<>();
        map.put("pendingStatus",PendingStatus.PENDING);
        map.put("isOnSale",true);
        return goodsMapper.cntByIOS_PS(map);
    }

    @Override
    public int cntOnSaleByInfoLike(String info) {
        Map<String,Object> map = new HashMap<>();
        map.put("pendingStatus",PendingStatus.PASS);
        map.put("isOnSale",true);
        map.put("info","%"+info+"%");
        return goodsMapper.cntByInfoLike_IOS_PS(map);
    }

    @Override
    public int cntBySellerId(int sellerId) {
        return goodsMapper.cntBySellerId(sellerId);
    }

    @Override
    public int addGoods(Goods goods) {
        return goodsMapper.addGoods(goods);
    }

    @Override
    public int cntOnSaleGoodsBySellerId(int sellerId) {
        return goodsMapper.cntBySellerId(sellerId);
    }

    @Override
    public Goods queryById(int goodsId) {
        return goodsMapper.queryById(goodsId);
    }

    @Override
    public int pendingGoodsPass(int goodsId) {
        Map<String,Object> map = new HashMap<>();
        map.put("newPS",PendingStatus.PASS);
        map.put("goodsId",goodsId);
        return goodsMapper.updatePSById(map);
    }

    @Override
    public int pendingGoodsNotPass(int goodsId) {
        Map<String,Object> map = new HashMap<>();
        map.put("newPS",PendingStatus.NO_PASS);
        map.put("goodsId",goodsId);
        return goodsMapper.updatePSById(map);
    }

    @Override
    public int updateIOSbyId(int id) {
        return goodsMapper.updateIOSbyId(id);
    }

    @Override
    public List<Goods> queryBySellerId(int sellerId) {
        return goodsMapper.queryBySellerId(sellerId);
    }

    @Override
    public int deleteIsOnSaleGoodsBySellerId(int sellerId,int goodsId) {
        Map<String,Object> map = new HashMap<>();
        map.put("sellerId",sellerId);
        map.put("goodsId",goodsId);
        return goodsMapper.deleteIsOnSaleGoodsBySellerId(map);
    }

    @Override
    public List<Goods> queryOnSaleGoodsBySellerId(int sellerId) {
        return goodsMapper.queryOnSaleGoodsBySellerId(sellerId);
    }


}
