import { createRouter, createWebHashHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Home from '../views/Home.vue'
import Cart from '../views/Cart.vue'
import Seckill from '../views/Seckill.vue'
import OrderConfirm from '../views/OrderConfirm.vue'
import PaySuccess from '../views/PaySuccess.vue'

const routes = [
  { path: '/login', name: 'Login', component: Login, meta: { showNav: false } },
  { path: '/register', name: 'Register', component: Register, meta: { showNav: false } },
  { path: '/', name: 'Home', component: Home },
  { path: '/cart', name: 'Cart', component: Cart },
  { path: '/seckill', name: 'Seckill', component: Seckill },
  { path: '/order/confirm', name: 'OrderConfirm', component: OrderConfirm },
  { path: '/pay/success/:id', name: 'PaySuccess', component: PaySuccess },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login' || to.path === '/register') {
    next()
    return
  }
  if (!token && to.path !== '/') {
    next('/login')
    return
  }
  next()
})

export default router
