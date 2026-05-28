import request from '@/utils/request'

// 查询样品列表
export function listSample(query) {
  return request({
    url: '/lims/sample/list',
    method: 'get',
    params: query
  })
}

// 查询样品详情
export function getSample(sampleId) {
  return request({
    url: '/lims/sample/' + sampleId,
    method: 'get'
  })
}

// 新增样品
export function addSample(data) {
  return request({
    url: '/lims/sample',
    method: 'post',
    data: data
  })
}

// 修改样品
export function updateSample(data) {
  return request({
    url: '/lims/sample',
    method: 'put',
    data: data
  })
}

// 删除样品
export function delSample(sampleIds) {
  return request({
    url: '/lims/sample/' + sampleIds,
    method: 'delete'
  })
}

// 样品接收
export function receiveSample(sampleId) {
  return request({
    url: '/lims/sample/receive/' + sampleId,
    method: 'put'
  })
}

// 样品状态变更
export function changeSampleStatus(sampleId, toStatus, reason) {
  return request({
    url: '/lims/sample/status/' + sampleId + '/' + toStatus,
    method: 'put',
    params: { reason }
  })
}

// 查询样品检测项
export function listSampleTestItems(sampleId) {
  return request({
    url: '/lims/sample/testitem/list/' + sampleId,
    method: 'get'
  })
}

// 新增样品检测项
export function addSampleTestItem(data) {
  return request({
    url: '/lims/sample/testitem',
    method: 'post',
    data: data
  })
}

// 修改样品检测项
export function updateSampleTestItem(data) {
  return request({
    url: '/lims/sample/testitem',
    method: 'put',
    data: data
  })
}

// 删除样品检测项
export function delSampleTestItem(id) {
  return request({
    url: '/lims/sample/testitem/' + id,
    method: 'delete'
  })
}

// 查询留样列表
export function listRetainSample(query) {
  return request({
    url: '/lims/sample/retain/list',
    method: 'get',
    params: query
  })
}

// 新增留样
export function addRetainSample(data) {
  return request({
    url: '/lims/sample/retain',
    method: 'post',
    data: data
  })
}

// 修改留样
export function updateRetainSample(data) {
  return request({
    url: '/lims/sample/retain',
    method: 'put',
    data: data
  })
}

// 留样处置
export function disposeRetainSample(retainId, disposeType, disposeApprover) {
  return request({
    url: '/lims/sample/retain/dispose/' + retainId,
    method: 'put',
    params: { disposeType, disposeApprover }
  })
}

// 查询留样(按样品)
export function listRetainBySample(sampleId) {
  return request({
    url: '/lims/sample/retain/list/' + sampleId,
    method: 'get'
  })
}

// 查询样品处置列表
export function listSampleDispose(query) {
  return request({
    url: '/lims/sample/dispose/list',
    method: 'get',
    params: query
  })
}

// 新增样品处置
export function addSampleDispose(data) {
  return request({
    url: '/lims/sample/dispose',
    method: 'post',
    data: data
  })
}

// 处置审批
export function approveSampleDispose(disposeId, approvedBy) {
  return request({
    url: '/lims/sample/dispose/approve/' + disposeId,
    method: 'put',
    params: { approvedBy }
  })
}

// 查询处置(按样品)
export function listDisposeBySample(sampleId) {
  return request({
    url: '/lims/sample/dispose/list/' + sampleId,
    method: 'get'
  })
}
