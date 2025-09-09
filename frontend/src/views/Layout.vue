<template>
  <div class="layout">
    <el-container>
      <el-aside width="200px">
        <el-menu 
          router 
          :default-active="$route.path"
          background-color="#f8f9fa"
          text-color="#333"
          active-text-color="#409eff"
        >
          <el-menu-item index="/dashboard">
            <span>📊 首页</span>
          </el-menu-item>
          <el-menu-item index="/expense">
            <span>💸 支出管理</span>
          </el-menu-item>
          <el-menu-item index="/income">
            <span>💰 收入管理</span>
          </el-menu-item>
          <!-- ✅ 新增：智能分析菜单 -->
          <el-menu-item index="/analysis">
            <span>🧠 智能分析</span>
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

/* 可选：美化菜单 */
.el-aside {
  box-shadow: 2px 0 6px rgba(0,0,0,0.1);
}
</style>