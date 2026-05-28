package com.wei.mobile.mes.dto

import com.wei.mes.pro.domain.ProFeedback
import com.wei.mes.pro.domain.ProTask


/**
 *
 * @author wei
 * @date 2024/8/8 14:31
 */
class TaskAndFeedbackDTO(val task: ProTask, val feedbackList: MutableList<ProFeedback>)
