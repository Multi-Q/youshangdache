package com.qrh.youshangdache.model.enums;


import lombok.Getter;

/**
 * 司机服务状态枚举类
 */
@Getter
public enum DriverServiceStatusEnum {
    /**
     * 司机未接单
     */
    DRIVER_NOT_SERVICE(0, "未接单"),
    /**
     * 司机开始接单
     */
    DRIVER_START_SERVICE(1, "开始接单");

    /**
     * 司机服务状态代号
     */
    private final Integer serviceStatus;
    /**
     * 司机服务状态代号对应的描述
     */
    private final String description;

    private DriverServiceStatusEnum(final Integer serviceStatus, final String description) {
        this.serviceStatus = serviceStatus;
        this.description = description;
    }


}
