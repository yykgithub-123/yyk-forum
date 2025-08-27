-- ----------------------------
-- 创建数据库，并指定字符集
-- ----------------------------
drop database if exists forum_db;
create database forum_db character set utf8mb4 collate utf8mb4_general_ci;
-- 选择数据库
use forum_db;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 创建帖⼦表 t_article
-- ----------------------------
DROP TABLE IF EXISTS `t_article`;
CREATE TABLE `t_article`  (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '帖⼦编号，主键，⾃增',
    `boardId` bigint(20) NOT NULL COMMENT '关联板块编号，⾮空',
    `userId` bigint(20) NOT NULL COMMENT '发帖⼈，⾮空，关联⽤⼾编号',
    `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT
    NULL COMMENT '标题，⾮空，最⼤⻓度100个字符',
    `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL
    COMMENT '帖⼦正⽂，⾮空',
    `visitCount` int(11) NOT NULL DEFAULT 0 COMMENT '访问量，默认0',
    `replyCount` int(11) NOT NULL DEFAULT 0 COMMENT '回复数据，默认0',
    `likeCount` int(11) NOT NULL DEFAULT 0 COMMENT '点赞数，默认0',
    `state` tinyint(4) NOT NULL DEFAULT 0 COMMENT '状态 0正常 1 禁⽤，默认0',
    `deleteState` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除 0 否 1 是，默认0',
    `createTime` datetime NOT NULL COMMENT '创建时间，精确到秒，⾮空',
    `updateTime` datetime NOT NULL COMMENT '修改时间，精确到秒，⾮空',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE =
utf8mb4_general_ci COMMENT = '帖⼦表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 创建帖⼦回复表 t_article_reply
-- ----------------------------
DROP TABLE IF EXISTS `t_article_reply`;
CREATE TABLE `t_article_reply`  (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号，主键，⾃增',
    `articleId` bigint(20) NOT NULL COMMENT '关联帖⼦编号，⾮空',
    `postUserId` bigint(20) NOT NULL COMMENT '楼主⽤⼾，关联⽤⼾编号，⾮空',
    `replyId` bigint(20) NULL DEFAULT NULL COMMENT '关联回复编号，⽀持楼中楼',
    `replyUserId` bigint(20) NULL DEFAULT NULL COMMENT '楼主下的回复⽤⼾编号，⽀持楼
    中楼',
    `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT
    NULL COMMENT '回贴内容，⻓度500个字符，⾮空',
    `likeCount` int(11) NOT NULL DEFAULT 0 COMMENT '点赞数，默认0',
    `state` tinyint(4) NOT NULL DEFAULT 0 COMMENT '状态 0 正常，1禁⽤，默认0',
    `deleteState` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除 0否 1是，默认0',
    `createTime` datetime NOT NULL COMMENT '创建时间，精确到秒，⾮空',
    `updateTime` datetime NOT NULL COMMENT '更新时间，精确到秒，⾮空',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE =
utf8mb4_general_ci COMMENT = '帖⼦回复表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 创建版块表 t_board
-- ----------------------------
DROP TABLE IF EXISTS `t_board`;
CREATE TABLE `t_board`  (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '版块编号，主键，⾃增',
    `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL
    COMMENT '版块名，⾮空',
    `articleCount` int(11) NOT NULL DEFAULT 0 COMMENT '帖⼦数量，默认0',
    `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序优先级，升序，默认0，',
    `state` tinyint(4) NOT NULL DEFAULT 0 COMMENT '状态，0 正常，1禁⽤，默认0',
    `deleteState` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除 0否，1是，默认0',
    `createTime` datetime NOT NULL COMMENT '创建时间，精确到秒，⾮空',
    `updateTime` datetime NOT NULL COMMENT '更新时间，精确到秒，⾮空',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE =
utf8mb4_general_ci COMMENT = '版块表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 创建站内信表 for t_message
-- ----------------------------
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message`  (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '站内信编号，主键，⾃增',
    `postUserId` bigint(20) NOT NULL COMMENT '发送者，并联⽤⼾编号',
    `receiveUserId` bigint(20) NOT NULL COMMENT '接收者，并联⽤⼾编号',
    `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT
    NULL COMMENT '内容，⾮空，⻓度255个字符',
    `state` tinyint(4) NOT NULL DEFAULT 0 COMMENT '状态 0未读 1已读，默认0',
    `deleteState` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除 0否，1是，默认0',
    `createTime` datetime NOT NULL COMMENT '创建时间，精确到秒，⾮空',
    `updateTime` datetime NOT NULL COMMENT '更新时间，精确到秒，⾮空',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE =
utf8mb4_general_ci COMMENT = '站内信表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 创建⽤⼾表 for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '⽤⼾编号，主键，⾃增',
    `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT
    NULL COMMENT '⽤⼾名，⾮空，唯⼀',
    `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT
    NULL COMMENT '加密后的密码',
    `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT
    NULL COMMENT '昵称，⾮空',
    `phoneNum` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL
    DEFAULT NULL COMMENT '⼿机号',
    `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL
    DEFAULT NULL COMMENT '邮箱地址',
    `gender` tinyint(4) NOT NULL DEFAULT 2 COMMENT '0⼥ 1男 2保密，⾮空，默认2',
    `salt` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL
    COMMENT '为密码加盐，⾮空',
    `avatarUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci
    NULL DEFAULT NULL COMMENT '⽤⼾头像URL，默认系统图⽚',
    `articleCount` int(11) NOT NULL DEFAULT 0 COMMENT '发帖数量，⾮空，默认0',
    `isAdmin` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否管理员，0否 1是，默认0',
    `remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL
    DEFAULT NULL COMMENT '备注，⾃我介绍',
    `state` tinyint(4) NOT NULL DEFAULT 0 COMMENT '状态 0 正常，1 禁⾔，默认0',
    `deleteState` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除 0否 1是，默认0',
    `createTime` datetime NOT NULL COMMENT '创建时间，精确到秒',
    `updateTime` datetime NOT NULL COMMENT '更新时间，精确到秒',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `user_username_uindex`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE =
utf8mb4_general_ci COMMENT = '⽤⼾表' ROW_FORMAT = Dynamic;
SET FOREIGN_KEY_CHECKS = 1;

-- 写⼊版块信息数据
INSERT INTO `t_board` (`id`, `name`, `articleCount`, `sort`, `state`,
`deleteState`, `createTime`, `updateTime`) VALUES (1, 'Java', 0, 1, 0, 0,NOW(),NOW());
INSERT INTO `t_board` (`id`, `name`, `articleCount`, `sort`, `state`,
`deleteState`, `createTime`, `updateTime`) VALUES (2, 'C++', 0, 2, 0, 0,NOW(), NOW());
INSERT INTO `t_board` (`id`, `name`, `articleCount`, `sort`, `state`,
`deleteState`, `createTime`, `updateTime`) VALUES (3, '前端技术', 0, 3, 0, 0,NOW(), NOW());
INSERT INTO `t_board` (`id`, `name`, `articleCount`, `sort`, `state`,
`deleteState`, `createTime`, `updateTime`) VALUES (4, 'MySQL', 0, 4, 0, 0,NOW(), NOW());
INSERT INTO `t_board` (`id`, `name`, `articleCount`, `sort`, `state`,
`deleteState`, `createTime`, `updateTime`) VALUES (5, '面试宝典', 0, 5, 0, 0,NOW(), NOW());
INSERT INTO `t_board` (`id`, `name`, `articleCount`, `sort`, `state`,
`deleteState`, `createTime`, `updateTime`) VALUES (6, '经验分享', 0, 6, 0, 0,NOW(), NOW());
INSERT INTO `t_board` (`id`, `name`, `articleCount`, `sort`, `state`,
`deleteState`, `createTime`, `updateTime`) VALUES (7, '招聘信息', 0, 7, 0, 0,NOW(), NOW());
INSERT INTO `t_board` (`id`, `name`, `articleCount`, `sort`, `state`,
`deleteState`, `createTime`, `updateTime`) VALUES (8, '福利待遇', 0, 8, 0, 0,NOW(), NOW());
INSERT INTO `t_board` (`id`, `name`, `articleCount`, `sort`, `state`,
`deleteState`, `createTime`, `updateTime`) VALUES (9, '灌水区', 0, 9, 0, 0,NOW(), NOW());