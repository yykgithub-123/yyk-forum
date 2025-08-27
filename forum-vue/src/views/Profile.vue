<template>
  <div class="profile-container">
    <header class="header">
      <div class="header-container">
        <div class="logo">
          <router-link to="/">论坛系统</router-link>
        </div>
        <div class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/article-list">文章列表</router-link>
          <router-link to="/article-edit" v-if="isLoggedIn">发布文章</router-link>
        </div>
        <div class="user-info" v-if="isLoggedIn">
          <el-dropdown trigger="click">
            <span class="el-dropdown-link">
              {{ username }}<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>
                <router-link to="/profile">个人主页</router-link>
              </el-dropdown-item>
              <el-dropdown-item>
                <router-link to="/settings">设置</router-link>
              </el-dropdown-item>
              <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </header>
    
    <div class="main-content">
      <div v-loading="loading" class="profile-content">
        <div v-if="userProfile" class="user-profile">
          <div class="profile-header">
            <div class="avatar">
              <img :src="userProfile.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" alt="用户头像">
            </div>
            <div class="user-info">
              <h2>{{ userProfile.username }}</h2>
              <p class="bio">{{ userProfile.bio || '这个人很懒，什么都没有留下' }}</p>
              <p class="join-time">注册时间: {{ formatDate(userProfile.createTime) }}</p>
            </div>
          </div>
          
          <div class="user-stats">
            <div class="stat-item">
              <div class="stat-value">{{ userProfile.articleCount || 0 }}</div>
              <div class="stat-label">文章</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ userProfile.likeCount || 0 }}</div>
              <div class="stat-label">获赞</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ userProfile.viewCount || 0 }}</div>
              <div class="stat-label">浏览</div>
            </div>
          </div>
          
          <div class="user-articles">
            <h3>发布的文章</h3>
            <div v-if="userArticles.length > 0">
              <div v-for="article in userArticles" :key="article.id" class="article-item">
                <h4>
                  <router-link :to="'/article/' + article.id">{{ article.title }}</router-link>
                </h4>
                <div class="article-meta">
                  <span>发布时间: {{ formatDate(article.createTime) }}</span>
                  <span>浏览: {{ article.viewCount }}</span>
                  <span>点赞: {{ article.likeCount || 0 }}</span>
                </div>
                <p class="article-summary">{{ article.summary }}</p>
              </div>
            </div>
            <div v-else class="empty-articles">
              暂无发布的文章
            </div>
          </div>
        </div>
        <div v-else-if="!loading" class="profile-not-found">
          用户不存在或已被删除
        </div>
      </div>
    </div>
    
    <footer class="footer">
      <p>© 2023 论坛系统 - 基于Vue的前后端分离项目</p>
    </footer>
  </div>
</template>

<script>
import { getUserProfile, getUserArticles } from '@/api/user'

export default {
  name: 'Profile',
  data() {
    return {
      loading: false,
      userProfile: null,
      userArticles: [],
      isLoggedIn: false,
      username: '',
      userId: null
    }
  },
  created() {
    this.checkLoginStatus()
    this.fetchUserProfile()
    this.fetchUserArticles()
  },
  methods: {
    checkLoginStatus() {
      const token = localStorage.getItem('token')
      const username = localStorage.getItem('username')
      const userId = localStorage.getItem('userId')
      if (token && username) {
        this.isLoggedIn = true
        this.username = username
        this.userId = userId
      } else {
        // 未登录，跳转到登录页
        this.$message.warning('请先登录')
        this.$router.push('/sign-in')
      }
    },
    async fetchUserProfile() {
      this.loading = true
      try {
        const userId = this.$route.query.id || this.userId
        const res = await getUserProfile(userId)
        this.userProfile = res.data
      } catch (error) {
        console.error('获取用户资料失败:', error)
        this.$message.error('获取用户资料失败')
      } finally {
        this.loading = false
      }
    },
    async fetchUserArticles() {
      try {
        const userId = this.$route.query.id || this.userId
        const res = await getUserArticles(userId)
        this.userArticles = res.data
      } catch (error) {
        console.error('获取用户文章失败:', error)
      }
    },
    formatDate(date) {
      if (!date) return ''
      const d = new Date(date)
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    },
    logout() {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('userId')
      this.isLoggedIn = false
      this.username = ''
      this.userId = null
      this.$message.success('退出登录成功')
      this.$router.push('/sign-in')
    }
  }
}
</script>

<style scoped>
.profile-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.header {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
  max-width: 1200px;
  margin: 0 auto;
}

.logo a {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
  text-decoration: none;
}

.nav {
  display: flex;
  gap: 20px;
}

.nav a {
  color: #333;
  text-decoration: none;
}

.nav a:hover {
  color: #409EFF;
}

.user-info {
  cursor: pointer;
}

.el-dropdown-link {
  color: #409EFF;
}

.main-content {
  flex: 1;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.profile-header {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
}

.avatar {
  margin-right: 20px;
}

.avatar img {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
}

.user-info h2 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #303133;
}

.bio {
  color: #606266;
  margin-bottom: 10px;
}

.join-time {
  color: #909399;
  font-size: 12px;
}

.user-stats {
  display: flex;
  margin-bottom: 30px;
  background-color: #f5f7fa;
  border-radius: 4px;
  padding: 20px;
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  color: #606266;
}

.user-articles h3 {
  margin-bottom: 20px;
  color: #303133;
}

.article-item {
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.article-item h4 {
  margin-top: 0;
  margin-bottom: 10px;
}

.article-item h4 a {
  color: #303133;
  text-decoration: none;
}

.article-item h4 a:hover {
  color: #409EFF;
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  color: #909399;
  font-size: 12px;
  margin-bottom: 10px;
}

.article-summary {
  color: #606266;
  margin: 10px 0;
  line-height: 1.5;
}

.empty-articles {
  text-align: center;
  padding: 30px 0;
  color: #909399;
}

.profile-not-found {
  text-align: center;
  padding: 50px 0;
  color: #909399;
  font-size: 18px;
}

.footer {
  background-color: #f5f7fa;
  padding: 20px;
  text-align: center;
  color: #909399;
}
</style> 