package com.wei.mobile.mes.controller

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.wei.common.annotation.RepeatSubmit
import com.wei.common.constant.MsgConstants
import com.wei.common.constant.UserConstants
import com.wei.common.core.controller.BaseController
import com.wei.common.core.domain.AjaxResult
import com.wei.common.core.page.TableDataInfo
import com.wei.common.enums.mes.StatisticsTypeEnum
import com.wei.common.exception.BusinessException
import com.wei.common.utils.SecurityUtils
import com.wei.common.utils.StringUtils
import com.wei.mes.pro.domain.ProTask
import com.wei.mes.pro.dto.TaskDTO
import com.wei.mes.pro.service.IProTaskService
import com.wei.mes.wm.dto.IssueHeaderAndLineDTO
import com.wei.mobile.common.ktextend.requireNotNullOrBlank
import com.wei.mobile.common.ktextend.requireNotNullOrPositive
import com.wei.mobile.mes.dto.IssueRequestDTO
import com.wei.mobile.mes.service.IIssueService
import io.swagger.annotations.ApiOperation
import jakarta.annotation.Resource
import org.springframework.web.bind.annotation.*


/**
 * 领料
 * @author wei
 * @date 2024/8/22 18:29
 */
@RestController
@RequestMapping(UserConstants.MOBILE_PATH + "/issue")
class IssueController : BaseController() {

    @Resource
    lateinit var proTaskService: IProTaskService

    @Resource
    lateinit var issueService: IIssueService

    /**
     * 查询任务列表
     */
    @ApiOperation("查询领料列表")
    @GetMapping("/list")
    fun getTaskList(task: ProTask): TableDataInfo {
        val page = getMPPage(TaskDTO())
        val queryWrapper = QueryWrapper<TaskDTO>()
        queryWrapper.likeRight(StringUtils.isNotBlank(task.taskName), "task_name", task.taskName)
        queryWrapper.eq("task_user_id", SecurityUtils.getUserId())
        return getDataTableWithPage(
            this.proTaskService.getWorkOrderGroupAndSelectTypeCount(
                page,
                queryWrapper,
                StatisticsTypeEnum.ISSUED_QUANTITY
            )
        )
    }

    @ApiOperation("领料申请")
    @RepeatSubmit
    @PostMapping
    fun issue(@RequestBody issueRequestDTO: IssueRequestDTO): AjaxResult {
        // 参数校验
        checkParam(issueRequestDTO)
        // 领料申请
        this.issueService.issue(issueRequestDTO)
        return AjaxResult.success()
    }

    @ApiOperation("领料申请详情")
    @GetMapping("/getIssueDetail")
    fun getIssueDetail(query: IssueHeaderAndLineDTO): AjaxResult {
        query.workorderCode.requireNotNullOrBlank()
        query.taskId.requireNotNullOrPositive()
        return AjaxResult.success(
            this.issueService.getIssueDetail(query)
        )
    }

    /**
     * 参数校验
     */
    private fun checkParam(issueRequestDTO: IssueRequestDTO) {
        issueRequestDTO.taskId.requireNotNullOrPositive()
        issueRequestDTO.workorderCode.requireNotNullOrBlank()
        issueRequestDTO.workorderId.requireNotNullOrPositive()
        val task = this.proTaskService.lambdaQuery()
            .eq(ProTask::getTaskId, issueRequestDTO.taskId).one() ?: throw BusinessException(MsgConstants.NO_OPERATION_AUTH)
        // 如果查询到的任务和当前登录用户不匹配，会认为没有领料权限
        if (!task.taskUserId.equals(SecurityUtils.getUserId())) {
            throw BusinessException(MsgConstants.NO_OPERATION_AUTH)
        }
        if (issueRequestDTO.issueLineList.isEmpty()) {
            throw BusinessException(MsgConstants.SELECT_AT_ADD_ONE)
        }
    }
}
