package com.qrh.youshangdache.mgr.service;

import com.qrh.youshangdache.model.entity.system.SysLoginLog;
import com.qrh.youshangdache.model.query.system.SysLoginLogQuery;
import com.qrh.youshangdache.model.vo.base.PageVo;

public interface SysLoginLogService {

    PageVo<SysLoginLog> findPage(Long page, Long limit, SysLoginLogQuery sysLoginLogQuery);

    /**
     * 记录登录信息
     *
     * @param sysLoginLog
     * @return
     */
    void recordLoginLog(SysLoginLog sysLoginLog);

    SysLoginLog getById(Long id);
}
