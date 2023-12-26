package com.example.dwbug.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dwbug.dao.RecordDao;
import com.example.dwbug.dao.UserDao;
import com.example.dwbug.dto.ChangePasswordDto;
import com.example.dwbug.dto.RegisterDto;
import com.example.dwbug.dto.UpdateUserBymanagerDto;
import com.example.dwbug.entity.*;
import com.example.dwbug.service.BugInvitationService;
import com.example.dwbug.service.CommentService;
import com.example.dwbug.service.LoopholeService;
import com.example.dwbug.service.UserService;
import com.example.dwbug.utils.*;
import io.netty.handler.ssl.OpenSslNpnApplicationProtocolNegotiator;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.convert.RedisData;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Pattern;


@Slf4j
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService
{


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    CheckEmail checkEmail;

    @Autowired
    MailUtils mailUtils;           //163邮箱
    @Autowired
    MyMailService myMailService;        //qq邮箱

    @Autowired
    RecordDao recordDao;

    @Autowired
            @Lazy
    LoopholeService loopholeService;
    @Autowired
            @Lazy
    CommentService commentService;
    @Resource
    UserDao userDao;
    @Resource
    BugInvitationService bugInvitationService;



    /**
     * 登录  返回token值
     * @param userEntity
     * @return
     */
    @Override
    public R login(UserEntity userEntity) {





        if(!Objects.isNull(stringRedisTemplate.opsForValue().get("freezeTime"+userEntity.getUsername()))){
            stringRedisTemplate.delete("passwordTime"+userEntity.getUsername());
            return R.error("输入密码错误超过三次,账户冻结一分钟");
        }


        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userEntity.getUsername(),userEntity.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误");
        }
        //使用userid生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUserEntity().getId().toString();

        String jwt = jwtutils.getJwtToken(userId);
        UserEntity userEntity1 = loginUser.getUserEntity();

        //authenticate存入redis

        stringRedisTemplate.opsForValue().set("LOGIN_USER_KEY:" + userId, JSONUtil.toJsonStr(loginUser));
        stringRedisTemplate.expire("LOGIN_USER_KEY:" + userId,30L, TimeUnit.MINUTES);


        //把token响应给前端
        Map<String,Object> map = new HashMap<>();
        map.put("token",jwt);
        map.put("role",loginUser.getUserEntity().getRole());
        map.put("name",loginUser.getUserEntity().getName());
        //到这里 一定登录成功
        //登录成功需要删除用户记录的登录次数
        stringRedisTemplate.delete("passwordTime"+userEntity.getUsername());


        return R.ok().put("data",map);

    }

    /**
     * 退出登录
     * @return
     */
    @Override
    public R logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userid = loginUser.getUserEntity().getId();
        Boolean delete = stringRedisTemplate.delete("LOGIN_USER_KEY:" + userid);

        return R.ok("退出成功");

    }

    /**
     * 返回用户中心信息
     * @return
     */
    @Override
    public R userMsg(Long id) {
        //根据id是否有值判断 是否查看别人信息
        if(id!=null){
            UserEntity otherMsg = getById(id);
            otherMsg.setPassword("");
            return R.ok().put("data",otherMsg);
        }
        //查看登陆者自己
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userid = loginUser.getUserEntity().getId();

        UserEntity ourMsg = getById(userid);
        ourMsg.setPassword("");
        ourMsg.setUsername("");

        return R.ok().put("data",ourMsg);

    }

    /**
     * 更改用户信息
     * @param userEntity
     * @return
     */
    @Override
    public R updateUser(UserEntity userEntity) {
        //获取Id
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userid = loginUser.getUserEntity().getId();
        userEntity.setId(userid);
        //执行更新
        boolean b = updateById(userEntity);
        if(b){
            return R.ok().put("data","");
        }
        else return R.error();

    }

    /**
     *新增管理员
     * @param userEntity
     * @return
     */
    @Override
    public R createManagetUser(UserEntity userEntity) {
        //判断是否只能是qq邮箱与网易邮箱
        String username = userEntity.getUsername();
        if(!(checkEmail.Check163Mail(username) || checkEmail.CheckqqMail(username))){
            return R.error(10001,"新增邮箱格式错误");
        }

        //获取Id
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userid = loginUser.getUserEntity().getId();
        Long role1 = loginUser.getUserEntity().getRole();

        //获取新增role
        Long role = userEntity.getRole();
        if(role1==0){
            if(role==1 || role==2){
                userEntity.setCreatedBy(userid);
                String encode = passwordEncoder.encode(userEntity.getPassword());
                userEntity.setPassword(encode);
                save(userEntity);
            }
            else{
                return R.error("新增角色错误").put("data","");
            }
        }
        else if(role1==1){
            if(role==2){
                userEntity.setCreatedBy(userid);
                String encode = passwordEncoder.encode(userEntity.getPassword());
                userEntity.setPassword(encode);
                save(userEntity);
            }
            else{
                return R.error("新增角色错误").put("data","");
            }

        }
        return R.ok().put("data","");

    }


    /**
     * 返回邀请码
     * @return
     */
    @Override
    public R invitationCode() {
        //生成验证码，存入redis
        String s = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set("INVITATION_CODE:"+s,s,5L,TimeUnit.MINUTES);
        return R.ok("邀请码生成成功，有效期五分钟").put("data",s);
    }


    /**
     * 注册流程
     * @param registerDto
     * @return
     */

    @Transactional
    @Override
    public R registerUser(RegisterDto registerDto) {
        String username = registerDto.getUsername();
        //判断是否只能是qq邮箱与网易邮箱
        if(!(checkEmail.Check163Mail(username) || checkEmail.CheckqqMail(username))){
            return R.error(10001,"新增邮箱格式错误");
        }


//        //先根据邀请码查看 redis中 是否存在
//        String invitationCode = registerDto.getInvitationCode();
//        String redisKey ="INVITATION_CODE:"+invitationCode;
//        String code = stringRedisTemplate.opsForValue().get(redisKey);
//        //使用后删除邀请码
//        stringRedisTemplate.delete(redisKey);
//
//        if(!StringUtils.hasText(code)){
//            return R.error("验证码失效").put("data","");
//        }

        BugInvitationEntity bugInvitationEntity = bugInvitationService.queryInvitation(registerDto.getInvitationCode());
        if(bugInvitationEntity==null){
            return R.error(501,"验证码不存在");
        }
        if(bugInvitationEntity.getExpireTime().isBefore(LocalDateTime.now())){
            return R.error(501,"验证码已过期");
        }
        //如果当前的状态是未使用，就改为已使用
        if(bugInvitationEntity.getInvitationState()==0)
        {
            bugInvitationEntity.setInvitationState(1L);
            bugInvitationService.updateById(bugInvitationEntity);
        }

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(registerDto,userEntity,"invitationCode");
        //默认注册为普通用户
        userEntity.setRole(2L);
        //对密码加密存储
        String encode = passwordEncoder.encode(userEntity.getPassword());
        userEntity.setPassword(encode);
        save(userEntity);

        return R.ok("注册成功").put("data","");


    }

    /**
     * 返回验证码 并存入redis
     * @return
     */
    @Override
    public R getCode() {
        //获取Id
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userid = loginUser.getUserEntity().getId();
        UserEntity userEntity = loginUser.getUserEntity();

        String s = stringRedisTemplate.opsForValue().get("password_code:" + userid);
        if(StringUtils.hasText(s)){
            return R.ok("验证码已发送,请3分钟以后再试");
        }
        //生成随机数 4位
       long code = new Random().nextInt(9999);//生成随机数，最大为9999
        if(code < 1000){
            code = code + 1000;//保证随机数为4位数字
        }

        //判断当前是需要发送哪种邮件
        String username = loginUser.getUsername();
        //将验证码存入redis

        if(checkEmail.CheckqqMail(username)){
            myMailService.sendMail(username,"奉天漏洞库",code+"");
        }
        else {
            mailUtils.sendMail(username,"奉天漏洞库",code+"");
        }
        //将验证码存入redis
        stringRedisTemplate.opsForValue().set("password_code:"+userid,code+"",3L,TimeUnit.MINUTES);


        return R.ok("验证码发送成功");


    }

    /**
     * 修改密码
     * @param changePasswordDto
     * @return
     */
    @Override
    public R change(ChangePasswordDto changePasswordDto) {
        //获取Id
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userid = loginUser.getUserEntity().getId();

        //判断code是否有效
        String s = stringRedisTemplate.opsForValue().get("password_code:" + userid);
        if(!changePasswordDto.getCode().equals(s)){
            return R.error("验证码无效或失效");
        }

        //判断原密码是否相同
        String originPassword = changePasswordDto.getOriginPassword();
        if(!passwordEncoder.matches(originPassword,loginUser.getUserEntity().getPassword())){
            return  R.error("原密码有误");
        }

        //修改密码
        UserEntity userEntity = new UserEntity();
        userEntity.setPassword(passwordEncoder.encode(changePasswordDto.getPassword()));
        userEntity.setId(userid);
       this.updateById(userEntity);
        stringRedisTemplate.delete("password_code:" + userid);
        return R.ok("修改密码成功");
    }


    /**
     * 月排行
     * @param year
     * @param month
     * @param pageSize
     * @param pageNum
     * @return
     */
    @Override
    public R monthRank(Integer  year,Integer month,Long pageSize,Long pageNum) {

        QueryWrapper<RecordEntity> queryWrapper = new QueryWrapper<>();
        String dateKey;
        if(month<10){
            dateKey=year+"-0"+month+"-01"+" 00:00:00";
        }else{
            dateKey=year+"-"+month+"-01"+" 00:00:00";
        }

        LocalDateTime localDate = LocalDateTime.parse(dateKey, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        queryWrapper.eq("create_time",localDate).orderByDesc("record_rank");
        Page<RecordEntity> page = new Page<>();
        page.setSize(pageSize);
        page.setCurrent(pageNum);

        Page<RecordEntity> recordEntityPage = recordDao.selectPage(page, queryWrapper);
        recordEntityPage.getRecords().stream().forEach(item->{
            UserEntity byId = getById(item.getUserId());
            item.setId(item.getUserId());
            item.setName(byId.getName());
        });
        return R.ok().put("data",recordEntityPage);


    }


    /**
     * 对用户年总排行
     * @param year
     * @return
     */
    @Override
    public R yearRank(Integer year,Long pageSize,Long pageNum) {

        for(int i=1;i<=12;i++){
            String dateKey =year+"";
            if(i<10){
                dateKey+="-0"+i+"-01"+" 00:00:00";
            }
            else {
                dateKey+="-"+i+"-01"+" 00:00:00";
            }
            LocalDateTime localDate = LocalDateTime.parse(dateKey, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            QueryWrapper<RecordEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("create_time",localDate);
            List<RecordEntity> recordEntities = recordDao.selectList(queryWrapper);
            recordEntities.stream().forEach(item->{
                Long recordRank = item.getRecordRank();
                Long userId = item.getUserId();
                stringRedisTemplate.opsForZSet().incrementScore("yearRecord:"+year,userId+"",recordRank);
                stringRedisTemplate.expire("yearRecord:"+year,1L,TimeUnit.SECONDS);

            });


        }
        return  sortMonth("yearRecord:" + year, pageSize, pageNum);

    }

    /**
     *  用户总排行
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public R userRank(long pageNum, Long pageSize) {
        Page<UserEntity> iPage = new Page<>();
        iPage.setSize(pageSize);
        iPage.setCurrent(pageNum);

        Page<UserEntity> page = query().orderByDesc("user_rank").page(iPage);
        page.getRecords().stream().forEach(item->{
            item.setPassword("");
            item.setUsername("");
        });
        return R.ok().put("data",page);
    }

    /**
     * 超管查看返回用户列表
     * @return
     */
    @Override
    public IPage<UserEntity> userList(String name,String email, Long pageSize, Long pageNum) {
        IPage<UserEntity> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);

        //获取Id
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userid = loginUser.getUserEntity().getId();
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<UserEntity>();
        if(StringUtils.hasText(name)){
            queryWrapper.like("name",name);
        }
        if(StringUtils.hasText(email)){
            queryWrapper.like("username",email);
        }
        queryWrapper.ne("id", userid).orderByDesc("create_time");

        IPage<UserEntity> page1 = this.page(page, queryWrapper);
        page1.getRecords().stream().forEach(i->{
            i.setPassword("");

        });
        return page1;


    }


    /**
     * 删除用户
     * @param id
     * @return
     */
    @Value("${upload.url}")
    public String IMAGE_UPLOAD_DIR;
    @Transactional
    @Override
    public R deleteUser(List<Long> ids) {


        List<LoopholeEntity> author = loopholeService.list(new QueryWrapper<LoopholeEntity>().in("author", ids));

        if (author.size()!=0 ){
            for (LoopholeEntity l : author) {
                if("审核通过".equals(l.getStatus())){
                    return R.error("用户关联有审核通过的漏洞,无法删除");
                }
            }
        }

        //循环删除
        for(int id=0;id<ids.size();id++) {

            UserEntity byId = getById(ids.get(id));
            if(Objects.isNull(byId)){
                return R.error(501,"删除账户不存在");
            }
            String filename = byId.getAvator();


            if (StringUtils.hasText(filename) && !filename.equals("https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png")) {
                int i = filename.lastIndexOf("/images");
                String substring = filename.substring(i);
                File file = new File(IMAGE_UPLOAD_DIR, substring);
                if (file.isDirectory()) {
                    return R.error("删除用户头像的文件名称错误");
                }
                FileUtil.del(file);
            }
        }

        //删除评论
        commentService.removeByIds(ids);
        //删除用户的增加rank的月记录
        recordDao.deleteBatchIds(ids);
        this.removeByIds(ids);




        return R.ok("删除成功");


    }






    /**
     * 超管更改用户信息
     * @param updateUserBymanagerDto
     * @return
     */

    @Transactional
    @Override
    public R updateByManger(UpdateUserBymanagerDto updateUserBymanagerDto) {
        String username = updateUserBymanagerDto.getUsername();
        //判断是否只能是qq邮箱与网易邮箱
        if(!(checkEmail.Check163Mail(username) || checkEmail.CheckqqMail(username))){
            return R.error(10001,"新增邮箱格式错误");
        }

        //校验密码

        if (! "".equals(updateUserBymanagerDto.getPassword())){
            String  regexp = "[a-zA-Z0-9]+$";
            if(!updateUserBymanagerDto.getPassword().matches(regexp)){
                return R.error(10001, "密码只能为字母或数字");
            }
            if(updateUserBymanagerDto.getPassword().length()<6 || updateUserBymanagerDto.getPassword().length()>15){
                return R.error(10001,"密码长度应该在6到15");
            }

        }


        //查询username
        UserEntity username1 = this.getOne(new QueryWrapper<UserEntity>().eq("username", username));
        if(username1==null)
        {
            return R.error("用户名不存在");
        }

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(updateUserBymanagerDto,userEntity);

        //超管更新信息后把用户下线
        stringRedisTemplate.delete("LOGIN_USER_KEY:"+username1.getId());


        userEntity.setId(username1.getId());
        if(userEntity.getPassword().equals("")){
            userEntity.setPassword(null);

        }else {
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        }

        this.updateById(userEntity);
        return R.ok("更改成功");
    }

    /**
     * 根据num导出数据
     * @param num
     * @return
     */
    @Override
    public List<UserEntity> serachUsers(Long num) {
        List<UserEntity> list = userDao.searchByNum(num);
        return list;
    }


    /**
     *对月  年排序
     * @param key
     * @return
     */
    public R sortMonth(String key,Long pageSize,Long pageNum){

        Map<String,Object> map = new HashMap<>();

        //根据key查询
        Set<String> range = stringRedisTemplate.opsForZSet().range(key, 0, -1);


        List<Long> ids = range.stream().map(Long::valueOf).collect(Collectors.toList());

        //根据得到的value 查询score
        for(Long id:ids){
            Double score = stringRedisTemplate.opsForZSet().score(key, id+"");
            map.put(id+"",score);
        }
        if(ids.size()==0){
            return R.error(503,"暂无信息");
        }

        //再封装名字等数据
        List<UserEntity> userEntities=new ArrayList<>();
        List<Map<String,Object>> list =new ArrayList<>();

        for (Long i:ids){
           UserEntity byId = getById(i);
           Map<String, Object> map1 = new HashMap<>();
//           map1.put("userId",byId.getId());
            map1.put("id",byId.getId());
           map1.put("name", byId.getName());
           map1.put("recordRank", map.get(byId.getId()+""));

           list.add(map1);
       }

        //对list分页返回
        Collections.reverse(list);
        Map<String,Object> map2 = ListPage.startPage(list, pageNum.intValue(), pageSize.intValue());

        return R.ok().put("data",map2);

    }


    /**
     * 用于给login中 校验验证码
     */
    public  R verifyImageCode(Integer moveLength,String stringKey) {

        Double dMoveLength = Double.valueOf(moveLength);
        R resultMap;
        try {
            //从redis或者reqest中获取 横向移动的距离
            String s = stringRedisTemplate.opsForValue().get("xwidth:" + stringKey);
            stringRedisTemplate.delete("xwidth:" + stringKey);

            int xWidth = Integer.parseInt(s);

            if (xWidth < 1) {
                resultMap = R.error(503,"验证过期，请重试");
            } else {
                // 设置允许的偏差值
                if (Math.abs((int) xWidth - dMoveLength) > 10) {
                    resultMap = R.error(503,"验证不通过");
                } else {
                    resultMap = R.ok("验证通过");
                }
            }
        } catch (Exception e) {
            resultMap = R.ok("验证失败，请重试");
        }
        return resultMap;
    }





}