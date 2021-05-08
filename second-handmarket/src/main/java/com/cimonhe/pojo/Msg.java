package com.cimonhe.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Msg {

    int id;
    int goodsId;
    String content;
    Date date;
    int criticId;

    public Msg(int goodsId, String content, Date date, int criticId) {
        this.goodsId = goodsId;
        this.content = content;
        this.date = date;
        this.criticId = criticId;
    }


}
