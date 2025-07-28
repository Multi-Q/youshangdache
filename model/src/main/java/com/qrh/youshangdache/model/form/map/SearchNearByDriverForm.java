package com.qrh.youshangdache.model.form.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SearchNearByDriverForm {
    /**
     * 经度
     */
    @Schema(description = "经度")
    private BigDecimal longitude;
    /**
     * 纬度
     */
    @Schema(description = "纬度")
    private BigDecimal latitude;
    /**
     * 里程
     */
    @Schema(description = "里程")
    private BigDecimal mileageDistance;
}
