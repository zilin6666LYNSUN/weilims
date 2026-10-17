import request from '@/utils/request'

// 委托单模板
export function listEntrustTemplate(query) {
  return request({ url: '/lims/entrust/template/list', method: 'get', params: query })
}
export function getEntrustTemplate(templateId) {
  return request({ url: '/lims/entrust/template/' + templateId, method: 'get' })
}
export function addEntrustTemplate(data) {
  return request({ url: '/lims/entrust/template', method: 'post', data: data })
}
export function updateEntrustTemplate(data) {
  return request({ url: '/lims/entrust/template', method: 'put', data: data })
}
export function delEntrustTemplate(templateIds) {
  return request({ url: '/lims/entrust/template/' + templateIds, method: 'delete' })
}

// 委托单
export function listEntrust(query) {
  return request({ url: '/lims/entrust/list', method: 'get', params: query })
}
export function getEntrust(entrustId) {
  return request({ url: '/lims/entrust/' + entrustId, method: 'get' })
}
export function addEntrust(data) {
  return request({ url: '/lims/entrust', method: 'post', data: data })
}
export function updateEntrust(data) {
  return request({ url: '/lims/entrust', method: 'put', data: data })
}
export function delEntrust(entrustIds) {
  return request({ url: '/lims/entrust/' + entrustIds, method: 'delete' })
}

// 委托检验项目
export function listEntrustItem(query) {
  return request({ url: '/lims/entrust/item/list', method: 'get', params: query })
}
export function getEntrustItem(itemId) {
  return request({ url: '/lims/entrust/item/' + itemId, method: 'get' })
}
export function addEntrustItem(data) {
  return request({ url: '/lims/entrust/item', method: 'post', data: data })
}
export function updateEntrustItem(data) {
  return request({ url: '/lims/entrust/item', method: 'put', data: data })
}
export function delEntrustItem(itemIds) {
  return request({ url: '/lims/entrust/item/' + itemIds, method: 'delete' })
}

// 委托单业务流转
export function submitEntrust(entrustId) {
  return request({ url: '/lims/entrust/submit/' + entrustId, method: 'put' })
}
export function reviewEntrust(data) {
  return request({ url: '/lims/entrust/review', method: 'put', data: data })
}
export function registerEntrust(entrustId) {
  return request({ url: '/lims/entrust/register/' + entrustId, method: 'put' })
}

