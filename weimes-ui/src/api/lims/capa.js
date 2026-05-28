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
