<template>
  <div class="user-profile-page">
    <h2>个人信息管理</h2>

    <!-- 用户信息卡片 -->
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
        </div>
      </template>

      <el-descriptions :column="1" size="medium" border>
        <el-descriptions-item label="用户名">{{ userInfo.name }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ userInfo.phone }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag size="small" :type="roleType">{{ roleName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="所属家庭">
          <el-tag type="primary">{{ userInfo.familyName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatTime(userInfo.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatTime(userInfo.updatedAt) }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 加入家庭 -->
    <el-card class="family-card">
      <template #header>
        <div class="card-header">
          <span>加入其他家庭</span>
        </div>
      </template>

      <el-form label-width="100px" style="max-width: 400px">
        <el-form-item label="家庭ID">
          <el-input
            v-model.number="familyIdInput"
            placeholder="请输入要加入的家庭ID（邀请码）"
            type="number"
            :min="1"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="joining"
            @click="handleJoinFamily"
            :disabled="!familyIdInput"
          >
            加入家庭
          </el-button>
        </el-form-item>
      </el-form>

      <div class="tip">
        💡 提示：输入家庭ID即可加入，相当于“邀请码”。请确保ID正确。
      </div>
    </el-card>

    <!-- 修改密码 -->
    <el-card class="password-card">
      <template #header>
        <div class="card-header">
          <span>修改密码</span>
        </div>
      </template>

      <el-form
        :model="passwordForm"
        :rules="passwordRules"
        ref="passwordFormRef"
        label-width="100px"
        style="max-width: 400px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入原密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码（至少6位）"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button type="warning" @click="resetForm">重置</el-button>
          <el-button type="success" :loading="changing" @click="handleChangePassword">
            修改密码
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 数据定义
const userInfo = ref({})
const familyIdInput = ref(null)
const joining = ref(false)
const changing = ref(false)
const passwordFormRef = ref(null)

const passwordForm = ref({
  oldPassword: '',
  newPassword: ''
})

// 表单校验规则
const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码不能少于6位', trigger: 'blur' }
  ]
}

// 角色映射
const roleMap = {
  U: '普通成员',
  A: '管理员'
}
const roleTypeMap = {
  U: 'info',
  A: 'success'
}

const roleName = computed(() => roleMap[userInfo.value.role] || '未知')
const roleType = computed(() => roleTypeMap[userInfo.value.role] || 'info')

// 格式化时间（简单处理，实际可用 dayjs）
const formatTime = (timeStr) => {
  return timeStr ? timeStr.replace('T', ' ') : '--'
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const res = await request.get('/user/info')
    if (res.data.code === 200) {
      userInfo.value = res.data.data
    } else {
      ElMessage.error(res.data.msg || '加载失败')
    }
  } catch (err) {
    ElMessage.error('网络错误，加载用户信息失败')
  }
}

// 加入家庭
const handleJoinFamily = () => {
  if (!familyIdInput.value) {
    ElMessage.warning('请输入家庭ID')
    return
  }

  ElMessageBox.confirm(
    `确定要加入 ID 为【${familyIdInput.value}】的家庭吗？`,
    '提示',
    { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' }
  ).then(async () => {
    joining.value = true
    try {
      const res = await request.post('/user/join-family', {
        familyId: familyIdInput.value
      })
      if (res.data.code === 200) {
        ElMessage.success(res.data.data)
        await loadUserInfo() // 刷新信息
        familyIdInput.value = null
      } else {
        ElMessage.error(res.data.msg)
      }
    } catch (err) {
      ElMessage.error(err.response?.data?.msg || '加入失败，请检查网络或ID是否正确')
    } finally {
      joining.value = false
    }
  }).catch(() => {
    ElMessage.info('已取消')
  })
}

// 修改密码
const handleChangePassword = () => {
  passwordFormRef.value.validate(async (valid) => {
    if (!valid) return

    changing.value = true
    try {
      const res = await request.post('/user/change-password', {
        oldPassword: passwordForm.value.oldPassword,
        newPassword: passwordForm.value.newPassword
      })

      if (res.data.code === 200) {
        ElMessage.success(res.data.data)
        resetForm()
      } else {
        ElMessage.error(res.data.msg)
      }
    } catch (err) {
      ElMessage.error(err.response?.data?.msg || '修改失败')
    } finally {
      changing.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  passwordFormRef.value.resetFields()
}

// 页面初始化
onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.user-profile-page {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  font-weight: 600;
}

.profile-card, .family-card, .password-card {
  margin-bottom: 20px;
}

.tip {
  font-size: 13px;
  color: #999;
  margin-top: 10px;
}
</style>