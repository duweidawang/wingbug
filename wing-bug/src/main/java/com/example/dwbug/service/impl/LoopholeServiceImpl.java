package com.example.dwbug.service.impl;

import cn.hutool.db.dialect.impl.PhoenixDialect;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dwbug.dao.LoopholeDao;
import com.example.dwbug.dao.RecordDao;
import com.example.dwbug.dao.UnitDao;
import com.example.dwbug.dao.UserDao;
import com.example.dwbug.dto.*;
import com.example.dwbug.entity.*;
import com.example.dwbug.service.*;
import com.example.dwbug.utils.R;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Service("loopholeService")
public class LoopholeServiceImpl extends ServiceImpl<LoopholeDao, LoopholeEntity> implements LoopholeService {


        @Autowired
        LoopholeDao loopholeDao;
        @Autowired
        UserDao userDao;
        @Autowired
        UnitService unitService;
        @Autowired
        UserService userService;
        @Autowired
    CategoryService categoryService;
        @Autowired
    StringRedisTemplate stringRedisTemplate;
        @Autowired
    CommentService commentService;
        @Autowired
    RecordDao recordDao;

        @Autowired
    UnitDao unitDao;

    /**
     * 漏洞提交
     * @param createLoopholeDto
     */
    @Transactional
    @Override
    public R saveLoophole(CreateLoopholeDto createLoopholeDto) {
        //判断单位与分类是否存在
        Long unitId= createLoopholeDto.getUnitId();
        Long categoryId = createLoopholeDto.getCategoryId();

        CategoryEntity byId3 = categoryService.getById(categoryId);
        UnitEntity byId2 = unitService.getById(unitId);
        if(Objects.isNull(byId2) || Objects.isNull(byId3)){
            return R.error(503,"单位或分类不存在");

        }

        // 获得漏洞提交者
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userid = loginUser.getUserEntity().getId();

        //保存
        LoopholeEntity loopholeEntity = new LoopholeEntity();
        BeanUtils.copyProperties(createLoopholeDto,loopholeEntity);





        loopholeEntity.setAuthor(userid);
        loopholeEntity.setCreateTime(LocalDateTime.now());
        save(loopholeEntity);

        // 修改用户的漏洞提交数
        UserEntity byId = userService.getById(userid);
        byId.setBugNum(byId.getBugNum()+1);
        userService.updateById(byId);


        return R.ok();
    }


    /**
     *  查看漏洞列表  可多条件搜索
     * @param
     * @param map
     * @return
     */
    @Override
    public R queryList(Long pageNum,Long pageSize, Map<String, Object> map) {
        Long index = (pageNum-1)*pageSize;


        //封装数据
        LoopSeatchDto loopSeatchDto = saveMap(map);
        loopSeatchDto.setIndex(index);
        loopSeatchDto.setPageSize(pageSize);

        //查询author
          if(!StringUtils.isEmpty(map.get("author"))) {
              QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
              queryWrapper.like("name", map.get("author"));
              List<UserEntity> userEntities = userDao.selectList(queryWrapper);
              //
              if(userEntities.size()==0){
                  Map<String,Object> map1 = new HashMap<>();
                  map1.put("data",new ArrayList<>());
                  map1.put("total",0);
                  map1.put("pageSize",pageSize);
                  map1.put("pageNum",pageNum);
                  return R.ok().put("page",map1);

              }

              List<Long> list = new ArrayList<>();
              userEntities.stream().forEach(item -> {
                  Long id = item.getId();
                  list.add(id);
              });
              loopSeatchDto.setAuthorId(list);
          }

          //查询单位
        if(!StringUtils.isEmpty(map.get("unit"))) {
            QueryWrapper<UnitEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("unit_name", map.get("unit"));
            List<UnitEntity> unitEntities = unitDao.selectList(queryWrapper);
            //
            if(unitEntities.size()==0){
                Map<String,Object> map1 = new HashMap<>();
                map1.put("data",new ArrayList<>());
                map1.put("total",0);
                map1.put("pageSize",pageSize);
                map1.put("pageNum",pageNum);
                return R.ok().put("page",map1);

            }

            List<Long> list = new ArrayList<>();
            unitEntities.stream().forEach(item -> {
                Long id = item.getId();
                list.add(id);
            });
            loopSeatchDto.setUnitId(list);
        }


            List<LoopholeEntity> loopholeEntitie = loopholeDao.queryList(loopSeatchDto);
            if(loopholeEntitie.size()==0){
                Map<String,Object> map1 = new HashMap<>();
                map1.put("data",new ArrayList<>());
                map1.put("total",0);
                map1.put("pageSize",pageSize);
                map1.put("pageNum",pageNum);
                return R.ok().put("page",map1);
          }
            List<LoopholeListDto> loops = loopholeEntitie.stream().map(item -> {
                LoopholeListDto loopholeListDto = new LoopholeListDto();
                BeanUtils.copyProperties(item,loopholeListDto);

                Long author = item.getAuthor();
                UserEntity author1 = userService.getById(author);
                String name = author1.getName();
                loopholeListDto.setAuthorName(name);

                Long unitId = item.getUnitId();
                UnitEntity byId = unitService.getById(unitId);
                loopholeListDto.setUnitName(byId.getUnitName());

                return loopholeListDto;

            }).collect(Collectors.toList());


            Map<String, Object> map1 = new HashMap<>();
            map1.put("data", loops);
            //todo
            map1.put("total", loopholeDao.countList(loopSeatchDto));
//            map1.put("total", loopholeEntitie.size());
            map1.put("pageSize", pageSize);
            map1.put("pageNum", pageNum);

            return R.ok().put("page", map1);
        }




