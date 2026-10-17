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


// 试剂批次效期
export function listReagentLot(query) {
  return request({ url: '/lims/reagent/lot/list', method: 'get', params: query })
}
export function getReagentLot(lotId) {
  return request({ url: '/lims/reagent/lot/' + lotId, method: 'get' })
}
export function addReagentLot(data) {
  return request({ url: '/lims/reagent/lot', method: 'post', data: data })
}
export function updateReagentLot(data) {
  return request({ url: '/lims/reagent/lot', method: 'put', data: data })
}
export function delReagentLot(lotIds) {
  return request({ url: '/lims/reagent/lot/' + lotIds, method: 'delete' })
}

// 标准品证书/资质
export function listStandardQualification(query) {
  return request({ url: '/lims/reagent/standard/qualification/list', method: 'get', params: query })
}
export function getStandardQualification(qualificationId) {
  return request({ url: '/lims/reagent/standard/qualification/' + qualificationId, method: 'get' })
}
export function addStandardQualification(data) {
  return request({ url: '/lims/reagent/standard/qualification', method: 'post', data: data })
}
export function updateStandardQualification(data) {
  return request({ url: '/lims/reagent/standard/qualification', method: 'put', data: data })
}
export function delStandardQualification(qualificationIds) {
  return request({ url: '/lims/reagent/standard/qualification/' + qualificationIds, method: 'delete' })
}

// 标准品使用记录
export function listStandardUsage(query) {
  return request({ url: '/lims/reagent/standard/usage/list', method: 'get', params: query })
}
export function getStandardUsage(usageId) {
  return request({ url: '/lims/reagent/standard/usage/' + usageId, method: 'get' })
}
export function addStandardUsage(data) {
  return request({ url: '/lims/reagent/standard/usage', method: 'post', data: data })
}
export function updateStandardUsage(data) {
  return request({ url: '/lims/reagent/standard/usage', method: 'put', data: data })
}
export function delStandardUsage(usageIds) {
  return request({ url: '/lims/reagent/standard/usage/' + usageIds, method: 'delete' })
}

// 培养基管理
export function listCultureMedia(query) {
  return request({ url: '/lims/reagent/media/list', method: 'get', params: query })
}
export function getCultureMedia(mediaId) {
  return request({ url: '/lims/reagent/media/' + mediaId, method: 'get' })
}
export function addCultureMedia(data) {
  return request({ url: '/lims/reagent/media', method: 'post', data: data })
}
export function updateCultureMedia(data) {
  return request({ url: '/lims/reagent/media', method: 'put', data: data })
}
export function delCultureMedia(mediaIds) {
  return request({ url: '/lims/reagent/media/' + mediaIds, method: 'delete' })
}

// 菌种流转：接收入库/传代/销毁
export function receiveCulture(cultureId) {
  return request({ url: '/lims/reagent/culture/receive/' + cultureId, method: 'put' })
}
export function passageCulture(cultureId) {
  return request({ url: '/lims/reagent/culture/passage/' + cultureId, method: 'put' })
}
export function discardCulture(cultureId) {
  return request({ url: '/lims/reagent/culture/discard/' + cultureId, method: 'put' })
}
