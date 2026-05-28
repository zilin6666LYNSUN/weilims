import request from '@/utils/request'

// 仪器类型
export function listInstrumentType(query) {
  return request({ url: '/lims/instrument/type/list', method: 'get', params: query })
}
export function getInstrumentType(instrumentTypeId) {
  return request({ url: '/lims/instrument/type/' + instrumentTypeId, method: 'get' })
}
export function addInstrumentType(data) {
  return request({ url: '/lims/instrument/type', method: 'post', data: data })
}
export function updateInstrumentType(data) {
  return request({ url: '/lims/instrument/type', method: 'put', data: data })
}
export function delInstrumentType(instrumentTypeIds) {
  return request({ url: '/lims/instrument/type/' + instrumentTypeIds, method: 'delete' })
}

// 仪器校准
export function listCalibration(query) {
  return request({ url: '/lims/instrument/calibration/list', method: 'get', params: query })
}
export function listCalibrationByInstrument(instrumentId) {
  return request({ url: '/lims/instrument/calibration/list/' + instrumentId, method: 'get' })
}
export function addCalibration(data) {
  return request({ url: '/lims/instrument/calibration', method: 'post', data: data })
}
export function updateCalibration(data) {
  return request({ url: '/lims/instrument/calibration', method: 'put', data: data })
}
export function delCalibration(calibrationIds) {
  return request({ url: '/lims/instrument/calibration/' + calibrationIds, method: 'delete' })
}

// 仪器维护
export function listMaintenance(query) {
  return request({ url: '/lims/instrument/maintenance/list', method: 'get', params: query })
}
export function listMaintenanceByInstrument(instrumentId) {
  return request({ url: '/lims/instrument/maintenance/list/' + instrumentId, method: 'get' })
}
export function addMaintenance(data) {
  return request({ url: '/lims/instrument/maintenance', method: 'post', data: data })
}
export function updateMaintenance(data) {
  return request({ url: '/lims/instrument/maintenance', method: 'put', data: data })
}
export function delMaintenance(maintenanceIds) {
  return request({ url: '/lims/instrument/maintenance/' + maintenanceIds, method: 'delete' })
}

// 仪器预约
export function listReservation(query) {
  return request({ url: '/lims/instrument/reservation/list', method: 'get', params: query })
}
export function listReservationByInstrument(instrumentId) {
  return request({ url: '/lims/instrument/reservation/list/' + instrumentId, method: 'get' })
}
export function addReservation(data) {
  return request({ url: '/lims/instrument/reservation', method: 'post', data: data })
}
export function updateReservation(data) {
  return request({ url: '/lims/instrument/reservation', method: 'put', data: data })
}
export function delReservation(reservationIds) {
  return request({ url: '/lims/instrument/reservation/' + reservationIds, method: 'delete' })
}
