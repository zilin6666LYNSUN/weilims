import request from '@/utils/request'

// 查询职级管理列表
export function listHrmRank(query) {
  return request({
    url: '/md/hrm-rank/list',
    method: 'get',
    params: query
  })
}

// 查询职级管理详细
export function getHrmRank(rankId) {
  return request({
    url: '/md/hrm-rank/' + rankId,
    method: 'get'
  })
}

// 新增职级管理
export function addHrmRank(data) {
  return request({
    url: '/md/hrm-rank',
    method: 'post',
    data: data
  })
}

// 修改职级管理
export function updateHrmRank(data) {
  return request({
    url: '/md/hrm-rank',
    method: 'put',
    data: data
  })
}

// 删除职级管理
export function delHrmRank(rankId) {
  return request({
    url: '/md/hrm-rank/' + rankId,
    method: 'delete'
  })
}