    /**
     * 在审核提交之前修改信息
     * @param createLoopholeDto
     *
     */
    @Transactional
    @Override
    public R updateLoop(CreateLoopholeDto createLoopholeDto) {
        // 获得登录人
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userid = loginUser.getUserEntity().getId();

        Long id = createLoopholeDto.getId();
        LoopholeEntity byId = getById(id);
        if(byId.getStatus().equals("审核通过")){
            return R.error("审核已通过，无法修改");

        }
        if(byId.getAuthor()!=userid){
            return R.error("修改的不是自己的");
        }
        //判断单位与分类是否存在
        Long unitId= createLoopholeDto.getUnitId();
        Long categoryId = createLoopholeDto.getCategoryId();
        CategoryEntity byId3 = categoryService.getById(categoryId);
        UnitEntity byId2 = unitService.getById(unitId);
        if(Objects.isNull(byId2) || Objects.isNull(byId3)){
            return R.error("单位或分类不存在");

        }

        //判断是否为审核未通过
        String status = byId.getStatus();
        if("审核未通过".equals(status)){
            LoopholeEntity loopholeEntity = new LoopholeEntity();
            BeanUtils.copyProperties(createLoopholeDto,loopholeEntity);
            loopholeEntity.setStatus("待审核");
            loopholeEntity.setUpdateTime(null);
            loopholeEntity.setLoopRank(0L);
            loopholeEntity.setComments("");
            updateById(loopholeEntity);
            return R.ok();
        }


        LoopholeEntity loopholeEntity = new LoopholeEntity();
        BeanUtils.copyProperties(createLoopholeDto,loopholeEntity);
        loopholeEntity.setId(id);
        updateById(loopholeEntity);
        return R.ok();
    }

    /**
     * 返回所有待审核的漏洞
     * @return
     */
    @Override
    public R queryProcess(Long pageNum,Long pageSize) {

        IPage<LoopholeEntity> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        QueryWrapper<LoopholeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status","待审核");
        IPage<LoopholeEntity> page1 = page(page, queryWrapper);
        List<LoopholeEntity> records = page1.getRecords();
        List<LoopAuthorDto> loops = records.stream().map(item -> {
            Long author = item.getAuthor();
            UserEntity author1 = userService.getById(author);
            String name = author1.getName();
            LoopAuthorDto loopAuthorDto = new LoopAuthorDto();
            BeanUtils.copyProperties(item, loopAuthorDto);
            loopAuthorDto.setAuthorName(name);
            return loopAuthorDto;

        }).collect(Collectors.toList());

        IPage<LoopAuthorDto> page2 = new Page<>();
        BeanUtils.copyProperties(page1,page2,"records");
        page2.setRecords(loops);

        return R.ok().put("data",page2);

    }

