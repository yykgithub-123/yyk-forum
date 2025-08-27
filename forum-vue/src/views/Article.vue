<template>
  <div class="article-container">
    <header class="header">
      <div class="header-container">
        <div class="logo">
          <router-link to="/">论坛系统</router-link>
        </div>
        <div class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/article-list">文章列表</router-link>
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
      <div v-loading="loading" class="article-content">
        <div v-if="article">
          <h1 class="article-title">{{ article.title }}</h1>
          <div class="article-meta">
            <span class="author">作者: {{ article.author }}</span>
            <span class="time">发布时间: {{ formatDate(article.createTime) }}</span>
            <span class="views">浏览: {{ article.viewCount }}</span>
          </div>
          <div class="article-body" v-html="article.content"></div>
          
          <div class="article-actions">
            <el-button type="primary" @click="handleLike" :disabled="!isLoggedIn">
              <i class="el-icon-star-off"></i> 点赞 ({{ article.likeCount || 0 }})
            </el-button>
            <el-button type="success" @click="handleFavorite" :disabled="!isLoggedIn">
              <i class="el-icon-collection"></i> 收藏 ({{ article.favoriteCount || 0 }})
            </el-button>
          </div>
          
          <div class="comment-section">
            <h3>评论 ({{ comments.length }})</h3>
            <div v-if="isLoggedIn" class="comment-form">
              <el-input
                type="textarea"
                :rows="4"
                placeholder="发表你的评论..."
                v-model="commentContent"
              ></el-input>
              <el-button type="primary" @click="submitComment" :loading="commentLoading">提交评论</el-button>
            </div>
            <div v-else class="comment-login-tip">
              <router-link to="/sign-in">登录</router-link> 后发表评论
            </div>
            
            <div class="comment-list">
              <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <div class="comment-header">
                  <span class="comment-author">{{ comment.author }}</span>
                  <span class="comment-time">{{ formatDate(comment.createTime) }}</span>
                </div>
                <div class="comment-content">{{ comment.content }}</div>
              </div>
              <div v-if="comments.length === 0" class="no-comments">
                暂无评论，快来发表第一条评论吧！
              </div>
            </div>
          </div>
        </div>
        <div v-else-if="!loading" class="article-not-found">
          文章不存在或已被删除
        </div>
      </div>
    </div>
    
    <footer class="footer">
      <p>© 2023 论坛系统 - 基于Vue的前后端分离项目</p>
    </footer>
  </div>
</template>

<script>
import { getArticleDetail, getArticleComments, likeArticle, favoriteArticle } from '@/api/article'
import { createComment } from '@/api/article'

export default {
  name: 'Article',
  data() {
    return {
      loading: false,
      article: null,
      comments: [],
      commentContent: '',
      commentLoading: false,
      isLoggedIn: false,
      username: ''
    }
  },
  created() {
    this.checkLoginStatus()
    this.fetchArticleDetail()
    this.fetchComments()
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
    async fetchArticleDetail() {
      this.loading = true
      try {
        const articleId = this.$route.params.id
        const res = await getArticleDetail(articleId)
        this.article = res.data
      } catch (error) {
        console.error('获取文章详情失败:', error)
        this.$message.error('获取文章详情失败')
      } finally {
        this.loading = false
      }
    },
    async fetchComments() {
      try {
        const articleId = this.$route.params.id
        const res = await getArticleComments(articleId)
        this.comments = res.data
      } catch (error) {
        console.error('获取评论失败:', error)
      }
    },
    async submitComment() {
      if (!this.commentContent.trim()) {
        this.$message.warning('评论内容不能为空')
        return
      }
      
      this.commentLoading = true
      try {
        await createComment({
          articleId: this.$route.params.id,
          content: this.commentContent
        })
        this.$message.success('评论发表成功')
        this.commentContent = ''
        // 重新获取评论列表
        await this.fetchComments()
      } catch (error) {
        console.error('发表评论失败:', error)
      } finally {
        this.commentLoading = false
      }
    },
    async handleLike() {
      if (!this.isLoggedIn) {
        this.$message.warning('请先登录')
        return
      }
      
      try {
        await likeArticle(this.$route.params.id)
        this.$message.success('点赞成功')
        // 更新文章信息
        await this.fetchArticleDetail()
      } catch (error) {
        console.error('点赞失败:', error)
      }
    },
    async handleFavorite() {
      if (!this.isLoggedIn) {
        this.$message.warning('请先登录')
        return
      }
      
      try {
        await favoriteArticle(this.$route.params.id)
        this.$message.success('收藏成功')
        // 更新文章信息
        await this.fetchArticleDetail()
      } catch (error) {
        console.error('收藏失败:', error)
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
      this.isLoggedIn = false
      this.username = ''
      this.$message.success('退出登录成功')
      this.$router.push('/sign-in')
    }
  }
}
</script>

<style scoped>
.article-container {
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
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.article-title {
  font-size: 28px;
  margin-bottom: 20px;
  color: #303133;
}

.article-meta {
  display: flex;
  gap: 15px;
  color: #909399;
  font-size: 14px;
  margin-bottom: 30px;
}

.article-body {
  line-height: 1.8;
  color: #303133;
  margin-bottom: 30px;
}

.article-actions {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
}

.comment-section {
  margin-top: 40px;
  border-top: 1px solid #ebeef5;
  padding-top: 20px;
}

.comment-form {
  margin-bottom: 20px;
}

.comment-form .el-button {
  margin-top: 10px;
}

.comment-login-tip {
  margin-bottom: 20px;
  color: #909399;
}

.comment-login-tip a {
  color: #409EFF;
  text-decoration: none;
}

.comment-list {
  margin-top: 20px;
}

.comment-item {
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.comment-author {
  font-weight: bold;
  color: #303133;
}

.comment-time {
  color: #909399;
  font-size: 12px;
}

.comment-content {
  color: #606266;
  line-height: 1.5;
}

.no-comments {
  color: #909399;
  text-align: center;
  padding: 20px 0;
}

.article-not-found {
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