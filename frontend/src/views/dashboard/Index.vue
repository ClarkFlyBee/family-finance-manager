<!-- src/views/dashboard/Index.vue -->
<template>
  <div class="dashboard-page">
    <h1>家庭财务仪表盘</h1>
    <!-- 可以在这里添加更多的数据卡片或图表 -->
    <TrendChart :query="chartQuery" height="400px" />
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import TrendChart from '@/components/Chart/TrendChart.vue'

// 图表查询条件（示例）
const chartQuery = reactive({
  ownerId: 1, // 示例ID，请根据实际情况调整
  ownerType: 'M',
  startTime: '2025-08-01', // 示例时间范围
  endTime: '2025-09-07'
})
</script>

<style scoped>
.dashboard-page {
  padding: 20px;
}
</style>