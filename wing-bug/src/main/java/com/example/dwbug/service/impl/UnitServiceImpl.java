package com.example.dwbug.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dwbug.dao.UnitDao;
import com.example.dwbug.entity.LoopholeEntity;
import com.example.dwbug.entity.UnitEntity;
import com.example.dwbug.service.LoopholeService;
import com.example.dwbug.service.UnitService;
import com.example.dwbug.utils.R;
//import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;


@Service("unitService")
public class UnitServiceImpl extends ServiceImpl<UnitDao, UnitEntity> implements UnitService {

    @Autowired
    @Lazy
    LoopholeService loopholeService;

    @Resource
    UnitDao unitDao;
    /**
     * 单位分页
     * @param params
     * @return
     */
    @Override
    public R returnList(Map<String,Object> params) {
        Page<UnitEntity> page = new Page<>();
        page.setSize(Integer.parseInt(String.valueOf(params.get("pageSize"))));
        page.setCurrent(Integer.parseInt(String.valueOf(params.get("pageNum"))));

        Page<UnitEntity> page1 = new Page<>();
        if(params.get("unitName")!=null) {
           page1 = query()
                    .like("unit_name", params.get("unitName"))
                    .page(page);
        }else{
            page1 =query().page(page);
        }


        return R.ok().put("data",page1);

    }

    /**
     *
     * @return
     */
    @Override
    public R unitRank(Long pageSize,Long pageNum) {
        //rank 前五
        QueryWrapper<UnitEntity> queryWrapper  =new QueryWrapper<>();
        queryWrapper.orderByDesc("unit_rank");

        IPage<UnitEntity> page = new Page<>();
        page.setSize(pageSize);
        page.setCurrent(pageNum);
        IPage<UnitEntity> page1 = page(page, queryWrapper);
        List<UnitEntity> unitEntities = page1.getRecords();


        //赋值漏洞总数

        List<Object> list1 = new ArrayList<>();


        unitEntities.stream().forEach(item->{
            Map<String,Object> map = new HashMap<>();
            map.put("unitName",item.getUnitName());
            Long id = item.getId();
            QueryWrapper<LoopholeEntity> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("unit_id",id).and(item1->{item1.eq("status","审核通过");});
            int count = loopholeService.count(queryWrapper1);
            map.put("loopNum",count);
            list1.add(map);
        });
        return R.ok().put("data",page);
    }

    @Override
    public R removeUnit(List<Long> deleteIds) {

        QueryWrapper<LoopholeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("unit_id",deleteIds);
        int count = loopholeService.count(queryWrapper);
        if(count!=0){
            return R.error(503,"有漏洞关联单位,无法删除");
        }

        this.removeByIds(deleteIds);
        return R.ok("删除成功");

    }


    /**
     * 查询单位，根据num
     * @param num
     * @return
     */
    @Override
    public List<UnitEntity> searchByNum(Long num) {

        List<UnitEntity> unitEntityList = unitDao.searchByNum(num);
        return unitEntityList;


    }


}