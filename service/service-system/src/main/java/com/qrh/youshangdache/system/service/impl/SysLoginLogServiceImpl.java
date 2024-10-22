package com.qrh.youshangdache.system.service.impl;

import com.qrh.youshangdache.model.entity.system.SysLoginLog;
import com.qrh.youshangdache.model.query.system.SysLoginLogQuery;
import com.qrh.youshangdache.model.vo.base.PageVo;
import com.qrh.youshangdache.system.mapper.SysLoginLogMapper;
import com.qrh.youshangdache.system.service.SysLoginLogService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements SysLoginLogService {

	@Resource
	private SysLoginLogMapper sysLoginLogMapper;

	@Override
	public PageVo<SysLoginLog> findPage(Page<SysLoginLog> pageParam, SysLoginLogQuery sysLoginLogQuery) {
		IPage<SysLoginLog> pageInfo = sysLoginLogMapper.selectPage(pageParam, sysLoginLogQuery);
		return new PageVo(pageInfo.getRecords(), pageInfo.getPages(), pageInfo.getTotal());
	}

	/**
	 * 记录登录信息
	 *
	 * @param sysLoginLog
	 * @return
	 */
	public void recordLoginLog(SysLoginLog sysLoginLog) {
		sysLoginLogMapper.insert(sysLoginLog);
	}
}