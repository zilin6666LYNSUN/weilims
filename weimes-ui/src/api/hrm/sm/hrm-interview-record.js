import request from '@/utils/request'

// 查询面试记录列表
export function listHrmInterviewRecord(query) {
  return request({
    url: '/sm/hrm-interview-record/list',
    method: 'get',
    params: query
  })
}

// 查询面试记录详细
export function getHrmInterviewRecord(interviewRecordId) {
  return request({
    url: '/sm/hrm-interview-record/' + interviewRecordId,
    method: 'get'
  })
}

// 新增面试记录
export function addHrmInterviewRecord(data) {
  return request({
    url: '/sm/hrm-interview-record',
    method: 'post',
    data: data
  })
}

// 修改面试记录
export function updateHrmInterviewRecord(data) {
  return request({
    url: '/sm/hrm-interview-record',
    method: 'put',
    data: data
  })
}

// 删除面试记录
export function delHrmInterviewRecord(interviewRecordId) {
  return request({
    url: '/sm/hrm-interview-record/' + interviewRecordId,
    method: 'delete'
  })
}

// 邀请面试
export function interview(staffId) {
  return request({
    url: '/sm/hrm-interview-record/interview/' + staffId,
    method: 'put'
  })
}
