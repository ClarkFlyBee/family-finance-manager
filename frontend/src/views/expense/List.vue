<template>
  <div class="page-container">
    <h2>支出记录</h2>
    <el-button type="primary" @click="$router.push('/expense/add')">新增支出</el-button>

    <el-table :data="list" style="width: 100%" :loading="loading">
      <el-table-column prop="expNo" label="编号" width="120" />
      <el-table-column prop="amount" label="金额" width="100" />
      <el-table-column prop="categoryName" label="分类" width="100" />
      <el-table-column prop="expTime" label="时间" width="160" />
      <el-table-column prop="remark" label="备注" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="$router.push(`/expense/edit/${row.id}`)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import request from '@/utils/request'

const list = ref([])
const loading = ref(false)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/expense')
    list.value = res.data.data
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await request.delete(`/expense/${id}`)
    fetchData()
  } catch (err) {
    alert('删除失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>