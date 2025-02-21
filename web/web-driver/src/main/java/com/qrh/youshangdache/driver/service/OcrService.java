package com.qrh.youshangdache.driver.service;

import com.qrh.youshangdache.model.vo.driver.DriverLicenseOcrVo;
import com.qrh.youshangdache.model.vo.driver.IdCardOcrVo;
import org.springframework.web.multipart.MultipartFile;

public interface OcrService {

    IdCardOcrVo idCardOcr(MultipartFile file);

    DriverLicenseOcrVo driverLicenseOcr(MultipartFile file);

}
