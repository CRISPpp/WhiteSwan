package com.crisp.saleproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crisp.saleproject.common.R;
import com.crisp.saleproject.dto.SetmealDto;
import com.crisp.saleproject.entity.Dish;
import com.crisp.saleproject.entity.Setmeal;
import com.crisp.saleproject.entity.SetmealDish;
import com.crisp.saleproject.mapper.SetmealMapper;
import com.crisp.saleproject.service.SetmealDishService;
import com.crisp.saleproject.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    SetmealDishService setmealDishService;
    @Autowired
    SetmealMapper setmealMapper;
    @Override
    @Transactional//要么全成功要么全失败
    public void saveWithDish(SetmealDto setmealDto) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto, setmeal);
        //判断是否存在
        Setmeal tmpMeal = setmealMapper.getDelByName(setmeal.getName());
        if(tmpMeal != null){
            setmealMapper.upddateIsDel(tmpMeal.getName());
            setmeal.setId(tmpMeal.getId());
            tmpMeal.setImage(setmeal.getImage());
            tmpMeal.setCategoryId(setmeal.getCategoryId());
            tmpMeal.setCode(setmeal.getCode());
            tmpMeal.setDescription(setmeal.getDescription());
            tmpMeal.setPrice(setmeal.getPrice());
            setmealMapper.updateById(tmpMeal);
        }else{
            this.save(setmeal);
        }
        Long id = setmeal.getId();
        List<SetmealDish> list = setmealDto.getSetmealDishes().stream().map((item) -> {
            item.setSetmealId(id);
            return item;
        }).toList();

        setmealDishService.saveBatch(list);
    }

    @Override
    public SetmealDto getWithDish(Long id) {
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = this.getById(id);
        BeanUtils.copyProperties(setmeal, setmealDto);
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> list = setmealDishService.list(wrapper);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto,setmeal);

        this.updateById(setmeal);

        Long id = setmeal.getId();
        //先清除
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, id);
        setmealDishService.remove(wrapper);

        //再添加
        List<SetmealDish> list = setmealDto.getSetmealDishes().stream().map((item) -> {
            item.setSetmealId(id);
            return item;
        }).toList();

        setmealDishService.saveBatch(list);
    }
}
