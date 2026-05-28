import request from '@/utils/request'

// 查询销售订单产品列列表
export function listItem(query) {
  return request({
    url: '/sales/item/list',
    method: 'get',
    params: query
  })
}

// 查询销售订单产品列详细
export function getItem(salesOrderItmeId) {
  return request({
    url: '/sales/item/' + salesOrderItmeId,
    method: 'get'
  })
}

// 新增销售订单产品列
export function addItem(data) {
  return request({
    url: '/sales/item',
    method: 'post',
    data: data
  })
}

// 修改销售订单产品列
export function updateItem(data) {
  return request({
    url: '/sales/item',
    method: 'put',
    data: data
  })
}

// 删除销售订单产品列
export function delItem(salesOrderId) {
  return request({
    url: '/sales/item/' + salesOrderId,
    method: 'delete'
  })
}
/**
 * 打印
 */
 export function print(id) {
  return request({
    url: '/sales/item/print' + salesOrderId,
    method: 'date'
  })
}

// 弹出框查询销售订单列表
export function selectItem(query) {
  return request({
    url: '/sales/order/selectItem',
    method: 'get',
    params: query
  })
}

export function generateWorkOrder(itemId){
  return request({
    url: '/sales/item/execute/' + itemId,
    method: 'post'
  })
}