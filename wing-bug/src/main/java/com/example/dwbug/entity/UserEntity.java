package com.example.dwbug.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

import javax.validation.constraints.Email;

/**
 * 
 * 
 * @author weisheng
 * @email sunlightcs@gmail.com
 * @date 2023-08-25 13:24:36
 */
@Data
@TableName("bug_user")
public class UserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 
	 */
	@Email
	private String username;
	/**
	 * 
	 */
	private String password;
	/**
	 * 
	 */
	private String name;
	/**
	 * 
	 */
	private String description;
	/**
	 * 
	 */
	private String avator;
	/**
	 * 
	 */
	private Long userRank;
	/**
	 * 
	 */
	private Long bugNum;
	/**
	 * 
	 */
	private Long approved;
	/**
	 *
	 */
	private Long role;
	/**
	 * 
	 */
	private Long createdBy;
	/**
	 * 
	 */
	private Date createTime;

	/**
	 *
	 */
	private Long deleteId;




}
