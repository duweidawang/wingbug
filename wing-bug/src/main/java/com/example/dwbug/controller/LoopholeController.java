package com.example.dwbug.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dwbug.annotation.LogAnnotation;
import com.example.dwbug.dto.CreateLoopholeDto;
import com.example.dwbug.dto.ModifyLoophole;
import com.example.dwbug.entity.LoopholeEntity;
import com.example.dwbug.service.LoopholeService;
import com.example.dwbug.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * 
 *
 * @author weisheng
 * @email sunlightcs@gmail.com
 * @date 2023-08-25 13:24:36
 */
@RestController
@RequestMapping("api/bug")
public class LoopholeController {
    @Autowired
    private LoopholeService loopholeService;

    /**
     * 列表
     */
    @GetMapping ("/list")
    public R list(@RequestParam(value = "pageNum", defaultValue ="1") Long pageNum,
                  @RequestParam(value= "pageSize",defaultValue = "10") Long pageSize,
                  @RequestParam Map<String,Object> map){
      return loopholeService.queryList(pageNum,pageSize,map);

    }


    /**
     * 在审核提交之前修改信息
     * @param createLoopholeDto
     *
     * @return
     */

    @PutMapping("/update")
    @LogAnnotation(operation = "在审核提交之前修改了信息")
    public R updateLoop(@Valid @RequestBody CreateLoopholeDto createLoopholeDto){

        return  loopholeService.updateLoop(createLoopholeDto);

    }

    /**
     * 返回所有待审核的漏洞      只有超管和管理员有
     * @return
     */
    @PreAuthorize("hasAnyAuthority('bug:user:seachloop')")
    @GetMapping("/process/list")

    public R queryProcess(@RequestParam("pageNum")Long pageNum,@RequestParam("pageSize") long pageSize){
        return loopholeService.queryProcess(pageNum,pageSize);
    }

    /**
     * 审核上传
     * @param loopholeEntity
     * @return
     */

    @PreAuthorize("hasAnyAuthority('bug:user:loopprocess')")
    @PostMapping("process")
    @LogAnnotation(operation = "进行了审核")
    public R process(@RequestBody LoopholeEntity loopholeEntity){

        if("审核未通过".equals(loopholeEntity.getStatus())){
            if(!Objects.isNull(loopholeEntity.getLoopRank())){
                return R.error (10001,"审核未通过不能设置rank");
            }
        }else {
            Long loopRank = loopholeEntity.getLoopRank();
            if (loopRank < 0) {
                return R.error(10001, "rank值不能为负数");
            }

        }
        return loopholeService.process(loopholeEntity);
    }

    /**新增漏洞
     * 保存
     */
    @LogAnnotation(operation = "添加一个漏洞")
    @PostMapping("/save")
    public R save(@Valid @RequestBody CreateLoopholeDto createLoopholeDto){

		return  loopholeService.saveLoophole(createLoopholeDto);

    }


    /**超管才可以
     * 删除
     */
    @PreAuthorize("hasAnyAuthority('bug:user:loopDelete')")
    @DeleteMapping("/delete")
    @LogAnnotation(operation = "删除一个漏洞")
    public R delete( @RequestParam("ids")  Long ids){

		loopholeService.removeLoop(ids);
        return R.ok();
    }

    /**
     *返回用户提交的漏洞
     * @param pageSize
     * @param pageNum
     * @return
     */
    @GetMapping("/loopList")
    public R returnUserLoop(@RequestParam("pageSize") Long pageSize,@RequestParam("pageNum") Long pageNum){
       return loopholeService.returnUserLoop(pageSize,pageNum);
    }

    /**
     * 返回漏洞详细信息
     * @param id
     * @return
     */
    @GetMapping("/detail")
    public R loopDetail(@RequestParam("id") Long id){
        System.out.println(id);
      return   loopholeService.loopDetail(id);

    }


    /**
     * 超管修改以通过漏洞的信息
     * @param modifyLoophole
     * @return
     */
    @PreAuthorize("hasAnyAuthority('bug:user:edit')")
    @PutMapping("/edit/loophole")
    @LogAnnotation(operation = "超管修改通过漏洞信息")
    public  R editLoophole( @Valid @RequestBody ModifyLoophole modifyLoophole){

       return loopholeService.editloophole(modifyLoophole);






    }


}
