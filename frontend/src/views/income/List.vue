<!-- src/views/income/List.vue -->
<template>
  <div class="container">
    <h3>收入记录</h3>
    <el-button type="primary" @click="$router.push('/income/add')">新增收入</el-button>

    <el-table :data="list" style="width: 100%" :loading="loading" class="mt-4">
      <el-table-column prop="incNo" label="编号" width="200">
        <template #default="{ row }">
          {{ row.incNo }}
        </template>
      </el-table-column>
      <el-table-column prop="amount" label="金额" width="100" align="right" />

      <!-- 使用映射表显示分类名 -->
      <el-table-column label="分类" width="120">
        <template #default="{ row }">
          {{ row.categoryName || '未知分类' }}
        </template>
      </el-table-column>

      <el-table-column prop="incTime" label="时间" width="160">
        <template #default="{ row }">
          {{ new Date(row.incTime).toLocaleDateString() }}
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="200" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="$router.push(`/income/edit/${row.id}`)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const list = ref([])
const loading = ref(false)

// 分类 id -> name 映射表
const categoryMap = ref({})

// 递归提取所有分类名称
const flattenCategories = (categories) => {
  const map = {}
  const traverse = (items) => {
    items.forEach(item => {
      map[item.id] = item.name
      if (item.children && item.children.length > 0) {
        traverse(item.children)
      }
    })
  }
  traverse(categories)
  return map
}

const fetchCategories = async () => {
  try {
    const res = await request.get('/category', { params: { type: 'I' } })
    const categoryList = res.data?.data?.list

    if (Array.isArray(categoryList)) {
      categoryMap.value = flattenCategories(categoryList)
    } else {
      console.warn('收入分类数据格式异常:', categoryList)
      categoryMap.value = {}
    }
  } catch (err) {
    console.error('加载收入分类失败:', err)
    ElMessage.error('加载分类失败')
    categoryMap.value = {}
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/income')

    const innerData = res.data?.data
    if (innerData && Array.isArray(innerData.data)) {
      list.value = innerData.data
    } else {
      list.value = []
      ElMessage.warning('收入数据格式异常')
    }
  } catch (err) {
    ElMessage.error('加载收入列表失败')
    list.value = []
  } finally {
    loading.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除这条收入记录吗？', '警告', { type: 'warning' })
    await request.delete(`/income/${id}`)
    ElMessage.success('删除成功')
    fetchData()
  } catch (err) {
    // 取消或删除失败
  }
}

onMounted(() => {
  fetchCategories()
  fetchData()

  // 调试工具
  window.debugIncome = {
    list: list,
    categoryMap: categoryMap,
    refresh: () => {
      console.log('🔁 收入调试信息:')
      console.log('list:', list.value)
      console.log('categoryMap:', categoryMap.value)
    }
  }
})
</script>

<style scoped>
.container {
  padding: 20px;
}
.mt-4 {
  margin-top: 16px;
}
</style>