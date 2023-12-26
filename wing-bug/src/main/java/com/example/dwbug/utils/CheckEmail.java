package com.example.dwbug.utils;


import org.springframework.stereotype.Component;

@Component
public class CheckEmail {
    public Boolean CheckqqMail(String mail) {
        String str = "^[1-9][0-9]{4,}@qq.com$";
        if (mail.matches(str)) {
            return true;
        } else {
            return false;
        }
    }

    public  Boolean Check163Mail(String mail) {
        String str = "^[1-9][0-9]{4,}@163.com$";
        if (mail.matches(str)) {
            return true;
        } else {
            return false;
        }
    }
}
