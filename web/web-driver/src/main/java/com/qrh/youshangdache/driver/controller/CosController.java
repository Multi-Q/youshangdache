package com.qrh.youshangdache.driver.controller;

import com.atguigu.daijia.common.login.Login;
import com.atguigu.daijia.common.result.Result;
import com.qrh.youshangdache.driver.service.CosService;
import com.atguigu.daijia.model.vo.driver.CosUploadVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "腾讯云cos上传接口管理")
@RestController
@RequestMapping(value="/cos")
@SuppressWarnings({"unchecked", "rawtypes"})
public class CosController {
    @Resource
    private CosService cosService;


    @Operation(summary = "文件上传")
    @Login
    @PostMapping("/upload")
    public Result<CosUploadVo> upload(@RequestPart MultipartFile file,
                                      @RequestParam(name="path",defaultValue = "auth")String path){
        CosUploadVo cosUploadVo=cosService.uploadFile(file,path);
        return Result.ok(cosUploadVo);
    }

}
