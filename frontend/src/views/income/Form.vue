<!-- src/views/income/Form.vue -->
<template>
  <div class="form-container">
    <h2>{{ id ? '编辑' : '新增' }}收入</h2>
    <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
      <el-form-item label="分类" prop="categoryId">
        <el-cascader
          v-model="form.categoryId"
          :options="categoryOptions"
          :props="{
            value: 'id',
            label: 'name',
            children: 'children',
            emitPath: false,
            checkStrictly: true
          }"
          placeholder="请选择分类"
          style="width: 100%"
          clearable
          filterable
        />
      </el-form-item>

      <el-form-item label="金额" prop="amount">
        <el-input v-model.number="form.amount" type="number" placeholder="请输入金额" />
      </el-form-item>

      <el-form-item label="时间" prop="incTime">
        <el-date-picker
          v-model="form.incTime"
          type="date"
          value-format="YYYY-MM-DD"
          format="YYYY-MM-DD"
          placeholder="选择收入日期"
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
  incTime: new Date(), // 注意字段为 incTime
  remark: '',
  ownerType: 'M' // 默认为个人
})

const categoryOptions = ref([])
const loadingCategories = ref(false)
const submitting = ref(false)

const rules = {
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  amount: [
    { required: true, message: '请输入金额', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '金额必须大于0', trigger: 'blur' }
  ],
  incTime: [{ required: true, message: '请选择时间', trigger: 'change' }],
  ownerType: [{ required: true, message: '请选择归属范围', trigger: 'change' }]
}

const buildCategoryOptions = (list) => {
  return list.map(item => ({
    id: item.id,
    name: item.name,
    children: item.children && item.children.length > 0
      ? buildCategoryOptions(item.children)
      : undefined
  })).filter(Boolean)
}

const fetchCategories = async () => {
  loadingCategories.value = true
  try {
    const res = await request.get('/category', { params: { type: 'I' } })
    const list = res.data?.data?.list || []

    if (list.length === 0) {
      console.warn('【警告】收入分类列表为空！')
    }

    const options = buildCategoryOptions(list)
    categoryOptions.value = options

    if (!id && !form.categoryId && categoryOptions.value.length > 0) {
      const firstLeaf = findFirstLeaf(categoryOptions.value[0])
      form.categoryId = firstLeaf.id
    }
  } catch (err) {
    console.error('获取收入分类失败:', err)
    ElMessage.error('加载分类失败')
  } finally {
    loadingCategories.value = false
  }
}

const findFirstLeaf = (node) => {
  if (!node.children || node.children.length === 0) return node
  return findFirstLeaf(node.children[0])
}

const fetchDetail = async () => {
  if (!id) return
  try {
    const res = await request.get(`/income/${id}`)
    const data = res.data.data

    form.categoryId = data.categoryId
    form.amount = data.amount
    form.incTime = new Date(data.incTime)
    form.remark = data.remark
    form.ownerType = data.ownerType
  } catch (err) {
    ElMessage.error('加载收入详情失败')
    router.back()
  }
}

const userId = ref(null)
const familyId = ref(null)

const fetchUserInfo = async () => {
  try {
    const res = await request.get('/auth/info')
    const data = res.data.data
    userId.value = data.userId
    familyId.value = data.familyId
  } catch (err) {
    ElMessage.error('获取用户信息失败，请重新登录')
    throw err
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true

  try {
    if (!userId.value || !familyId.value) {
      await fetchUserInfo()
    }

    const ownerId = form.ownerType === 'M' ? userId.value : familyId.value

    const payload = {
      ...form,
      ownerId
    }

    if (id) {
      await request.put(`/income/${id}`, payload)
      ElMessage.success('更新成功')
    } else {
      await request.post('/income', payload)
      ElMessage.success('新增成功')
    }
    router.push('/income')
  } catch (err) {
    ElMessage.error(id ? '更新失败' : '保存失败')
  } finally {
    submitting.value = false
  }
}

import { watch } from 'vue'
watch(() => route.params.id, (newId) => {
  if (newId) fetchDetail()
})

onMounted(() => {
  fetchCategories()
  fetchDetail()
  fetchUserInfo()
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