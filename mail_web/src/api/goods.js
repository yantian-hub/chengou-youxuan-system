import request from '../utils/request'

export function queryBrand(name) {
  return request.get('/goods/goods/search/brand', { params: { name } })
}

export function querySpu(name) {
  return request.get('/goods/goods/search/spu', { params: { name } })
}
