import request from '@/utils/request'

export function listStability(query) {
  return request({ url: '/lims/stability/list', method: 'get', params: query })
}

export function getStability(protocolId) {
  return request({ url: '/lims/stability/' + protocolId, method: 'get' })
}

export function addStability(data) {
  return request({ url: '/lims/stability', method: 'post', data: data })
}

export function updateStability(data) {
  return request({ url: '/lims/stability', method: 'put', data: data })
}

export function delStability(protocolIds) {
  return request({ url: '/lims/stability/' + protocolIds, method: 'delete' })
}

export function listStabilityTests(protocolId) {
  return request({ url: '/lims/stability/test/list/' + protocolId, method: 'get' })
}

export function updateStabilityTest(data) {
  return request({ url: '/lims/stability/test', method: 'put', data: data })
}
