package com.cimonhe.service;

import com.cimonhe.pojo.Msg;

import java.util.List;

public interface MsgService {

    int addMsg(Msg msg);

    List<Msg> queryMsgByGoodsId(int goodsId);

}
