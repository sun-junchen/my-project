import axios from 'axios'
import { ElMessage } from 'element-plus'

const instance = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {}
})

const authItemName = 'access_token'
// request 拦截器
// 可以自请求发送前对请求做一些处理
// 比如统一加token，对请求参数统一加密
instance.interceptors.request.use(
  (config) => {
    const token = getAccessToken()
    // config.headers['Content-Type'] = 'application/json;charset=utf-8'
    config.headers['Authorization'] = `Bearer ${token}` // 设置请求头
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

function deleteAccessToken() {
  localStorage.removeItem(authItemName)
  sessionStorage.removeItem(authItemName)
}

function getAccessToken() {
  const authObjStr = localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName)
  if (!authObjStr) return null
  const authObj = JSON.parse(authObjStr)
  if (authObj.expire <= new Date()) {
    deleteAccessToken()
    ElMessage.warning('登录状态已过期，请重新登录')
    return null
  }
  return authObj.token
}

// 添加响应拦截器
instance.interceptors.response.use(
  function (response) {
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么
    const data = response.data
    if (data.code === 200) {
      return data
    } else {
      console.error('request error', data)
      return response
    }
  },
  function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    return Promise.reject(error)
  }
)

function unauthorized() {
  return !getAccessToken()
}
export default instance
export { deleteAccessToken, unauthorized }
