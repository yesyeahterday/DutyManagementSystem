package com.luke.service;

import com.luke.dto.DutyDetailRowDTO;
import com.luke.dto.SalaryExcelRowDTO;
import com.luke.entity.DutySchedule;
import com.luke.result.Result;

import java.util.List;

public interface SalaryService {
    List<SalaryExcelRowDTO> generateSalaryReport(List<DutySchedule> scheduleList);

    Result getScheduleList(String startDate, String endDate, String username);


    List<DutyDetailRowDTO> generateDutyDetailRows(List<DutySchedule> scheduleList);
}
