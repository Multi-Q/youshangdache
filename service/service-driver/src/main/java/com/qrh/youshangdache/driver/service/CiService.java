package com.qrh.youshangdache.driver.service;

import com.qrh.youshangdache.model.vo.order.TextAuditingVo;

public interface CiService {

    Boolean imageAuditing(String path);

    TextAuditingVo textAuditing(String content);
}
