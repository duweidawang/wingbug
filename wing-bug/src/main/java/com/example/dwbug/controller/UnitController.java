package com.example.dwbug.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;


import com.example.dwbug.annotation.LogAnnotation;
import com.example.dwbug.dto.DeleteIds;
import com.example.dwbug.entity.UnitEntity;
import com.example.dwbug.service.UnitService;
import com.example.dwbug.utils.R;
import org.apache.logging.log4j.core.config.plugins.PluginVisitorStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * 
 *
 * @author weisheng
 * @email sunlightcs@gmail.com
 * @date 2023-08-25 13:24:35
 */
@RestController
@RequestMapping("api/unit")
public class UnitController {
    @Autowired
    private UnitService unitService;

    /**
     * 单位获取
     */
    @PreAuthorize("hasAnyAuthority('bug:user:lookUnit')")
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        return unitService.returnList(params);
    }


    /**
     * 保存
     */
    @PreAuthorize("hasAnyAuthority('bug：user:creatUnit')")
    @PostMapping("/save")
    @LogAnnotation(operation = "新增单位")
    public R save(@Valid @RequestBody UnitEntity unitEntity){
        String unitName = unitEntity.getUnitName();
        if(0<unitName.length() && unitName.length()<25){
            UnitEntity unit = new UnitEntity();
            unit.setUnitName(unitName);

            unit.setCreateTime(LocalDateTime.now());
            unitService.save(unit);

            return R.ok();
        }
        else {
            throw  new InternalAuthenticationServiceException("单位为空或长度过长");
        }
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('bug:user:updateUnit')")
    @LogAnnotation(operation = "修改单位")
    public R update(@RequestBody Map map){
        Long id = Long.valueOf((int)map.get("id"));
        String unitName =(String) map.get("unitName");
        if(unitName.length()<=0 || unitName.length()>25){
            throw  new InternalAuthenticationServiceException("单位为空或长度过长");
        }
        UnitEntity unit= new UnitEntity();
        unit.setId(id);
        unit.setUnitName(unitName);
		unitService.updateById(unit);
        return R.ok();
    }
//
//    /**
//     * 删除
//     */
//    @DeleteMapping ("/delete")
//    @PreAuthorize("hasAnyAuthority('bug:user:deleteUnit')")
//    public R delete(@RequestParam("ids") Long ids){
//		return unitService.removeUnit(ids);
//
//    }

    /**
     * 删除单位
     * @param
     * @return
     */
    @DeleteMapping ("/deleteids")
    @PreAuthorize("hasAnyAuthority('bug:user:deleteUnit')")
    @LogAnnotation(operation = "删除单位")
    public R deleteIds(@RequestBody DeleteIds deleteIds){
        List<Long> ids = deleteIds.getIds();
        System.out.println(ids);
        return unitService.removeUnit(ids);



    }


    /**
     * 返回单位排行榜
     * @return
     */
    @GetMapping("/rank")
    public R unitRank(@RequestParam("pageSize") Long pageSize,@RequestParam("pageNum") Long pageNum){

        return unitService.unitRank(pageSize,pageNum);


    }


    @GetMapping("loophole/list")
    public R loopholeList(){
        List<UnitEntity> list = unitService.list();

        list.stream().forEach(unit->{
            unit.setUnitRank(null);
            unit.setCreateTime(null);
        });
        return R.ok().put("data",list);

    }






}
