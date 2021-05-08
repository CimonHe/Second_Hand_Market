package com.cimonhe.mapper;

import com.cimonhe.pojo.Msg;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Max;
import java.util.List;

@Mapper
@Repository
public interface MsgMapper {

    int addMsg(Msg msg);

    List<Msg> queryMsgByGoodsId(int goodsId);

}
