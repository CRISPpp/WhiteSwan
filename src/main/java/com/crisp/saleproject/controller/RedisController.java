package com.crisp.saleproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
//测试redis
@Slf4j
@RestController
@RequestMapping("/redis")
public class RedisController {
    @Autowired
    RedisTemplate redisTemplate;
    @GetMapping
    public void getRedis(){
        redisTemplate.opsForValue().set("guangdong", "shantou");
        log.info((String) redisTemplate.opsForValue().get("guangdong"));
        redisTemplate.opsForValue().set("onemin", "1min", 1L, TimeUnit.MINUTES);
        log.info(String.valueOf(redisTemplate.getExpire("onemin")));



        HashOperations hashOperations = redisTemplate.opsForHash();
        Map<String, String> mp = new HashMap<>();
        mp.put("name", "crisp");
        mp.put("age", "18");
        hashOperations.putAll("crisp", mp);
        log.info((String) hashOperations.get("crisp", "name"));
        Set keys = hashOperations.keys("crisp");
        for (Object key : keys) {
            log.info((String) key);
        }
        List list = hashOperations.values("crisp");
        for (Object o : list) {
            log.info((String) o);
        }


        ListOperations listOperations = redisTemplate.opsForList();
        listOperations.leftPushAll("mylist", "a", "v");
        List mylist = listOperations.range("mylist", 1, -1);
        for (Object o : mylist) {
            log.info((String) o);
        }
        Long lenth = listOperations.size("mylist");
        for(Long i = 0L; i < lenth; i++){
           log.info((String) listOperations.rightPop("mylist"));
        }


        SetOperations setOperations = redisTemplate.opsForSet();
        setOperations.add("setOne", "a", "b", "a");
        Set myset = setOperations.members("setOne");
        for (Object o : myset) {
            log.info((String) o);
        }
        setOperations.remove("setOne", "a", "b");




        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add("zsetOne", "a", 10.0);
        zSetOperations.add("zsetOne", "b", 12.0);
        zSetOperations.add("zsetOne", "c", 11.0);
        Set<String> zset = zSetOperations.range("zsetOne", 0, -1);
        for (String s : zset) {
            log.info(s);
        }
        zSetOperations.removeRange("zsetOne", 0, -1);


        Set<String> mykeys = redisTemplate.keys("*");
        for (String mykey : mykeys) {
            log.info(mykey);
        }
        log.info(String.valueOf(redisTemplate.hasKey("aaaa")));
        log.info(String.valueOf(redisTemplate.delete("onemin")));
        log.info(String.valueOf(redisTemplate.type("guangdong")));
    }
}
