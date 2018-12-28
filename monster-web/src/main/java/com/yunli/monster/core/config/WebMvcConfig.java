package com.yunli.monster.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 作者：zhouchao
 * @version V1.0
 * @Title: WebMvcConfig.java
 * @Package com.unitop.scheduler.config
 * @Description: 定制mvc服务
 * @date 创建时间：2018年10月19日 下午5:43:21
 * @since JDK 1.8
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 自定义拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

    }

    /**
     * 静态资源访问
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

}
