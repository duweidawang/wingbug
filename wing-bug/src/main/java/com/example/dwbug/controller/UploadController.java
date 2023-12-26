package com.example.dwbug.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;


import cn.hutool.http.server.HttpServerResponse;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.dwbug.annotation.LogAnnotation;
import com.example.dwbug.config.ExcelUtils;
import com.example.dwbug.en.RoleEnum;
import com.example.dwbug.entity.BugInvitationEntity;
import com.example.dwbug.entity.LoginUser;
import com.example.dwbug.entity.UnitEntity;
import com.example.dwbug.entity.UserEntity;
import com.example.dwbug.service.BugInvitationService;
import com.example.dwbug.service.UnitService;
import com.example.dwbug.service.UserService;
import com.example.dwbug.utils.CheckEmail;
import com.example.dwbug.utils.MailUtils;
import com.example.dwbug.utils.MyMailService;
import com.example.dwbug.utils.R;

import io.lettuce.core.GeoArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Email;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UploadController {

    @Autowired
    UserService userService;

    @Value("${upload.url}")
    public String IMAGE_UPLOAD_DIR;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UnitService unitService;

    @Autowired
    BugInvitationService invitationService;











    @PostMapping("/upload")
    @LogAnnotation(operation = "上传头像")
    public R uploadImage(@RequestParam("file") MultipartFile image) {
        try {
            // 获取原始文件名称
            String originalFilename = image.getOriginalFilename();
            // 生成新文件名
            String fileName = createNewFileName(originalFilename);
            // 保存文件
            image.transferTo(new File(IMAGE_UPLOAD_DIR, fileName));
            // 返回结果
            log.debug("上传成功，{}", fileName);
            return R.ok("上传成功").put("data", fileName);
        } catch (IOException e) {
            throw new RuntimeException("上传失败", e);
        }
    }

    @GetMapping("/upload/delete")
    public R deleteBlogImg(@RequestParam("name") String filename) {
        File file = new File(IMAGE_UPLOAD_DIR, filename);
        if (file.isDirectory()) {
            return R.error("错误的文件名称");
        }
        FileUtil.del(file);
        return R.ok("删除成功");
    }

    private String createNewFileName(String originalFilename) {
        // 获取后缀
        String suffix = StrUtil.subAfter(originalFilename, ".", true);
        // 生成目录
        String name = UUID.randomUUID().toString();
//        int hash = name.hashCode();
//        int d1 = hash & 0xF;
//        int d2 = (hash >> 4) & 0xF;
        // 判断目录是否存在
        File dir = new File(IMAGE_UPLOAD_DIR, StrUtil.format("/images"));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 生成文件名
        return StrUtil.format("/images/{}.{}", name, suffix);
    }


    //导入用户账号以及密码
    @LogAnnotation(operation = "导出用户")
    @PreAuthorize("hasAnyAuthority('bug:user:invitation')")
    @PostMapping("/excel")
    public R uploadExcel(@RequestParam("file") MultipartFile file) throws IOException {


        List<UserEntity> users = new ArrayList<>();
        String name = file.getOriginalFilename();
        CheckEmail checkEmail = new CheckEmail();
        List<String> email = new ArrayList<>();

        InputStream inputStream = file.getInputStream();

        try {
            Workbook workbook = null;

            if (name.substring(name.lastIndexOf(".") + 1).equals("xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else if (name.substring(name.lastIndexOf(".") + 1).equals("xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                workbook = new XSSFWorkbook(inputStream);
            }

            Sheet sheet = workbook.getSheetAt(0);
            // sheet.getPhysicalNumberOfRows()获取总的行数
            // 循环读取每一行
            for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
                // 循环读取每一个格
                Row row = sheet.getRow(i);
                int key = 0;
                UserEntity user = new UserEntity();
                // row.getPhysicalNumberOfCells()获取总的列数
                for (int index = 0; index < 3; index++) {
                    // 获取数据，但是我们获取的cell类型
                    //代码上的内容自己根据实际需要自己调整就可以，这里只是展示一个样式···~
                    Cell cell = row.getCell(index);
                    // 转换为字符串类型
                    cell.setCellType(CellType.STRING);
                    // 获取得到字符串
                    String id = cell.getStringCellValue();

                    if (index == 0) {
                        //如果既不是qq也不是163 就跳过
                        if (!checkEmail.CheckqqMail(id) && !checkEmail.Check163Mail(id)) {
                            email.add(id);
                            key++;
                            break;
                        }
                        user.setUsername(id);
                    } else if (index == 1) {
                        String encode = passwordEncoder.encode(id);
                        user.setPassword(encode);
                    } else {
                        user.setName(id);
                    }

                }
                if (key == 0) {
                    user.setRole(2L);
                    users.add(user);
                }

            }
            workbook.close();
        } catch (Exception e) {
            return R.error(501, "读取文件异常");
        }


        System.out.println(users);

        email.remove(0);

        if (email.size() > 0) {
            return R.error(501, "内容错误").put("存在以下账户格式不符", email);
        }
        userService.saveBatch(users);
        return R.ok("导入成功");
    }

    //导入单位 超管和管理才可以
    @LogAnnotation(operation = "导入单位")
    @PreAuthorize("hasAnyAuthority('bug:user:invitation')")
    @PostMapping("/excelunit")

    public R uploadExcelUnit(@RequestParam("file") MultipartFile file) throws IOException {

        String name = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        List<UnitEntity> unitEntityList = new ArrayList<>();

        try {
            Workbook workbook = null;

            if (name.substring(name.lastIndexOf(".") + 1).equals("xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else if (name.substring(name.lastIndexOf(".") + 1).equals("xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                workbook = new XSSFWorkbook(inputStream);
            }

            Sheet sheet = workbook.getSheetAt(0);
            sheet.getPhysicalNumberOfRows();//获取总的行数
            // 循环读取每一行
            for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
                // 循环读取每一个格
                Row row = sheet.getRow(i);

                // row.getPhysicalNumberOfCells()获取总的列数
                for (int index = 0; index < row.getPhysicalNumberOfCells(); index++) {
                    UnitEntity unitEntity = new UnitEntity();
                    // 获取数据，但是我们获取的cell类型
                    //代码上的内容自己根据实际需要自己调整就可以，这里只是展示一个样式···~
                    Cell cell = row.getCell(index);
                    // 转换为字符串类型
                    cell.setCellType(CellType.STRING);
                    // 获取得到字符串

                    String id = cell.getStringCellValue();

                    if (!StringUtils.isBlank(id)) {
                        unitEntity.setUnitName(id);
                        unitEntityList.add(unitEntity);
                    }

                }
            }
            workbook.close();
        } catch (Exception e) {
            return R.error(501, "读取文件异常");
        }
        System.out.println(unitEntityList);
        unitService.saveBatch(unitEntityList);
        return R.ok("导入成功");

    }

    /**
     * 将单位导出为excel
     *
     * @param num
     * @param response
     * @throws IOException
     */
    @LogAnnotation(operation = "导出单位")
    @PreAuthorize("hasAnyAuthority('bug:user:invitation')")
    @GetMapping("/readFile")
    public void readFile(@RequestParam Long num,HttpServletResponse response) throws IOException {


        if(num<=0){
            throw new RuntimeException();
        }else{
            List<UnitEntity> unitEntityList = unitService.searchByNum(num);
            String[] s = new String[4];
            s[0] = "单位序号";
            s[1] = "单位名称";
            s[2] = "单位Rank";
            s[3] = "单位创建时间";
            ExcelUtils.download(response, "单位表", unitEntityList, s);
        }



    }

    /**
     * 将用户导出
     *
     * @param num
     * @param response
     * @throws IOException
     */
    @LogAnnotation(operation = "导出用户")
    @PreAuthorize("hasAnyAuthority('bug:user:invitation')")
    @GetMapping("/readUserFile")
    public void readUserFile(@RequestParam Long num, HttpServletResponse response) throws IOException {

        if(num<=0){
            throw new RuntimeException();
        }else{
            List<UserEntity> list = userService.serachUsers(num);
            String[] s = new String[9];

            s[0] = "用户序号";
            s[1] = "用户账号";
            s[2] = "用户名称";
            s[3] = "用户描述";
            s[4] = "用户Rank";
            s[5] = "用户漏洞数";
            s[6] = "漏洞审核通过数";
            s[7] = "用户角色";
            s[8] = "创建时间";

            ExcelUtils.download1(response, "用户表", list, s);

        }
    }

    /**邀请码
     * 导出
     * @param num
     * @param
     * @param response
     * @throws IOException
     */
   @LogAnnotation(operation = "导出邀请码")
    @PreAuthorize("hasAnyAuthority('bug:user:invitation')")
    @GetMapping("/readinvitation")
    public void readInvationFile(@RequestParam Long num, HttpServletResponse response) throws IOException {


        if(num<=0){
            throw new RuntimeException();
        }else{
            List<BugInvitationEntity> bugInvitationEntities = invitationService.selectInvitation(num);
            String[] s = new String[6];

            s[0] = "邀请码序号";
            s[1] = "邀请码";
            s[2] = "邀请码状态";
            s[3] = "邀请码备注";
            s[4] = "结束时间";
            s[5] = "创建时间";

            ExcelUtils.download2(response, "邀请码表", bugInvitationEntities, s);

        }
    }
}








