import request from '@/utils/request'

// 客户档案
export function listCustomer(query) {
  return request({ url: '/lims/customer/list', method: 'get', params: query })
}
export function getCustomer(customerId) {
  return request({ url: '/lims/customer/' + customerId, method: 'get' })
}
export function addCustomer(data) {
  return request({ url: '/lims/customer', method: 'post', data: data })
}
export function updateCustomer(data) {
  return request({ url: '/lims/customer', method: 'put', data: data })
}
export function delCustomer(customerIds) {
  return request({ url: '/lims/customer/' + customerIds, method: 'delete' })
}

