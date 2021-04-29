import axios from 'axios'
import Qs from 'qs'
import { Message, MessageBox } from 'element-ui'

// 创建axios实例
const service = axios.create({
  baseURL: process.env.BASE_API, // api的base_url
  timeout: 15000, // 请求超时时间

  //config里面有这个transformRquest，这个选项会在发送参数前进行处理。
  //这时候我们通过Qs.stringify转换为表单查询参数
  transformRequest: [function (data) {
    data = Qs.stringify(data);
    return data;
  }],
  //设置Content-Type
  headers: { 'Content-Type': 'application/x-www-form-urlencoded' }

})


// request拦截器
service.interceptors.request.use(
  config => {
    return config
  },
  error => {
    Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(response => {
    let type = response.headers['content-type']
    const res = response.data
    if (res.code !== 1) {
      if (res.code === 0) {
        Message({
          message: '' + res.message,
          type: 'error',
          duration: 2 * 1000
        })
      } else {
        Message({
          message: '' + res.message,
          type: 'warning',
          duration: 2 * 1000
        })
      }
      return Promise.reject('error')
    } else {
      return response.data
    }
  },
  error => {
    console.log('err' + error)
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)


export default service
