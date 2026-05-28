import request from '@/utils/request'

// 查询员工花名册列表
export function listHrmStaff(query) {
  return request({
    url: '/hrm/sm/hrm-staff/list',
    method: 'get',
    params: query
  })
}

// 查询员工花名册列表
export function listTalents(query) {
  return request({
    url: '/hrm/sm/hrm-staff/listTalents',
    method: 'get',
    params: query
  })
}

// 查询人才花名册详细
export function getTalent(staffId) {
  return request({
    url: '/hrm/sm/hrm-staff/getTalent/' + staffId,
    method: 'get'
  })
}
// 新增员工花名册
export function addHrmStaff(data) {
  return request({
    url: '/hrm/sm/hrm-staff',
    method: 'post',
    data: data
  })
}

// 修改员工花名册
export function updateHrmStaff(data) {
  return request({
    url: '/hrm/sm/hrm-staff',
    method: 'put',
    data: data
  })
}

// 删除员工花名册
export function delHrmStaff(staffId) {
  return request({
    url: '/hrm/sm/hrm-staff/' + staffId,
    method: 'delete'
  })
}
