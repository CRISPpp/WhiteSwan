package com.crisp.saleproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crisp.saleproject.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {
    @Select("SELECT * FROM dish_flavor WHERE name=#{name} AND is_deleted=1;")
    public DishFlavor getEmById(String name);
    @Update("UPDATE dish_flavor SET is_deleted=0 WHERE name=#{name}")
    public void upddateIsDel(String name);
}
