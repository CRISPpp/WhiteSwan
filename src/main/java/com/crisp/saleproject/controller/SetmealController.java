package com.crisp.saleproject.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crisp.saleproject.common.R;
import com.crisp.saleproject.dto.SetmealDishDto;
import com.crisp.saleproject.dto.SetmealDto;
import com.crisp.saleproject.entity.Category;
import com.crisp.saleproject.entity.Dish;
import com.crisp.saleproject.entity.Setmeal;
import com.crisp.saleproject.entity.SetmealDish;
import com.crisp.saleproject.service.CategoryService;
import com.crisp.saleproject.service.DishService;
import com.crisp.saleproject.service.SetmealDishService;
import com.crisp.saleproject.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private DishService dishService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @CacheEvict(value = "setmealCache", allEntries = true)
    @PostMapping
    public R<String> saveSetmeal(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("添加成功");
    }

    /**
     * 分页
     */
    @GetMapping("/page")
    public R<Page> getPage(int page, int pageSize, String name){
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Setmeal::getName, name);
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        setmealService.page(pageInfo, wrapper);

        Page<SetmealDto> retPage = new Page<>();
        BeanUtils.copyProperties(pageInfo, retPage, "records");        //对象拷贝,records我们要自己处理，先忽略拷贝

        List<Setmeal> list = pageInfo.getRecords();
        List<SetmealDto> retList = list.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long id = item.getCategoryId();

            Category category = categoryService.getById(id);
            setmealDto.setCategoryName(category.getName());
            return setmealDto;
        }).toList();
        //记得会写记录
        retPage.setRecords(retList);
        return R.success(retPage);
    }

    /**
     * 删除
     */
    @CacheEvict(value = "setmealCache", allEntries = true) //清除setmealCache下所有的缓存数据
    @DeleteMapping
    public R<String> delSetmeal(@RequestParam Long[] ids){
        boolean flag = false;
        for (Long id : ids) {
            Setmeal setmeal = setmealService.getById(id);
            if(setmeal.getStatus() == 1){
                flag = true;
                continue;
            }
            //清除setmealdish
            LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SetmealDish::getSetmealId, id);
            setmealDishService.remove(wrapper);

            //再清除相关的setmeal
            setmealService.removeById(id);
        }
        if(flag){
            return R.error("有部分套餐还在售卖");
        }
        return R.success("删除成功");
    }

    /**
     * 停售
     */
    /**
     * CachePut：将返回值存到缓存中
     * value：缓存的名称，一个缓存可有多个key
     * key：key
     */

    @PostMapping("/status/0")
    public R<String> stopSale(@RequestParam Long[] ids){
        for (Long id : ids) {
            Setmeal setmeal = setmealService.getById(id);
            setmeal.setStatus(0);
            setmealService.updateById(setmeal);
        }
        return  R.success("停售成功");
    }

    /**
     * 启售
     */
    /**
     * CacheEvict：删除缓存的数据
     * value：缓存的名称，一个缓存可有多个key
     * key：key
     */

    @PostMapping("/status/1")
    public R<String> openSale(@RequestParam Long[] ids){
        for (Long id : ids) {
            Setmeal setmeal = setmealService.getById(id);
            setmeal.setStatus(1);
            setmealService.updateById(setmeal);
        }
        return  R.success("启售成功");
    }

    /**
     * 获取数据
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getDto(@PathVariable Long id){
        return R.success(setmealService.getWithDish(id));
    }

    /**
     * 修改数据
     */
    @PutMapping
    public R<String> updateSetmeal(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("修改成功");
    }

    /**
     * 请求list
     */
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    @GetMapping("/list")
    public R<List<SetmealDto>> getList(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        wrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(wrapper);

        List<SetmealDto> ret = list.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            LambdaQueryWrapper<SetmealDish> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(SetmealDish::getSetmealId, item.getId());
            List<SetmealDish> list1 = setmealDishService.list(wrapper1);
            setmealDto.setSetmealDishes(list1);
            return setmealDto;
        }).toList();

        return R.success(ret);
    }

    /**
     * 获取dish
     */
    @GetMapping("/dish/{id}")
    public R<List<SetmealDishDto>> getDish(@PathVariable Long id){
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> ret = setmealDishService.list(wrapper);
        List<SetmealDishDto> retDto = ret.stream().map((item) -> {
            SetmealDishDto setmealDto = new SetmealDishDto();
            BeanUtils.copyProperties(item,setmealDto);
            Dish dish = dishService.getById(item.getDishId());
            setmealDto.setImage(dish.getImage());
            return setmealDto;
        }).toList();
        return R.success(retDto);
    }
}
