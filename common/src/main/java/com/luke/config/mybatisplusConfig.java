package com.luke.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.luke.mapper")
public class mybatisplusConfig {

}
