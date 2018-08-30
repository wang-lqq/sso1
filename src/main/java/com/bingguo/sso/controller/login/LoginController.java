package com.bingguo.sso.controller.login;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReactiveKeyCommands.ExpireAtCommand;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bingguo.sso.domain.User;
import com.bingguo.sso.service.UserService;
import com.bingguo.sso.util.JsonUtils;
import com.bingguo.sso.util.MD5;
import com.bingguo.sso.util.RedisOperator;
import com.bingguo.sso.vo.IMoocJSONResult;
/**
 * 用户统一登录接口
 * @author v001
 *
 */
@Controller
@RequestMapping("sso")
public class LoginController {
	@Autowired
	RedisOperator jedis;
	
	@Autowired
	UserService userService;
	
	@RequestMapping(value="login",method=RequestMethod.POST)
	@ResponseBody
	public IMoocJSONResult login(HttpServletRequest request,HttpServletResponse response) {
		String key=null;
		try {
			String sign="wzw_123";
			String username=request.getParameter("username");
			String password=request.getParameter("password");
			if(username==null || "".equals(username) || password==null || "".equals(password)) {
				return IMoocJSONResult.ok("用户名和密码不能为空");
			}
			User user=userService.selectByUsername(username.trim());
			if(user==null) {
				return IMoocJSONResult.ok("用户名或密码错误");
			}
			if(!user.getPassword().equals(password.trim())) {
				return IMoocJSONResult.ok("用户名或密码错误");
			}else {
				//登录成功
				//生成token,并返回给客户端
				key=MD5.getMd5(user.getPassword()+user.getUsername()+sign);
				jedis.set(key, JsonUtils.objectToJson(user));
				//设置key过期时间
				jedis.expire(key, 1800);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 6、返回IMoocJSONResult包装token。
		
		return IMoocJSONResult.ok(key);
	}
}
