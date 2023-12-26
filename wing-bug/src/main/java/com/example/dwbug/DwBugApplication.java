package com.example.dwbug;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * 项目的两个图片路径问题
 * 1：	1：上传图片的路径         //图片存在服务器上的文件下       路径在yml中配置  upload: url: /home/bugApp/wing-bug/
 * 		2：获取图片的路径        //从服务器文件读取
 * 2： 1：前端滑动校验图片存储在服务器                  路径在 UserController中配置  String IMG_PATH = "file:"+"/home/bugApp/wing-bug/image/bg/*.*";
 * 		2 并从中获取
 *
 */


@EnableAspectJAutoProxy
@EnableTransactionManagement
@SpringBootApplication
@MapperScan("com.example.dwbug.dao")
public class DwBugApplication {

	public static void main(String[] args) {
		SpringApplication.run(DwBugApplication.class, args);
	}

}
