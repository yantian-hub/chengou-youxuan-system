import request from '../utils/request'

export function createOrder(order) {
  return request.post('/order/order/add', order)
}
