package com.cimonhe.service;

import com.cimonhe.mapper.MsgMapper;
import com.cimonhe.pojo.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MsgServiceImpl implements MsgService{

    @Autowired
    MsgMapper msgMapper;

    @Override
    public int addMsg(Msg msg) {
        return msgMapper.addMsg(msg);
    }

    @Override
    public List<Msg> queryMsgByGoodsId(int goodsId) {
        return msgMapper.queryMsgByGoodsId(goodsId);
    }
}
