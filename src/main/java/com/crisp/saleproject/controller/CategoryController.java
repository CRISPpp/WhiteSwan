package com.crisp.saleproject.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crisp.saleproject.common.R;
import com.crisp.saleproject.entity.Category;
import com.crisp.saleproject.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 分类管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

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
        if(category.getIsDeleted() == null) category.setIsDeleted(0);
        categoryService.save(category);
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
        categoryService.removeById(ids);
        return R.success("删除成功");
    }
}
