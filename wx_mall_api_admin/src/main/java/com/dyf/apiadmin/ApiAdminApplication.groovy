package com.dyf.apiadmin

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@MapperScan("com.dyf.*")
@ComponentScan("com.dyf.*")
class ApiAdminApplication {
    static void main(String[] args) {
        SpringApplication.run(ApiAdminApplication.class)
    }
}
