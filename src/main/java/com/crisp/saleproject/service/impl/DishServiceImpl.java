package com.crisp.saleproject.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crisp.saleproject.entity.Dish;
import com.crisp.saleproject.mapper.DishMapper;
import com.crisp.saleproject.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
