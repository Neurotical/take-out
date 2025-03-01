package com.sky.service;

import com.sky.vo.OrderSubmitVO;
import com.sky.dto.OrdersSubmitDTO;

public interface OrderService {


    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
}
