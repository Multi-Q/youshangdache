package com.qrh.youshangdache.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum TradeTypeEnum {

    REWARD(1, "系统奖励"),
    ;

    @EnumValue
    private Integer type;
    private String content;

    TradeTypeEnum(Integer type, String content) {
        this.type = type;
        this.content = content;
    }

}
