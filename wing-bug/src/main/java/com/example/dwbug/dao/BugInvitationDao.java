
package com.example.dwbug.dao;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dwbug.entity.BugInvitationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;


/**
 * @author 杜伟
 * @description 针对表【bug_invitation】的数据库操作Mapper
 * @createDate 2023-11-25 19:31:27
 * @Entity generator.domain.BugInvitation
 */

@Mapper
public interface BugInvitationDao extends BaseMapper<BugInvitationEntity> {

    LocalDateTime searchTime(Long id);

    List<BugInvitationEntity> selectInvitation(Long sum);

}