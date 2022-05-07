package com.crisp.saleproject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crisp.saleproject.entity.Orders;

public interface OrderService extends IService<Orders> {
    public abstract void submit(Orders orders);
}
