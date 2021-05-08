package com.cimonhe.mapper;

import com.cimonhe.pojo.Goods;
import com.fasterxml.jackson.databind.util.ObjectBuffer;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface GoodsMapper {

    int cntByIOS_PS(Map<String,Object> map);
    int cntByInfoLike_IOS_PS(Map<String,Object> map);
    int cntByTagId(int tagId);
    int cntBySellerId(int sellerId);
    int cntOnSaleGoodsBySellerId(int sellerId);
    List<Goods> queryByIOSandPS(Map<String,Object> map);
    List<Goods> queryByInfoLike_IOS_PS(Map<String,Object> map);
    List<Goods> queryByTagId(int tagId);
    List<Goods> queryBySellerId(int sellerId);
    List<Goods> queryOnSaleGoodsBySellerId(int sellerId);
    Goods queryById(int goodsId);
    int deleteIsOnSaleGoodsBySellerId(Map<String,Object> map);
    int addGoods(Goods goods);

    int updatePSById(Map map);

    int updateIOSbyId(int id);



}
