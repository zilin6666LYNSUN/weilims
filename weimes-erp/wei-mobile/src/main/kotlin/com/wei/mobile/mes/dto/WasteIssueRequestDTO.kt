package com.wei.mobile.mes.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.wei.mes.wm.domain.WmRtIssue
import com.wei.mes.wm.domain.WmRtIssueLine
import com.wei.mes.wm.domain.WmWasteLine


/**
 * 退料DTO
 * @author wei
 * @date 2025/3/16 20:26
 */
data class WasteIssueRequestDTO @JsonCreator constructor(
    @JsonProperty("issueLineList") val issueLineList: MutableList<WmWasteLine>
) : WmRtIssue()
