package com.cimonhe.service;

import com.cimonhe.pojo.Order;

import java.util.List;

public interface OrderService {

    int addOrder(Order order);
    List<Order> queryOrderBySellerId(int sellerId);
    List<Order> queryOrderByBuyerId(int buyerId);
    int cntOrderBySellerId(int sellerId);
    int cntOrderByBuyerId(int buyerId);
    public int upOrderIsReceivedByIdAndBuyerId(int id,int evaluation, int buyerId);
}
