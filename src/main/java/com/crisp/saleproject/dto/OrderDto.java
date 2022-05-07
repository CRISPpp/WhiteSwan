package com.crisp.saleproject.dto;

import com.crisp.saleproject.entity.OrderDetail;
import com.crisp.saleproject.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends Orders {
    List<OrderDetail> orderDetails;
}
