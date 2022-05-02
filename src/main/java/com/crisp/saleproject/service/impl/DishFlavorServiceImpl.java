package com.crisp.saleproject.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crisp.saleproject.entity.DishFlavor;
import com.crisp.saleproject.mapper.DishFlavorMapper;
import com.crisp.saleproject.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
