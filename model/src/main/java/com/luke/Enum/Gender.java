package com.luke.Enum;

public enum Gender {
    MALE(0,"男"),
    FEMALE(1,"女");

    private final int code;
    private final String gender;

    Gender(int code, String gender) {
        this.code = code;
        this.gender = gender;
    }
    public int getCode() {
        return code;
    }
    public String getGender() {
        return gender;
    }
}
