<!-- src/views/Login.vue -->
<template>
  <div class="auth-container">
    <h2>登录</h2>
    <el-form :model="form" label-width="80px" @submit.prevent="handleLogin">
      <el-form-item label="手机号">
        <el-input v-model="form.phone" placeholder="请输入手机号" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.password" type="password" placeholder="请输入密码" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" native-type="submit">登录</el-button>
        <router-link to="/register">没有账号？去注册</router-link>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()
const form = ref({
  phone: '',
  password: ''
})

const handleLogin = async () => {
  if (!form.value.phone || !form.value.password) {
    alert('请输入手机号和密码')
    return
  }

  try {
    const data = {
      phone: form.value.phone,
      password: form.value.password
    }

    const res = await request.post('/auth/login', data)

    // ✅ 修改这里：正确判断和取值
    if (res.data.code === 200) {
      const token = res.data.data.token

      // 1. 保存 token 到 Pinia store（如果 store 没问题）
      userStore.setToken(token)

      // 2. ✅ 同时保存到 localStorage，确保刷新后还能读取
      localStorage.setItem('token', token)

      // 3. 跳转首页
      router.push('/')
    } else {
      alert(res.data.msg || '登录失败')
    }
  } catch (err) {
    alert('登录失败：' + (err.response?.data?.msg || err.message))
  }
}
</script>

<style scoped>
.auth-container {
  max-width: 400px;
  margin: 100px auto;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}
</style>