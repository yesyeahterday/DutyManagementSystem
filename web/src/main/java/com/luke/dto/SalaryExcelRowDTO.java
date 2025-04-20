package com.luke.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalaryExcelRowDTO {
    String realName;
    String username;
    BigDecimal totalSalary;
}
