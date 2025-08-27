<template>
  <div class="home-container">
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
        <div class="login-btns" v-else>
          <router-link to="/sign-in">登录</router-link>
          <router-link to="/sign-up">注册</router-link>
        </div>
      </div>
    </header>
    
    <div class="main-content">
      <div class="banner">
        <h1>欢迎来到论坛系统</h1>
        <p>这里是一个分享知识、交流经验的平台</p>
      </div>
      
      <div class="article-list">
        <h2>最新文章</h2>
        <div v-loading="loading" class="articles">
          <div v-for="article in articles" :key="article.id" class="article-item">
            <h3>
              <router-link :to="'/article/' + article.id">{{ article.title }}</router-link>
            </h3>
            <div class="article-meta">
              <span>作者: {{ article.author }}</span>
              <span>发布时间: {{ formatDate(article.createTime) }}</span>
              <span>浏览: {{ article.viewCount }}</span>
            </div>
            <p class="article-summary">{{ article.summary }}</p>
          </div>
        </div>
        <div class="load-more" v-if="hasMore">
          <el-button @click="loadMore">加载更多</el-button>
        </div>
      </div>
    </div>
    
    <footer class="footer">
      <p>© 2023 论坛系统 - 基于Vue的前后端分离项目</p>
    </footer>
  </div>
</template>

<script>
import { getArticleList } from '@/api/article'

export default {
  name: 'Home',
  data() {
    return {
      loading: false,
      articles: [],
      page: 1,
      size: 10,
      hasMore: true,
      isLoggedIn: false,
      username: ''
    }
  },
  created() {
    this.checkLoginStatus()
    this.fetchArticles()
  },
  methods: {
    checkLoginStatus() {
      const token = localStorage.getItem('token')
      const username = localStorage.getItem('username')
      if (token && username) {
        this.isLoggedIn = true
        this.username = username
      }
    },
    async fetchArticles() {
      this.loading = true
      try {
        const res = await getArticleList({
          page: this.page,
          size: this.size
        })
        this.articles = [...this.articles, ...res.data]
        this.hasMore = res.data.length === this.size
      } catch (error) {
        console.error('获取文章列表失败:', error)
        this.$message.error('获取文章列表失败')
      } finally {
        this.loading = false
      }
    },
    loadMore() {
      this.page += 1
      this.fetchArticles()
    },
    formatDate(date) {
      if (!date) return ''
      const d = new Date(date)
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    },
    logout() {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      this.isLoggedIn = false
      this.username = ''
      this.$message.success('退出登录成功')
      this.$router.push('/sign-in')
    }
  }
}
</script>

<style scoped>
.home-container {
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

.login-btns {
  display: flex;
  gap: 15px;
}

.login-btns a {
  color: #409EFF;
  text-decoration: none;
}

.main-content {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.banner {
  background-color: #f5f7fa;
  padding: 40px;
  border-radius: 4px;
  text-align: center;
  margin-bottom: 30px;
}

.banner h1 {
  margin-top: 0;
  color: #303133;
}

.article-list {
  margin-top: 20px;
}

.article-item {
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.article-item h3 {
  margin-top: 0;
  margin-bottom: 10px;
}

.article-item h3 a {
  color: #303133;
  text-decoration: none;
}

.article-item h3 a:hover {
  color: #409EFF;
}

.article-meta {
  display: flex;
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

.load-more {
  text-align: center;
  margin-top: 20px;
}

.footer {
  background-color: #f5f7fa;
  padding: 20px;
  text-align: center;
  color: #909399;
}
</style> 