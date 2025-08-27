<template>
  <div class="article-edit-container">
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
      </div>
    </header>
    
    <div class="main-content">
      <h1 class="page-title">{{ isEdit ? '编辑文章' : '发布新文章' }}</h1>
      
      <el-form :model="articleForm" :rules="rules" ref="articleForm" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="articleForm.title" placeholder="请输入文章标题"></el-input>
        </el-form-item>
        
        <el-form-item label="摘要" prop="summary">
          <el-input type="textarea" :rows="3" v-model="articleForm.summary" placeholder="请输入文章摘要"></el-input>
        </el-form-item>
        
        <el-form-item label="内容" prop="content">
          <el-input type="textarea" :rows="15" v-model="articleForm.content" placeholder="请输入文章内容"></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">{{ isEdit ? '更新' : '发布' }}</el-button>
          <el-button @click="cancel">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <footer class="footer">
      <p>© 2023 论坛系统 - 基于Vue的前后端分离项目</p>
    </footer>
  </div>
</template>

<script>
import { createArticle, updateArticle, getArticleDetail } from '@/api/article'

export default {
  name: 'ArticleEdit',
  data() {
    return {
      isEdit: false,
      articleId: null,
      loading: false,
      isLoggedIn: false,
      username: '',
      articleForm: {
        title: '',
        summary: '',
        content: ''
      },
      rules: {
        title: [
          { required: true, message: '请输入文章标题', trigger: 'blur' },
          { min: 3, max: 100, message: '标题长度在 3 到 100 个字符', trigger: 'blur' }
        ],
        summary: [
          { required: true, message: '请输入文章摘要', trigger: 'blur' },
          { max: 200, message: '摘要最多 200 个字符', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入文章内容', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.checkLoginStatus()
    // 检查是否是编辑模式
    if (this.$route.query.id) {
      this.isEdit = true
      this.articleId = this.$route.query.id
      this.fetchArticleDetail()
    }
  },
  methods: {
    checkLoginStatus() {
      const token = localStorage.getItem('token')
      const username = localStorage.getItem('username')
      if (token && username) {
        this.isLoggedIn = true
        this.username = username
      } else {
        // 未登录，跳转到登录页
        this.$message.warning('请先登录')
        this.$router.push('/sign-in')
      }
    },
    async fetchArticleDetail() {
      this.loading = true
      try {
        const res = await getArticleDetail(this.articleId)
        const article = res.data
        this.articleForm.title = article.title
        this.articleForm.summary = article.summary
        this.articleForm.content = article.content
      } catch (error) {
        console.error('获取文章详情失败:', error)
        this.$message.error('获取文章详情失败')
      } finally {
        this.loading = false
      }
    },
    submitForm() {
      this.$refs.articleForm.validate(async valid => {
        if (valid) {
          this.loading = true
          try {
            if (this.isEdit) {
              // 更新文章
              await updateArticle({
                id: this.articleId,
                ...this.articleForm
              })
              this.$message.success('文章更新成功')
            } else {
              // 创建新文章
              await createArticle(this.articleForm)
              this.$message.success('文章发布成功')
            }
            // 跳转到文章列表页
            this.$router.push('/article-list')
          } catch (error) {
            console.error('保存文章失败:', error)
            this.$message.error('保存文章失败')
          } finally {
            this.loading = false
          }
        } else {
          return false
        }
      })
    },
    cancel() {
      this.$router.go(-1)
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
.article-edit-container {
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

.page-title {
  font-size: 24px;
  margin-bottom: 30px;
  color: #303133;
}

.footer {
  background-color: #f5f7fa;
  padding: 20px;
  text-align: center;
  color: #909399;
}
</style> 