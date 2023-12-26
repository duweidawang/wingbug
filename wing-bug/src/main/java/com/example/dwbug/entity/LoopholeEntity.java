package com.example.dwbug.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * 
 * 
 * @author weisheng
 * @email sunlightcs@gmail.com
 * @date 2023-08-25 13:24:36
 */
@Data
@TableName("bug_loophole")
public class LoopholeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 
	 */
	private String title;
	/**
	 * 
	 */
	private Long unitId;
	/**
	 * 
	 */
	private Long categoryId;
	/**
	 * 
	 */
	private String description;
	/**
	 * 富文本
	 */
	private String content;

	private Long author;
	/**
	 * 
	 */
	private Long visible;
	/**
	 * 
	 */

	private Long loopRank;
	/**
	 * 
	 */
	private Long grade;


	private String comments;
	/**
	 * 
	 */
	private String status;
	/**
	 * 
	 */
	private LocalDateTime createTime;
	/**
	 * 
	 */
	private LocalDateTime updateTime;

}
