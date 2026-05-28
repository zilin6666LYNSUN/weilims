import request from '@/utils/request'

// 查询销售送货单列列表
export function listLine(query) {
  return request({
    url: '/sales/tranOrder/line/list',
    method: 'get',
    params: query
  })
}

// 查询销售送货单列详细
export function getLine(tranOrdeLineId) {
  return request({
    url: '/sales/tranOrder/line/' + tranOrdeLineId,
    method: 'get'
  })
}

// 新增销售送货单列
export function addLine(data) {
  return request({
    url: '/sales/tranOrder/line',
    method: 'post',
    data: data
  })
}

// 修改销售送货单列
export function updateLine(data) {
  return request({
    url: '/sales/tranOrder/line',
    method: 'put',
    data: data
  })
}

// 删除销售送货单列
export function delLine(tranOrdeLineId) {
  return request({
    url: '/sales/tranOrder/line/' + tranOrdeLineId,
    method: 'delete'
  })
}
