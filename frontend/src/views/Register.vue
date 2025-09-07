<!-- src/views/Register.vue -->
<template>
  <div class="auth-container">
    <h2>注册</h2>
    <el-form :model="form" label-width="80px" @submit.prevent="handleRegister">
      <el-form-item label="姓名">
        <el-input v-model="form.name" placeholder="请输入姓名" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="form.phone" placeholder="请输入手机号" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.password" type="password" placeholder="请输入密码" />
      </el-form-item>
      <el-form-item label="确认密码">
        <el-input v-model="form.confirmPassword" type="password" placeholder="请确认密码" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" native-type="submit">注册</el-button>
        <router-link to="/login">已有账号？去登录</router-link>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const form = ref({
  name: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const handleRegister = async () => {
  // 校验
  if (!form.value.name || !form.value.phone || !form.value.password) {
    alert('请填写所有字段')
    return
  }
  if (form.value.password !== form.value.confirmPassword) {
    alert('两次密码不一致')
    return
  }

  try {
    // ✅ 发送的数据结构完全匹配后端期望
    const data = {
      name: form.value.name,
      phone: form.value.phone,
      password: form.value.password
    }

    await request.post('/auth/register', data)
    alert('注册成功')
    router.push('/login')
  } catch (err) {
    alert('注册失败：' + (err.response?.data?.message || err.message))
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