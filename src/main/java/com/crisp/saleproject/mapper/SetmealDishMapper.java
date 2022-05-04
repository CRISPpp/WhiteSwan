package com.crisp.saleproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crisp.saleproject.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {
    @Select("SELECT * FROM setmeal_dish WHERE name=#{name} AND is_deleted=1;")
    public SetmealDish getDelByName(String name);
    @Update("UPDATE setmeal_dish SET is_deleted=0 WHERE name=#{name}")
    public void upddateIsDel(String name);
}
