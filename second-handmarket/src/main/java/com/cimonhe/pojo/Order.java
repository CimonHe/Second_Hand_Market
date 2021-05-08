package com.cimonhe.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    int id;

    int goodsId;

    Date deliveryTime;

    int addressId;

    boolean isReceived;

    Integer evaluation;

    public Order(int goodsId, Date deliveryTime, int addressId) {
        this.goodsId = goodsId;
        this.deliveryTime = deliveryTime;
        this.addressId = addressId;
    }
}
