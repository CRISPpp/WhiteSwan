package com.crisp.saleproject.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crisp.saleproject.common.R;
import com.crisp.saleproject.entity.Category;
import com.crisp.saleproject.entity.Setmeal;
import com.crisp.saleproject.mapper.CategoryMapper;
import com.crisp.saleproject.mapper.SetmealMapper;
import com.crisp.saleproject.service.CategoryService;
import com.crisp.saleproject.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * 分类管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 分页
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> pageFind(int page, int pageSize){
        Page pageInfo = new Page(page, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Category::getSort);
        categoryService.page(pageInfo, wrapper);
        return R.success(pageInfo);
    }

    /**
     * 增加分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> addCategory(@RequestBody Category category){
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, category.getName());
        if(categoryService.getOne(wrapper) != null){
            return R.error("分类已经存在，添加失败");
        }
        Category category1 = categoryMapper.getCaById(category.getName());
        if(category1 != null){
            category1.setSort(category.getSort());
            categoryMapper.upddateIsDel(category.getName());
            categoryService.updateById(category1);
//            //修改Setmeal
//            setmealMapper.upddateIsDel(category.getName());
            return R.success("添加 成功");
        }
//        if(category.getIsDeleted() == null) category.setIsDeleted(0);
        categoryService.save(category);
//        //2的话还要修改SetMeal
//        if(category.getType() == 2){
//            setmeal.setCategoryId(category.getId());
//            setmeal.setName(category.getName());
//            setmeal.setPrice(BigDecimal.valueOf(0));
//            setmeal.setStatus(1);
//            setmeal.setImage("1adaa0fc-ba29-43cd-9827-231f22b30a72.jpg");
//            setmealService.save(setmeal);
//        }

        return R.success("添加成功");
    }

    /**
     * 删除分类
     */
    @DeleteMapping
    public R<String> deleteCategory(Long ids){
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getId, ids);
        Category category = categoryService.getOne(wrapper);
        if(category == null) return R.error("请求错误，检索不到id");


//        setmealService.removeById(ids);
        categoryService.remove(ids);
        return R.success("删除成功");
    }

    /**
     * 修改分类信息
     */
    @PutMapping
    public R<String> editCategory(@RequestBody Category category){
        if(categoryService.getById(category.getId()) == null) return R.error("id不存在");

        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 展示分类
     */
    @GetMapping("/list")
    public R<List<Category>> getList(Category category){//为了复用，所以传的是category
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(category.getType() != null, Category::getType, category.getType());
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> ret = categoryService.list(wrapper);
        return R.success(ret);
    }
}
