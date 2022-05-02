package com.crisp.saleproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crisp.saleproject.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {
    @Select("SELECT * FROM setmeal WHERE name=#{name} AND is_deleted=1;")
    public Setmeal getEmById(String name);
    @Update("UPDATE setmeal SET is_deleted=0 WHERE name=#{name}")
    public void upddateIsDel(String name);
}
