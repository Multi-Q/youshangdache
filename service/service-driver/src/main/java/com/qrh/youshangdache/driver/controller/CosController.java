package com.qrh.youshangdache.driver.controller;

import com.qrh.youshangdache.common.result.Result;
import com.qrh.youshangdache.driver.service.CosService;
import com.qrh.youshangdache.model.vo.driver.CosUploadVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "腾讯云cos上传接口管理")
@RestController
@RequestMapping(value="/cos")
public class CosController {
    @Resource
    private CosService cosService;

    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public Result<CosUploadVo> upload(@RequestPart MultipartFile file,
                               @RequestParam(name="path",defaultValue = "auth")String path){
        return Result.ok(cosService.upload(file,path));
    }

}

