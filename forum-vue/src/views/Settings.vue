<template>
  <div class="settings-container">
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
      <h1 class="page-title">账号设置</h1>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="个人资料" name="profile">
          <el-form :model="profileForm" :rules="profileRules" ref="profileForm" label-width="100px">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="profileForm.username" disabled></el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email"></el-input>
            </el-form-item>
            <el-form-item label="个人简介" prop="bio">
              <el-input type="textarea" :rows="4" v-model="profileForm.bio" placeholder="介绍一下自己吧"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updateProfile" :loading="profileLoading">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="修改密码" name="password">
          <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px">
            <el-form-item label="当前密码" prop="oldPassword">
              <el-input type="password" v-model="passwordForm.oldPassword" placeholder="请输入当前密码"></el-input>
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input type="password" v-model="passwordForm.newPassword" placeholder="请输入新密码"></el-input>
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input type="password" v-model="passwordForm.confirmPassword" placeholder="请确认新密码"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updatePassword" :loading="passwordLoading">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
    
    <footer class="footer">
      <p>© 2023 论坛系统 - 基于Vue的前后端分离项目</p>
    </footer>
  </div>
</template>

<script>
import { getUserInfo, updateUserInfo, updatePassword } from '@/api/user'

export default {
  name: 'Settings',
  data() {
    // 密码确认验证
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    
    return {
      activeTab: 'profile',
      isLoggedIn: false,
      username: '',
      userId: null,
      profileLoading: false,
      passwordLoading: false,
      
      profileForm: {
        username: '',
        email: '',
        bio: ''
      },
      profileRules: {
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        bio: [
          { max: 200, message: '个人简介不能超过200个字符', trigger: 'blur' }
        ]
      },
      
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入当前密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.checkLoginStatus()
    this.fetchUserInfo()
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
    async fetchUserInfo() {
      this.profileLoading = true
      try {
        const res = await getUserInfo()
        const userInfo = res.data
        this.profileForm.username = userInfo.username
        this.profileForm.email = userInfo.email
        this.profileForm.bio = userInfo.bio
      } catch (error) {
        console.error('获取用户信息失败:', error)
        this.$message.error('获取用户信息失败')
      } finally {
        this.profileLoading = false
      }
    },
    updateProfile() {
      this.$refs.profileForm.validate(async valid => {
        if (valid) {
          this.profileLoading = true
          try {
            await updateUserInfo(this.profileForm)
            this.$message.success('个人资料更新成功')
          } catch (error) {
            console.error('更新个人资料失败:', error)
            this.$message.error('更新个人资料失败')
          } finally {
            this.profileLoading = false
          }
        } else {
          return false
        }
      })
    },
    updatePassword() {
      this.$refs.passwordForm.validate(async valid => {
        if (valid) {
          this.passwordLoading = true
          try {
            await updatePassword(this.passwordForm)
            this.$message.success('密码修改成功，请重新登录')
            // 清除登录状态，跳转到登录页
            this.logout()
          } catch (error) {
            console.error('修改密码失败:', error)
            this.$message.error('修改密码失败，请确认当前密码是否正确')
          } finally {
            this.passwordLoading = false
          }
        } else {
          return false
        }
      })
    },
    logout() {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('userId')
      this.$router.push('/sign-in')
    }
  }
}
</script>

<style scoped>
.settings-container {
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