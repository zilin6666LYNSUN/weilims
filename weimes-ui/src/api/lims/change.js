import request from '@/utils/request'

// 变更控制
export function listChange(query) {
  return request({ url: '/lims/change/control/list', method: 'get', params: query })
}
export function getChange(changeId) {
  return request({ url: '/lims/change/control/' + changeId, method: 'get' })
}
export function addChange(data) {
  return request({ url: '/lims/change/control', method: 'post', data: data })
}
export function updateChange(data) {
  return request({ url: '/lims/change/control', method: 'put', data: data })
}
export function delChange(changeIds) {
  return request({ url: '/lims/change/control/' + changeIds, method: 'delete' })
}
export function submitChange(changeId) {
  return request({ url: '/lims/change/control/submit/' + changeId, method: 'put' })
}
export function approveChange(changeId) {
  return request({ url: '/lims/change/control/approve/' + changeId, method: 'put' })
}
export function implementChange(changeId) {
  return request({ url: '/lims/change/control/implement/' + changeId, method: 'put' })
}
export function completeChange(changeId) {
  return request({ url: '/lims/change/control/complete/' + changeId, method: 'put' })
}


// 变更影响评估
export function listChangeImpact(query) {
  return request({ url: '/lims/quality/change/impact/list', method: 'get', params: query })
}
export function listChangeImpactByChange(changeId) {
  return request({ url: '/lims/quality/change/impact/list/' + changeId, method: 'get' })
}
export function getChangeImpact(impactId) {
  return request({ url: '/lims/quality/change/impact/' + impactId, method: 'get' })
}
export function addChangeImpact(data) {
  return request({ url: '/lims/quality/change/impact', method: 'post', data: data })
}
export function updateChangeImpact(data) {
  return request({ url: '/lims/quality/change/impact', method: 'put', data: data })
}
export function delChangeImpact(impactIds) {
  return request({ url: '/lims/quality/change/impact/' + impactIds, method: 'delete' })
}
