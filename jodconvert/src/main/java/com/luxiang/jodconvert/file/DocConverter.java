package com.luxiang.jodconvert.file;

import com.google.common.collect.Lists;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.*;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.office.OfficeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * office 文档预览 除了xlsx和xls 会转换成html格式 其他都是转换成 pdf格式
 */
public class DocConverter {

    private static Logger logger = LoggerFactory.getLogger(DocConverter.class);

    private static List TO_HTML_EXTENSIONS = Lists.newArrayList();

    public static void convert(File source) {
        Objects.requireNonNull(source, "source is not null");
        if ("pdf".equals(FilenameUtils.getExtension(source.getName()))) {
            return;
        }
        DocumentConverter documentConverter = SpringContextUtil.getBean(DocumentConverter.class);
        File tmpFile = null;
        try {
            File targetFile = getTargetFile(source);
            tmpFile = officeformat(source);
            documentConverter
                    .convert(tmpFile == null ? source : tmpFile)
                    .as(Objects.requireNonNull(DefaultDocumentFormatRegistry.getFormatByExtension(FilenameUtils.getExtension(source.getName()))))
                    .to(targetFile)
                    .as(Objects.requireNonNull(DefaultDocumentFormatRegistry.getFormatByExtension(FilenameUtils.getExtension(targetFile.getName()))))
                    .execute();
        } catch (OfficeException | IOException e) {
            logger.error("convert error {}", e.getMessage());
            e.printStackTrace();
        }
        if (tmpFile != null && tmpFile.exists()) {
            final String tmpFileName = tmpFile.getAbsolutePath();
            new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(15000L);
                        boolean delete = new File(tmpFileName).delete();
                        if (delete) {
                            logger.info("delete tmpfile {} {}", tmpFileName, delete);
                        } else {
                            logger.error("delete tmpfile error {} {}", tmpFileName, delete);
                        }
                    } catch (InterruptedException e) {
                        logger.error("delete tmpfile error {} {}", tmpFileName, e);
                    }
                    super.run();
                }
            }.start();
        }
    }

    public static File getTargetFile(File source) {
        //文件名 例如：abc.xlsx
        String sourceName = source.getName();
        //扩展名 例如：xlsx
        String extension = FilenameUtils.getExtension(sourceName);
        //绝对路径 例如：D:\jodconvert_test\abc.xlsx
        String absolutePath = source.getAbsolutePath();
        //全路径 例如：D:\jodconvert_test\
        String fullPath = FilenameUtils.getFullPath(absolutePath);
        //基本的名称 例如：abc
        String baseName = FilenameUtils.getBaseName(sourceName);
        //转html
        if (TO_HTML_EXTENSIONS.contains(extension)) {
            return new File(fullPath + baseName + ".html");
            //转pdf
        } else {
            return new File(fullPath + baseName + ".pdf");
        }
    }

    static CellStyle getPreferredCellStyle(Cell cell) {
        // a method to get the preferred cell style for a cell
        // this is either the already applied cell style
        // or if that not present, then the row style (default cell style for this row)
        // or if that not present, then the column style (default cell style for this column)
        CellStyle cellStyle = cell.getCellStyle();
        // if no explicit cell style applied then cellStyle.getIndex() is 0 for XSSF
        // or 15 (0xF = the index to the default ExtendedFormatRecord (0xF)) for HSSF
        if ((cell instanceof XSSFCell && cellStyle.getIndex() == 0) || (cell instanceof HSSFCell && cellStyle.getIndex() == 15))
            cellStyle = cell.getRow().getRowStyle();
        if (cellStyle == null) cellStyle = cell.getSheet().getColumnStyle(cell.getColumnIndex());
        if (cellStyle == null) cellStyle = cell.getCellStyle();
        return cellStyle;
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
                XSSFCellStyle rowStyle = xlsxworkbook.createCellStyle();
                rowStyle.setWrapText(true);
                float maxWidth = 0F;
                for (int i = 0; i < xlsxworkbook.getNumberOfSheets(); i++) {
                    Sheet sheet = xlsxworkbook.getSheetAt(i);
                    int maxCols = 0;
                    List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
                    for (CellRangeAddress mergedRegion : mergedRegions) {
                        RegionUtil.setBorderTop(BorderStyle.THIN, mergedRegion, sheet);
                        RegionUtil.setBorderBottom(BorderStyle.THIN, mergedRegion, sheet);
                        RegionUtil.setBorderLeft(BorderStyle.THIN, mergedRegion, sheet);
                        RegionUtil.setBorderRight(BorderStyle.THIN, mergedRegion, sheet);
                    }
                    int rowNum = sheet.getPhysicalNumberOfRows();
                    for (int m = 0; m < rowNum; m++) {
                        Row row = sheet.getRow(m);
                        if (row == null) {
                            continue;
                        }
                        CellStyle rs = row.getRowStyle();
                        if (rs == null) {
                            row.setRowStyle(rowStyle);
                        } else {
                            rs.setWrapText(true);
                        }
                        row.setZeroHeight(false);
                        int colNum = row.getPhysicalNumberOfCells();
                        float count = 0F;
                        float tallestCell = -1;
                        if (colNum > maxCols) {
                            maxCols = colNum;
                        }
                        for (int n = 0; n < colNum; n++) {
                            Cell cell = row.getCell(n);
                            if (cell == null) {
                                cell = row.createCell(n);
                            }
                            //计算行高结束
                            int fontSize = getFontSize(xlsxworkbook, cell);
                            if (cell.getCellType() == CellType.STRING) {
                                String value = cell.getStringCellValue();
                                int numLines = 1;
                                for (int length = 0; length < value.length(); length++) {
                                    if (value.charAt(length) == '\n') numLines++;
                                }
                                float cellHeight = computeRowHeightInPoints(fontSize, numLines, sheet);
                                if (cellHeight > tallestCell) {
                                    tallestCell = cellHeight;
                                }
                            }
                            //计算行高结束
                            count += sheet.getColumnWidthInPixels(cell.getColumnIndex());
                        }
                        row.setHeightInPoints(tallestCell);
                        logger.info("row height: {}", tallestCell);
                        if (count > maxWidth) {
                            maxWidth = count;
                        }
                    }
                    for (int j = 0; j < maxCols; j++) {
                        sheet.autoSizeColumn(j, true);
                    }
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
                    printSetup.setScale((short) v1);//自定义缩放①，此处100为无缩放
                    printSetup.setFitHeight((short) 1);//设置高度为自动分页
                    printSetup.setFitWidth((short) 1);//设置宽度为一页
                    //初始化
                    maxWidth = 0F;
                    outputStream = new FileOutputStream(targetFile);
                    xlsxworkbook.write(outputStream);
                }
            } else if ("xls".equalsIgnoreCase(extension)) {
                inputStream = new FileInputStream(sourceFile);
                xlsworkbook = new HSSFWorkbook(inputStream);
                HSSFCellStyle rowStyle = xlsworkbook.createCellStyle();
                for (int i = 0; i < xlsworkbook.getNumberOfSheets(); i++) {
                    Sheet sheet = xlsworkbook.getSheetAt(i);
                    sheet.setAutobreaks(true);
                    List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
                    for (CellRangeAddress mergedRegion : mergedRegions) {
                        RegionUtil.setBorderTop(BorderStyle.THIN, mergedRegion, sheet);
                        RegionUtil.setBorderBottom(BorderStyle.THIN, mergedRegion, sheet);
                        RegionUtil.setBorderLeft(BorderStyle.THIN, mergedRegion, sheet);
                        RegionUtil.setBorderRight(BorderStyle.THIN, mergedRegion, sheet);
                    }
                    int rowNum = sheet.getPhysicalNumberOfRows();
                    int maxCols = 0;
                    for (int j = 0; j < rowNum; j++) {
                        Row row = sheet.getRow(j);
                        if (row == null) {
                            continue;
                        }
                        CellStyle rs = row.getRowStyle();
                        if (rs == null) {
                            row.setRowStyle(rowStyle);
                        } else {
                            rs.setWrapText(true);
                        }
                        row.setZeroHeight(false);
                        int colNum = row.getPhysicalNumberOfCells();
                        float tallestCell = -1;
                        if (colNum > maxCols) {
                            maxCols = colNum;
                        }
                        for (int k = 0; k < colNum; k++) {
                            Cell cell = row.getCell(k);
                            if (cell == null) {
                                cell = row.createCell(k);
                            }
                            CellStyle cellStyle = cell.getCellStyle();
                            cellStyle.setWrapText(true);
                            cell.setCellStyle(cellStyle);
                            //计算行高结束
                            int fontSize = getFontSize(xlsworkbook, cell);
                            if (cell.getCellType() == CellType.STRING) {
                                String value = cell.getStringCellValue();
                                int numLines = 1;
                                for (int length = 0; length < value.length(); length++) {
                                    if (value.charAt(length) == '\n') numLines++;
                                }
                                float cellHeight = computeRowHeightInPoints(fontSize, numLines, sheet);
                                if (cellHeight > tallestCell) {
                                    tallestCell = cellHeight;
                                }
                            }
                            //计算行高结束
                        }
                        row.setHeightInPoints(tallestCell);
                    }
                    for (int j = 0; j < maxCols; j++) {
                        sheet.autoSizeColumn(j, true);
                    }
                    outputStream = new FileOutputStream(targetFile);
                    xlsworkbook.write(outputStream);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            return null;
        }
    }

    private static int getFontSize(Workbook workbook, Cell cell) {
        CellStyle cellStyle = cell.getCellStyle();
        if (cellStyle != null) {
            int fontIndexAsInt = cellStyle.getFontIndexAsInt();
            Font fontAt = workbook.getFontAt(fontIndexAsInt);
            return fontAt.getFontHeightInPoints();
        }
        return XSSFFont.DEFAULT_FONT_SIZE;
    }

    public static float computeRowHeightInPoints(int fontSizeInPoints, int numLines, Sheet sheet) {
        float factor = 0F;
        if (sheet instanceof XSSFSheet) {
            factor = 1.5F;
        } else {
            factor = 1.36F;
        }
        // a crude approximation of what excel does
        float lineHeightInPoints = factor * fontSizeInPoints;
        float rowHeightInPoints = lineHeightInPoints * numLines;
        rowHeightInPoints = Math.round(rowHeightInPoints * 4) / 4f;        // round to the nearest 0.25

        // Don't shrink rows to fit the font, only grow them
        float defaultRowHeightInPoints = sheet.getDefaultRowHeightInPoints();
        if (rowHeightInPoints < defaultRowHeightInPoints + 1) {
            rowHeightInPoints = defaultRowHeightInPoints;
        }
        return rowHeightInPoints;
    }
}
