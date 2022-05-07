package com.crisp.saleproject.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crisp.saleproject.entity.ShoppingCart;
import com.crisp.saleproject.mapper.ShoppingCartMapper;
import com.crisp.saleproject.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShorppingCardServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
