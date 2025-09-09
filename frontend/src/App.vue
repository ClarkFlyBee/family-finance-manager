<!-- src/App.vue -->
<template>
  <div class="app-container">
    <!-- 路由出口：当前匹配的组件会在这里渲染 -->
    <router-view />
  </div>
</template>

<script setup>
// 这里可以写全局逻辑（比如检查登录状态）
</script>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: "Arial", sans-serif;
  background-color: #f5f5f5;
}

.app-container {
  width: 100vw;
  height: 100vh;
}
</style>