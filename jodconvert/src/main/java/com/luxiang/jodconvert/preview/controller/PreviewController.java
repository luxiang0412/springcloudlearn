package com.luxiang.jodconvert.preview.controller;


import com.luxiang.jodconvert.preview.dto.FileConvertResultDTO;
import com.luxiang.jodconvert.preview.service.PreviewService;
import com.luxiang.jodconvert.preview.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;

@RestController
@Slf4j
@RequestMapping(value = "/preview")
public class PreviewController {

    @Value("${jodconverter.store.path}")
    private String storePath;

    @Autowired
    private PreviewService previewService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    /**
     * 读取已经转换好的文件
     *
     * @param fileName
     * @return
     */
    @GetMapping(value = "/readFile")
    public ResponseEntity<byte[]> readFile(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            log.warn("fileName is blank");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String fileExt = FileUtil.getExtension(fileName);
        if (StringUtils.isBlank(fileExt)) {
            fileName = fileName + FileUtil.PDF;
        }
        String filePath = storePath + FileUtil.SLASH_ONE + fileName;
        File file = new File(filePath);


        if (!file.exists()) {
            log.warn("fileName:{} is not found", fileName);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            //判断文件类型
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            response.setContentType(mimeType);

            //设置文件响应大小
            response.setContentLengthLong(file.length());

            byte[] bytes = FileUtil.readFileToByteArray(file);
            response.getOutputStream().write(bytes);
            return new ResponseEntity<>(bytes, HttpStatus.OK);
        } catch (IOException e) {
            log.error("readFile error:" + e.getMessage(), e);
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);

    }

    /**
     * 文件转换
     *
     * @param file
     * @param fileExt
     * @return
     */
    @PostMapping(value = "/convertFile")
    public ResponseEntity<FileConvertResultDTO> convertFile(@RequestParam("file") MultipartFile file,
                                                            @RequestParam(value = "fileExt", required = false) String fileExt) {
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (file.isEmpty()) {
            log.warn("file is empty");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String fileName = file.getOriginalFilename();
        String ext = FileUtil.getExtension(fileName);


        try {

            if (StringUtils.isBlank(ext)) {
                ext = fileExt;
                if (StringUtils.isBlank(ext)) {
                    ext = FileUtil.getExtension(file.getInputStream());
                }
            }
            FileConvertResultDTO fileConvertResultDTO = previewService.convertInputStream2pdf(file.getInputStream(), fileName, ext);
            return new ResponseEntity<>(fileConvertResultDTO, HttpStatus.OK);
        } catch (IOException e) {
            log.error("convertFile error:" + e.getMessage(), e);
        }

        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);

    }

    /**
     * 文件转换
     *
     * @param path
     * @param fileExt
     * @return
     */
    @PostMapping(value = "/convertFile2")
    public void convertFile(String path,
                            @RequestParam(value = "fileExt", required = false) String fileExt) throws Exception {
        File file = new File(path);
        previewService.convertFile2pdf(file,fileExt);
    }

}
