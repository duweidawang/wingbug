package com.example.dwbug.config;

import com.example.dwbug.entity.BugInvitationEntity;
import com.example.dwbug.entity.UnitEntity;
import com.example.dwbug.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.mybatis.logging.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Logger;

@Slf4j
public class ExcelUtils {


    /**
     * 下载execel表模板
     *
     * @param response   HttpServletResponse
     * @param fileName   导出文件名
     * @param list       目标表单
     * @param title      表头数组
     *
     */
    public static void download(HttpServletResponse response, String fileName, List<UnitEntity> list, String[] title) {
        HSSFWorkbook workbook = new HSSFWorkbook();//创建Excel文件(Workbook)

        HSSFSheet sheet = workbook.createSheet("sheet1");//创建工作表(Sheet)
        //设置第一列宽（3766）
        HSSFRow row = sheet.createRow(0);// 创建行,从0开始
        for (int i = 0; i < title.length; i++) {
            HSSFCellStyle style = workbook.createCellStyle();
            style.setDataFormat(workbook.createDataFormat().getFormat("@"));
            sheet.setDefaultColumnStyle(i,style);
            HSSFCell cells = row.createCell(i);// 设置单元格内容,重载
            styleOne(workbook, cells).setCellValue(title[i]);
        }
        for(int i = 0; i < list.size() ;i++){
            HSSFRow row_one = sheet.createRow(i+1);
            styleTwo(workbook, row_one);
                        row_one.createCell(0).setCellValue(list.get(i).getId());
                        row_one.createCell(1).setCellValue(list.get(i).getUnitName());
                        row_one.createCell(2).setCellValue(list.get(i).getUnitRank());
                        row_one.createCell(3).setCellValue(list.get(i).getCreateTime().toString());
                }

        OutputStream outputStream = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName+".xlsx", "utf-8"));
            outputStream = response.getOutputStream();
            workbook.write(outputStream);//保存Excel文件
            if (outputStream != null) {
                outputStream.close();//关闭文件流
            }
        } catch (Exception e) {
            log.info("execel流输出时错误,错误详情：{}", e.getMessage());
        }
        System.out.println("OK!");
    }



    public static void download1(HttpServletResponse response, String fileName, List<UserEntity> list, String[] title) {
        HSSFWorkbook workbook = new HSSFWorkbook();//创建Excel文件(Workbook)

        HSSFSheet sheet = workbook.createSheet("sheet1");//创建工作表(Sheet)
        //设置第一列宽（3766）
        HSSFRow row = sheet.createRow(0);// 创建行,从0开始
        for (int i = 0; i < title.length; i++) {
            HSSFCellStyle style = workbook.createCellStyle();
            style.setDataFormat(workbook.createDataFormat().getFormat("@"));
            sheet.setDefaultColumnStyle(i,style);
            HSSFCell cells = row.createCell(i);// 设置单元格内容,重载
            styleOne(workbook, cells).setCellValue(title[i]);
        }
        for(int i = 0; i < list.size() ;i++){
            HSSFRow row_one = sheet.createRow(i+1);
            styleTwo(workbook, row_one);
            row_one.createCell(0).setCellValue(list.get(i).getId());
            row_one.createCell(1).setCellValue(list.get(i).getUsername());
            row_one.createCell(2).setCellValue(list.get(i).getName());
            row_one.createCell(3).setCellValue(list.get(i).getDescription());
            row_one.createCell(4).setCellValue(list.get(i).getUserRank());
            row_one.createCell(5).setCellValue(list.get(i).getBugNum());
            row_one.createCell(6).setCellValue(list.get(i).getApproved());
            row_one.createCell(7).setCellValue(list.get(i).getRole());
            row_one.createCell(8).setCellValue(list.get(i).getCreateTime().toString());
        }

        OutputStream outputStream = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName+".xlsx", "utf-8"));
            outputStream = response.getOutputStream();
            workbook.write(outputStream);//保存Excel文件
            if (outputStream != null) {
                outputStream.close();//关闭文件流
            }
        } catch (Exception e) {
            log.info("execel流输出时错误,错误详情：{}", e.getMessage());
        }
        System.out.println("OK!");
    }



    public static void download2(HttpServletResponse response, String fileName, List<BugInvitationEntity> list, String[] title) {
        HSSFWorkbook workbook = new HSSFWorkbook();//创建Excel文件(Workbook)

        HSSFSheet sheet = workbook.createSheet("sheet1");//创建工作表(Sheet)
        //设置第一列宽（3766）
        HSSFRow row = sheet.createRow(0);// 创建行,从0开始
        for (int i = 0; i < title.length; i++) {
            HSSFCellStyle style = workbook.createCellStyle();
            style.setDataFormat(workbook.createDataFormat().getFormat("@"));
            sheet.setDefaultColumnStyle(i,style);
            HSSFCell cells = row.createCell(i);// 设置单元格内容,重载
            styleOne(workbook, cells).setCellValue(title[i]);
        }
        for(int i = 0; i < list.size() ;i++){
            HSSFRow row_one = sheet.createRow(i+1);
            styleTwo(workbook, row_one);
            row_one.createCell(0).setCellValue(list.get(i).getInvitationId());
            row_one.createCell(1).setCellValue(list.get(i).getInvitation());
            row_one.createCell(2).setCellValue(list.get(i).getInvitationState());
            row_one.createCell(3).setCellValue(list.get(i).getNotes());
            row_one.createCell(4).setCellValue(list.get(i).getExpireTime().toString());
            row_one.createCell(5).setCellValue(list.get(i).getCreateTime().toString());


        }

        OutputStream outputStream = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName+".xlsx", "utf-8"));
            outputStream = response.getOutputStream();
            workbook.write(outputStream);//保存Excel文件
            if (outputStream != null) {
                outputStream.close();//关闭文件流
            }
        } catch (Exception e) {
            log.info("execel流输出时错误,错误详情：{}", e.getMessage());
        }

    }


    private static HSSFCell styleOne(HSSFWorkbook workbook, HSSFCell cell) {
        //创建CellStyle或HSSFCellStyle对象
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        //设置单元格字体位置水平方向
        style.setAlignment(HorizontalAlignment.LEFT);
        //设置单元格字体位置垂直方向
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置边框
        style.setBorderBottom(BorderStyle.THIN);   //底部边框样式
        //通过颜色索引设置底部颜色
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex()); //底部边框颜色

        //同理，设置左边样式
        style.setBorderLeft(BorderStyle.THIN);    //左边边框样式
        style.setLeftBorderColor(IndexedColors.BLUE.getIndex());    //左边边框颜色

        //同理，设置右边样式
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.GREEN.getIndex());
        //最后，设置顶部样式
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BROWN.getIndex());
        //设置字体
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 12); // 字体高度
        font.setFontName(" 宋体 "); // 字体
        style.setFont(font);
        cell.setCellStyle(style);
        return cell;
    }
    private static HSSFRow styleTwo(HSSFWorkbook workbook, HSSFRow cell) {
        //设置单元格数据格式
        HSSFCellStyle textStyle = workbook.createCellStyle();
        HSSFDataFormat format = workbook.createDataFormat();
        textStyle.setDataFormat(format.getFormat("#,##0.000"));
        cell.setRowStyle(textStyle);
        return cell;
    }




}
