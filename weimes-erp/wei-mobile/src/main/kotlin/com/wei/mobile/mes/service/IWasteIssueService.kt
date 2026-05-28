package com.wei.mobile.mes.service

import com.baomidou.mybatisplus.core.conditions.Wrapper
import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.core.toolkit.Constants
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.wei.mes.pro.domain.ProTask
import com.wei.mes.pro.dto.TaskDTO
import com.wei.mes.wm.dto.WasteHeaderAndLineDTO
import com.wei.mobile.mes.dto.WasteIssueRequestDTO
import org.apache.ibatis.annotations.Param


/**
 * 废料service
 * @author wei
 * @date 2025/3/9 22:45
 */
interface IWasteIssueService {

    /**
     * 新增废料
     */
    fun addWasteIssue(wasteIssueRequestDTO: WasteIssueRequestDTO, proTask: ProTask)

    /**
     * 查询任务，并统计废料次数
     * @param query 查询条件
     */
    fun getTaskListAndWasteIssueCount(page: IPage<TaskDTO>, @Param(Constants.WRAPPER) query: Wrapper<TaskDTO>): Page<TaskDTO>

    /**
     * 查询退料详情
     */
    fun getWasteIssueDetail(query: WasteHeaderAndLineDTO): MutableList<WasteHeaderAndLineDTO>

    /**
     * 查询退料详情列表
     */
    fun getWasteIssueDetailList(query: WasteHeaderAndLineDTO): MutableList<WasteHeaderAndLineDTO>
}
