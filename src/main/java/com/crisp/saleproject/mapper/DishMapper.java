package com.crisp.saleproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crisp.saleproject.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
    @Select("SELECT * FROM dish WHERE name=#{name} AND is_deleted=1;")
    public Dish getEmById(String name);
    @Update("UPDATE dish SET is_deleted=0 WHERE name=#{name}")
    public void upddateIsDel(String name);
}
