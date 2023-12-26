package com.example.dwbug.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
class WebAppConfig extends WebMvcConfigurerAdapter {


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:static/");

                //如果要让添加的静态资源立即生效，则不能把它放到项目资源里面，应该放在外面，比如下面的目录
                //这样一旦静态资源添加到这个目录，则直接从该位置获取，而不是从编译的target位置获取
        registry.addResourceHandler("/images/**").addResourceLocations("file:"+"/www/wwwroot/loophole/wing-bug/images/");

    }
}
