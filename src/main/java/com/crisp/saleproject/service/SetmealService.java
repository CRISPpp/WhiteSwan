package com.crisp.saleproject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crisp.saleproject.dto.SetmealDto;
import com.crisp.saleproject.entity.Setmeal;


public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);
    public SetmealDto getWithDish(Long id);
    public void updateWithDish(SetmealDto setmealDto);
}
