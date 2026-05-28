import request from '@/utils/request'

// 查询销售送货单列表
export function listTranOrde(query) {
  return request({
    url: '/sales/tranOrder/list',
    method: 'get',
    params: query
  })
}

// 查询销售送货单详细
export function getTranOrde(tranOrderId) {
  return request({
    url: '/sales/tranOrder/' + tranOrderId,
    method: 'get'
  })
}

// 新增销售送货单
export function addTranOrde(data) {
  return request({
    url: '/sales/tranOrder',
    method: 'post',
    data: data
  })
}

// 修改销售送货单
export function updateTranOrde(data) {
  return request({
    url: '/sales/tranOrder',
    method: 'put',
    data: data
  })
}

// 删除销售送货单
export function delTranOrde(tranOrderId) {
  return request({
    url: '/sales/tranOrder/' + tranOrderId,
    method: 'delete'
  })
}

// 删除销售订单产品列表
export function delOrderItem(itemId) {
  return request({
    url: '/sales/tranOrder/delOrderItem/' + itemId,
    method: 'delete'
  })
}


// 审批
export function refuse(tranOrderId,status) {
  return request({
    url: '/sales/tranOrder/refuse/' + tranOrderId+','+status,
    method: 'put'
  })
}
// 审批通过执行
export function execute(tranOrderId) {
  return request({
    url: '/sales/tranOrder/execute/' + tranOrderId,
    method: 'post'
  })
}

