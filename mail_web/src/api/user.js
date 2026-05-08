import request from '../utils/request'

export function login(data) {
  return request.post('/usr/usrLogin/login', data)
}

export function register(data) {
  return request.post('/usr/usrLogin/register', data)
}

export function sendCode(phone) {
  return request.get(`/usr/usrLogin/sendCode/${phone}`)
}
