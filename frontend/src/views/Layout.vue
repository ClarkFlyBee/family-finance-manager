<!-- src/views/Layout.vue -->
<template>
  <div class="layout">
    <el-container>
      <el-aside width="200px">
        <el-menu router :default-active="$route.path">
          <el-menu-item index="/expense">
            <span>支出管理</span>
          </el-menu-item>
          <el-menu-item index="/income">
            <span>收入管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>
.layout {
  height: 100vh;
}
</style>