import request from '@/utils/request'

// 查询客户订单材料列表
export function listClientOrderItem(query) {
  return request({
    url: '/pro/client-order-item/list',
    method: 'get',
    params: query
  })
}

// 查询客户订单材料详细
export function getClientOrderItem(orderItemId) {
  return request({
    url: '/pro/client-order-item/' + orderItemId,
    method: 'get'
  })
}

// 获取客户订单的级别
export function getClientOrderItemLevel(clientOrderId) {
  return request({
    url: '/pro/client-order-item/getClientOrderItemLevel/' + clientOrderId,
    method: 'get'
  })
}

// 新增客户订单材料
export function addClientOrderItem(data) {
  return request({
    url: '/pro/client-order-item',
    method: 'post',
    data: data
  })
}

// 修改客户订单材料
export function updateClientOrderItem(data) {
  return request({
    url: '/pro/client-order-item',
    method: 'put',
    data: data
  })
}

// 删除客户订单材料
export function delClientOrderItem(orderItemId) {
  return request({
    url: '/pro/client-order-item/' + orderItemId,
    method: 'delete'
  })
}
