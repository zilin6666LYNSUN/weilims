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


// 微生物检测记录（限度/无菌/菌种鉴定）
export function listMicroTestRecord(query) {
  return request({ url: '/lims/test/micro/list', method: 'get', params: query })
}
export function listMicroTestRecordByOrder(testOrderId) {
  return request({ url: '/lims/test/micro/list/order/' + testOrderId, method: 'get' })
}
export function getMicroTestRecord(recordId) {
  return request({ url: '/lims/test/micro/' + recordId, method: 'get' })
}
export function addMicroTestRecord(data) {
  return request({ url: '/lims/test/micro', method: 'post', data: data })
}
export function updateMicroTestRecord(data) {
  return request({ url: '/lims/test/micro', method: 'put', data: data })
}
export function recordMicroTestResult(data) {
  return request({ url: '/lims/test/micro/result', method: 'put', data: data })
}
export function delMicroTestRecord(recordIds) {
  return request({ url: '/lims/test/micro/' + recordIds, method: 'delete' })
}
