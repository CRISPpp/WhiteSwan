package com.crisp.saleproject.controller;

import com.crisp.saleproject.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 负责文件上传和下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${whiteswan.path}")
    private String basePath;
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {//参数名必须和前端上传的name保持一致
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));//获取后缀
        String newFileName = UUID.randomUUID().toString() + suffix;

        //basePath没有就创建
        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();
        }

        file.transferTo(new File(basePath + newFileName));
        return R.success(newFileName);
    }

    /**
     * 不需要返回值，直接写回前端页面
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        //输入流读取文件内容
        FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
        //输出流写回浏览器
        ServletOutputStream servletOutputStream = response.getOutputStream();
        response.setContentType("image/jpeg");
        int len = 0;
        byte[] bytes = new byte[1024];
        while((len = fileInputStream.read(bytes)) != -1){
            servletOutputStream.write(bytes,0, len);
            servletOutputStream.flush();
        }
        servletOutputStream.close();
        fileInputStream.close();
    }
}
