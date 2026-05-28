import request from '@/utils/request'

// 查询日志写入失败记录列表
export function listWmLogFailure(query) {
  return request({
    url: '/wm/wm-log-failure/list',
    method: 'get',
    params: query
  })
}

// 查询日志写入失败记录详细
export function getWmLogFailure(failureId) {
  return request({
    url: '/wm/wm-log-failure/' + failureId,
    method: 'get'
  })
}

// 新增日志写入失败记录
export function addWmLogFailure(data) {
  return request({
    url: '/wm/wm-log-failure',
    method: 'post',
    data: data
  })
}

// 修改日志写入失败记录
export function updateWmLogFailure(data) {
  return request({
    url: '/wm/wm-log-failure',
    method: 'put',
    data: data
  })
}

// 删除日志写入失败记录
export function delWmLogFailure(failureId) {
  return request({
    url: '/wm/wm-log-failure/' + failureId,
    method: 'delete'
  })
}
