import request from '@/utils/request'

// CAPA管理
export function listCapa(query) {
  return request({ url: '/lims/capa/list', method: 'get', params: query })
}
export function getCapa(capaId) {
  return request({ url: '/lims/capa/' + capaId, method: 'get' })
}
export function addCapa(data) {
  return request({ url: '/lims/capa', method: 'post', data: data })
}
export function updateCapa(data) {
  return request({ url: '/lims/capa', method: 'put', data: data })
}
export function delCapa(capaIds) {
  return request({ url: '/lims/capa/' + capaIds, method: 'delete' })
}
export function submitCapa(capaId) {
  return request({ url: '/lims/capa/submit/' + capaId, method: 'put' })
}
export function approveCapa(capaId) {
  return request({ url: '/lims/capa/approve/' + capaId, method: 'put' })
}
export function closeCapa(capaId) {
  return request({ url: '/lims/capa/close/' + capaId, method: 'put' })
}


// CAPA任务
export function listCapaTask(query) {
  return request({ url: '/lims/quality/capa/task/list', method: 'get', params: query })
}
export function listCapaTaskByCapa(capaId) {
  return request({ url: '/lims/quality/capa/task/list/' + capaId, method: 'get' })
}
export function getCapaTask(taskId) {
  return request({ url: '/lims/quality/capa/task/' + taskId, method: 'get' })
}
export function addCapaTask(data) {
  return request({ url: '/lims/quality/capa/task', method: 'post', data: data })
}
export function updateCapaTask(data) {
  return request({ url: '/lims/quality/capa/task', method: 'put', data: data })
}
export function delCapaTask(taskIds) {
  return request({ url: '/lims/quality/capa/task/' + taskIds, method: 'delete' })
}
export function completeCapaTask(taskId) {
  return request({ url: '/lims/quality/capa/task/complete/' + taskId, method: 'put' })
}
