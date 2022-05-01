package com.crisp.saleproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crisp.saleproject.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    @Select("SELECT * FROM category WHERE name=#{name} AND is_deleted=1;")
    public Category getCaById(String name);
    @Update("UPDATE category SET is_deleted=0 WHERE name=#{name}")
    public void upddateIsDel(String name);
}
