package com.cimonhe.mapper;

import com.cimonhe.pojo.Collect;
import com.cimonhe.pojo.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CollectMapper {

    public Collect queryCollectByCollect(Collect collect);
    public int addCollect(Collect collect);
    public int deleteCollect(Collect collect);
    public int cntGoodsByCollectorId(int collectorId);
    public List<Goods> queryGoodsByCollectorId(int collectorId);

}
