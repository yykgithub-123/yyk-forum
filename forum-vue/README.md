# 论坛系统前端

这是一个基于Vue.js的论坛系统前端项目，使用Vue 2、Element UI和Axios等技术栈。

## 项目设置

### 安装依赖
```
npm install
```

### 启动开发服务器
```
npm run serve
```

### 构建生产版本
```
npm run build
```

## 技术栈
- Vue.js 2.x
- Vue Router
- Element UI
- Axios

## 项目结构
```
forum-vue/
├── public/              # 静态资源
├── src/                 # 源代码
│   ├── api/             # API请求
│   ├── assets/          # 资源文件
│   ├── components/      # 公共组件
│   ├── router/          # 路由配置
│   ├── views/           # 页面组件
│   ├── App.vue          # 根组件
│   └── main.js          # 入口文件
├── babel.config.js      # Babel配置
├── package.json         # 项目依赖
└── vue.config.js        # Vue CLI配置
```

## 后端API
后端API基于Spring Boot，需要先启动后端服务。 