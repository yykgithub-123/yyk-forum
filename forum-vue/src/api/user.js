import request from './request'

// 用户登录
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

// 用户注册
export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

// 更新用户信息
export function updateUserInfo(data) {
  return request({
    url: '/user/update',
    method: 'post',
    data
  })
}

// 更新用户密码
export function updatePassword(data) {
  return request({
    url: '/user/updatePassword',
    method: 'post',
    data
  })
}

// 获取用户发布的文章
export function getUserArticles(userId) {
  return request({
    url: `/user/${userId}/articles`,
    method: 'get'
  })
}

// 获取用户资料
export function getUserProfile(userId) {
  return request({
    url: `/user/${userId}/profile`,
    method: 'get'
  })
} 