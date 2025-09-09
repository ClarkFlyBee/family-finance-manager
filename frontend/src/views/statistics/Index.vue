<template>
  <div class="statistics-page">
    <h1>收支趋势分析</h1>

    <!-- 查询条件 -->
    <el-form :model="query" inline>
      <el-form-item label="时间范围">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="onDateRangeChange"
        />
      </el-form-item>
      <el-form-item label="归属">
        <el-radio-group v-model="query.ownerType" @change="fetchUserInfo">
          <el-radio label="M">仅自己</el-radio>
          <el-radio label="F">全家人</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>

    <!-- 趋势图 -->
    <TrendChart :query="query" height="500px" />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import TrendChart from '@/components/Chart/TrendChart.vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const dateRange = ref(['2025-08-01', '2025-09-07'])
const userId = ref(null)
const familyId = ref(null)

const query = reactive({
  ownerId: null,
  ownerType: 'M',
  startTime: '2025-08-01',
  endTime: '2025-09-07'
})

const onDateRangeChange = (val) => {
  if (val) {
    query.startTime = val[0]
    query.endTime = val[1]
  }
}

const fetchUserInfo = async () => {
  try {
    const res = await request.get('/auth/info')
    const data = res.data.data
    userId.value = data.userId
    familyId.value = data.familyId
    query.ownerId = query.ownerType === 'M' ? userId.value : familyId.value
  } catch (err) {
    ElMessage.error('获取用户信息失败')
  }
}

// 初始化
fetchUserInfo()
</script>

<style scoped>
.statistics-page {
  padding: 20px;
}
</style>