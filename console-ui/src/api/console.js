import request from '../utils/request'
 
export function getAnyMetricsList(params) {
  return request({
    url: '/rest/anyMetrics/list',
    method: 'get',
    params
  })
}

export function getAnyMetricsTaskState(params) {
  return request({
    url: '/rest/anyMetrics/getTaskState',
    method: 'get',
    params
  })
}

export function startTask(params) {
  return request({
    url: '/rest/anyMetrics/startTask',
    method: 'get',
    params
  })
}

export function stopTask(params) {
  return request({
    url: '/rest/anyMetrics/stopTask',
    method: 'get',
    params
  })
}

export function addTask(params) {
  return request({
    url: '/rest/anyMetrics/addTask',
    method: 'post',
    data: params
  })
}

export function addAndStartTask(params) {
  return request({
    url: '/rest/anyMetrics/addAndStartTask',
    method: 'post',
    data: params
  })
}

export function updateTask(params) {
  return request({
    url: '/rest/anyMetrics/updateTask',
    method: 'post',
    data: params
  })
}

export function updateAndStartTask(params) {
  return request({
    url: '/rest/anyMetrics/updateAndStartTask',
    method: 'post',
    data: params
  })
}

export function deleteTask(params) {
  return request({
    url: '/rest/anyMetrics/deleteTask',
    method: 'get',
    params
  })
}


