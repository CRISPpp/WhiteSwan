package com.crisp.saleproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crisp.saleproject.dto.DishDto;
import com.crisp.saleproject.entity.Dish;
import com.crisp.saleproject.entity.DishFlavor;
import com.crisp.saleproject.mapper.DishMapper;
import com.crisp.saleproject.service.DishFlavorService;
import com.crisp.saleproject.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    DishFlavorService dishFlavorService;
    @Autowired
    DishMapper dishMapper;
    /**
     * dish 和 dishFlavor
     * @param dishDto
     */
    @Transactional//开启事务支持,要么全成功要么全失败
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //修改isDeleted字段
        String name = dishDto.getName();
        Dish dish = dishMapper.getDishByName(name);
        if(dish != null){
            dishMapper.upddateIsDel(name);
            dish.setCategoryId(dishDto.getCategoryId());
            dish.setImage(dishDto.getImage());
            dish.setPrice(dishDto.getPrice());
            dish.setDescription(dishDto.getDescription());
            this.updateById(dish);
            dishDto.setId(dish.getId());
        }
        else {
            //保存到dish
            this.save(dishDto);
        }
        //save后雪花算法已经生成了id，能直接获取
        Long dishId = dishDto.getId();
        List<DishFlavor> list = dishDto.getFlavors();
        for (DishFlavor dishFlavor : list) {
            dishFlavor.setDishId(dishId);
        }

        dishFlavorService.saveBatch(list);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查到dish
        Dish dish = this.getById(id);
        DishDto ret = new DishDto();
        BeanUtils.copyProperties(dish, ret);

        //查flavor
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> list = dishFlavorService.list(wrapper);
        ret.setFlavors(list);
        return ret;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);
        //删除原来的flavor标签
        Long id = dishDto.getId();
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, id);
        dishFlavorService.remove(wrapper);

        //插入新的
        List<DishFlavor> list = dishDto.getFlavors().stream().map((item) -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(list);
    }
}
