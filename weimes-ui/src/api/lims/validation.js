import request from '@/utils/request'

// 查询校验规则列表
export function listValidationRule(query) {
  return request({
    url: '/lims/report/validation/list',
    method: 'get',
    params: query
  })
}

// 查询校验规则详情
export function getValidationRule(ruleId) {
  return request({
    url: '/lims/report/validation/' + ruleId,
    method: 'get'
  })
}

// 新增校验规则
export function addValidationRule(data) {
  return request({
    url: '/lims/report/validation',
    method: 'post',
    data: data
  })
}

// 修改校验规则
export function updateValidationRule(data) {
  return request({
    url: '/lims/report/validation',
    method: 'put',
    data: data
  })
}

// 删除校验规则
export function delValidationRule(ruleIds) {
  return request({
    url: '/lims/report/validation/' + ruleIds,
    method: 'delete'
  })
}

// 执行数据校验
export function executeValidation(testOrderId) {
  return request({
    url: '/lims/report/validation/validate/' + testOrderId,
    method: 'post'
  })
}

// 查询校验日志
export function getValidationLogs(testOrderId) {
  return request({
    url: '/lims/report/validation/logs/' + testOrderId,
    method: 'get'
  })
}

// 检查是否全部通过
export function checkValidation(testOrderId) {
  return request({
    url: '/lims/report/validation/check/' + testOrderId,
    method: 'get'
  })
}
