import request from '@/utils/request'

// 查询销售订单列表
export function listOrder(query) {
  return request({
    url: '/sales/order/list',
    method: 'get',
    params: query
  })
}

// 查询销售订单详细
export function getOrder(salesOrderId) {
  return request({
    url: '/sales/order/' + salesOrderId,
    method: 'get'
  })
}

// 新增销售订单
export function addOrder(data) {
  return request({
    url: '/sales/order',
    method: 'post',
    data: data
  })
}

// 修改销售订单
export function updateOrder(data) {
  return request({
    url: '/sales/order',
    method: 'put',
    data: data
  })
}

// 删除销售订单
export function delOrder(salesOrderId) {
  return request({
    url: '/sales/order/' + salesOrderId,
    method: 'delete'
  })
}

// 删除销售订单产品列表
export function delOrderItem(itemId) {
  return request({
    url: '/sales/order/delOrderItem/' + itemId,
    method: 'delete'
  })
}

// 审批
export function refuse(salesOrderId) {
  return request({
    url: '/sales/order/refuse/' + salesOrderId,
    method: 'put'
  })
}

// 审批通过生成生产工单
export function execute(salesOrderId) {
  return request({
    url: '/sales/order/execute/' + salesOrderId,
    method: 'post'
  })
}
// 生成生产工单
export function pushMes(salesOrderId) {
  return request({
    url: '/sales/order/push/' + salesOrderId,
    method: 'post'
  })
}