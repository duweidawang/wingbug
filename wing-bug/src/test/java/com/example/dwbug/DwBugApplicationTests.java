package com.example.dwbug;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.dwbug.dao.BugInvitationDao;
import com.example.dwbug.dao.CommentDao;
import com.example.dwbug.dao.RecordDao;
import com.example.dwbug.dao.UserDao;
import com.example.dwbug.entity.CommentEntity;
import com.example.dwbug.entity.RecordEntity;
import com.example.dwbug.entity.UserEntity;
import com.example.dwbug.service.LoopholeService;
import com.example.dwbug.service.UserService;
import com.example.dwbug.utils.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.Local;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@SpringBootTest
class DwBugApplicationTests {

    @Resource
    BugInvitationDao bugInvitationDao;


    @Test
    public void test1(){

        LocalDateTime localDateTime = bugInvitationDao.searchTime(1L);
        long time = new Date().getTime();
        System.out.println(time);
        System.out.println(localDateTime);
    }


    @Test
    public void test(){
        DateTime date = new DateTime();

        System.out.println();
        System.out.println(LocalDate.now());
    }
    @Test
    public void test11(){
        String randomString = UUID.randomUUID().toString();
        System.out.println(randomString);
        System.out.println(randomString.length());
        System.out.println(randomString.substring(0,10));
        randomString = randomString.replace("-", "");
        Random random  = new Random();
        int i = random.nextInt(22);
        String substring = randomString.substring(i, i + 10);

    }



}
