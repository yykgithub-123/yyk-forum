import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

// 定义路由
const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/article/:id',
    name: 'Article',
    component: () => import('../views/Article.vue')
  },
  {
    path: '/article-edit',
    name: 'ArticleEdit',
    component: () => import('../views/ArticleEdit.vue')
  },
  {
    path: '/article-list',
    name: 'ArticleList',
    component: () => import('../views/ArticleList.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue')
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('../views/Settings.vue')
  },
  {
    path: '/sign-in',
    name: 'SignIn',
    component: () => import('../views/SignIn.vue')
  },
  {
    path: '/sign-up',
    name: 'SignUp',
    component: () => import('../views/SignUp.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router 