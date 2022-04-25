package com.crisp.saleproject.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crisp.saleproject.entity.Employee;
import com.crisp.saleproject.mapper.EmployeeMapper;
import com.crisp.saleproject.service.EmployeeService;
import org.springframework.stereotype.Service;


@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
