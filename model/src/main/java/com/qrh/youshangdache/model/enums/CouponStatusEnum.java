package com.qrh.youshangdache.model.enums;

import lombok.Getter;

/**
 * 优惠券状态 枚举类
 */
@Getter
public enum CouponStatusEnum {
    NOT_USED(1, "未使用"),
    USED(2, "已使用");

    /**
     * 优惠券状态代号
     */
    private final Integer code;
    /**
     * 状态描述（值）
     */
    private final String statusValue;

    CouponStatusEnum(final Integer code, final String statusValue) {
        this.code = code;
        this.statusValue = statusValue;
    }

}
