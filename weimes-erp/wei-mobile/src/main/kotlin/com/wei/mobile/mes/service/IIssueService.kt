package com.wei.mobile.mes.service

import com.wei.mes.wm.dto.IssueHeaderAndLineDTO
import com.wei.mobile.mes.dto.IssueRequestDTO


/**
 * 领料申请
 * @author wei
 * @date 2024/9/1 13:30
 */
interface IIssueService {
    /**
     * 领料申请
     */
    fun issue(issueRequestDTO: IssueRequestDTO)

    /**
     * 领料申请详情
     */
    fun getIssueDetail(query: IssueHeaderAndLineDTO): MutableList<IssueHeaderAndLineDTO>
}
