package com.example.dwbug.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.dwbug.annotation.LogAnnotation;
import com.example.dwbug.dto.*;
import com.example.dwbug.entity.UserEntity;
import com.example.dwbug.service.UserService;
import com.example.dwbug.utils.R;
import com.example.dwbug.utils.VerifyImageUtil;
import com.example.dwbug.validate.addUser;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;


/**
 * 
 *
 * @author weisheng
 * @email sunlightcs@gmail.com
 * @date 2023-08-25 13:24:36
 */
@Slf4j
@RestController
@RequestMapping("api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @LogAnnotation(operation = "返回一个名字")
    @PutMapping("/name")
    public String getUserNAme(@RequestBody String name, HttpServletRequest request) throws IOException {
        System.out.println(request.getMethod());

        return name;

    }





    /**
     * 返回用户列表
     * @return
     */

    @PreAuthorize("hasAnyAuthority('bug:user:user')")
    @GetMapping("/list")
    public R userList(@RequestParam(value = "name",required = false) String name, @RequestParam(value = "email",required = false) String email,
                      @RequestParam("pageSize") Long pageSize,
                      @RequestParam("pageNum") Long pageNum){
      IPage<UserEntity> list =  userService.userList(name,email,pageSize,pageNum);
        return R.ok().put("data",list);
    }

    /**
     * 删除用户
     * @param deleteIds
     * @return
     */
    @PreAuthorize("hasAnyAuthority('bug:user:user')")
    @DeleteMapping("/delete")
    @LogAnnotation(operation = "删除用户")
    public R deleteUser(@RequestBody() DeleteIds deleteIds){
        System.out.println(deleteIds.getIds());
       return userService.deleteUser(deleteIds.getIds());

    }





    /**'
     * 登录
     * @param userEntity
     * @return
     */
   @PostMapping("/login")
   @LogAnnotation(operation = "进行了登录")
   public R login(@Valid @RequestBody UserEntity userEntity){

       return userService.login(userEntity);

   }


    /**
     * 退出登录
     * @return
     */

   @GetMapping("/logout")
   @LogAnnotation(operation = "退出登录")
   public R logout(){
       return userService.logout();
   }


    /**
     * 用户中心
     * @return
     */

    @GetMapping("/query")
    public R userMsg(@RequestParam(value = "id",required = false) Long id){
        return userService.userMsg(id);
    }


    /**
     * 超管更改用户信息
     * @param updateUserBymanagerDto
     * @return
     */
    @PreAuthorize("hasAnyAuthority('bug:user:user')")
    @PutMapping("/updateUser")
    @LogAnnotation(operation = "超管更改用户信息")
    public R updateUser(@Valid @RequestBody UpdateUserBymanagerDto updateUserBymanagerDto){
        String password = updateUserBymanagerDto.getPassword();
        if(StringUtils.hasText(password)){
           if(password.length()<6 || password.length()>15){
               return R.error(10001,"密码长度应该在6到15");
           }

           String regexp = "[a-zA-Z0-9]+$";
           if(!password.matches(regexp)){
               return R.error(10001,"密码只能为字母或数字");
           }
        }

        return userService.updateByManger(updateUserBymanagerDto);

    }

    /**
     * 更改用户信息
     * @param updateUserDto
     * @return
     */

    @PutMapping("/update")
    @LogAnnotation(operation = "自己更改用户信息")
    public R updateUser(@Valid @RequestBody UpdateUserDto updateUserDto){
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(updateUserDto,userEntity);
        return userService.updateUser(userEntity);
    }

    /**
     * 创建角色
     * @param createUserDto
     * @return
     */
    @PreAuthorize("hasAnyAuthority('bug:user:saveManager','bug:user:saveOrdinary')")
    @PostMapping("/createUser")
    @LogAnnotation(operation = "超管创建了一个用户")
    public R createManageUser(@Valid @RequestBody CreateUserDto createUserDto) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(createUserDto,userEntity);

        return userService.createManagetUser(userEntity);
    }

