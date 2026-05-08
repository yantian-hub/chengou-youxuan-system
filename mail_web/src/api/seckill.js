import request from '../utils/request'

export function doSeckill(goodsId) {
  return request.post(`/seckill/seckill/doSeckill/${goodsId}`)
}

export function querySeckillOrder(orderId) {
  return request.get(`/seckill/seckill/queryOrder/${orderId}`)
}
