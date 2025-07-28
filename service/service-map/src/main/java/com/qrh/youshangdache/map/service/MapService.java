package com.qrh.youshangdache.map.service;

import com.qrh.youshangdache.model.form.map.CalculateDrivingLineForm;
import com.qrh.youshangdache.model.vo.map.DrivingLineVo;

public interface MapService {
    /**
     * 计算驾驶线路
     * @param calculateDrivingLineForm
     * @return
     */
    DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm calculateDrivingLineForm);
}