//    /**
//     * 返回注册邀请码，超管才可以生成
//     * @return
//     */
//    @PreAuthorize("hasAnyAuthority('bug:user:invitation')")
//    @GetMapping("/invitation")
//    public R invitationCode(){
//        return userService.invitationCode();
//
//    }

    /**
     * 注册
     * @param registerDto
     * @return
     */
    @PostMapping("/sign")

    public R register(@Validated(addUser.class) @RequestBody RegisterDto registerDto){
        return userService.registerUser(registerDto);

    }

    /**
     * 修改密码获取验证码
     * @return
     */
    @GetMapping("/getCode")
    @LogAnnotation(operation = "获取改密验证码")
        public R getCode(){
        return userService.getCode();
        }

    /**
     * 修改密码
     * @param changePasswordDto
     * @return
     */
    @PutMapping("/change")
    @LogAnnotation(operation = "进行了密码修改")
    public R change(@Valid @RequestBody ChangePasswordDto changePasswordDto){
        return userService.change(changePasswordDto);

    }



    /**
     * 用户月排行榜
     * @param year
     * @param month
     * @param pageSize
     * @param pageNum
     * @return
     */
    @GetMapping("/monthRank")
    public R monthRank(@RequestParam("year") Integer  year,@RequestParam("month") Integer month,
                       @RequestParam("pageSize") Long pageSize, @RequestParam("pageNum") long pageNum
    ){
        return userService.monthRank(year,month,pageSize,pageNum);

    }

    /**
     * 用户年排行榜
     * @param year
     * @param pageSize
     * @param pageNum
     * @return
     */
    @GetMapping("/yearRank")
    public R yearRank(@RequestParam("year") Integer  year,@RequestParam("pageSize") Long pageSize, @RequestParam("pageNum") long pageNum){

        return userService.yearRank(year,pageSize,pageNum);
    }

    /**
     * 用户的总排行榜
     * @param pageSize
     * @param pageNum
     * @return
     */
    @GetMapping("/userRank")
    public R userRank(@RequestParam("pageSize") Long pageSize, @RequestParam("pageNum") long pageNum){
        return userService.userRank(pageNum,pageSize);

    }




    /**
     * 获取资源图片路径
     */
    public List<String> queryFileList(String path) {

        //获取容器资源解析器
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<String> filelist = new ArrayList<String>();
        // 获取远程服务器IP和端口
        try {
            //获取所有匹配的文件
            Resource[] resources = resolver.getResources(path);
            for (Resource resource : resources) {
                filelist.add(resource.getFile().getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filelist;
    }

    /**
     * 获取验证码
     *
     * VerifyImageUtil 可考虑 加强验证难度，比如【小红书】 背景图生成时 多抠图一次随机模板。
     *
     *
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getImageVerify")
    public R getImageVerifyCode() {
        //bg图
        String IMG_PATH = "file:"+"/www/wwwroot/loophole/wing-bug/image/bg/*.*";
        //String IMG_PATH = "image/bg/*.*";

        //模板图
        String TEMP_IMG_PATH = "file:"+"/www/wwwroot/loophole/wing-bug/image/fg/*.*";
        //String TEMP_IMG_PATH = "image/fg/*.*";

        // 读取背景图目录
        List<String> imgList = queryFileList(IMG_PATH);
        int randNum = new Random().nextInt(imgList.size());
        File targetFile = new File(imgList.get(randNum));

        List<String> tempimgList = queryFileList(TEMP_IMG_PATH);
        randNum = new Random().nextInt(tempimgList.size());
        File tempImgFile = new File(tempimgList.get(randNum));

        // 根据模板裁剪图片
        Map<String, Object> resultMap = null;
        try {
            log.info("sdfsfsd");
            resultMap = VerifyImageUtil.pictureTemplatesCut(tempImgFile, targetFile);
            log.info("sdfsfsdsdssss");
            int xWidth = (int) resultMap.get("xWidth");
            //缓存记录key，项目实现 redis 或者 requset seesion
            // Cached.set(key, xWidth);
            String string = UUID.randomUUID().toString();
            stringRedisTemplate.opsForValue().set("xwidth:"+string,xWidth+"",3L, TimeUnit.MINUTES);

//            re quest.setAttribute("xWidth",xWidth);
            // 移除map的滑动距离，不返回给前端
            resultMap.remove("xWidth");
            resultMap.put("uid",string);
            //resultMap 包含 背景图片和浮动图片
            return R.ok().put("data",resultMap);

        } catch (Exception e) {
            log.info(e.getMessage()+""+e.getCause());
            return R.error(503,"获取验证码失败");
        }
    }

    /**
     * 校验滑块拼图验证码
     *
     * moveLength 移动距离
     * @return BaseRestResult 返回类型
     * @Description: 生成图形验证码
     */
    @RequestMapping(value = "/verifyImageCode", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    public R verifyImageCode(@RequestBody Map<String,Object> map, HttpServletRequest request) {
        Integer moveLength=(Integer) map.get("moveLength");
        String stringKey = map.get("uid").toString();
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
            resultMap = R.error(503,"验证失败，请重试");
        }
        return resultMap;
    }








}
