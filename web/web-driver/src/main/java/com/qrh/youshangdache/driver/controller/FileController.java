package com.qrh.youshangdache.driver.controller;

import com.qrh.youshangdache.common.login.Login;
import com.qrh.youshangdache.common.result.Result;
import com.qrh.youshangdache.driver.service.CosService;
import com.qrh.youshangdache.driver.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "上传管理接口")
@RestController
@RequestMapping("/file")
public class FileController {
    @Resource
    private CosService cosService;
    @Resource
    private FileService fileService;

    @Operation(summary = "上传")
    @Login
    @PostMapping("/upload")
    public Result<String> upload(@RequestPart MultipartFile file) {
        String url = fileService.upload(file);
        return Result.ok(url);
    }
}
