package com.crisp.saleproject.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crisp.saleproject.common.BaseContext;
import com.crisp.saleproject.common.R;
import com.crisp.saleproject.entity.ShoppingCart;
import com.crisp.saleproject.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    ShoppingCartService shoppingCartService;

    /**
     * 获取订单列表
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> getList(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> list = shoppingCartService.list(wrapper);
        return R.success(list);
    }
}
