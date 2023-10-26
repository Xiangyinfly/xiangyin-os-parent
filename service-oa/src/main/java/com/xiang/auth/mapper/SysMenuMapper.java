package com.xiang.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiang.model.system.SysMenu;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author xiangyin
 * @since 2023-10-23
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> findUserMenuByUserId(Long userId);
}
