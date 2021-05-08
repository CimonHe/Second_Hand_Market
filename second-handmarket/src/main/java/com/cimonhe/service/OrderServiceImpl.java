package com.cimonhe.service;

import com.cimonhe.mapper.OrderMapper;
import com.cimonhe.pojo.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    OrderMapper orderMapper;

    @Override
    public int addOrder(Order order) {
        return orderMapper.addOrder(order);
    }

    @Override
    public List<Order> queryOrderBySellerId(int sellerId) {
        return orderMapper.queryOrderBySellerId(sellerId);
    }

    @Override
    public List<Order> queryOrderByBuyerId(int buyerId) {
        return orderMapper.queryOrderByBuyerId(buyerId);
    }

    @Override
    public int cntOrderBySellerId(int sellerId) {
        return orderMapper.cntOrderBySellerId(sellerId);
    }

    @Override
    public int cntOrderByBuyerId(int buyerId) {
        return orderMapper.cntOrderByBuyerId(buyerId);
    }

    @Override
    public int upOrderIsReceivedByIdAndBuyerId(int id,int evaluation, int buyerId) {
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("buyerId",buyerId);
        map.put("evaluation",evaluation);
        return orderMapper.upOrderIsReceivedByIdAndBuyerId(map);
    }


}
