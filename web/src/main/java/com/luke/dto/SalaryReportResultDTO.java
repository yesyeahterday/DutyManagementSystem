package com.luke.dto;

import lombok.Data;

import java.util.List;

@Data
public class SalaryReportResultDTO {
    private List<SalaryExcelRowDTO> salaryList;
    private List<DutyDetailRowDTO> dutyDetails;
}
