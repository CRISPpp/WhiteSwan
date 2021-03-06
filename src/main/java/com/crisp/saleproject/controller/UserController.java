package com.crisp.saleproject.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crisp.saleproject.common.R;
import com.crisp.saleproject.dto.UserDto;
import com.crisp.saleproject.entity.User;
import com.crisp.saleproject.service.UserService;
import com.crisp.saleproject.utils.SMSUtils;
import com.crisp.saleproject.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 发送验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if(phone != null) {
            //生成4位验证码
            String vcode = String.valueOf(ValidateCodeUtils.generateValidateCode(4));
            log.info("code =====" + vcode);
            //发送短信
            SMSUtils.sendMessagetxt("WhiteSwan", "", phone, vcode);
//            //存到session或者redis
//            session.setAttribute(phone, vcode);
            //缓存到redis，5分钟
            redisTemplate.opsForValue().set(phone, vcode,5, TimeUnit.MINUTES);
            return R.success("发送成功");
        }
        return R.error("手机号为空");
    }

    /**
     * 登录校验
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody UserDto userDto, HttpSession session){
        //获取手机号
        String phone = userDto.getPhone();
        String code =userDto.getCode();
        if(phone != null) {
//            //获取session手机号
//            String vcode = (String) session.getAttribute(phone);
            //从redis获取验证码
            String vcode = (String) redisTemplate.opsForValue().get(phone);
            //校验
            if(vcode == null || !vcode.equals(code)){
                return R.error("验证码错误");
            }
            //查询是否为新用户
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, phone);
            User user = userService.getOne(wrapper);
            if(user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            //登录成功删除缓存的验证码
            redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("手机号为空");
    }

    /**
     * 登出
     */
    @PostMapping("/loginout")
    public R<String> logour(HttpSession session){
        session.removeAttribute("user");
        return R.success("logout");
    }
}
