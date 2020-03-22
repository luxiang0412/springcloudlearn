package com.luxiang.jodconvert.preview.service.impl;


import com.luxiang.jodconvert.preview.constants.Constants;
import com.luxiang.jodconvert.preview.dto.FileConvertResultDTO;
import com.luxiang.jodconvert.preview.service.PreviewService;
import com.luxiang.jodconvert.preview.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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

@Service
@Slf4j
public class PreviewServiceImpl implements PreviewService {

    @Value("${jodconverter.store.path}")
    private String storePath;

    @Autowired
    private DocumentConverter documentConverter;

    @Override
    public FileConvertResultDTO convertFile2pdf(File sourceFile, String fileExt) throws Exception {
        officeformat(sourceFile);
        FileConvertResultDTO fileConvertResultDTO = new FileConvertResultDTO();
        try {
            fileExt = fileExt.toLowerCase();
            String fileName = FileUtil.getWithoutExtension(sourceFile.getName());
            String targetFileExt = getTargetFileExt(fileExt);
            File targetFile = new File(storePath + FileUtil.SLASH_ONE + fileName + FileUtil.DOT + targetFileExt);
            documentConverter.convert(sourceFile).as(DefaultDocumentFormatRegistry.getFormatByExtension(fileExt))
                    .to(targetFile).as(DefaultDocumentFormatRegistry.getFormatByExtension(targetFileExt)).execute();
            fileConvertResultDTO.setStatus("success");
            fileConvertResultDTO.setTargetFileName(targetFile.getName());
        } catch (OfficeException e) {
            log.error("convertFile2pdf error : " + e.getMessage(), e);
            fileConvertResultDTO.setStatus("fail");
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
            documentConverter.convert(in).as(DefaultDocumentFormatRegistry.getFormatByExtension(fileExt))
                    .to(targetFile).as(DefaultDocumentFormatRegistry.getFormatByExtension(targetFileExt)).execute();
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

    public static void officeformat(File filename) throws IOException {
        FileInputStream fileInputStream = null;
        OutputStream out = null;
        try {
            //获取文件类型，即文件后缀名
            int start = filename.getAbsolutePath().length() - 4;
            int end = filename.getAbsolutePath().length();
            String excEnd = filename.getAbsolutePath().substring(start, end);//得到文件的后缀名
            if (excEnd.equalsIgnoreCase("xlsx")) {
                fileInputStream = new FileInputStream(filename);
                XSSFWorkbook workbook1 = new XSSFWorkbook(fileInputStream);
                float maxWidth = 0F;
                for (int i = 0; i < workbook1.getNumberOfSheets(); i++) {
                    Sheet sheet = workbook1.getSheetAt(i);
                    int rowNum = sheet.getLastRowNum();
                    for (int m = 0; m <= rowNum; m++) {
                        Row row = sheet.getRow(m);
                        if (isRowEmpty(row)) {
                            continue;
                        }
                        int colNum = row.getLastCellNum();
                        float count = 0F;
                        for (int n = 0; n < colNum; n++) {
                            Cell cell = row.getCell(n);
                            if (isCellEmpty(cell)) {
                                continue;
                            }
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
                    try {
                        out = new FileOutputStream(filename);
                        workbook1.write(out);
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }finally {
                        if (workbook1 != null) {
                            workbook1.close();
                        }
                    }
                }
            } else if (excEnd.equalsIgnoreCase(".xls")) {
                fileInputStream = new FileInputStream(filename);
                HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    Sheet sheet = workbook.getSheetAt(i);
                    sheet.setAutobreaks(true);
                    try {
                        workbook.write(new FileOutputStream(filename));
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                }
                workbook.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            if (out != null) {
                out.close();
            }
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
