package com.crisp.saleproject.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crisp.saleproject.common.BaseContext;
import com.crisp.saleproject.common.R;
import com.crisp.saleproject.entity.ShoppingCart;
import com.crisp.saleproject.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    ShoppingCartService shoppingCartService;

    /**
     * 获取订单列表
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> getList(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> list = shoppingCartService.list(wrapper);
        return R.success(list);
    }

    /**
     * 加入购物车
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        Long id = BaseContext.getCurrentId();
        shoppingCart.setUserId(id);
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, id);
        Long dishId = shoppingCart.getDishId();
        if(dishId != null){
            wrapper.eq(ShoppingCart::getDishId, dishId);
        }else{
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart shoppingCart1 = shoppingCartService.getOne(wrapper);
        //如果原先有数据就+1
        if(shoppingCart1 != null){
            shoppingCart1.setNumber(shoppingCart1.getNumber() + 1);
            shoppingCartService.updateById(shoppingCart1);
        }
        else{
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            shoppingCart1 = shoppingCart;
        }
        return  R.success(shoppingCart1);
    }

    /**
     * 减去菜品
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        Long dishId = shoppingCart.getDishId();
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        if(dishId != null){
            wrapper.eq(ShoppingCart::getDishId, dishId);
        }else{
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart shoppingCart1 = shoppingCartService.getOne(wrapper);

        if(shoppingCart1 != null){
            Integer num = shoppingCart1.getNumber();
            num--;
            if(num == 0){
                shoppingCartService.removeById(shoppingCart1.getId());
            }else{
                shoppingCart1.setNumber(num);
                shoppingCartService.updateById(shoppingCart1);
            }

        }
        return R.success(shoppingCart1);
    }

    /**
     * 清空
     */
    @DeleteMapping("/clean")
    public R<String> clear(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        shoppingCartService.remove(wrapper);
        return R.success("clear");
    }
}
