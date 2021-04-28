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

export default service