    /**
     * 审核上传
     * @param loopholeEntity
     * @return
     */
    @Transactional
    @Override
    public R process(LoopholeEntity loopholeEntity) {
        Long author = loopholeEntity.getAuthor();


        //判断单位与分类是否存在

        Long unitId= loopholeEntity.getUnitId();
        Long id1 = loopholeEntity.getId();


        LoopholeEntity loopholeEntity1 = loopholeDao.selectById(id1);
        if(loopholeEntity1.getAuthor()!=author){
            return R.error(10001,"作者暂不能修改");
        }


        Long categoryId = loopholeEntity.getCategoryId();
            CategoryEntity byId3 = categoryService.getById(categoryId);
            UnitEntity byId4 = unitService.getById(unitId);
            if(Objects.isNull(byId4) || Objects.isNull(byId3)){
                return R.error("单位或分类不存在");
            }
            //判断是否已经审核
            Long id = loopholeEntity.getId();
            LoopholeEntity byId5 = getById(id);
            if(!byId5.getStatus().equals("待审核")){
                return R.error("此漏洞已经审核");
            }


//        Date date =new Date();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String format = simpleDateFormat.format(date);
//        String key =format+"dayRnk";
//        stringRedisTemplate.opsForZSet().incrementScore("rankKey:"+key,byId1.getId()+"",loopholeEntity.getLoopRank());


        loopholeEntity.setUpdateTime(LocalDateTime.now());
        boolean b = updateById(loopholeEntity);


        if(b){
            if(loopholeEntity.getStatus().equals("审核通过")) {
                //记录单位的rank值
                UnitEntity byId = unitService.getById(loopholeEntity.getUnitId());
                byId.setUnitRank(byId.getUnitRank() + loopholeEntity.getLoopRank());
                unitService.updateById(byId);

                //记录用户的rank值
                UserEntity byId1 = userService.getById(loopholeEntity.getAuthor());
                byId1.setUserRank(byId1.getUserRank() + loopholeEntity.getLoopRank());

                //审核通过需要改变用户的漏洞通过数
                byId1.setApproved(byId1.getApproved()+1);
                userService.updateById(byId1);


                //保存新增记录

                QueryWrapper<RecordEntity> queryWrapper = new QueryWrapper<>();
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
                String format = simpleDateFormat.format(date) + "-01 00:00:00";
                LocalDateTime localDate = LocalDateTime.parse(format, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                queryWrapper.eq("user_id", byId1.getId()).eq("create_time", localDate);
                Integer integer = recordDao.selectCount(queryWrapper);
                //说明本月以后新增rank 则将之后的rank添加到这个rank之上
                if (integer!=0) {
                    RecordEntity recordEntity = recordDao.selectOne(queryWrapper);
                    recordEntity.setRecordRank(recordEntity.getRecordRank() + loopholeEntity.getLoopRank());
                    recordDao.updateById(recordEntity);
                }
                else {
                    //否则说明本月没有数据  那么就新增本月数据
                    Map<String, Object> map = new HashMap();
                    map.put("userId", byId1.getId());
                    map.put("rank", loopholeEntity.getLoopRank());
                    map.put("createTime", localDate);
                    recordDao.insert(map);
                }



            }
            return R.ok("审核成功");
        }
        return R.error("审核失败");


    }

    /**
     * 返回用户的漏洞
     * @param pageSize
     * @param pageNum
     * @return
     */
    @Override
    public R returnUserLoop(Long pageSize, Long pageNum) {
        // 获得登录人
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userid = loginUser.getUserEntity().getId();
        //
        IPage<LoopholeEntity> page= new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        QueryWrapper<LoopholeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("author",userid).orderByDesc("update_time");
        IPage<LoopholeEntity> page1 = page(page, queryWrapper);

        return R.ok().put("data",page1);

    }

    /**
     * 返回漏洞详细信息
     * @param id
     * @return
     */
    @Override
    public R loopDetail(Long id) {
        LoopholeEntity loop = getById(id);
        // 获得登录人
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userId = loginUser.getUserEntity().getId();
        Long role = loginUser.getUserEntity().getRole();
        Long visible = loop.getVisible();
        Long author = loop.getAuthor();
        //为普通用户且漏洞设置了不可见并且该漏洞不是自己的 则将内容隐藏
        if(userId!=author  && role==2 && visible==0){
            loop.setContent("");
        }

        Map<String,Object> map = new HashMap<>();
        map.put("loop",loop);

        //封装单位以及分类
        UnitEntity unit = unitService.getById(loop.getUnitId());
        CategoryEntity category = categoryService.getById(loop.getCategoryId());
        map.put("unit",unit);
        map.put("category",category);

        //封装评论
        QueryWrapper<CommentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("loop_id",id).orderByDesc("create_time");
        List<CommentEntity> comments = commentService.list(queryWrapper);
        List<CommentEntity> collect = comments.stream().map(item -> {
            UserEntity user = userService.getById(item.getUserId());
            item.setUserName(user.getName());
            item.setAvator(user.getAvator());
            return item;
        }).collect(Collectors.toList());
        map.put("comments",collect);


        return R.ok().put("data",map);

    }

    /**
     * 删除漏洞
     * @param ids
     */
    @Transactional
    @Override
    public void removeLoop(Long ids) {
        LoopholeEntity loophole = this.getById(ids);
        Long author = loophole.getAuthor();
        Long unitId = loophole.getUnitId();
        Long rank = loophole.getLoopRank();
        String status = loophole.getStatus();

        //单位rank
        UnitEntity unit = unitService.getById(unitId);
        unit.setUnitRank(unit.getUnitRank()-rank);
        unitService.updateById(unit);

        //用户rank  用户提交数   用户通过数
        UserEntity user = userService.getById(author);
        user.setUserRank(user.getUserRank()-rank);
        user.setBugNum(user.getBugNum()-1);
        if("审核通过".equals(status)){
            user.setApproved(user.getApproved()-1);
        }
        userService.updateById(user);

        //月记录
        if("审核通过".equals(status)){
            LocalDateTime updateTime = loophole.getUpdateTime();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String format = dateTimeFormatter.format(updateTime);
            String[] s = format.split(" ");
            String substring = s[0].substring(0, 7);
            String date = substring+"-01 00:00:00";
            RecordEntity records = recordDao.selectOne(new QueryWrapper<RecordEntity>().eq("create_time", date).eq("user_id",author));
            records.setRecordRank(records.getRecordRank()-rank);
            recordDao.updateById(records);
        }
        commentService.remove(new QueryWrapper<CommentEntity>().eq("loop_id",ids));

        //删除漏洞
        this.removeById(ids);

    }

    /**
     * 修改已通过的漏洞
     * @param modifyLoophole
     */

    @Transactional
    @Override
    public R editloophole(ModifyLoophole modifyLoophole) {

        Long unitId1 = modifyLoophole.getUnitId();
        Long categoryId = modifyLoophole.getCategoryId();
        CategoryEntity byId1 = categoryService.getById(categoryId);
        UnitEntity byId2 = unitService.getById(unitId1);
        if(Objects.isNull(byId1) && Objects.isNull(byId2)) {
            return R.error("单位或分类不存在");
        }
        //获取原来的旧值
        Long id = modifyLoophole.getId();
        LoopholeEntity byId = this.getById(id);
        Long unitId = byId.getUnitId();
        Long rank = byId.getLoopRank();
        Long author = byId.getAuthor();
        String status = byId.getStatus();
        LocalDateTime updateTime1 = byId.getUpdateTime();

        if("审核未通过".equals(modifyLoophole.getStatus())){

            if(!Objects.isNull(modifyLoophole.getLoopRank())) {
                return R.error(10001,"审核通过才能添加rank");
            }

            //改为审核未通过后，需要改变
            // 用户的rank    用户的漏洞通过数         月记录rank；  单位rank


            //单位rank
            UnitEntity unit = unitService.getById(unitId);
            unit.setUnitRank(unit.getUnitRank()-rank);
            unitService.updateById(unit);

            //用户rank    用户通过数
            UserEntity user = userService.getById(author);
            user.setUserRank(user.getUserRank()-rank);
            user.setApproved(user.getApproved()-1);
            userService.updateById(user);

            //月记录
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String format = dateTimeFormatter.format(updateTime1);
                String[] s = format.split(" ");
                String substring = s[0].substring(0, 7);
                String date = substring+"-01 00:00:00";
                RecordEntity records = recordDao.selectOne(new QueryWrapper<RecordEntity>().eq("create_time", date).eq("user_id",author));
                records.setRecordRank(records.getRecordRank()-rank);
                recordDao.updateById(records);
            //审核未通过应删除评论
            commentService.remove(new QueryWrapper<CommentEntity>().eq("loop_id",id));
            //修改漏洞信息

            LoopholeEntity loopholeEntity = new LoopholeEntity();
            BeanUtils.copyProperties(modifyLoophole,loopholeEntity);
            loopholeEntity.setLoopRank(0L);
            this.updateById(loopholeEntity);

            return R.ok("更改成功");
            }

        else {
            if(!modifyLoophole.getStatus().equals("审核通过")) {
                return R.error(10001,"只能为审核通过与未通过");
            }
            Long loopRank = modifyLoophole.getLoopRank();
            if(loopRank<0){
                return R.error(10001,"rank值不能为负数");
            }
            LoopholeEntity loopholeEntity = new LoopholeEntity();
            BeanUtils.copyProperties(modifyLoophole,loopholeEntity);
            //如果修改rank值，也要修改其他关联表 但rank又是一个新值，我需要先减掉以前的，在新增之后的
            //单位
            //倘若更换了单位 应该吧原单位的rank减少，然后 在新单位加上rank
            if(modifyLoophole.getUnitId()!=unitId){
                //新单位
                UnitEntity byId3 = unitService.getById(modifyLoophole.getUnitId());
                byId3.setUnitRank(byId3.getUnitRank()+rank);
                unitService.updateById(byId3);
                //旧单位
                UnitEntity byId4 = unitService.getById(unitId);
                byId4.setUnitRank(byId4.getUnitRank()-rank);
                unitService.updateById(byId4);
            }else{
                UnitEntity unitEntity = unitService.getById(unitId);
                unitEntity.setUnitRank(unitEntity.getUnitRank()-rank+modifyLoophole.getLoopRank());
                unitService.updateById(unitEntity);
            }

            UserEntity userEntity = userService.getById(author);
            userEntity.setUserRank(userEntity.getUserRank()-rank+modifyLoophole.getLoopRank());
            userService.updateById(userEntity);
            //记录rank
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String format = dateTimeFormatter.format(updateTime1);
            String[] s = format.split(" ");
            String substring = s[0].substring(0, 7);
            String date = substring+"-01 00:00:00";
            RecordEntity records = recordDao.selectOne(new QueryWrapper<RecordEntity>().eq("create_time", date).eq("user_id",author));
            records.setRecordRank(records.getRecordRank()-rank+modifyLoophole.getLoopRank());
            recordDao.updateById(records);






            this.updateById(loopholeEntity);
            return R.ok("更改成功");

        }


        }





    /**
     * 将map数据封装到loopSearchDto
     * @param map
     * @return
     */
    public LoopSeatchDto saveMap(Map<String,Object> map){
        LoopSeatchDto loopSeatchDto = new LoopSeatchDto();
        String  title = (String) map.get("title");
        if(StringUtils.hasText(title)) {
            loopSeatchDto.setTitle("%" + title + "%");
        }

       if(map.get("grade")!=null){
           if(StringUtils.hasText(map.get("grade").toString())){
               Long grade = Long .valueOf(String.valueOf(map.get("grade")));
               loopSeatchDto.setGrade(grade);
           }
       }

//        if(map.get("gradeStart")!=null) {
//            if (StringUtils.hasText(map.get("gradeStart").toString())) {
//                Long gradeStart = Long.valueOf(String.valueOf(map.get("gradeStart")));
//                loopSeatchDto.setGradeStart(gradeStart);
//                Long gradeEnd = Long.valueOf(String.valueOf(map.get("gradeEnd")));
//                loopSeatchDto.setGradeEnd(gradeEnd);
//            }
//        }

        if(map.get("endTime")!=null && map.get("startTime")!=null) {

            if (StringUtils.hasText(map.get("endTime").toString())) {
                LocalDateTime endTime = LocalDateTime.parse(map.get("endTime").toString() + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                LocalDateTime startTime = LocalDateTime.parse(map.get("startTime").toString() + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                loopSeatchDto.setStartTime(startTime);
                loopSeatchDto.setEndTime(endTime);
            }
        }

        if(map.get("visible")!=null){
            if(StringUtils.hasText(map.get("visible").toString())){
                Long visible = Long.valueOf(String.valueOf(map.get("visible")));
                loopSeatchDto.setVisible(visible);
            }
        }
        return  loopSeatchDto;

    }
}