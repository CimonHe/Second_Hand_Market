package com.cimonhe.service;

import com.cimonhe.pojo.Collect;
import com.cimonhe.pojo.Goods;

import java.util.List;

public interface CollectService {

    public Collect queryCollectByCollect(Collect collect);
    public int addCollect(Collect collect);
    public int deleteCollect(Collect collect);
    public int cntGoodsByCollectorId(int collectorId);
    public List<Goods> queryGoodsByCollectorId(int collectorId);

}
