package com.qrh.youshangdache.driver.mapper;

import com.qrh.youshangdache.model.entity.driver.DriverAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;

@Mapper
public interface DriverAccountMapper extends BaseMapper<DriverAccount> {


    void add(Long driverId, BigDecimal amount);

}
