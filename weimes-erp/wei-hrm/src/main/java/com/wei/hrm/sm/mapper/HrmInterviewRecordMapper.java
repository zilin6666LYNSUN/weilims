package com.wei.hrm.sm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.wei.hrm.sm.domain.HrmInterviewRecord;
import com.wei.hrm.sm.dto.InterviewRecordDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 面试记录Mapper接口
 *
 * @author wei
 * @date 2024-09-15
 */
@Mapper
public interface HrmInterviewRecordMapper extends BaseMapper<HrmInterviewRecord> {

    Page<InterviewRecordDTO> pageGroupByStaff(@Param("query") HrmInterviewRecord query,@Param("statusList") List<Integer> statusList);
}
