package com.luke.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName duty_reassign
 */
@TableName(value ="duty_reassign")


@Data
public class DutyReassign implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 原排班ID
     */
    @TableField(value = "original_schedule_id")
    private Long originalScheduleId;

    /**
     * 新值班员ID
     */
    @TableField(value = "new_user_id")
    private Long newUserId;

    /**
     * 替班时间
     */
    @TableField(value = "reassign_time")
    private Date reassignTime;

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
        DutyReassign other = (DutyReassign) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOriginalScheduleId() == null ? other.getOriginalScheduleId() == null : this.getOriginalScheduleId().equals(other.getOriginalScheduleId()))
            && (this.getNewUserId() == null ? other.getNewUserId() == null : this.getNewUserId().equals(other.getNewUserId()))
            && (this.getReassignTime() == null ? other.getReassignTime() == null : this.getReassignTime().equals(other.getReassignTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOriginalScheduleId() == null) ? 0 : getOriginalScheduleId().hashCode());
        result = prime * result + ((getNewUserId() == null) ? 0 : getNewUserId().hashCode());
        result = prime * result + ((getReassignTime() == null) ? 0 : getReassignTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", original_schedule_id=").append(originalScheduleId);
        sb.append(", new_user_id=").append(newUserId);
        sb.append(", reassign_time=").append(reassignTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}