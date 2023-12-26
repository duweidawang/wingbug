package com.example.dwbug.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dwbug.dao.CommentDao;
import com.example.dwbug.entity.CommentEntity;
import com.example.dwbug.entity.LoginUser;
import com.example.dwbug.service.CommentService;
import com.example.dwbug.service.LoopholeService;
import com.example.dwbug.service.UserService;
import com.example.dwbug.utils.R;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentDao, CommentEntity> implements CommentService {

    @Autowired
    UserService userService;
    @Autowired
    @Lazy
    LoopholeService loopholeService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;


   //保存漏洞评论
    @Transactional
    @Override
    public R saveComment(CommentEntity commentEntity) {
        // 获得登录人
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userid = loginUser.getUserEntity().getId();
        //判断userid和loopid是否正确
        if(userService.getById(userid)==null || loopholeService.getById(commentEntity.getLoopId())==null){
            return R.error("用户名或漏洞不存在");
        }

        //限制同一人在评论同一篇文章时的时间间隔为一分钟
        if(!Objects.isNull(stringRedisTemplate.opsForValue().get("commentTime"+userid+commentEntity.getLoopId()))){
            return R.error("发送频率太快");
        }


        stringRedisTemplate.opsForValue().set("commentTime"+userid+commentEntity.getLoopId(),"1",1, TimeUnit.MINUTES);
        commentEntity.setUserId(userid);
        commentEntity.setCreateTime(LocalDateTime.now());
        save(commentEntity);
        return R.ok();

    }
}