package com.crisp.saleproject.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crisp.saleproject.common.R;
import com.crisp.saleproject.entity.Category;
import com.crisp.saleproject.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/page")
    public R<Page> pageFind(int page, int pageSize){
        Page pageInfo = new Page(page, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Category::getSort);
        categoryService.page(pageInfo, wrapper);
        return R.success(pageInfo);
    }
}
