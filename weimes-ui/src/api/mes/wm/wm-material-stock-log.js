import request from '@/utils/request'

// 查询库存变化日志列表
export function listWmMaterialStockLog(query) {
  return request({
    url: '/wm/wm-material-stock-log/list',
    method: 'get',
    params: query
  })
}

// 查询库存变化日志详细
export function getWmMaterialStockLog(logId) {
  return request({
    url: '/wm/wm-material-stock-log/' + logId,
    method: 'get'
  })
}

// 新增库存变化日志
export function addWmMaterialStockLog(data) {
  return request({
    url: '/wm/wm-material-stock-log',
    method: 'post',
    data: data
  })
}

// 修改库存变化日志
export function updateWmMaterialStockLog(data) {
  return request({
    url: '/wm/wm-material-stock-log',
    method: 'put',
    data: data
  })
}

// 删除库存变化日志
export function delWmMaterialStockLog(logId) {
  return request({
    url: '/wm/wm-material-stock-log/' + logId,
    method: 'delete'
  })
}
