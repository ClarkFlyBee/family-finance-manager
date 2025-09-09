// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Layout from '../views/Layout.vue'
import ExpenseList from '../views/expense/List.vue'
import ExpenseForm from '../views/expense/Form.vue'
import IncomeList from '../views/income/List.vue'
import IncomeForm from '../views/income/Form.vue'
import FianaceAnalysisPanel from '../views/analysis/FianaceAnalysisPanel.vue'
import UserProfile from '../views/UserProfile.vue'

const routes = [
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  {
    path: '/',
    component: Layout,
    meta: { requiresAuth: true }, // ✅ 标记：需要登录才能访问
    redirect: '/dashboard', // 新增：重定向到 dashboard
    children: [
      { path: '/expense', component: ExpenseList },
      { path: '/expense/add', component: ExpenseForm },
      { path: '/expense/edit/:id', component: ExpenseForm },
      { path: '/income', component: IncomeList },
      { path: '/income/add', component: IncomeForm },
      { path: '/income/edit/:id', component: IncomeForm },
      { path: '/dashboard', 
        name: 'Dashboard', 
        component: ()=> import('../views/dashboard/Index.vue'),
        meta: {title: '数据看板'}
      },
      { path: '/analysis', component: FianaceAnalysisPanel, meta: { title:"AI 财务分析" } },
      { path: '/profile', component: UserProfile }
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
  const isAuthenticated = !!token

  // 1. 如果访问的是登录或注册页
  if (to.path === '/login' || to.path === '/register') {
    if (isAuthenticated) {
      // 已登录，不允许再访问登录/注册页，跳转到主页
      next('/')
    } else {
      // 未登录，允许访问登录/注册页
      next()
    }
    return // ✅ 提前返回，避免继续执行
  }

  // 2. 如果访问的是需要登录的页面（如 /, /expense 等）
  if (to.meta.requiresAuth) {
    if (isAuthenticated) {
      next() // 已登录，放行
    } else {
      next('/login') // 未登录，跳登录
    }
  } else {
    // 其他不需要登录的页面（如果有）
    next()
  }
})