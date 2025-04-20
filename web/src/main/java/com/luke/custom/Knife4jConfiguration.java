package com.luke.custom;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI().info(
                new Info()
                        .title("周末值班管理系统API")
                        .version("1.0")
                        .description("周末值班管理系统API"));
    }

    @Bean
    public GroupedOpenApi dormAPI() {

        return GroupedOpenApi.builder().group("楼栋管理").
                pathsToMatch(
                        "/dorm/**"
                ).
                build();
    }

    @Bean
    public GroupedOpenApi leaveAPI() {

        return GroupedOpenApi.builder().group("替班管理").
                pathsToMatch(
                        "/leave/**"

                ).build();
    }
    @Bean
    public GroupedOpenApi notificationAPI() {
        return GroupedOpenApi.builder().group("消息管理").
                pathsToMatch(
                        "/notifications/**"

                ).build();
    }

    @Bean
    public GroupedOpenApi salaryAPI() {
        return GroupedOpenApi.builder().group("工资管理").
                pathsToMatch(
                        "/salary/**"
                ).build();
    }
    @Bean
    public GroupedOpenApi scheduleAPI() {
        return GroupedOpenApi.builder().group("排班管理").
                pathsToMatch(
                        "/schedule/**"
                ).build();
    }

    @Bean
    public GroupedOpenApi userAPI() {
        return GroupedOpenApi.builder().group("用户管理").
                pathsToMatch(
                        "/user/**"
                ).build();
    }

}
