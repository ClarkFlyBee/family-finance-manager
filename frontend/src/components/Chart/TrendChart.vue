<!-- src/components/Chart/TrendChart.vue -->
<template>
  <div class="trend-chart-container">
    <!-- 粒度切换 + 显示开关 -->
    <div class="controls">
      <el-radio-group v-model="granularity" size="small" @change="fetchData">
        <el-radio label="daily">按日统计</el-radio>
        <el-radio label="monthly">按月统计</el-radio>
      </el-radio-group>

      <div class="display-controls">
        <el-checkbox v-model="showIncome" @change="renderChart">显示收入</el-checkbox>
        <el-checkbox v-model="showExpense" @change="renderChart">显示支出</el-checkbox>
      </div>
    </div>

    <!-- 图表容器 -->
    <div ref="chartRef" class="chart" :style="{ height: height }"></div>

    <!-- 加载中 / 无数据 -->
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="data.length === 0" class="no-data">暂无数据</div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import * as echarts from 'echarts'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const props = defineProps({
  query: {
    type: Object,
    required: true
  },
  height: {
    type: String,
    default: '400px'
  }
})

// 图表实例
const chartRef = ref(null)
const chartInstance = ref(null)

// 状态
const loading = ref(false)
const data = ref([])
const granularity = ref('daily')

// 👉 新增：控制是否显示收入/支出
const showIncome = ref(true)
const showExpense = ref(true)

// 日期格式化工具
const formatDate = (dateStr, isMonth) => {
  const date = new Date(dateStr)
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return isMonth ? `${y}-${m}` : `${y}-${m}-${d}`
}

// 补全日期（防止断线）
const fillMissingDates = (rawData, isMonth) => {
  if (rawData.length === 0) return []

  const start = new Date(props.query.startTime)
  const end = new Date(props.query.endTime)
  const result = []
  const map = new Map(rawData.map(item => [item.date, item]))

  let current = new Date(start)
  while (current <= end) {
    const key = formatDate(current, isMonth)
    const item = map.get(key) || {
      date: key,
      income: 0,
      expense: 0
    }
    result.push(item)
    if (isMonth) {
      current.setMonth(current.getMonth() + 1)
      if (current.getDate() > 1) current.setDate(1) // 保证每月1号对齐
    } else {
      current.setDate(current.getDate() + 1)
    }
  }

  return result
}

// 获取数据
const fetchData = async () => {
  if (!props.query.ownerId || !props.query.startTime || !props.query.endTime) {
    ElMessage.warning('请先选择完整查询条件')
    return
  }

  loading.value = true
  try {
    const res = await request.get('/statistics/trend', {
      params: {
        ownerId: props.query.ownerId,
        ownerType: props.query.ownerType,     // ✅ 确保带上 ownerType
        startTime: props.query.startTime,
        endTime: props.query.endTime,
        granularity: granularity.value
      }
    })

    const rawData = res.data.data?.data || []
    data.value = fillMissingDates(rawData, granularity.value === 'monthly')
    renderChart() // 数据更新后重新渲染
  } catch (err) {
    ElMessage.error('加载趋势数据失败：' + (err.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 渲染图表
const renderChart = () => {
  if (!chartInstance.value) return

  const dates = data.value.map(item => item.date)
  const incomes = data.value.map(item => parseFloat(item.income))
  const expenses = data.value.map(item => parseFloat(item.expense))

  // 动态生成 series
  const series = []
  if (showIncome.value) {
    series.push({
      name: '收入',
      type: 'line',
      data: incomes,
      smooth: true,
      itemStyle: { color: '#67C23A' },
      areaStyle: { opacity: 0.1 }
    })
  }
  if (showExpense.value) {
    series.push({
      name: '支出',
      type: 'line',
      data: expenses,
      smooth: true,
      itemStyle: { color: '#F56C6C' },
      areaStyle: { opacity: 0.1 }
    })
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        const date = params[0].name
        return `
          <strong>${date}</strong><br/>
          ${params.map(p => `${p.seriesName}：¥${p.value}`).join('<br/>')}
        `
      }
    },
    legend: {
      data: ['收入', '支出'].filter(name =>
        (name === '收入' && showIncome.value) ||
        (name === '支出' && showExpense.value)
      ),
      bottom: 10
    },
    grid: {
      left: '8%',     // ✅ 增大左侧间距避免 y 轴标签被遮挡
      right: '5%',
      bottom: '20%',
      top: '10%',
      containLabel: true  // ✅ 自动留白，防止标签被裁剪
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        rotate: 45,
        margin: 10
      }
    },
    yAxis: {
      type: 'value',
      name: '金额 (¥)',
      splitLine: { show: true, lineStyle: { type: 'dashed' } }
    },
    series,
    animationDuration: 800
  }

  chartInstance.value.setOption(option, true)
}

// 初始化图表
onMounted(() => {
  chartInstance.value = echarts.init(chartRef.value)
  window.addEventListener('resize', () => chartInstance.value?.resize())
  fetchData()
})

// 销毁实例
onBeforeUnmount(() => {
  window.removeEventListener('resize', () => {})
  chartInstance.value?.dispose()
})

// 监听 query 变化（如时间范围、ownerType 改变）
watch(() => props.query, fetchData, { deep: true })
</script>

<style scoped>
.trend-chart-container {
  position: relative;
  margin-top: 10px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.controls {
  padding: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f9f9f9;
  border-bottom: 1px solid #eee;
}

.display-controls .el-checkbox {
  margin-left: 15px;
}

.chart {
  width: 100%;
  min-height: 300px;
}

.loading,
.no-data {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #999;
  font-size: 16px;
}
</style>