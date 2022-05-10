package com.crisp.saleproject.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crisp.saleproject.common.R;
import com.crisp.saleproject.dto.DishDto;
import com.crisp.saleproject.entity.Category;
import com.crisp.saleproject.entity.Dish;
import com.crisp.saleproject.entity.DishFlavor;
import com.crisp.saleproject.mapper.DishFlavorMapper;
import com.crisp.saleproject.mapper.DishMapper;
import com.crisp.saleproject.service.CategoryService;
import com.crisp.saleproject.service.DishFlavorService;
import com.crisp.saleproject.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    DishService dishService;

    @Autowired
    DishMapper dishMapper;

    @Autowired
    DishFlavorService dishFlavorService;

    @Autowired
    DishFlavorMapper dishFlavorMapper;

    @Autowired
    CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 分页
     * @return
     */

    @GetMapping("/page")
    public R<Page> gPage(int page, int pageSize,String name){
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Dish::getName, name);
        wrapper.orderByDesc(Dish::getUpdateTime).orderByAsc(Dish::getSort);
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> pageDto = new Page<>();//原Dish中没有categoryName，所以必须返回另一个有该属性的页

        dishService.page(pageInfo, wrapper);
        //对象拷贝,records我们要自己处理，先忽略拷贝
        BeanUtils.copyProperties(pageInfo, pageDto, "records");

        List<Dish> dishList = pageInfo.getRecords();
        List<DishDto> retList = dishList.stream().map((item) ->{
            DishDto tmp = new DishDto();
            BeanUtils.copyProperties(item, tmp);
            Long cId = item.getCategoryId();
            Category cTmp = categoryService.getById(cId);
            tmp.setCategoryName(cTmp.getName());
            return tmp;
        }).collect(Collectors.toList());

        pageDto.setRecords(retList);
        return R.success(pageDto);
    }

    /**
     * 新增dish
     * @param
     * @return
     */
    @PostMapping
    public R<String> dSave(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("添加成功");
    }

    //修改时返回的页
    @GetMapping("/{id}")
    public R<DishDto> gDto(@PathVariable long id){
        return R.success(dishService.getByIdWithFlavor(id));
    }

    //修改功能
    @PutMapping
    public R<String> changeDish(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);

        //清除redis缓存
        String key = "Dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("修改成功");
    }

    /**
     * 批量删除
     */
    @DeleteMapping
    @Transactional
    public R<String> deleteDish(Long[] ids){
        boolean flag = false;
        for(Long id : ids){
            Dish dish = dishService.getById(id);
            if(dish.getStatus() == 1){
                flag = true;
                continue;
            }
            //删除相关flavor
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId, id);
            dishFlavorService.remove(wrapper);

            //删除相关dish
            dishService.removeById(id);
        }
        if(flag){
            return R.error("请确定所有都为停售状态");
        }
        return R.success("删除成功");
    }

    /**
     * 停售
     */
    @PostMapping("/status/0")
    public R<String> stopSale(Long[] ids){
        for(Long id : ids){
            Dish dish = dishService.getById(id);
            dish.setStatus(0);
            dishService.updateById(dish);
        }
        return R.success("已停售");
    }

    /**
     * 起售
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> startSale(Long[] ids){
        for(Long id : ids){
            Dish dish = dishService.getById(id);
            dish.setStatus(1);
            dishService.updateById(dish);
        }
        return R.success("已起售");
    }

    /**
     * 根据条件查询菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> gList(Dish dish){
        List<DishDto> ret = null;
        //先从redis缓存获取数据
        //动态构造key
        String key = "Dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        ret = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if(ret != null){
            log.info("get data by redis");
            return R.success(ret);
        }
        //不存在查数据库
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        wrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        wrapper.eq(Dish::getStatus, 1);
        List<Dish> list = dishService.list(wrapper);

            ret = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            LambdaQueryWrapper<DishFlavor> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(DishFlavor::getDishId, item.getId());
            List<DishFlavor> list1 = dishFlavorService.list(wrapper1);
            dishDto.setFlavors(list1);
            return dishDto;
        }).toList();
        redisTemplate.opsForValue().set(key, ret, 10, TimeUnit.MINUTES);
        return R.success(ret);
    }
}
