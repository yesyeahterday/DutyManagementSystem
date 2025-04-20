package com.luke.Enum;

public enum Status {
    NORMAL(0,"正常值班"),
    LEAVE(1,"请假"),
    REASSIGN(2,"替班"),
    EMPTY(3,"待排班");

    private final int code;
    private final String description;

    private Status(int code, String description) {
        this.code = code;
        this.description = description;
    }
    public int getCode() {
        return code;
    }
    public String getDescription() {
        return description;
    }
}
