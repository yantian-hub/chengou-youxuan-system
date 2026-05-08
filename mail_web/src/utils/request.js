import axios from 'axios'

const request = axios.create({
  baseURL: '',
  timeout: 15000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 40100 || res.code === 40101 || res.code === 40102) {
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      window.location.href = '/#/login'
    }
    return res
  },
  error => {
    console.error('请求异常:', error)
    return Promise.reject(error)
  }
)

export default request
