package com.xiang.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiang.auth.mapper.SysRoleMapper;
import com.xiang.auth.service.SysRoleService;
import com.xiang.auth.service.SysUserRoleService;
import com.xiang.model.system.SysRole;
import com.xiang.model.system.SysUserRole;
import com.xiang.vo.system.AssignRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Override
    public Map<String, Object> findRoleDataByUserId(Long userId) {
        //获得所有角色集合
        List<SysRole> allRolesList = baseMapper.selectList(null);

        //获得中间表userId相等的条目的集合
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,userId);
        List<SysUserRole> existUserRoleList = sysUserRoleService.list(wrapper);
        //得到该userId对应的所有roleId的集合
        List<Long> existRoleIdList = existUserRoleList.stream().map(c -> c.getRoleId()).collect(Collectors.toList());

        //找到existRoleIdList中的id对应的role
        ArrayList<SysRole> assignRoleList = new ArrayList<>();
        for (SysRole sysRole : allRolesList) {
            if (existRoleIdList.contains(sysRole.getId())) {
                assignRoleList.add(sysRole);
            }
        }

        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("assignRoleList", assignRoleList);//返回userId对应的角色集合
        roleMap.put("allRolesList", allRolesList);//返回所有角色集合
        return roleMap;
    }

    @Transactional
    @Override
    public void doAssign(AssignRoleVo assignRoleVo) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,assignRoleVo.getUserId());
        sysUserRoleService.remove(wrapper);

        //重新分配
        List<Long> roleIdList = assignRoleVo.getRoleIdList();
        for (Long roleId : roleIdList) {
            if (StringUtils.isEmpty(roleId)) {
                continue;
            }
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(roleId);
            sysUserRole.setUserId(assignRoleVo.getUserId());
            sysUserRoleService.save(sysUserRole);
        }
    }
}
