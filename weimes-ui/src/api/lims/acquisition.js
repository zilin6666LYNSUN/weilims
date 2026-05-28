import request from '@/utils/request'

// 查询采集配置列表
export function listAcquisition(query) {
  return request({
    url: '/lims/instrument/acquisition/list',
    method: 'get',
    params: query
  })
}

// 查询采集配置详情
export function getAcquisition(acquisitionId) {
  return request({
    url: '/lims/instrument/acquisition/' + acquisitionId,
    method: 'get'
  })
}

// 根据仪器ID查询采集配置
export function getAcquisitionByInstrument(instrumentId) {
  return request({
    url: '/lims/instrument/acquisition/instrument/' + instrumentId,
    method: 'get'
  })
}

// 新增采集配置
export function addAcquisition(data) {
  return request({
    url: '/lims/instrument/acquisition',
    method: 'post',
    data: data
  })
}

// 修改采集配置
export function updateAcquisition(data) {
  return request({
    url: '/lims/instrument/acquisition',
    method: 'put',
    data: data
  })
}

// 删除采集配置
export function delAcquisition(acquisitionIds) {
  return request({
    url: '/lims/instrument/acquisition/' + acquisitionIds,
    method: 'delete'
  })
}

// 模拟从仪器采集数据
export function importInstrumentData(instrumentId, testOrderId) {
  return request({
    url: '/lims/instrument/acquisition/import',
    method: 'post',
    params: { instrumentId, testOrderId }
  })
}
