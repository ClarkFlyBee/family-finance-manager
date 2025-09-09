import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || ''  // ✅ 刷新时从 localStorage 恢复
  }),
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token) // ✅ 同时保存
    },
    clearToken() {
      this.token = ''
      localStorage.removeItem('token')
    }
  },
  getters: {
    isAuthenticated() {
      return !!this.token
    }
  }
})