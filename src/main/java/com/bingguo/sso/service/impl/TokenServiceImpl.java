package com.bingguo.sso.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.bingguo.sso.domain.User;
import com.bingguo.sso.service.TokenService;
import com.bingguo.sso.util.JsonUtils;
import com.bingguo.sso.util.RedisOperator;
import com.bingguo.sso.vo.IMoocJSONResult;
@Service
/**
 * 根据token取用户信息
 * @author Administrator
 *
 */
public class TokenServiceImpl implements TokenService{
	@Autowired
	RedisOperator jedis;
	public IMoocJSONResult getUserByToken(String token) {
        //根据token到redis中取用户信息
        String json = jedis.get(token);
        if(StringUtils.isEmpty(json)){
            //取不到信息，登录过期，返回登录过期, "用户登录已经过期"
            return IMoocJSONResult.build(201, "用户登录已经过期", null);
        }
        //取到用户信息，跟新token的过期时间
        jedis.expire(token, 1800);
        //返回结果,IMoocJSONResult其中包含用户对象
        User user = JsonUtils.jsonToPojo(json, User.class);
        return IMoocJSONResult.ok(user); 
    }
}
