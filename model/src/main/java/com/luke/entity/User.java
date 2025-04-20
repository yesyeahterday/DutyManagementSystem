package com.luke.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 用户ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学号，也是登录账号
     */
    @TableField(value = "username")
    private String username;

    /**
     * 加密密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 真实姓名
     */
    @TableField(value = "real_name")
    private String realName;

    /**
     * 性别
     */
    @TableField(value = "gender")
    private String gender;

    /**
     * 年级
     */
    @TableField(value = "grade")
    private String grade;

    /**
     * 联系电话
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 所住楼栋
     */
    @TableField(value = "dorm")
    private Long dorm;

    /**
     * 宿舍号
     */
    @TableField(value = "dorm_number")
    private String dormNumber;

    /**
     * 职位
     */
    @TableField(value = "position")
    private String position;

    /**
     * 负责片区/楼栋
     */
    @TableField(value = "managed_zone")
    private String managedZone;

    /**
     * 在职状态(0-离职 1-在职)
     */
    @TableField(value = "is_active")
    private Integer isActive;

    /*
     *角色权限
     */
    @TableField(value = "role")
    private String role;

    @TableField(value = "email_address")
    private String emailAddress;

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
        User other = (User) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUsername() == null ? other.getUsername() == null : this.getUsername().equals(other.getUsername()))
            && (this.getPassword() == null ? other.getPassword() == null : this.getPassword().equals(other.getPassword()))
            && (this.getRealName() == null ? other.getRealName() == null : this.getRealName().equals(other.getRealName()))
            && (this.getGender() == null ? other.getGender() == null : this.getGender().equals(other.getGender()))
            && (this.getGrade() == null ? other.getGrade() == null : this.getGrade().equals(other.getGrade()))
            && (this.getPhone() == null ? other.getPhone() == null : this.getPhone().equals(other.getPhone()))
            && (this.getDorm() == null ? other.getDorm() == null : this.getDorm().equals(other.getDorm()))
            && (this.getDormNumber() == null ? other.getDormNumber() == null : this.getDormNumber().equals(other.getDormNumber()))
            && (this.getPosition() == null ? other.getPosition() == null : this.getPosition().equals(other.getPosition()))
            && (this.getManagedZone() == null ? other.getManagedZone() == null : this.getManagedZone().equals(other.getManagedZone()))
            && (this.getIsActive() == null ? other.getIsActive() == null : this.getIsActive().equals(other.getIsActive()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUsername() == null) ? 0 : getUsername().hashCode());
        result = prime * result + ((getPassword() == null) ? 0 : getPassword().hashCode());
        result = prime * result + ((getRealName() == null) ? 0 : getRealName().hashCode());
        result = prime * result + ((getGender() == null) ? 0 : getGender().hashCode());
        result = prime * result + ((getGrade() == null) ? 0 : getGrade().hashCode());
        result = prime * result + ((getPhone() == null) ? 0 : getPhone().hashCode());
        result = prime * result + ((getDorm() == null) ? 0 : getDorm().hashCode());
        result = prime * result + ((getDormNumber() == null) ? 0 : getDormNumber().hashCode());
        result = prime * result + ((getPosition() == null) ? 0 : getPosition().hashCode());
        result = prime * result + ((getManagedZone() == null) ? 0 : getManagedZone().hashCode());
        result = prime * result + ((getIsActive() == null) ? 0 : getIsActive().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", username=").append(username);
        sb.append(", password=").append(password);
        sb.append(", real_name=").append(realName);
        sb.append(", gender=").append(gender);
        sb.append(", grade=").append(grade);
        sb.append(", phone=").append(phone);
        sb.append(", dorm=").append(dorm);
        sb.append(", dorm_number=").append(dormNumber);
        sb.append(", position=").append(position);
        sb.append(", managed_zone=").append(managedZone);
        sb.append(", is_active=").append(isActive);
        sb.append(",role=").append(role);
        sb.append(",email_address=").append(emailAddress);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}