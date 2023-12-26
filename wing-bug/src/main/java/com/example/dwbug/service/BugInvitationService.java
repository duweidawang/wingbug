package com.example.dwbug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dwbug.dto.GeneralInvitation;
import com.example.dwbug.dto.UpdateInvation;
import com.example.dwbug.entity.BugInvitationEntity;
import com.example.dwbug.utils.R;

import java.util.List;


/**
* @author 杜伟
* @description 针对表【bug_invitation】的数据库操作Service
* @createDate 2023-11-25 19:31:27
*/
public interface BugInvitationService extends IService<BugInvitationEntity> {

    R generalInvitation(GeneralInvitation generalInvitation);

    R deleteInvitation(List<Long> ids);

    R updateInvitation(UpdateInvation updateInvation);

    R queryListInvitation(Long state, String date, Long pageNum, Long pageSize,String invitation,Long expire);

    BugInvitationEntity queryInvitation(String invation);

    List<BugInvitationEntity> selectInvitation(Long num);
}
