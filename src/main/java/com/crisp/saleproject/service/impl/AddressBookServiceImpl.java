package com.crisp.saleproject.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crisp.saleproject.entity.AddressBook;
import com.crisp.saleproject.mapper.AddressBookMapper;
import com.crisp.saleproject.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
