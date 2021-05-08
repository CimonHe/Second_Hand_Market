package com.cimonhe.mapper;

import com.cimonhe.pojo.Tag_Goods;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface Tag_GoodsMapper {

    int addTag_Goods(Tag_Goods tag_goods);

    List<Tag_Goods> queryByGoodsId(int goodsId);

}
