package com.cimonhe.mapper;

import com.cimonhe.pojo.Order;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface OrderMapper {

    int addOrder(Order order);
    List<Order> queryOrderBySellerId(int sellerId);
    List<Order> queryOrderByBuyerId(int buyerId);
    int cntOrderBySellerId(int sellerId);
    int cntOrderByBuyerId(int buyerId);
    int upOrderIsReceivedByIdAndBuyerId(Map<String,Object> map);
}
