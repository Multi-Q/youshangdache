package com.qrh.youshangdache.driver.service;

import com.qrh.youshangdache.model.vo.driver.CosUploadVo;
import org.springframework.web.multipart.MultipartFile;

public interface CosService {

    /**
     * 文件上传
     * @param file 上传的文件
     * @param path 文件存储路劲
     * @return
     */
    CosUploadVo upload(MultipartFile file, String path);

    /**
     * 获取图片路径
     * @param path
     * @return
     */
    String getImageUrl(String path);
}
