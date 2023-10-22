package com.xiang.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiang.auth.service.SysRoleService;
import com.xiang.common.config.exception.MyException;
import com.xiang.common.result.Result;
import com.xiang.model.system.SysRole;
import com.xiang.vo.system.AssignRoleVo;
import com.xiang.vo.system.SysRoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Api(tags = "角色管理")
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation("获取角色")
    @GetMapping("/toAssign/{userId}")
    public Result toAssign(@PathVariable Long userId) {
        Map<String, Object> map = sysRoleService.findRoleDataByUserId(userId);
        return Result.ok(map);
    }

    @ApiOperation("为用户分配角色")
    @PostMapping("/doAssign")
    public Result doAssign(@RequestBody AssignRoleVo assignRoleVo) {
        sysRoleService.doAssign(assignRoleVo);
        return Result.ok();
    }

    @ApiOperation(value = "查询所有角色")
    @GetMapping("findAll")
    public Result findAll() {
        List<SysRole> roleList = sysRoleService.list();
        return Result.ok(roleList);
    }


    /**
     *
     * @param page
     * @param limit
     * @param sysRoleQueryVo 条件对象
     * @return
     */
    @ApiOperation(value = "条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result pageQueryRole(@PathVariable Long page, @PathVariable Long limit, SysRoleQueryVo sysRoleQueryVo) {
        Page<SysRole> sysRolePage = new Page<>(page, limit);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        if (!StringUtils.isEmpty(roleName)) {
            wrapper.like(SysRole::getRoleName,roleName);
        }
        IPage<SysRole> data = sysRoleService.page(sysRolePage, wrapper);
        return Result.ok(data);
    }

    @ApiOperation(value = "添加角色")
    @PostMapping("save")
    public Result save(@RequestBody SysRole sysRole) {
        boolean save = sysRoleService.save(sysRole);
        if (save) {
            return Result.ok();
        }
        return Result.fail();
    }

    @ApiOperation(value = "根据id查询")
    @GetMapping("get/{id}")
    public Result getById(@PathVariable Long id) {
        SysRole role = sysRoleService.getById(id);
        return Result.ok(role);
    }

    @ApiOperation(value = "修改角色")
    @PutMapping("update")
    public Result update(@RequestBody SysRole sysRole) {
        boolean update = sysRoleService.updateById(sysRole);
        if (update) {
            return Result.ok();
        }
        return Result.fail();
    }

    @ApiOperation(value = "根据id删除")
    @DeleteMapping("remove/{id}")
    public Result removeById(@PathVariable Long id) {
        boolean remove = sysRoleService.removeById(id);
        if (remove) {
            return Result.ok();
        }
        return Result.fail();
    }

    //json对象格式会转为java对象格式，json数组格式会转为java集合
    @ApiOperation(value = "批量删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        boolean remove = sysRoleService.removeByIds(idList);
        if (remove) {
            return Result.ok();
        }
        return Result.fail();
    }
}
