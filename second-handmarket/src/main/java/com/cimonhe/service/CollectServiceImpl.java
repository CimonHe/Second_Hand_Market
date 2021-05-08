package com.cimonhe.service;

import com.cimonhe.mapper.CollectMapper;
import com.cimonhe.pojo.Collect;
import com.cimonhe.pojo.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectServiceImpl implements CollectService{

    @Autowired
    CollectMapper collectMapper;

    @Override
    public Collect queryCollectByCollect(Collect collect) {
        return collectMapper.queryCollectByCollect(collect);
    }

    @Override
    public int addCollect(Collect collect) {
        return collectMapper.addCollect(collect);
    }

    @Override
    public int deleteCollect(Collect collect) {
        return collectMapper.deleteCollect(collect);
    }

    @Override
    public int cntGoodsByCollectorId(int collectorId) {
        return collectMapper.cntGoodsByCollectorId(collectorId);
    }

    @Override
    public List<Goods> queryGoodsByCollectorId(int collectorId) {
        return collectMapper.queryGoodsByCollectorId(collectorId);
    }
}
