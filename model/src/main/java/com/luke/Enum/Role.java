package com.luke.Enum;

public enum Role {
    // 枚举命名规范：ROLE_ 前缀 + 大写角色名
    ROLE_LEADER("总负责人"),
    ROLE_ZONEKEPPER("片区负责人"),
    ROLE_GENERAL("普通值班员");


    private final String description;

    Role(String description) {
        this.description = description;
    }

    // 获取 Spring Security 需要的权限字符串
    public String getAuthority() {
        return this.name(); // 直接返回枚举名称（包含ROLE_前缀）
    }

    public String getDescription() {
        return description;
    }
}