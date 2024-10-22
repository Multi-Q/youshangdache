package com.qrh.youshangdache.system.service;

import com.qrh.youshangdache.model.entity.system.SysPost;
import com.qrh.youshangdache.model.query.system.SysPostQuery;
import com.qrh.youshangdache.model.vo.base.PageVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysPostService extends IService<SysPost> {

    PageVo<SysPost> findPage(Page<SysPost> pageParam, SysPostQuery sysPostQuery);

    void updateStatus(Long id, Integer status);

    List<SysPost> findAll();
}
