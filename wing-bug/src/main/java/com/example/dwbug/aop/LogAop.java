package com.example.dwbug.aop;


import com.example.dwbug.annotation.LogAnnotation;
import com.example.dwbug.entity.LoginUser;
import com.example.dwbug.entity.UserLogEntity;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.Objects;


@Aspect
@Component
public class LogAop {

    @Value("${address.url}")
    String ADDRESS_SITE;

    @After("@annotation(logAnnotation)")
    public void userLogInteceptor(JoinPoint joinPoint, LogAnnotation logAnnotation) throws Throwable {


        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
        int status = response.getStatus();
        if(status==200){

            UserLogEntity userLogEntity = new UserLogEntity();
            //填充操作
            String operation = logAnnotation.operation();
            userLogEntity.setOperation(operation);
            //获取登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(!Objects.isNull(authentication)){
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();

                String username = loginUser.getUserEntity().getUsername();
                userLogEntity.setUserName(username);
            }else{
                userLogEntity.setUserName(joinPoint.getArgs()[0].toString());
            }


            //获取请求参数
            // 获取所有请求参数的名称
//            if("GET".equals(request.getMethod())){
//                Enumeration<String> parameterNames = request.getParameterNames();
//                StringBuffer parameter = new StringBuffer();
//                while (parameterNames.hasMoreElements()) {
//                    String parameterName = parameterNames.nextElement();
//                    String parameterValue = request.getParameter(parameterName);
//                    parameter.append("请求参数"+parameterName+" : "+ parameterValue );
//                }
//                userLogEntity.setDetails(parameter);
//            }


            StringBuffer parameter = new StringBuffer();
            Object[] args = joinPoint.getArgs();
            for(Object i : args){
               parameter.append(i.toString());
            }
            userLogEntity.setDetails(parameter);


            //
            userLogEntity.setTimestamp(LocalDateTime.now());


            System.out.println(userLogEntity);

            //创建文件并写入
            try {


                File file = new File(ADDRESS_SITE);
                //不存在则创建
                if (!file.exists()) {
                    file.mkdirs();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(ADDRESS_SITE + "/" + LocalDate.now() + ".txt", true);
                fileOutputStream.write(userLogEntity.toString().getBytes());
                fileOutputStream.write('\r');
                fileOutputStream.close();
            }
            catch (Exception e){

            }finally {

            }

        }




    }
}
