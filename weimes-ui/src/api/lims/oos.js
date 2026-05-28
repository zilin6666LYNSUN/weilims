import request from '@/utils/request'

// OOS/OOT调查
export function listInvestigation(query) {
  return request({ url: '/lims/oos/investigation/list', method: 'get', params: query })
}
export function getInvestigation(investigationId) {
  return request({ url: '/lims/oos/investigation/' + investigationId, method: 'get' })
}
export function addInvestigation(data) {
  return request({ url: '/lims/oos/investigation', method: 'post', data: data })
}
export function updateInvestigation(data) {
  return request({ url: '/lims/oos/investigation', method: 'put', data: data })
}
export function delInvestigation(investigationIds) {
  return request({ url: '/lims/oos/investigation/' + investigationIds, method: 'delete' })
}
export function submitInvestigation(investigationId) {
  return request({ url: '/lims/oos/investigation/submit/' + investigationId, method: 'put' })
}
export function reviewInvestigation(investigationId) {
  return request({ url: '/lims/oos/investigation/review/' + investigationId, method: 'put' })
}
export function approveInvestigation(investigationId) {
  return request({ url: '/lims/oos/investigation/approve/' + investigationId, method: 'put' })
}

// 调查阶段记录
export function listPhaseRecords(investigationId) {
  return request({ url: '/lims/oos/phase/list/' + investigationId, method: 'get' })
}
export function addPhaseRecord(data) {
  return request({ url: '/lims/oos/phase', method: 'post', data: data })
}
export function updatePhaseRecord(data) {
  return request({ url: '/lims/oos/phase', method: 'put', data: data })
}
