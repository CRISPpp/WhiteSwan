package com.crisp.saleproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crisp.saleproject.common.CustomException;
import com.crisp.saleproject.entity.Category;
import com.crisp.saleproject.entity.Dish;
import com.crisp.saleproject.entity.Setmeal;
import com.crisp.saleproject.mapper.CategoryMapper;
import com.crisp.saleproject.mapper.DishMapper;
import com.crisp.saleproject.service.CategoryService;
import com.crisp.saleproject.service.DishService;
import com.crisp.saleproject.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;


    @Autowired
    private SetmealService setmealService;

    /**
     * 删除之前看有无关联
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getCategoryId, id);

        int cnt = dishService.count(wrapper);
        if(cnt > 0){
            //业务异常
            throw new CustomException("该分类已关联菜品");
        }
        LambdaQueryWrapper<Setmeal> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(Setmeal::getCategoryId, id);
        cnt = setmealService.count(wrapper1);
        if(cnt > 0){
            throw new CustomException("该分类已关联套餐");
        }
        //没有关联，正常删除
        super.removeById(id);

    }
}
