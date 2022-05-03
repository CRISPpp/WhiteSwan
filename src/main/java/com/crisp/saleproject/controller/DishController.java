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
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        return R.success("修改成功");
    }

    /**
     * 批量删除
     */
    @DeleteMapping
    public R<String> deleteDish(Long[] ids){
        for(Long id : ids){
            //删除相关flavor
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId, id);
            dishFlavorService.remove(wrapper);

            //删除相关dish
            dishService.removeById(id);
        }
        return R.success("删除成功");
    }

}
