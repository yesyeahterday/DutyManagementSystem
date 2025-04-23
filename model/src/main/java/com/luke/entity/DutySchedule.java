package com.luke.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName duty_schedule
 */
@TableName(value ="duty_schedule")
@Data
public class DutySchedule implements Serializable {
    /**
     * 排班ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 楼栋ID
     */
    @TableField(value = "building_id")
    private Long buildingId;

    @TableField(value = "zone")
    private String zone;
    /**
     * 值班日期
     */
    @TableField(value = "duty_date")
    private Date dutyDate;

    /**
     * 工资倍数
     */
    @TableField(value = "salary_ratio")
    private BigDecimal salaryRatio;

    /**
     * 值班员ID，不是学号
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 状态(0-正常 1-请假中(leave) 2-已替班(relay) 3-待分配(empty))
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 乐观锁版本
     */
    @TableField(value = "version")
    @Version
    private Integer version;

    /**
     * 历史标记
     */
    @TableField(value = "history_flag")
    private Integer historyFlag;

    @TableField(value = "is_deleted")
    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        DutySchedule other = (DutySchedule) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getBuildingId() == null ? other.getBuildingId() == null : this.getBuildingId().equals(other.getBuildingId()))
            && (this.getDutyDate() == null ? other.getDutyDate() == null : this.getDutyDate().equals(other.getDutyDate()))
            && (this.getSalaryRatio() == null ? other.getSalaryRatio() == null : this.getSalaryRatio().equals(other.getSalaryRatio()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()))
            && (this.getHistoryFlag() == null ? other.getHistoryFlag() == null : this.getHistoryFlag().equals(other.getHistoryFlag()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getBuildingId() == null) ? 0 : getBuildingId().hashCode());
        result = prime * result + ((getDutyDate() == null) ? 0 : getDutyDate().hashCode());
        result = prime * result + ((getSalaryRatio() == null) ? 0 : getSalaryRatio().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + ((getHistoryFlag() == null) ? 0 : getHistoryFlag().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", building_id=").append(buildingId);
        sb.append(", duty_date=").append(dutyDate);
        sb.append(", salary_ratio=").append(salaryRatio);
        sb.append(", user_id=").append(userId);
        sb.append(", status=").append(status);
        sb.append(", version=").append(version);
        sb.append(", history_flag=").append(historyFlag);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}