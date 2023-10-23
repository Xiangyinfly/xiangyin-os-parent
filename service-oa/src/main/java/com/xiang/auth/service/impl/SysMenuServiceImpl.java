package com.xiang.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiang.auth.mapper.SysMenuMapper;
import com.xiang.auth.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiang.auth.utils.MenuHelper;
import com.xiang.common.config.exception.MyException;
import com.xiang.model.system.SysMenu;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author xiangyin
 * @since 2023-10-23
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Override
    public List<SysMenu> findNodes() {
        List<SysMenu> sysMenuList = baseMapper.selectList(null);
        List<SysMenu> resultList = MenuHelper.buildTree(sysMenuList);
        return resultList;
    }

    @Override
    public void removeMenuById(Long id) {
        //判断菜单是否有下一层级
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getId,id);
        Integer count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new MyException(201,"无法删除菜单！");
        }
        baseMapper.deleteById(id);
    }
}
