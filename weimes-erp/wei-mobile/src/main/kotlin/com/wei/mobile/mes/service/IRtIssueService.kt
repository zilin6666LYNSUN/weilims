package com.wei.mobile.mes.service

import com.baomidou.mybatisplus.core.conditions.Wrapper
import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.core.toolkit.Constants
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.wei.mes.pro.domain.ProTask
import com.wei.mes.pro.dto.TaskDTO
import com.wei.mes.wm.dto.RtIssueHeaderAndLineDTO
import com.wei.mobile.mes.dto.RtIssueRequestDTO
import org.apache.ibatis.annotations.Param


/**
 * 退料service
 * @author wei
 * @date 2025/3/9 22:45
 */
interface IRtIssueService {

    /**
     * 新增退料
     */
    fun rtIssue(rtIssueRequestDTO: RtIssueRequestDTO, proTask: ProTask)

    /**
     * 查询任务，并统计退料次数
     * @param query 查询条件
     */
    fun getTaskListAndRtIssueCount(page: IPage<TaskDTO>, @Param(Constants.WRAPPER) query: Wrapper<TaskDTO>): Page<TaskDTO>

    /**
     * 查询退料详情
     */
    fun getRtIssueDetail(query: RtIssueHeaderAndLineDTO): MutableList<RtIssueHeaderAndLineDTO>

    /**
     * 查询退料详情列表
     */
    fun getRtIssueDetailList(query: RtIssueHeaderAndLineDTO): MutableList<RtIssueHeaderAndLineDTO>
}
