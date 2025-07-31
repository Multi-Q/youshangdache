package com.qrh.youshangdache.model.enums;

import lombok.Getter;

/**
 * 司机和用户认证状态枚举类
 */
@Getter
public enum AuthStatusEnum {
    // 0:未认证 1：审核中 2：认证通过 -1：认证未通过UNAUTHENTIC
    UNAUTHORIZED(0, "未认证"),
    REVIEWING(1, "审核中"),
    AUTHENTICATION_PASSED(2, "认证通过"),
    AUTHENTICATION_FAILED(-1, "认证未通过");
    /**
     * 状态代号
     */
    private final Integer status;
    /**
     * 状态代号对应的描述
     */
    private final String description;

    private AuthStatusEnum(final Integer status, final String description) {
        this.status = status;
        this.description = description;
    }
}
