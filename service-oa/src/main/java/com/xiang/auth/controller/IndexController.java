package com.xiang.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiang.auth.service.SysMenuService;
import com.xiang.auth.service.SysUserService;
import com.xiang.common.config.exception.MyException;
import com.xiang.common.jwt.JwtHelper;
import com.xiang.common.result.Result;
import com.xiang.common.utils.MD5;
import com.xiang.model.system.SysUser;
import com.xiang.vo.system.LoginVo;
import com.xiang.vo.system.RouterVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername,loginVo.getUsername());
        SysUser user = sysUserService.getOne(wrapper);
        if (user == null) {
            throw new MyException(201,"用户不存在！");
        }

        String inputPwd = MD5.encrypt(loginVo.getPassword());
        if (!inputPwd.equals(user.getPassword())) {
            throw new MyException(201,"用户密码错误！");
        }

        if (user.getStatus().intValue() == 0) {
            throw new MyException(201,"用户被禁用！");
        }

        String token = JwtHelper.createToken(user.getId(), user.getName());
        HashMap<String, Object> data = new HashMap<>();
        data.put("token",token);
        return Result.ok(data);
    }

    @GetMapping("info")
    public Result info(HttpServletRequest request) {
        //根据请求头获取token
        String token = request.getHeader("token");
        //获取用户信息
        Long userId = JwtHelper.getUserId(token);
        SysUser user = sysUserService.getById(userId);
        //获取用户可以操作的菜单
        List<RouterVo> routerList = sysMenuService.findUserMenuListByUserId(userId);
        //获取用户可以操作的按钮
        List<String> permsList = sysMenuService.findUserPermsByUserId(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("name",user.getName());
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        map.put("routers",routerList);
        map.put("buttons",permsList);
        return Result.ok(map);
    }

    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }
}
