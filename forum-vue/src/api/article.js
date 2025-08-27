import request from './request'

// 获取文章列表
export function getArticleList(params) {
  return request({
    url: '/article/list',
    method: 'get',
    params
  })
}

// 获取文章详情
export function getArticleDetail(id) {
  return request({
    url: `/article/${id}`,
    method: 'get'
  })
}

// 创建文章
export function createArticle(data) {
  return request({
    url: '/article/create',
    method: 'post',
    data
  })
}

// 更新文章
export function updateArticle(data) {
  return request({
    url: '/article/update',
    method: 'post',
    data
  })
}

// 删除文章
export function deleteArticle(id) {
  return request({
    url: `/article/${id}/delete`,
    method: 'post'
  })
}

// 点赞文章
export function likeArticle(id) {
  return request({
    url: `/article/${id}/like`,
    method: 'post'
  })
}

// 收藏文章
export function favoriteArticle(id) {
  return request({
    url: `/article/${id}/favorite`,
    method: 'post'
  })
}

// 获取文章评论
export function getArticleComments(id) {
  return request({
    url: `/article/${id}/comments`,
    method: 'get'
  })
}

// 发表评论
export function createComment(data) {
  return request({
    url: '/comment/create',
    method: 'post',
    data
  })
} 