package com.example.dwbug.service.impl;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dwbug.dao.BugInvitationDao;
import com.example.dwbug.dto.GeneralInvitation;
import com.example.dwbug.dto.UpdateInvation;
import com.example.dwbug.entity.BugInvitationEntity;
import com.example.dwbug.entity.UnitEntity;
import com.example.dwbug.service.BugInvitationService;
import com.example.dwbug.utils.R;
import org.springframework.stereotype.Service;

import java.lang.invoke.LambdaConversionException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
* @author 杜伟
* @description 针对表【bug_invitation】的数据库操作Service实现
* @createDate 2023-11-25 19:31:27
*/
@Service
public class BugInvitationServiceImpl extends ServiceImpl<BugInvitationDao, BugInvitationEntity>
    implements BugInvitationService {




    /**
     * 生成邀请码
     * @param
     * @param
     * @return
     */
    @Override
    public R generalInvitation(GeneralInvitation generalInvitation) {

        if(generalInvitation==null || generalInvitation.getNum()<0 || generalInvitation.getDate()<0 || generalInvitation.getNote().length()>200)
        {
            return R.error(10001,"参数校验失败");
        }


        List<BugInvitationEntity> bugInvitationEntities = new ArrayList<>();

        for(int i=0;i<generalInvitation.getNum();i++){
            //生成邀请码十位
            String randomString = UUID.randomUUID().toString();
            randomString = randomString.replace("-", "");
            Random random  = new Random();
            int num = random.nextInt(randomString.length()-10);
            String substring = randomString.substring(num, num + 10);

            BugInvitationEntity bugInvitationEntity = new BugInvitationEntity();
            bugInvitationEntity.setInvitation(substring);
            bugInvitationEntity.setCreateTime(LocalDateTime.now());
            //具体加多少天
            bugInvitationEntity.setExpireTime(LocalDateTime.now().plusDays(generalInvitation.getDate()));
            bugInvitationEntity.setNotes(generalInvitation.getNote());
            bugInvitationEntities.add(bugInvitationEntity);
        }


        this.saveBatch(bugInvitationEntities);
        return R.ok("生成成功");

    }

    /**
     * 批量删除
     * @param ids
     * @return
     */

    @Override
    public R deleteInvitation(List<Long> ids) {
      if(ids.size()<0){
          return R.error(10001,"请求参数异常");

      }
      this.removeByIds(ids);
      return R.ok("删除成功");
    }

    /**
     * 根新时间
     * @param
     * @return
     */
    @Override
    public R updateInvitation(UpdateInvation updateInvation) {

        if(updateInvation==null){
            return R.error(10001,"参数异常");
        }

        BugInvitationEntity byId = this.getById(updateInvation.getId());
        LocalDateTime expireTime = this.baseMapper.searchTime(updateInvation.getId());

        if(!Objects.isNull(updateInvation.getDate()) && !Objects.isNull(updateInvation.getDatestate())){
            //说明要减
            if(updateInvation.getDatestate()==0){
                byId.setExpireTime(expireTime.minusDays(updateInvation.getDate()));

            }

            //说明要加
            if(updateInvation.getDatestate()==1){
                byId.setExpireTime(expireTime.plusDays(updateInvation.getDate()));

            }
        }


        //设置状态
//        if(!Objects.isNull(updateInvation.getState())){
//            if(updateInvation.getState()!=0 && updateInvation.getState()!=1){
//                return R.error(400,"参数异常");
//            }else{
//                byId.setInvitation_state(updateInvation.getState());
//            }
//        }

        byId.setInvitationId(updateInvation.getId());

        System.out.println(byId);
        this.updateById(byId);

        return R.ok("更改成功");
    }


    /**
     * 分页查询
     * @param
     * @param date
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public R queryListInvitation(Long state, String date, Long pageNum, Long pageSize,String invatation,Long expire) {
        if(pageNum<0 || pageSize<0){
            return R.error(10001,"参数异常");
        }
        QueryWrapper<BugInvitationEntity> queryWrapper = new QueryWrapper<>();
        if(state!=null){
            if(state!=0 && state !=1){
                return R.error(10001,"参数异常");
            }
            queryWrapper.eq("invitation_state",state);

        }
        if(!StringUtils.isBlank(invatation)){
            queryWrapper.like("invitation",invatation);
        }

        if(!StringUtils.isBlank(date)){
            LocalDateTime endTime = LocalDateTime.parse(date + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime startTime = LocalDateTime.parse(date + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryWrapper.between("expire_time",startTime,endTime);
        }

        if(!Objects.isNull(expire)) {
            if (expire != 0 && expire != 1) {
                return R.error(10001, "参数异常");
            }
            //查询过期数据
            if (expire == 1) {
                queryWrapper.le("expire_time",LocalDateTime.now());

            } else {
                queryWrapper.ge("expire_time",LocalDateTime.now());
            }
        }
        queryWrapper.orderByDesc("create_time");

        Page<BugInvitationEntity> page = new Page<>();
        page.setSize(Integer.parseInt(String.valueOf(pageSize)));
        page.setCurrent(Integer.parseInt(String.valueOf(pageNum)));

        Page<BugInvitationEntity> page1 = new Page<>();

        page1 = page(page,queryWrapper);

        return R.ok().put("data",page1);






    }

    /**
     * 根据邀请码查询
     * @param invation
     * @return
     */
    @Override
    public BugInvitationEntity queryInvitation(String invation) {
        QueryWrapper<BugInvitationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("invitation",invation);
        BugInvitationEntity one = getOne(queryWrapper);

        return one;
    }

    @Override
    public List<BugInvitationEntity> selectInvitation(Long num) {
       return this.baseMapper.selectInvitation(num);
    }
}




