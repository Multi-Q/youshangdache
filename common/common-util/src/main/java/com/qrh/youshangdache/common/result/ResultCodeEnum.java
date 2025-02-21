package com.qrh.youshangdache.common.result;

import lombok.Getter;

/**
 * 统一返回结果状态信息类
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS(200, "成功"),
    FAIL(201, "失败"),
    SERVICE_ERROR(202, "服务异常"),
    DATA_ERROR(203, "数据异常"),
    ILLEGAL_REQUEST(204, "非法请求"),
    REPEAT_SUBMIT(205, "重复提交"),
    FEIGN_FAIL(206, "远程调用失败"),
    UPDATE_ERROR(207, "数据更新失败"),

    ARGUMENT_VALID_ERROR(208, "参数校验异常"),
    SIGN_ERROR(209, "签名错误"),
    SIGN_OVERDUE(210, "签名已过期"),
    VALIDATOR_ERROR(211, "验证码错误"),

    LOGIN_AUTH(212, "未登陆"),
    PERMISSION(213, "没有权限"),
    ACCOUNT_ERROR(214, "账号不正确"),
    PASSWORD_ERROR(215, "密码不正确"),
    PHONE_CODE_ERROR(216, "手机验证码不正确"),
    LOGIN_ACCOUNT_ERROR(217, "账号不正确"),
    ACCOUNT_STOP(218, "账号已停用"),
    NODE_ERROR(219, "该节点下有子节点，不可以删除"),

    COB_NEW_ORDER_FAIL(220, "抢单失败"),
    MAP_FAIL(221, "地图服务调用失败"),
    PROFITSHARING_FAIL(222, "分账调用失败"),
    NO_START_SERVICE(223, "未开启代驾服务，不能更新位置信息"),
    DRIVER_START_LOCATION_DISTION_ERROR(224, "距离代驾起始点1公里以内才能确认"),
    DRIVER_END_LOCATION_DISTION_ERROR(225, "距离代驾终点2公里以内才能确认"),
    IMAGE_AUDITION_FAIL(226, "图片审核不通过"),
    UPLOAD_FAIL(227,"上传失败"),
    AUTH_ERROR(228, "认证通过后才可以开启代驾服务"),
    FACE_ERROR(229, "当日未进行人脸识别"),

    COUPON_EXPIRE(230, "优惠券已过期"),
    COUPON_LESS(231, "优惠券库存不足"),
    COUPON_USER_LIMIT(232, "超出领取数量"),
    WX_CREATE_ERROR(233,"微信创建失败"),
    XXL_JOB_ERROR(234,"任务调度失败"),
    LOGIN_MOBILE_ERROR(235,"手机号登陆失败" ),
    ACCOUNT_NOT_EXIST(236, "账号不存在"),
    UNCORRECTED_PHONE_NUMBER(237, "手机号不正确"),;

    private final Integer code;

    private final String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
