<template>
  <div class="container">
    <h3>支出记录</h3>
    <el-button type="primary" @click="$router.push('/expense/add')">新增支出</el-button>

    <el-table :data="list" style="width: 100%" :loading="loading">
      <el-table-column prop="expNo" label="编号" width="200">
        <template #default="{ row }">
          {{ row.expNo }}
        </template>
      </el-table-column>
      <el-table-column prop="amount" label="金额" width="100" align="right" />
      
      <!-- 使用映射表显示分类名 -->
      <el-table-column label="分类" width="120">
        <template #default="{ row }">
          {{ row.categoryName || '未知分类' }}
        </template>
      </el-table-column>

      <el-table-column prop="expTime" label="时间" width="160">
        <template #default="{ row }">
          {{ new Date(row.expTime).toLocaleDateString() }}
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="200" />
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useRouter } from 'vue-router'

const list = ref([])
const loading = ref(false)
const router = useRouter()

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
    const res = await request.get('/category', { params: { type: 'E' } })

    // 👇 正确提取：res.data.data.list
    const categoryList = res.data.data?.list

    if (Array.isArray(categoryList)) {
      // 将树形结构扁平化，生成 { categoryId: category } 的映射
      categoryMap.value = flattenCategories(categoryList)
    } else {
      console.warn('分类数据格式异常，预期为数组:', categoryList)
      categoryMap.value = {}
    }

  } catch (err) {
    console.error('加载分类失败:', err)
    ElMessage.error('加载分类失败')
    categoryMap.value = {}
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/expense')

    // 👇 正确提取：res.data.data.data
    const innerData = res.data.data  // { total: 2, data: [...] }

    if (innerData && Array.isArray(innerData.data)) {
      list.value = innerData.data  // ✅ 取出真正的数组
    } else {
      list.value = []
      ElMessage.warning('数据格式异常，已重置为空列表')
    }

  } catch (err) {
    ElMessage.error('加载失败')
    list.value = []
  } finally {
    loading.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除这条记录吗？', '警告', { type: 'warning' })
    await request.delete(`/expense/${id}`)
    ElMessage.success('删除成功')
    fetchData() // 刷新列表
  } catch (err) {
    // 取消或失败
  }
}

onMounted(() => {
  fetchCategories()
  fetchData()
  
  // 👇 添加以下调试代码（关键！）
  const debugTimer = setTimeout(() => {
    console.log('🔍 当前页面数据 list:', list.value)
    console.log('🔍 当前分类映射表 categoryMap:', categoryMap.value)
    
    if (list.value.length > 0) {
      const firstItem = list.value[0]
      console.log('🧪 第一条数据的 categoryId:', firstItem.categoryId)
      console.log('❓ 映射表中是否有该 id?', categoryMap.value[firstItem.categoryId])
    }
  }, 1000) // 等待数据加载

  // 暴露 debug 对象到 window（方便你在浏览器控制台手动调用）
  window.debugExpense = {
    list: list,
    categoryMap: categoryMap,
    refresh: () => {
      console.log('🔁 手动刷新调试信息')
      console.log('list:', list.value)
      console.log('categoryMap:', categoryMap.value)
      if (list.value.length > 0) {
        const item = list.value[0]
        console.log('第一条 categoryId:', item.categoryId)
        console.log('查映射表:', categoryMap.value[item.categoryId])
      }
    }
  }
})
</script>