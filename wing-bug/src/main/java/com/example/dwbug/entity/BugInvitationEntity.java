package com.example.dwbug.entity;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 
 * @TableName bug_invitation
 */
@TableName(value ="bug_invitation")
@Data
public class BugInvitationEntity implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long invitationId;

    /**
     * 
     */
    private String invitation;

    /**
     * 不可使用
     */
    private Long invitationState;

    /**
     * 
     */
    private String notes;

    /**
     * 
     */

    private LocalDateTime expireTime;

    /**
     * 
     */


    private LocalDateTime createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}