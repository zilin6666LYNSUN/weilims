import request from '@/utils/request'

// 查询参数列表
export function list(query) {
  return request({
    url: '/weimes/jmreport',
    method: 'get',
    params: query
  })
}