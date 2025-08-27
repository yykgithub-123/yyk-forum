<template>
  <div class="article-list-container">
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
      <h1 class="page-title">文章列表</h1>
      
      <div class="search-bar">
        <el-input
          placeholder="搜索文章"
          v-model="searchQuery"
          class="search-input"
          @keyup.enter.native="handleSearch"
        >
          <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
        </el-input>
      </div>
      
      <div v-loading="loading" class="article-list">
        <div v-for="article in articles" :key="article.id" class="article-item">
          <h3 class="article-title">
            <router-link :to="'/article/' + article.id">{{ article.title }}</router-link>
          </h3>
          <div class="article-meta">
            <span class="author">作者: {{ article.author }}</span>
            <span class="time">发布时间: {{ formatDate(article.createTime) }}</span>
            <span class="views">浏览: {{ article.viewCount }}</span>
            <span class="likes">点赞: {{ article.likeCount || 0 }}</span>
          </div>
          <p class="article-summary">{{ article.summary }}</p>
          <div class="article-actions" v-if="isLoggedIn && article.authorId === userId">
            <el-button size="mini" type="primary" @click="editArticle(article.id)">编辑</el-button>
            <el-button size="mini" type="danger" @click="deleteArticle(article.id)">删除</el-button>
          </div>
        </div>
        
        <div v-if="articles.length === 0 && !loading" class="empty-list">
          暂无文章
        </div>
        
        <el-pagination
          v-if="total > 0"
          @current-change="handleCurrentChange"
          :current-page.sync="page"
          :page-size="size"
          layout="prev, pager, next"
          :total="total"
          class="pagination"
        ></el-pagination>
      </div>
    </div>
    
    <footer class="footer">
      <p>© 2023 论坛系统 - 基于Vue的前后端分离项目</p>
    </footer>
  </div>
</template>

<script>
import { getArticleList, deleteArticle } from '@/api/article'

export default {
  name: 'ArticleList',
  data() {
    return {
      loading: false,
      articles: [],
      page: 1,
      size: 10,
      total: 0,
      searchQuery: '',
      isLoggedIn: false,
      username: '',
      userId: null
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
      const userId = localStorage.getItem('userId')
      if (token && username) {
        this.isLoggedIn = true
        this.username = username
        this.userId = userId
      }
    },
    async fetchArticles() {
      this.loading = true
      try {
        const res = await getArticleList({
          page: this.page,
          size: this.size,
          keyword: this.searchQuery
        })
        this.articles = res.data
        this.total = res.total
      } catch (error) {
        console.error('获取文章列表失败:', error)
        this.$message.error('获取文章列表失败')
      } finally {
        this.loading = false
      }
    },
    handleCurrentChange(val) {
      this.page = val
      this.fetchArticles()
    },
    handleSearch() {
      this.page = 1
      this.fetchArticles()
    },
    editArticle(id) {
      this.$router.push(`/article-edit?id=${id}`)
    },
    async deleteArticle(id) {
      this.$confirm('确定要删除这篇文章吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteArticle(id)
          this.$message.success('删除成功')
          // 重新获取文章列表
          this.fetchArticles()
        } catch (error) {
          console.error('删除文章失败:', error)
          this.$message.error('删除文章失败')
        }
      }).catch(() => {
        // 取消删除
      })
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
.article-list-container {
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
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #303133;
}

.search-bar {
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.article-list {
  margin-top: 20px;
}

.article-item {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
  position: relative;
}

.article-title {
  margin-top: 0;
  margin-bottom: 10px;
}

.article-title a {
  color: #303133;
  text-decoration: none;
}

.article-title a:hover {
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

.article-actions {
  margin-top: 10px;
}

.empty-list {
  text-align: center;
  padding: 30px 0;
  color: #909399;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}

.footer {
  background-color: #f5f7fa;
  padding: 20px;
  text-align: center;
  color: #909399;
}
</style> 