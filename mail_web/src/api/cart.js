import request from '../utils/request'

export function addCart(id, num) {
  return request.get(`/cart/cart/${id}/${num}`)
}

export function getCartList() {
  return request.get('/cart/cart/list')
}

export function getCartByIds(ids) {
  return request.post('/cart/cart/listByIds', ids)
}

export function deleteCart(skuId) {
  return request.delete(`/cart/cart/${skuId}`)
}

export function updateCartNum(skuId, num) {
  return request.put(`/cart/cart/${skuId}/${num}`)
}

export function clearCart() {
  return request.delete('/cart/cart/clear')
}
