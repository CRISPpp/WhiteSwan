package com.crisp.saleproject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crisp.saleproject.dto.DishDto;
import com.crisp.saleproject.entity.Dish;

public interface DishService extends IService<Dish>  {
    //通过dto添加dish和dishflavor两张表
    public void saveWithFlavor(DishDto dishDto);
    public DishDto getByIdWithFlavor(Long id);
    public void updateWithFlavor(DishDto dishDto);
}
