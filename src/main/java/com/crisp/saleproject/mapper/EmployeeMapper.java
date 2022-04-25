package com.crisp.saleproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crisp.saleproject.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
