package com.dyf.wxapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.dyf.*")
@MapperScan("com.dyf.*")
public class WxApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(WxApiApplication.class);
    }
}
