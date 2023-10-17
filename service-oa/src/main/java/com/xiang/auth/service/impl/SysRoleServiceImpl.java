package com.xiang.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiang.auth.mapper.SysRoleMapper;
import com.xiang.auth.service.SysRoleService;
import com.xiang.model.system.SysRole;
import org.springframework.stereotype.Service;


@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
}
