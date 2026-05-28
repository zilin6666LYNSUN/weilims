package com.wei.mobile.mes.controller

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.wei.common.constant.MsgConstants
import com.wei.common.constant.UserConstants
import com.wei.common.core.controller.BaseController
import com.wei.common.core.domain.AjaxResult
import com.wei.common.core.page.TableDataInfo
import com.wei.common.exception.BusinessException
import com.wei.common.utils.SecurityUtils
import com.wei.common.utils.StringUtils
import com.wei.mes.pro.domain.ProTask
import com.wei.mes.pro.dto.TaskDTO
import com.wei.mes.pro.service.IProTaskService
import com.wei.mes.wm.dto.WasteHeaderAndLineDTO
import com.wei.mobile.common.ktextend.requireNotNullOrBlank
import com.wei.mobile.common.ktextend.requireNotNullOrPositive
import com.wei.mobile.mes.dto.WasteIssueRequestDTO
import com.wei.mobile.mes.service.IWasteIssueService
import io.swagger.annotations.ApiOperation
import jakarta.annotation.Resource
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal


/**
 * 废料controller
 * @author wei
 * @date 2025/3/20 09:40
 */
@RestController
@RequestMapping(UserConstants.MOBILE_PATH + "/wasteIssue")
class WasterIssueController : BaseController() {
    @Resource
    lateinit var wasteIssueService: IWasteIssueService

    @Resource
    lateinit var proTaskService: IProTaskService

    /**
     * 新增退料
     */
    @ApiOperation("新增退料")
    @PostMapping
    fun addWasteIssue(@RequestBody wasteIssueRequestDTO: WasteIssueRequestDTO): AjaxResult {
        // 参数校验
        val task = checkParam(wasteIssueRequestDTO)
        this.wasteIssueService.addWasteIssue(wasteIssueRequestDTO, task)
        return AjaxResult.success()
    }

    /**
     * 查询退料详情
     */
    @ApiOperation("查询废料详情")
    @GetMapping("/getWasteIssueDetail")
    fun getWasteIssueDetail(query: WasteHeaderAndLineDTO): AjaxResult {
        query.workorderCode.requireNotNullOrBlank()
        query.taskId.requireNotNullOrPositive()
        return AjaxResult.success(
            this.wasteIssueService.getWasteIssueDetail(query)
        )
    }

    /**
     * 查询退料详情
     */
    @ApiOperation("查询废料详情列表")
    @GetMapping("/getWasteIssueDetailList")
    fun getRtIssueDetailList(query: WasteHeaderAndLineDTO): AjaxResult {
        query.workorderCode.requireNotNullOrBlank()
        query.taskId.requireNotNullOrPositive()
        return AjaxResult.success(
            this.wasteIssueService.getWasteIssueDetailList(query)
        )
    }


    /**
     * 查询任务列表
     */
    @ApiOperation("查询报工列表")
    @GetMapping("/list")
    fun getTaskList(task: ProTask): TableDataInfo {
        val page = getMPPage(TaskDTO())
        val queryWrapper = QueryWrapper<TaskDTO>()
        queryWrapper.likeRight(StringUtils.isNotBlank(task.taskName), "task_name", task.taskName)
        queryWrapper.eq("task_user_id", SecurityUtils.getUserId())
        return getDataTableWithPage(
            this.wasteIssueService.getTaskListAndWasteIssueCount(page, queryWrapper)
        )
    }

    /**
     * 参数校验
     */
    private fun checkParam(wasteIssueRequestDTO: WasteIssueRequestDTO): ProTask {
        wasteIssueRequestDTO.taskId.requireNotNullOrPositive()
        wasteIssueRequestDTO.workorderCode.requireNotNullOrBlank()
        wasteIssueRequestDTO.workorderId.requireNotNullOrPositive()
        // 查询领料单
        val task = this.proTaskService.lambdaQuery()
            .eq(ProTask::getTaskId, wasteIssueRequestDTO.taskId).one() ?: throw BusinessException(MsgConstants.NO_OPERATION_AUTH)
        // 如果查询到的任务和当前登录用户不匹配，会认为没有领料权限
        if (!task.taskUserId.equals(SecurityUtils.getUserId())) {
            throw BusinessException(MsgConstants.NO_OPERATION_AUTH)
        }
        if (wasteIssueRequestDTO.issueLineList.isEmpty()) {
            throw BusinessException(MsgConstants.PARAM_ERROR)
        }
        if (wasteIssueRequestDTO.issueLineList.all { (it.quantityWaste ?: BigDecimal(0)) == BigDecimal(0) }) {
            throw BusinessException("所有退料数量都为 0，请检查数据")
        }
        return task
    }
}
