package com.qrh.youshangdache.mgr.service;

import com.qrh.youshangdache.model.entity.system.SysOperLog;
import com.qrh.youshangdache.model.query.system.SysOperLogQuery;
import com.qrh.youshangdache.model.vo.base.PageVo;

public interface SysOperLogService {

    PageVo<SysOperLog> findPage(Long page, Long limit, SysOperLogQuery sysOperLogQuery);

    /**
     * 保存系统日志记录
     */
    void saveSysLog(SysOperLog sysOperLog);

    SysOperLog getById(Long id);
}
