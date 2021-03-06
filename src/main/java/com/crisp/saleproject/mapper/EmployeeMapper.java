package com.crisp.saleproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.crisp.saleproject.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;



@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
    @Select("SELECT * FROM employee WHERE username=#{username} AND is_deleted=1;")
    public Employee getEmById(String username);
    @Update("UPDATE employee SET is_deleted=0 WHERE username=#{username}")
    public void upddateIsDel(String username);
}
