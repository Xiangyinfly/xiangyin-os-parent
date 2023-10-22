package com.xiang.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiang.model.system.SysRole;
import com.xiang.vo.system.AssignRoleVo;

import java.util.Map;

public interface SysRoleService extends IService<SysRole> {
    Map<String, Object> findRoleDataByUserId(Long userId);

    void doAssign(AssignRoleVo assignRoleVo);
}
