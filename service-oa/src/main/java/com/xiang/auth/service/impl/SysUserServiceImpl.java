package com.xiang.auth.service.impl;

import com.xiang.auth.mapper.SysUserMapper;
import com.xiang.auth.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiang.model.system.SysUser;
import org.springframework.stereotype.Service;


@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public void updateStatus(Long id, Integer status) {
        SysUser sysUser = baseMapper.selectById(id);
        sysUser.setStatus(status);
        baseMapper.updateById(sysUser);
    }
}
