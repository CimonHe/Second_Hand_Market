package com.cimonhe;

import com.cimonhe.constant.PendingStatus;
import com.cimonhe.mapper.GoodsMapper;
import com.cimonhe.pojo.Goods;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class GoodsTest {

    @Autowired
    GoodsMapper goodsMapper;

    @Test
    public void queryPassGoods() {
        Map<String,Object> map = new HashMap<>();
        map.put("pendingStatus",PendingStatus.PASS);
        map.put("isOnSale",true);
        for (Goods goods : goodsMapper.queryByTagId(1)) {
            System.out.println(goods);
        }
    }

    @Test
    public void addGoods() {
        Goods goods =  new Goods("三八八",20.5,1,"132114321");
        System.out.println(goodsMapper.addGoods(goods));
        System.out.println(goods.getId());

    }

    @Test
    public void cntOnSaleByInfoLike() {
        Map<String,Object> map = new HashMap<>();
        map.put("pendingStatus",PendingStatus.PASS);
        map.put("isOnSale",true);
        map.put("info","%红%");
        System.out.println(goodsMapper.cntByInfoLike_IOS_PS(map));
    }

}
