package com.cimonhe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {


    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/goods/**").addResourceLocations("file:C:/second_hand_market/goods/");
        registry.addResourceHandler("/user/portrait/**").addResourceLocations("file:C:/second_hand_market/user/portrait/");
    }
}
