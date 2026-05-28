import request from '@/utils/request'

// 试剂管理
export function listReagent(query) {
  return request({ url: '/lims/reagent/list', method: 'get', params: query })
}
export function getReagent(reagentId) {
  return request({ url: '/lims/reagent/' + reagentId, method: 'get' })
}
export function addReagent(data) {
  return request({ url: '/lims/reagent', method: 'post', data: data })
}
export function updateReagent(data) {
  return request({ url: '/lims/reagent', method: 'put', data: data })
}
export function delReagent(reagentIds) {
  return request({ url: '/lims/reagent/' + reagentIds, method: 'delete' })
}

// 试剂出入库
export function listStockLog(query) {
  return request({ url: '/lims/reagent/stocklog/list', method: 'get', params: query })
}
export function listStockLogByReagent(reagentId) {
  return request({ url: '/lims/reagent/stocklog/list/' + reagentId, method: 'get' })
}
export function addStockLog(data) {
  return request({ url: '/lims/reagent/stocklog', method: 'post', data: data })
}

// 标准品
export function listStandard(query) {
  return request({ url: '/lims/reagent/standard/list', method: 'get', params: query })
}
export function getStandard(standardId) {
  return request({ url: '/lims/reagent/standard/' + standardId, method: 'get' })
}
export function addStandard(data) {
  return request({ url: '/lims/reagent/standard', method: 'post', data: data })
}
export function updateStandard(data) {
  return request({ url: '/lims/reagent/standard', method: 'put', data: data })
}
export function delStandard(standardIds) {
  return request({ url: '/lims/reagent/standard/' + standardIds, method: 'delete' })
}

// 菌种
export function listCulture(query) {
  return request({ url: '/lims/reagent/culture/list', method: 'get', params: query })
}
export function getCulture(cultureId) {
  return request({ url: '/lims/reagent/culture/' + cultureId, method: 'get' })
}
export function addCulture(data) {
  return request({ url: '/lims/reagent/culture', method: 'post', data: data })
}
export function updateCulture(data) {
  return request({ url: '/lims/reagent/culture', method: 'put', data: data })
}
export function delCulture(cultureIds) {
  return request({ url: '/lims/reagent/culture/' + cultureIds, method: 'delete' })
}
