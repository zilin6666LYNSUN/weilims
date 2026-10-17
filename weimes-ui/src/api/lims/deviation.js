import request from '@/utils/request'

// 偏差管理
export function listDeviation(query) {
  return request({ url: '/lims/deviation/list', method: 'get', params: query })
}
export function getDeviation(deviationId) {
  return request({ url: '/lims/deviation/' + deviationId, method: 'get' })
}
export function addDeviation(data) {
  return request({ url: '/lims/deviation', method: 'post', data: data })
}
export function updateDeviation(data) {
  return request({ url: '/lims/deviation', method: 'put', data: data })
}
export function delDeviation(deviationIds) {
  return request({ url: '/lims/deviation/' + deviationIds, method: 'delete' })
}
export function submitDeviation(deviationId) {
  return request({ url: '/lims/deviation/submit/' + deviationId, method: 'put' })
}
export function approveDeviation(deviationId) {
  return request({ url: '/lims/deviation/approve/' + deviationId, method: 'put' })
}
export function closeDeviation(deviationId) {
  return request({ url: '/lims/deviation/close/' + deviationId, method: 'put' })
}


// 偏差影响评估
export function listDeviationImpact(query) {
  return request({ url: '/lims/quality/deviation/impact/list', method: 'get', params: query })
}
export function listDeviationImpactByDeviation(deviationId) {
  return request({ url: '/lims/quality/deviation/impact/list/' + deviationId, method: 'get' })
}
export function getDeviationImpact(impactId) {
  return request({ url: '/lims/quality/deviation/impact/' + impactId, method: 'get' })
}
export function addDeviationImpact(data) {
  return request({ url: '/lims/quality/deviation/impact', method: 'post', data: data })
}
export function updateDeviationImpact(data) {
  return request({ url: '/lims/quality/deviation/impact', method: 'put', data: data })
}
export function delDeviationImpact(impactIds) {
  return request({ url: '/lims/quality/deviation/impact/' + impactIds, method: 'delete' })
}
