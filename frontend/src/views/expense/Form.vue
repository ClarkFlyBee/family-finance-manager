<template>
  <div class="form-container">
    <h2>{{ id ? '编辑' : '新增' }}支出</h2>
    <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
      <el-form-item label="分类" prop="categoryId">
        <el-select v-model="form.categoryId">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="金额" prop="amount">
        <el-input v-model.number="form.amount" type="number" />
      </el-form-item>
      <el-form-item label="时间" prop="expTime">
        <el-date-picker v-model="form.expTime" type="datetime" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { onMounted, ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const id = route.params.id

const formRef = ref()
const form = reactive({
  categoryId: null,
  amount: 0,
  expTime: new Date(),
  remark: ''
})

const categories = ref([])

// 表单验证
const rules = {
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  expTime: [{ required: true, message: '请选择时间', trigger: 'change' }]
}

const fetchCategories = async () => {
  const res = await request.get('/category?type=expense')
  categories.value = res.data
}

const fetchDetail = async () => {
  if (!id) return
  const res = await request.get(`/expense/${id}`)
  const data = res.data
  form.categoryId = data.categoryId
  form.amount = data.amount
  form.expTime = new Date(data.expTime)
  form.remark = data.remark
}

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    if (id) {
      await request.put(`/expense/${id}`, form)
    } else {
      await request.post('/expense', form)
    }
    router.push('/expense')
  } catch (err) {
    alert('保存失败')
  }
}

onMounted(() => {
  fetchCategories()
  fetchDetail()
})
</script>