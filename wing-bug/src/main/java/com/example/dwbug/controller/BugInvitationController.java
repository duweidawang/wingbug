package com.example.dwbug.controller;

import com.example.dwbug.annotation.LogAnnotation;
import com.example.dwbug.dto.DeleteIds;
import com.example.dwbug.dto.GeneralInvitation;
import com.example.dwbug.dto.UpdateInvation;
import com.example.dwbug.service.BugInvitationService;
import com.example.dwbug.utils.R;
import org.aspectj.lang.annotation.After;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/invitation")
public class BugInvitationController {

    @Resource
    BugInvitationService bugInvitationService;


    /**
     * 生成验证码
     *
     */
    @PreAuthorize("hasAnyAuthority('bug:user:invitation')")
    @LogAnnotation(operation = "生成邀请码")
    @PostMapping("/general")
    public R generalInvatition(@RequestBody GeneralInvitation generalInvitation){
        return bugInvitationService.generalInvitation(generalInvitation);

    }
    @PreAuthorize("hasAnyAuthority('bug:user:invitation')")
    @LogAnnotation(operation = "删除邀请码")
    @DeleteMapping("/delete")
    public R generalInvatition(@RequestBody DeleteIds deleteIds){
        return bugInvitationService.deleteInvitation(deleteIds.getIds());

    }

    /**
     * 在当前时间再新增多少天
     * @param
     * @param
     * @return
     */
    @PreAuthorize("hasAnyAuthority('bug:user:invitation')")
    @LogAnnotation(operation = "更新邀请码")
    @PutMapping("/update")
    public R generalInvatition(@RequestBody UpdateInvation updateInvation){
        return bugInvitationService.updateInvitation(updateInvation);

    }

    @PreAuthorize("hasAnyAuthority('bug:user:invitation')")
    @GetMapping("/list")
    public R generalInvatition(@RequestParam(value = "date",required = false) String date,
                               @RequestParam(value="state",required = false) Long state,
                               @RequestParam(value="invitation",required = false) String invitationCode,
                               @RequestParam(value="expire",required = false) Long expire,
                               @RequestParam("pageSize") Long pageSize,
                               @RequestParam("pageNum") Long pageNum ){
        return bugInvitationService.queryListInvitation(state,date,pageNum,pageSize,invitationCode,expire);

    }



}
