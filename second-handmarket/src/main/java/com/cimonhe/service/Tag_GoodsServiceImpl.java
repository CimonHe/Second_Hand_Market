package com.cimonhe.service;

import com.cimonhe.mapper.Tag_GoodsMapper;
import com.cimonhe.pojo.Tag_Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Tag_GoodsServiceImpl implements Tag_GoodsService {

    @Autowired
    Tag_GoodsMapper tag_goodsMapper;

    @Override
    public int addTag_Goods(Tag_Goods tag_goods) {
        return tag_goodsMapper.addTag_Goods(tag_goods);
    }

    @Override
    public List<Tag_Goods> queryByGoodsId(int goodsId) {
        return tag_goodsMapper.queryByGoodsId(goodsId);
    }


}
