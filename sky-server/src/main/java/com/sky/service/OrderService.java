package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {


    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    PageResult getHistoryOrders(Integer page, Integer pageSize, Integer status);

    OrderVO getOrderWithDetail(Long orderId);

    void cancelOrder(Long id);

    void repetition(Long id);

    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO getOrderStatistics();

    void confirmOrder(Long id);

    void rejectionOrder(OrdersRejectionDTO ordersRejectionDTO);

    void cancelOrderByAdmin(OrdersCancelDTO ordersCancelDTO);

    void delivery(Long id);

    void completeOrder(Long id);
}
