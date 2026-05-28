import request from '@/utils/request'

// 查询检验订单列表
export function listTestOrder(query) {
  return request({
    url: '/lims/test/order/list',
    method: 'get',
    params: query
  })
}

// 查询检验订单详情
export function getTestOrder(testOrderId) {
  return request({
    url: '/lims/test/order/' + testOrderId,
    method: 'get'
  })
}

// 新增检验订单
export function addTestOrder(data) {
  return request({
    url: '/lims/test/order',
    method: 'post',
    data: data
  })
}

// 修改检验订单
export function updateTestOrder(data) {
  return request({
    url: '/lims/test/order',
    method: 'put',
    data: data
  })
}

// 删除检验订单
export function delTestOrder(testOrderIds) {
  return request({
    url: '/lims/test/order/' + testOrderIds,
    method: 'delete'
  })
}

// 提交检验订单
export function submitTestOrder(testOrderId) {
  return request({
    url: '/lims/test/order/submit/' + testOrderId,
    method: 'put'
  })
}

// 检验复核
export function reviewTestOrder(testOrderId, reviewResult, comment) {
  return request({
    url: '/lims/test/order/review/' + testOrderId,
    method: 'put',
    params: { reviewResult, comment }
  })
}

// 检验批准
export function approveTestOrder(testOrderId, approveResult, comment) {
  return request({
    url: '/lims/test/order/approve/' + testOrderId,
    method: 'put',
    params: { approveResult, comment }
  })
}

// 查询检验结果
export function getTestResults(testOrderId) {
  return request({
    url: '/lims/test/result/order/' + testOrderId,
    method: 'get'
  })
}

// 录入检验结果
export function saveTestResult(data) {
  return request({
    url: '/lims/test/result',
    method: 'post',
    data: data
  })
}

// 批量录入检验结果
export function batchSaveTestResult(data) {
  return request({
    url: '/lims/test/result/batch',
    method: 'post',
    data: data
  })
}
