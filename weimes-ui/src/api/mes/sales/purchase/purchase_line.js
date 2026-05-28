import request from '@/utils/request'

// 查询采购订单列表列表
export function listPurchase_line(query) {
  return request({
    url: '/purchase/purchase_line/list',
    method: 'get',
    params: query
  })
}

// 查询采购订单列表详细
export function getPurchase_line(id) {
  return request({
    url: '/purchase/purchase_line/' + id,
    method: 'get'
  })
}

// 新增采购订单列表
export function addPurchase_line(data) {
  return request({
    url: '/purchase/purchase_line',
    method: 'post',
    data: data
  })
}

// 修改采购订单列表
export function updatePurchase_line(data) {
  return request({
    url: '/purchase/purchase_line',
    method: 'put',
    data: data
  })
}

// 删除采购订单列表
export function delPurchase_line(id) {
  return request({
    url: '/purchase/purchase_line/' + id,
    method: 'delete'
  })
}
