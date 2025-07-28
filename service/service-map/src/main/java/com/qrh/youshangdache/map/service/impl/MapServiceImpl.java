package com.qrh.youshangdache.map.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.qrh.youshangdache.common.execption.GuiguException;
import com.qrh.youshangdache.common.result.ResultCodeEnum;
import com.qrh.youshangdache.map.service.MapService;
import com.qrh.youshangdache.model.form.map.CalculateDrivingLineForm;
import com.qrh.youshangdache.model.vo.map.DrivingLineVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RefreshScope
public class MapServiceImpl implements MapService {
    @Resource
    private RestTemplate restTemplate;

    @Value("tencent.cloud.map")
    private String key;

    /**
     * 计算驾驶路线
     *
     * @param calculateDrivingLineForm
     * @return
     */
    @Override
    public DrivingLineVo calculateDrivingLine(CalculateDrivingLineForm calculateDrivingLineForm) {
        //请求腾讯提供的接口，最返回需要的结果
        String url = "https://apis.map.qq.com/ws/direction/v1/driving/?from={from}&to={to}&key={key}";
        //封装传递的参数
        Map<String, String> map = new HashMap<>();
        map.put("from", calculateDrivingLineForm.getStartPointLatitude() + "," + calculateDrivingLineForm.getStartPointLongitude());
        map.put("to", calculateDrivingLineForm.getEndPointLatitude() + "," + calculateDrivingLineForm.getEndPointLongitude());
        map.put("key", key);
        //使用restTemplate调用
        JSONObject result = restTemplate.getForObject(url, JSONObject.class, map);
        //返回处理结果
        int status = result.getIntValue("status");
        if (status != 0) {
            throw new GuiguException(ResultCodeEnum.MAP_FAIL);
        }
        //返回获取路线信息
        JSONObject route = result.getJSONObject("result")
                .getJSONArray("routes")
                .getJSONObject(0);
        DrivingLineVo drivingLineVo = new DrivingLineVo();
        drivingLineVo.setDuration(route.getBigDecimal("duration"));
        drivingLineVo.setDistance(route.getBigDecimal("distance")
                .divide(new BigDecimal("1000"))
                .setScale(2, RoundingMode.UP)
        );
        //路线
        drivingLineVo.setPolyline(route.getJSONArray("polyline"));
        return drivingLineVo;
    }
}
