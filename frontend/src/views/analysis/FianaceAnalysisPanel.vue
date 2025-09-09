<template>
  <div class="finance-analysis-panel">
    <!-- 时间选择 -->
    <div class="time-selector">
      <el-date-picker
        v-model="timeRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        format="YYYY-MM-DD"
        value-format="YYYY-MM-DD"
        :clearable="false"
      />
      <el-button
        type="primary"
        :loading="loading"
        @click="handleSuggest"
        style="margin-left: 10px"
      >
        智能分析
      </el-button>
    </div>

    <!-- 对话区域 -->
    <div class="chat-area" ref="chatAreaRef">
      <div
        v-for="(msg, index) in messages"
        :key="index"
        :class="['message', msg.role]"
      >
        <strong>{{ msg.role === 'user' ? '你' : 'AI' }}：</strong>
        <!-- <Markdown v-if="msg.role === 'ai'">{{ msg.content }}</Markdown> -->
        <Markdown v-if="msg.role === 'ai'" :source="msg.content" />
        <span v-else>{{ msg.content }}</span>
      </div>
      <div v-if="messages.length === 0" class="empty-tip">
        选择时间范围，点击「智能分析」获取财务报告
      </div>
    </div>

    <!-- 提问输入区 -->
    <div class="input-area">
      <el-input
        v-model="input"
        placeholder="请输入你的财务问题，例如：为什么支出这么高？"
        @keyup.enter="send"
        :disabled="loading"
      />
      <el-button
        type="primary"
        :loading="loading"
        @click="send"
        :disabled="!input.trim()"
      >
        发送
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, shallowRef, nextTick, onMounted, watch } from 'vue'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'
import Markdown from 'vue3-markdown-it'
import request from '@/utils/request'

// DOM 引用（用于滚动到底部）
const chatAreaRef = shallowRef()

// 时间选择
const timeRange = ref([
  dayjs().subtract(1, 'month').format('YYYY-MM-DD'),
  dayjs().format('YYYY-MM-DD')
])

// 对话消息
const messages = ref([])

// 输入与加载
const input = ref('')
const loading = ref(false)

// 格式化时间范围
const formatTimeRange = () => {
  return {
    startTime: timeRange.value?.[0] || '',
    endTime: timeRange.value?.[1] || ''
  }
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (chatAreaRef.value) {
      chatAreaRef.value.scrollTop = chatAreaRef.value.scrollHeight
    }
  })
}

// 一键智能分析
const handleSuggest = async () => {
  if (!timeRange.value || !timeRange.value[0] || !timeRange.value[1]) {
    ElMessage.warning('请先选择时间范围')
    return
  }

  const { startTime, endTime } = formatTimeRange()
  messages.value = [] // 清空历史
  loading.value = true

  try {
    const res = await request.get('/analysis/suggest', {
      params: { startTime, endTime }
    })
    if (res.data?.data) {
      console.log('AI 回复内容：', res.data.data) // 🔥 打印看看  
      messages.value.push({
        role: 'ai',
        content: res.data.data
      })
    } else {
      ElMessage.error('未获取到分析结果')
    }
  } catch (err) {
    ElMessage.error('分析失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 发送自定义问题
const send = async () => {
  if (!input.value.trim() || loading.value) return

  const question = input.value.trim()
  messages.value.push({ role: 'user', content: question })
  input.value = ''
  loading.value = true

  try {
    const { startTime, endTime } = formatTimeRange()
    const res = await request.post('/analysis/chat', {
      query: { startTime, endTime },
      question
    })
    if (res.data?.data) {
      console.log('AI 回复内容：', res.data.data) // 🔥 打印看看  
      messages.value.push({
        role: 'ai',
        content: res.data.data
      })
    } else {
      ElMessage.error('AI 未返回有效内容')
    }
  } catch (err) {
    ElMessage.error('AI 服务异常')
  } finally {
    loading.value = false
  }
}

// 自动滚动到底部
watch(messages, scrollToBottom, { deep: true })

// 组件挂载后自动滚动
onMounted(() => {
  scrollToBottom()
})
</script>

<style lang="scss" scoped>
.finance-analysis-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.time-selector {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.chat-area {
  flex: 1;
  overflow-y: auto;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 15px;
  background-color: white;
  margin-bottom: 20px;
  min-height: 300px;
}

.message {
  margin-bottom: 15px;
  line-height: 1.6;
}

.message.user {
  color: #333;
}

.message.ai {
  color: #444;
}

.empty-tip {
  text-align: center;
  color: #999;
  font-style: italic;
  margin-top: 20px;
}

.input-area {
  display: flex;
  gap: 10px;
}

/* Markdown 样式增强 */
.message.ai :deep(p) {
  margin: 0.5em 0;
}
.message.ai :deep(strong) {
  color: #333;
}
.message.ai :deep(ul) {
  margin: 0.5em 0;
  padding-left: 20px;
}
</style>