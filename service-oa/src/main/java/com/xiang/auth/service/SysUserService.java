package com.xiang.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiang.model.system.SysUser;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author xiangyin
 * @since 2023-10-21
 */
public interface SysUserService extends IService<SysUser> {

    void updateStatus(Long id, Integer status);
}
