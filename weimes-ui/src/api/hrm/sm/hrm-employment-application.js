import request from '@/utils/request'

// 查询面试记录列表
export function listHrmEmploymentApplication(query) {
  return request({
    url: '/sm/hrm-employment-application/list',
    method: 'get',
    params: query
  })
}

// 查询入职审核列表
export function listAudit(query) {
  return request({
    url: '/sm/hrm-employment-application/listAudit',
    method: 'get',
    params: query
  })
}

// 入职申请
export function employmentApplication(data) {
  return request({
    url: '/sm/hrm-employment-application',
    method: 'put',
    data: data
  })
}

// 入职申请通过
export function pass(data) {
  return request({
    url: '/sm/hrm-employment-application/pass',
    method: 'put',
    data: data
  })
}

// 入职申请拒绝
export function refuse(data) {
  return request({
    url: '/sm/hrm-employment-application/refuse',
    method: 'put',
    data: data
  })
}

