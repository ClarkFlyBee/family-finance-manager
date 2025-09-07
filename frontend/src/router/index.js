// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Layout from '../views/Layout.vue'
import ExpenseList from '../views/expense/List.vue'
import ExpenseForm from '../views/expense/Form.vue'

const routes = [
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  {
    path: '/',
    component: Layout,
    meta: { requiresAuth: true }, // ✅ 标记：需要登录才能访问
    children: [
      { path: '/expense', component: ExpenseList },
      { path: '/expense/add', component: ExpenseForm },
      { path: '/expense/edit/:id', component: ExpenseForm }
    ]
  }
]

export const router = createRouter({
  history: createWebHistory(),
  routes
})

// ✅ 添加：全局前置守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  // 如果目标页面需要登录
  if (to.meta.requiresAuth) {
    if (token) {
      next() // 已登录，放行
    } else {
      next('/login') // 未登录，跳转登录页
    }
  } else {
    // 如果是登录页或注册页，且已登录，则跳过
    if ((to.path === '/login' || to.path === '/register') && token) {
      next('/') // 已登录，跳转首页
    } else {
      next() // 放行（比如去登录页）
    }
  }
})