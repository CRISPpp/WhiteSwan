package com.crisp.saleproject.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crisp.saleproject.common.BaseContext;
import com.crisp.saleproject.common.R;
import com.crisp.saleproject.dto.OrderDto;
import com.crisp.saleproject.entity.OrderDetail;
import com.crisp.saleproject.entity.Orders;
import com.crisp.saleproject.service.OrderDetailService;
import com.crisp.saleproject.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderDetailService orderDetailService;
    /**
     * 提交订单
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("complete");
    }

    /**
     * 分页
     */
    @GetMapping("userPage")
    public R<Page> getPage(int page, int pageSize){
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, userId);
        orderService.page(pageInfo, wrapper);
        List<Orders> ordersList = pageInfo.getRecords();
        Page<OrderDto> pageDto = new Page<>(page, pageSize);
        BeanUtils.copyProperties(pageInfo, pageDto, "records");

        List<OrderDto> list = ordersList.stream().map((item) -> {
            OrderDto orderDto = new OrderDto();
            BeanUtils.copyProperties(item, orderDto);
            Long orderId = item.getId();
            LambdaQueryWrapper<OrderDetail> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(OrderDetail::getOrderId, orderId);
            List<OrderDetail> list1 = orderDetailService.list(wrapper1);
           orderDto.setOrderDetails(list1);
            return orderDto;
        }).toList();

        pageDto.setRecords(list);
        return R.success(pageDto);
    }
}
