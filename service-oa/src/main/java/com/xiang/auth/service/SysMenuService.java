package com.xiang.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiang.model.system.SysMenu;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author xiangyin
 * @since 2023-10-23
 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> findNodes();

    void removeMenuById(Long id);
}
