package com.qrh.youshangdache.driver.service.impl;

import com.qrh.youshangdache.driver.client.CosFeignClient;
import com.qrh.youshangdache.driver.service.CosService;
import com.qrh.youshangdache.model.vo.driver.CosUploadVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class CosServiceImpl implements CosService {

    @Resource
    private CosFeignClient cosFeignClient;

    @Override
    public CosUploadVo uploadFile(MultipartFile file,String path) {
        return cosFeignClient.upload(file,path).getData();
    }
}
