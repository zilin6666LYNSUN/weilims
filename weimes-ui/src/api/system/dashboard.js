import request from '@/utils/request'

// 查询参数列表
export function getLoginDataByTimeRange(type) {
  return request({
    url: '/system/dashboard/loginData/' + type,
    method: 'get'
  })
}

