package com.luxiang.jodconvert.file.controller;

import com.alibaba.fastjson.JSON;
import com.luxiang.jodconvert.common.exception.BizException;
import com.luxiang.jodconvert.common.model.Result;
import com.luxiang.jodconvert.common.util.HttpClientUtil;
import com.luxiang.jodconvert.file.constant.FileConstant;
import com.luxiang.jodconvert.preview.dto.FileConvertResultDTO;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/")
public class FileController {

    @GetMapping(value = "/")
    public String gotoUpload() {
        return "upload";
    }


    @PostMapping(value = "/upload")
    @ResponseBody
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put(FileConstant.FILE_PARAM_KEY, file.getBytes());
        String response = HttpClientUtil.INSTANCE.post(FileConstant.PREVIEW_REMOTE_URL, params, file.getOriginalFilename());
        FileConvertResultDTO fileConvertResultDTO = JSON.parseObject(response, FileConvertResultDTO.class);
        if (ObjectUtils.isNotEmpty(fileConvertResultDTO) && FileConstant.SUCCESS.equals(fileConvertResultDTO.getStatus())) {
            return new Result<String>().setData(fileConvertResultDTO.getTargetFileName());
        }
        throw new BizException("文件上传解析预览失败", 406);
    }

    @GetMapping(value = "toPreview")
    public String gotoPreview(String fileName, Model model) {
        model.addAttribute("fileName", fileName);
        return "preview";
    }


}
