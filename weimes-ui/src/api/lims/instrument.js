import request from '@/utils/request'

// 查询仪器列表
export function listInstrument(query) {
  return request({
    url: '/lims/instrument/list',
    method: 'get',
    params: query
  })
}

// 查询仪器详情
export function getInstrument(instrumentId) {
  return request({
    url: '/lims/instrument/' + instrumentId,
    method: 'get'
  })
}

// 新增仪器
export function addInstrument(data) {
  return request({
    url: '/lims/instrument',
    method: 'post',
    data: data
  })
}

// 修改仪器
export function updateInstrument(data) {
  return request({
    url: '/lims/instrument',
    method: 'put',
    data: data
  })
}

// 删除仪器
export function delInstrument(instrumentIds) {
  return request({
    url: '/lims/instrument/' + instrumentIds,
    method: 'delete'
  })
}

// 校准到期提醒
export function calibrationDue(withinDays) {
  return request({
    url: '/lims/instrument/calibration-due/' + withinDays,
    method: 'get'
  })
}
