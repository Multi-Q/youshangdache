package com.qrh.youshangdache.system.service;

import com.qrh.youshangdache.model.entity.system.SysRole;
import com.qrh.youshangdache.model.query.system.SysRoleQuery;
import com.qrh.youshangdache.model.vo.base.PageVo;
import com.qrh.youshangdache.model.vo.system.AssginRoleVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface SysRoleService extends IService<SysRole> {

    PageVo<SysRole> findPage(Page<SysRole> pageParam, SysRoleQuery roleQuery);

    /**
     * 根据用户获取角色数据
     * @param userId
     * @return
     */
    Map<String, Object> findRoleByUserId(Long userId);

    /**
     * 分配角色
     * @param assginRoleVo
     */
    void doAssign(AssginRoleVo assginRoleVo);
}
