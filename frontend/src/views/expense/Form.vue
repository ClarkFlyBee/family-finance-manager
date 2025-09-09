<template>
  <div class="form-container">
    <h2>{{ id ? '编辑' : '新增' }}支出</h2>
    <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
      <el-form-item label="分类" prop="categoryId">
        <!-- 使用 Cascader 展示树形分类 -->
        <el-cascader
          v-model="form.categoryId"
          :options="categoryOptions"
          :props="{ value: 'id', label: 'name', children: 'children', emitPath: false, checkStrictly: true }"
          placeholder="请选择分类"
          style="width: 100%"
          clearable
          filterable
        />
      </el-form-item>

      <el-form-item label="金额" prop="amount">
        <el-input
            v-model="form.amount"
            type="number"
            :step="0.01"
            :precision="2"     
            placeholder="请输入金额，如：99.99"
        />
    </el-form-item>

      <el-form-item label="时间" prop="expTime">
        <el-date-picker
          v-model="form.expTime"
          type="date"
          value-format="YYYY-MM-DD"
          format="YYYY-MM-DD"
          placeholder="选择支出日期"
          style="width: 100%"
        />
      </el-form-item>

      <el-form-item label="归属" prop="ownerType">
        <el-radio-group v-model="form.ownerType">
          <el-radio label="M">仅自己</el-radio>
          <el-radio label="F">全家人</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="备注">
        <el-input
          v-model="form.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入备注"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ id ? '更新' : '保存' }}
        </el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { onMounted, ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const id = route.params.id

const formRef = ref()
const form = reactive({
  categoryId: null,
  amount: 0,
  expTime: new Date(),
  remark: '',
  ownerType: 'M' // 默认为个人
})

const categoryOptions = ref([]) // 用于 cascader 的选项
const loadingCategories = ref(false)
const submitting = ref(false)

const rules = {
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  amount: [
    { required: true, message: '请输入金额', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        const num = Number(value)
        if (isNaN(num)) {
          callback(new Error('请输入有效数字'))
        } else if (num <= 0) {
          callback(new Error('金额必须大于0'))
        } else if (!/^\d+(\.\d{1,2})?$/.test(value)) {
          callback(new Error('最多保留两位小数'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  expTime: [{ required: true, message: '请选择时间', trigger: 'change' }],
  ownerType: [{ required: true, message: '请选择归属范围', trigger: 'change' }] // 新增
}

// 将嵌套结构映射为 el-cascader 所需格式（无需修改，直接使用）
const buildCategoryOptions = (list) => {
  return list.map(item => ({
    id: item.id,
    name: item.name,
    children: item.children && item.children.length > 0 ? buildCategoryOptions(item.children) : undefined
  })).filter(Boolean)
}

// 获取分类
const fetchCategories = async () => {
  loadingCategories.value = true
  try {
    const res = await request.get('/category?type=E')
    
    console.log('【DEBUG】完整响应:', res)
    console.log('【DEBUG】res.data:', res.data)
    console.log('【DEBUG】res.data.list:', res.data?.list)

    const list = res.data?.data?.list || []
    
    // 检查 list 是否为空
    if (list.length === 0) {
      console.warn('【警告】分类列表为空！')
    }

    // 检查第一个节点结构
    if (list[0]) {
      console.log('【DEBUG】第一个分类:', list[0])
      console.log('【DEBUG】第一个分类是否有 children:', list[0].children)
    }

    const options = buildCategoryOptions(list)
    console.log('【DEBUG】转换后的 options:', options)

    categoryOptions.value = options

    // 检查是否成功赋值
    console.log('【DEBUG】categoryOptions.value 已设置:', categoryOptions.value)

    if (!id && !form.categoryId && categoryOptions.value.length > 0) {
      const firstLeaf = findFirstLeaf(categoryOptions.value[0])
      form.categoryId = firstLeaf.id
      console.log('【DEBUG】自动选中第一个叶子节点:', firstLeaf)
    }
  } catch (err) {
    console.error('获取分类失败:', err)
    ElMessage.error('加载分类失败')
  } finally {
    loadingCategories.value = false
  }
}

// 辅助函数：找到第一个叶子节点（最深层的第一个分类）
const findFirstLeaf = (node) => {
  if (!node.children || node.children.length === 0) return node
  return findFirstLeaf(node.children[0])
}

// 获取详情
const fetchDetail = async () => {
  if (!id) return
  try {
    const res = await request.get(`/expense/${id}`)
    const data = res.data.data || res.data  // 兼容 data.data.data 或 data

    // ✅ 关键：必须把 ownerType 也赋值！
    form.categoryId = data.categoryId
    form.amount = data.amount
    form.expTime = new Date(data.expTime)   // 转成 Date 对象
    form.remark = data.remark
    form.ownerType = data.ownerType         // ⚠️ 你漏了这一行！

    console.log('【DEBUG】表单已填充:', form)
  } catch (err) {
    ElMessage.error('加载详情失败')
    router.back()
  }
}

const userId = ref(null)
const familyId = ref(null)

// 获取用户身份信息（从后端解析 token）
const fetchUserInfo = async () => {
  try {
    const res = await request.get('/auth/info') // 你后端需提供此接口
    const data = res.data.data
    userId.value = data.userId
    familyId.value = data.familyId
  } catch (err) {
    ElMessage.error('获取用户信息失败，请重新登录')
    throw err
  }
}

// 提交
const handleSubmit = async () => {
  await formRef.value.validate()

  submitting.value = true
  try {
    // 确保用户信息已加载
    if (!userId.value || !familyId.value) {
      await fetchUserInfo()
    }

    // 构造 ownerId
    const ownerId = form.ownerType === 'M' ? userId.value : familyId.value

    const payload = {
      ...form,
      ownerId, // 动态注入
      // 不传 ownerType 也可以，看后端是否需要
      // ownerType 可选择性保留或删除
    }

    if (id) {
      await request.put(`/expense/${id}`, payload)
      ElMessage.success('更新成功')
    } else {
      await request.post('/expense', payload)
      ElMessage.success('新增成功')
    }
    router.push('/expense')
  } catch (err) {
    ElMessage.error(id ? '更新失败' : '保存失败')
  } finally {
    submitting.value = false
  }
}

import { watch } from 'vue'

watch(() => route.params.id, (newId) => {
  if (newId) {
    fetchDetail()
  }
})

onMounted(() => {
  fetchCategories()
  fetchDetail()
  fetchUserInfo() // 获取用户身份
})
</script>

<style scoped>
.form-container {
  max-width: 600px;
  margin: 20px auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}
</style>