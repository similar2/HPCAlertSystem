import request from '@/utils/request'

export const artGetAllWarningService = () => request.get('/api/alerts/all')
export const artGetSearchWarningService = (data) => {
  const params = new URLSearchParams()
  for (const key in data) {
    if (data[key] !== null && data[key] !== '') {
      params.append(key, data[key])
    }
  }
  if (params.toString() === '') {
    return request.get('/api/alerts/all')
  } else {
    return request.get(`/api/alerts/all?${params.toString()}`)
  }
}
// 获取告警列表（分页查询）
export const getWarningList = (params) => {
  return request.get('/api/alerts/page', {
    params: params
  })
}
export const artGetWarningService = (id) => request.get(`/api/alerts/get/${id}`)
// 添加告警
export const artAddWarningService = (data) =>
  request.post('/api/alerts/add', data)
// 编辑告警
export const artEditWarningService = (data, id) =>
  request.post(`/api/alerts/modify/${id}`, data)
// 解决告警
export const artDelWarningService = (id, data) =>
  request.post(`/api/alerts/solve/${id}?solveMethod=${data}`)
//获取所有设备
export const artGetAllDevicesService = () => request.get('/api/devices')
export const artGetDeviceService = (id) => request.get(`/api/devices/${id}`)
//增加设备
export const artAddDeviceService = (data) => request.post('/api/devices', data)
//修改设备
export const artEditService = (data, id) =>
  request.patch(`/api/devices/${id}`, data)
//删除设备
export const artDelService = (id) => request.delete(`/api/devices/${id}`)
//获取target
export const artGetAllTarget = () =>
  request.get('/api/target?job=ipmi_exporter')
export const artGetAllTargetService = () => request.get('/api/target')
//增加target
export const artAddTargetService = (data) => {
  request.post('/api/target', data)
}
//删除target
export const artDelTargetService = (id) => request.post(`/api/target/${id}`)
//获取全部规则
export const artGetAllRulesService = () => request.get('/api/alerts/rules')
//增加规则
export const artAddRulesService = (data) =>
  request.post('/api/alerts/rules/add', data)
//删除规则
export const artDelRulesService = (data) =>
  request.post(
    `/api/alerts/rules/delete?type=${data.type}&alert_name=${data.alert_name}`
  )
//获取标准
export const artGetMetricNamesService = (data) =>
  request.get(`/api/alerts/rules/metric_names?type=${data}`)
export const artGetMetricFiltersService = (data, metric) =>
  request.get(`/api/alerts/rules/metric_filters?type=${data}&metric=${metric}`)
//获取告警人
export const artGetAllResponseService = () => request.get('/api/devices/assignees/get')
export const artGetResponseService = (id) =>
  request.get(`/api/devices/assignees/get?device_id=${id}`)
//添加告警人
export const artAddResponseService = (id, data) =>
  request.post(`/api/devices/assignees/add/${id}`, data)
//删除告警人
export const artDeleteResponseService = (id, data) =>
  request.post(`/api/devices/assignees/delete/${id}`, data)
export const artAddFile = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/api/devices/file', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
