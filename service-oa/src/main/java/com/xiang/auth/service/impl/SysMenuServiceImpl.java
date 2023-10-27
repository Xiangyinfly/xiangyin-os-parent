package com.xiang.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiang.auth.mapper.SysMenuMapper;
import com.xiang.auth.mapper.SysRoleMapper;
import com.xiang.auth.mapper.SysRoleMenuMapper;
import com.xiang.auth.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiang.auth.service.SysRoleMenuService;
import com.xiang.auth.utils.MenuHelper;
import com.xiang.common.config.exception.MyException;
import com.xiang.model.system.SysMenu;
import com.xiang.model.system.SysRoleMenu;
import com.xiang.vo.system.AssignMenuVo;
import com.xiang.vo.system.MetaVo;
import com.xiang.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

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

    @Override
    public List<SysMenu> findSysMenuByRoleId(Long roleId) {
        //查询所以status == 1的菜单
        LambdaQueryWrapper<SysMenu> sysMenuWrapper = new LambdaQueryWrapper<>();
        sysMenuWrapper.eq(SysMenu::getStatus,1);
        List<SysMenu> allSysMenuList = baseMapper.selectList(sysMenuWrapper);

        //根据roleId查询角色菜单表里roleId对应的菜单
        LambdaQueryWrapper<SysRoleMenu> sysRoleMenuWrapper = new LambdaQueryWrapper<>();
        sysRoleMenuWrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuService.list(sysRoleMenuWrapper);

        //获取菜单id
        List<Long> menuIdList = sysRoleMenuList.stream().map(c -> c.getMenuId()).collect(Collectors.toList());

        //菜单id与所有菜单id比对
        allSysMenuList.stream().forEach(item -> {
            if (menuIdList.contains(item.getId())) {
                item.setSelect(true);
            }
        });

        List<SysMenu> sysMenuList = MenuHelper.buildTree(allSysMenuList);
        return sysMenuList;
    }

    @Override
    public void doAssign(AssignMenuVo assignMenuVo) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId,assignMenuVo.getRoleId());
        sysRoleMenuService.remove(wrapper);

        for (Long menuId : assignMenuVo.getMenuIdList()) {
            if (StringUtils.isEmpty(menuId)) {
                continue;
            }

            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setRoleId(assignMenuVo.getRoleId());
            sysRoleMenuService.save(sysRoleMenu);
        }
    }


    //查询用户操作菜单
    @Override
    public List<RouterVo> findUserMenuListByUserId(Long userId) {
        List<SysMenu> sysMenuList = null;
        if (userId.longValue() == 1) {//规定userId == 1的时候为管理员
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1).orderByAsc(SysMenu::getSortValue);
            sysMenuList = baseMapper.selectList(wrapper);
        } else {
            sysMenuList = baseMapper.findUserMenuByUserId(userId);
        }

        //转化为树形结构
        List<SysMenu> sysMenuListTree = MenuHelper.buildTree(sysMenuList);
        //转化为框架需要的路由结构
        List<RouterVo> routerVoList = this.buildRouter(sysMenuListTree);
        return routerVoList;
    }

    private List<RouterVo> buildRouter(List<SysMenu> sysMenuListTree) {
        LinkedList<RouterVo> routers = new LinkedList<>();
        for (SysMenu sysMenu : sysMenuListTree) {
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(sysMenu));
            router.setComponent(sysMenu.getComponent());
            router.setMeta(new MetaVo(sysMenu.getName(), sysMenu.getIcon()));
            List<SysMenu> children = sysMenu.getChildren();
            if (sysMenu.getType().intValue() == 1) {
                List<SysMenu> hiddenMenuList = children.stream().filter(item -> !StringUtils.isEmpty(item.getComponent())).collect(Collectors.toList());
                for (SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routers.add(hiddenRouter);
                }
            } else {
                if (!CollectionUtils.isEmpty(children)) {
                    if(children.size() > 0) {
                        router.setAlwaysShow(true);
                    }
                    router.setChildren(buildRouter(children));//递归
                }
            }
            routers.add(router);
        }
        return routers;
    }

    private String getRouterPath(SysMenu sysMenu) {
        String routerPath = "/" + sysMenu.getPath();
        if(sysMenu.getParentId().intValue() != 0) {
            routerPath = sysMenu.getPath();
        }
        return routerPath;
    }


    //查询用户操作按钮
    @Override
    public List<String> findUserPermsByUserId(Long userId) {
        List<SysMenu> sysMenuList = null;
        if (userId.longValue() == 1) {//规定userId == 1的时候为管理员
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1);
            sysMenuList = baseMapper.selectList(wrapper);
        } else {
            sysMenuList = baseMapper.findUserMenuByUserId(userId);
        }

        List<String> permsList = sysMenuList.stream().filter(item -> item.getType() == 2).map(item -> item.getPerms()).collect(Collectors.toList());
        return permsList;
    }
}
