import request from '../utils/request'

export function createPay(id) {
  return request.get(`/wx/pay/creat/${id}`)
}

export function payResult(id, status) {
  return request.get(`/wx/pay/result/${id}/${status}`)
}
