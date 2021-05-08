package com.cimonhe.service;

import com.cimonhe.pojo.Tag_Goods;

import java.util.List;

public interface Tag_GoodsService {

    public int addTag_Goods(Tag_Goods tag_goods);

    List<Tag_Goods> queryByGoodsId(int goodsId);

}
