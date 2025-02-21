package com.qrh.youshangdache.driver.service;

import com.qrh.youshangdache.model.entity.order.OrderMonitor;
import com.qrh.youshangdache.model.form.order.OrderMonitorForm;
import org.springframework.web.multipart.MultipartFile;

public interface MonitorService {

    Boolean upload(MultipartFile file, OrderMonitorForm orderMonitorForm);


}
