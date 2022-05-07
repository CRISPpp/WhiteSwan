package com.crisp.saleproject.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crisp.saleproject.common.BaseContext;
import com.crisp.saleproject.common.R;
import com.crisp.saleproject.entity.AddressBook;
import com.crisp.saleproject.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.http.HttpRequest;
import java.util.List;

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 展示用户地址
     * @param session
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> getList(HttpSession session){
        Long userId = (Long) session.getAttribute("user");
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId, userId);
        List<AddressBook> ret = addressBookService.list(wrapper);
        return R.success(ret);
    }

    /**
     * 增加收货地址
     * @param addressBook
     * @param session
     * @return
     */
    @PostMapping
    public R<String> addAddressBook(@RequestBody AddressBook addressBook, HttpSession session){
        addressBook.setUserId((Long) session.getAttribute("user"));
        addressBookService.save(addressBook);
        return R.success("添加成功");
    }

    /**
     * 设置为默认地址
     */
    @PutMapping("/default")
    public R<String> defaultAddressBook(@RequestBody AddressBook addressBook3){
        Long id = addressBook3.getId();
        log.info("id ****************" + id);
        //去除原有的默认id
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getIsDefault, 1);
        AddressBook addressBook = addressBookService.getOne(wrapper);
        if(addressBook != null){
            addressBook.setIsDefault(0);
            addressBookService.updateById(addressBook);
        }

        //设置为默认地址
        LambdaQueryWrapper<AddressBook> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(AddressBook::getId, id);
        addressBook = addressBookService.getById(id);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }

    /**
     * 根据id获取
     */
    @GetMapping("/{id}")
    public R<AddressBook> getByid(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }

    /**
     * 修改地址信息
     */
    @PutMapping
    public R<String> changeById(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }

    /**
     * 删除地址信息
     */
    @DeleteMapping
    public R<String> delteByid(Long ids){
        addressBookService.removeById(ids);
        return R.success("删除成功");
    }

    /**
     * 获取默认地址
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getIsDefault, 1);
        wrapper.eq(AddressBook::getUserId, userId);
        AddressBook addressBook = addressBookService.getOne(wrapper);
        return R.success(addressBook);
    }
}
