import request from '@/utils/request'

// 查询客户订单列表
export function listClientOrder(query) {
  return request({
    url: '/pro/client-order/list',
    method: 'get',
    params: query
  })
}

// 查询客户订单详细
export function getClientOrder(clientOrderId) {
  return request({
    url: '/pro/client-order/' + clientOrderId,
    method: 'get'
  })
}

// 新增客户订单
export function addClientOrder(data) {
  return request({
    url: '/pro/client-order',
    method: 'post',
    data: data
  })
}

// 修改客户订单
export function updateClientOrder(data) {
  return request({
    url: '/pro/client-order',
    method: 'put',
    data: data
  })
}

// 删除客户订单
export function delClientOrder(clientOrderId) {
  return request({
    url: '/pro/client-order/' + clientOrderId,
    method: 'delete'
  })
}

// 生成生产工单
export function generateWorkOrder(clientOrderId) {
  return request({
    url: '/pro/client-order/generateWorkOrder/' + clientOrderId,
    method: 'post'
  })
}