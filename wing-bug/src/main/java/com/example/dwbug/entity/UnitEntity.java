package com.example.dwbug.entity;



import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 
 * 
 * @author weisheng
 * @email sunlightcs@gmail.com
 * @date 2023-08-25 13:24:35
 */
@Data
@TableName("bug_unit")
public class UnitEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 
	 */
	@Length(min = 1,max = 25 ,message = "单位名字过长或空缺")
	private String unitName;
	/**
	 * 
	 */
	private LocalDateTime createTime;

	private Long unitRank;



}
