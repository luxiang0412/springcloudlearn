package com.luxiang.jodconvert.preview.service.impl;


import com.luxiang.jodconvert.preview.constants.Constants;
import com.luxiang.jodconvert.preview.dto.FileConvertResultDTO;
import com.luxiang.jodconvert.preview.service.PreviewService;
import com.luxiang.jodconvert.preview.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.office.OfficeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class PreviewServiceImpl implements PreviewService {

    @Value("${jodconverter.store.path}")
    private String storePath;

    @Autowired
    private DocumentConverter documentConverter;

    @Override
    public FileConvertResultDTO convertFile2pdf(File sourceFile, String fileExt) throws Exception {
        FileConvertResultDTO fileConvertResultDTO = new FileConvertResultDTO();
        File targetFile = null;
        try {
            String extension = FilenameUtils.getExtension(sourceFile.getName());
            String targetFileExt = getTargetFileExt(extension);
            targetFile = officeformat(sourceFile);
            File outputFile = new File(storePath + FileUtil.SLASH_ONE + (sourceFile.getName().replace(extension, targetFileExt)));
            Objects.requireNonNull(targetFile, "file is not null");
            documentConverter.convert(targetFile).as(DefaultDocumentFormatRegistry.getFormatByExtension(extension))
                    .to(outputFile).as(DefaultDocumentFormatRegistry.getFormatByExtension(targetFileExt)).execute();
            fileConvertResultDTO.setStatus("success");
            fileConvertResultDTO.setTargetFileName(targetFile.getName());
        } catch (OfficeException e) {
            log.error("convertFile2pdf error : " + e.getMessage(), e);
            fileConvertResultDTO.setStatus("fail");
        } finally {
            if (targetFile != null && targetFile.exists()) {
                targetFile.delete();
            }
        }
        return fileConvertResultDTO;

    }

    @Override
    public FileConvertResultDTO convertInputStream2pdf(InputStream in, String fileName, String fileExt) {
        FileConvertResultDTO fileConvertResultDTO = new FileConvertResultDTO();
        try {
            fileExt = fileExt.toLowerCase();
            fileName = FileUtil.getWithoutExtension(fileName);
            String targetFileExt = getTargetFileExt(fileExt);
            File targetFile = new File(storePath + FileUtil.SLASH_ONE + fileName + FileUtil.DOT + targetFileExt);
            documentConverter
                    .convert(in)
                    .as(DefaultDocumentFormatRegistry.getFormatByExtension(fileExt))
                    .to(targetFile)
                    .as(DefaultDocumentFormatRegistry.getFormatByExtension(targetFileExt))
                    .execute();
            fileConvertResultDTO.setStatus("success");
            fileConvertResultDTO.setTargetFileName(targetFile.getName());
        } catch (OfficeException e) {
            log.error("convertInputStream2pdf error : " + e.getMessage(), e);
            fileConvertResultDTO.setStatus("fail");
        }
        return fileConvertResultDTO;
    }

    /**
     * 获取想要转换的格式类型
     *
     * @return
     */
    private String getTargetFileExt(String originFileExt) {
        if (Constants.fileType2Htmls.contains(originFileExt)) {
            return FileUtil.HTML;
        }
        return FileUtil.PDF;
    }

    @PostConstruct
    private void init() {
        File targetDir = new File(storePath);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
    }

    public static File officeformat(File sourceFile) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        HSSFWorkbook xlsworkbook = null;
        XSSFWorkbook xlsxworkbook = null;
        Objects.requireNonNull(sourceFile, "文件为空");
        String extension = FilenameUtils.getExtension(sourceFile.getName());
        if (extension.equals("")) {
            Objects.requireNonNull(sourceFile, "扩展名为空");
            return null;
        }
        String targetFilePath = sourceFile.getAbsolutePath().replace(sourceFile.getName(), "tmpfile_" + UUID.randomUUID() + "_" + System.nanoTime() + "." + extension);
        File targetFile = new File(targetFilePath);
        try {
            //获取文件类型，即文件后缀名
            if ("xlsx".equalsIgnoreCase(extension)) {
                inputStream = new FileInputStream(sourceFile);
                xlsxworkbook = new XSSFWorkbook(inputStream);
                float maxWidth = 0F;
                for (int i = 0; i < xlsxworkbook.getNumberOfSheets(); i++) {
                    Sheet sheet = xlsxworkbook.getSheetAt(i);
                    int rowNum = sheet.getLastRowNum();
                    for (int m = 0; m <= rowNum; m++) {
                        Row row = sheet.getRow(m);
                        if (isRowEmpty(row)) {
                            continue;
                        }
                        int colNum = row.getPhysicalNumberOfCells();
                        float count = 0F;
                        for (int n = 0; n < colNum; n++) {
                            Cell cell = row.getCell(n);
                            if (isCellEmpty(cell)) {
                                continue;
                            }
                            CellStyle cellStyle = cell.getCellStyle();
                            cellStyle.setBorderTop(BorderStyle.THIN);
                            cellStyle.setBorderBottom(BorderStyle.THIN);
                            cellStyle.setBorderLeft(BorderStyle.THIN);
                            cellStyle.setBorderRight(BorderStyle.THIN);
                            cellStyle.setWrapText(true);
                            cell.setCellStyle(cellStyle);
                            count += sheet.getColumnWidthInPixels(cell.getColumnIndex());
                        }
                        if (count > maxWidth) {
                            maxWidth = count;
                        }
                        count = 0;
                    }
                    System.out.println("第" + (i + 1) + "个sheet：" + maxWidth);
                    XSSFPrintSetup printSetup = (XSSFPrintSetup) sheet.getPrintSetup();
                    //595*842
                    printSetup.setPaperSize(XSSFPrintSetup.A4_PAPERSIZE); // 纸张
                    sheet.setHorizontallyCenter(true);//设置打印页面为水平居中
                    sheet.setVerticallyCenter(false);
                    sheet.setAutobreaks(true);
                    printSetup.setLandscape(true);
                    sheet.setMargin(XSSFSheet.BottomMargin, (double) 0.5);// 页边距（下）
                    sheet.setMargin(XSSFSheet.LeftMargin, (double) 0.1);// 页边距（左）
                    sheet.setMargin(XSSFSheet.RightMargin, (double) 0.1);// 页边距（右）
                    sheet.setMargin(XSSFSheet.TopMargin, (double) 0.5);// 页边距（上）
                    float v1 = 59500 / maxWidth + 10;
                    if (v1 > 400) {
                        v1 = 400;
                    } else if (v1 < 10) {
                        v1 = 10;
                    }
                    System.out.println("scale:" + (short) v1);
                    printSetup.setScale((short) v1);//自定义缩放①，此处100为无缩放
                    printSetup.setFitHeight((short) 1);//设置高度为自动分页
                    printSetup.setFitWidth((short) 1);//设置宽度为一页
                    //初始化
                    maxWidth = 0F;
                    outputStream = new FileOutputStream(targetFile);
                    xlsxworkbook.write(outputStream);
                }
                xlsxworkbook.close();
            } else if ("xls".equalsIgnoreCase(extension)) {
                inputStream = new FileInputStream(sourceFile);
                xlsworkbook = new HSSFWorkbook(inputStream);
                for (int i = 0; i < xlsworkbook.getNumberOfSheets(); i++) {
                    Sheet sheet = xlsworkbook.getSheetAt(i);
                    sheet.setAutobreaks(true);
                    int rowNum = sheet.getLastRowNum();
                    for (int j = 0; j <= rowNum; j++) {
                        Row row = sheet.getRow(j);
                        if (isRowEmpty(row)) {
                            continue;
                        }
                        int colNum = row.getPhysicalNumberOfCells();
                        for (int k = 0; k < colNum; k++) {
                            Cell cell = row.getCell(k);
                            CellStyle cellStyle = cell.getCellStyle();
                            cellStyle.setBorderTop(BorderStyle.THIN);
                            cellStyle.setBorderBottom(BorderStyle.THIN);
                            cellStyle.setBorderLeft(BorderStyle.THIN);
                            cellStyle.setBorderRight(BorderStyle.THIN);
                            cellStyle.setWrapText(true);
                            cell.setCellStyle(cellStyle);
                        }
                    }
                    outputStream = new FileOutputStream(targetFile);
                    xlsworkbook.write(outputStream);
                }
                xlsworkbook.close();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            if (xlsworkbook != null) {
                xlsworkbook.close();
            }
            if (xlsxworkbook != null) {
                xlsxworkbook.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
        if (targetFile.exists()) {
            return targetFile;
        } else {
            return sourceFile;
        }
    }

    /**
     * 判断该行是否为空行
     *
     * @param row 当前行
     * @return true 为空
     */
    public static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK)
                return false;
        }
        return true;
    }

    /**
     * 判断单元格是否为空
     *
     * @param cell 当前单元格
     * @return true表示为空
     */
    public static boolean isCellEmpty(Cell cell) {
        return cell == null || cell.getCellType() == CellType.BLANK;
    }
}
