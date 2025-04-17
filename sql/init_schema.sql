-- ----------------------------
-- Create Database
-- ----------------------------
CREATE DATABASE IF NOT EXISTS `admin_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `admin_db`;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名 (登录凭证)',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码 (加密存储)',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '昵称',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
  `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '头像地址',
  `gender` tinyint DEFAULT NULL COMMENT '用户性别 (1=男, 2=女)',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态 (0=正常, 1=禁用)',
  `dept_id` bigint DEFAULT NULL COMMENT '所属部门ID (可选)',
  `created_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `deleted_flag` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记 (0=未删除, 1=已删除)',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间 (可选)',
  `last_login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '最后登录IP (可选)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_username` (`username`) USING BTREE COMMENT '用户名唯一索引',
  UNIQUE KEY `uk_email` (`email`) USING BTREE COMMENT '邮箱唯一索引',
  UNIQUE KEY `uk_phone_number` (`phone_number`) USING BTREE COMMENT '手机号唯一索引',
  KEY `idx_nickname` (`nickname`) USING BTREE COMMENT '昵称索引（用于搜索）',
  KEY `idx_status` (`status`) USING BTREE COMMENT '状态索引',
  KEY `idx_deleted_flag` (`deleted_flag`) USING BTREE COMMENT '逻辑删除标记索引',
  KEY `idx_dept_id` (`dept_id`) USING BTREE COMMENT '部门ID索引（用于按部门查询）',
  KEY `idx_created_time` (`created_time`) USING BTREE COMMENT '创建时间索引（用于排序和筛选）'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户信息表';

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色权限字符串/标识',
  `role_sort` int DEFAULT '0' COMMENT '显示顺序',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态 (0=正常, 1=禁用)',
  `created_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `deleted_flag` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记 (0=未删除, 1=已删除)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_role_key` (`role_key`) USING BTREE COMMENT '角色标识唯一索引',
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_deleted_flag` (`deleted_flag`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色信息表';

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限/菜单名称',
  `parent_id` bigint DEFAULT '0' COMMENT '父权限ID (0表示顶级)',
  `permission_type` tinyint NOT NULL COMMENT '权限类型 (1=目录, 2=菜单, 3=按钮/操作, 4=API)',
  `permission_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '权限标识 (如 system:user:list)',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '路由地址 (菜单类型)',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组件路径 (菜单类型)',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '菜单图标 (可选)',
  `sort_order` int DEFAULT '0' COMMENT '显示顺序',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态 (0=正常/显示, 1=禁用/隐藏)',
  `created_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_permission_key` (`permission_key`) USING BTREE COMMENT '权限标识唯一索引',
  KEY `idx_parent_id` (`parent_id`) USING BTREE,
  KEY `idx_permission_type` (`permission_type`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权限(菜单)信息表';

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID (自增主键)',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`) USING BTREE COMMENT '用户角色关联唯一索引',
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_role_id` (`role_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色关联表';

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID (自增主键)',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`) USING BTREE COMMENT '角色权限关联唯一索引',
  KEY `idx_role_id` (`role_id`) USING BTREE,
  KEY `idx_permission_id` (`permission_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色权限关联表';

-- ----------------------------
-- Records for RBAC Core Modules
-- ----------------------------

-- Add Users (Password is '123456', MUST replace placeholder with actual bcrypt hash)
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `phone_number`, `avatar`, `gender`, `status`, `dept_id`, `created_by`, `updated_by`, `remark`, `deleted_flag`) VALUES
(1, 'admin', '{bcrypt}$2a$10$XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX', '超级管理员', 'admin@example.com', '13800000001', NULL, 1, 0, NULL, 'system', 'system', '系统内置超级管理员', 0),
(2, 'testuser', '{bcrypt}$2a$10$XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX', '测试用户', 'test@example.com', '13900000001', NULL, 1, 0, NULL, 'admin', 'admin', '用于功能测试的用户', 0),
(3, 'editor', '{bcrypt}$2a$10$XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX', '内容编辑', 'editor@example.com', '13700000001', NULL, 2, 0, NULL, 'admin', 'admin', '负责内容编辑的用户', 0),
(4, 'guest', '{bcrypt}$2a$10$XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX', '访客用户', 'guest@example.com', '13600000001', NULL, NULL, 0, NULL, 'admin', 'admin', '只能查看部分信息的用户', 0);

-- Add Roles
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `role_sort`, `status`, `created_by`, `updated_by`, `remark`, `deleted_flag`) VALUES
(1, '超级管理员', 'superadmin', 1, 0, 'system', 'system', '拥有系统所有权限', 0),
(2, '普通用户', 'user', 2, 0, 'system', 'system', '拥有基础操作和查看权限', 0),
(3, '内容编辑', 'editor', 3, 0, 'admin', 'admin', '负责内容管理模块的编辑权限', 0),
(4, '数据查看员', 'viewer', 4, 0, 'admin', 'admin', '只能查看系统数据，无修改权限', 0);

-- Add Permissions (Menu/Button/API)
-- System Management Module
INSERT INTO `sys_permission` (`id`, `permission_name`, `parent_id`, `permission_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `status`, `created_by`, `updated_by`, `remark`) VALUES
(1, '系统管理', 0, 1, 'system', '/system', NULL, 'setting', 1, 0, 'system', 'system', '系统管理顶级目录'),
(2, '用户管理', 1, 2, 'system:user', 'user', 'system/user/index', 'user', 1, 0, 'system', 'system', '用户管理菜单'),
(3, '角色管理', 1, 2, 'system:role', 'role', 'system/role/index', 'peoples', 2, 0, 'system', 'system', '角色管理菜单'),
(4, '权限管理', 1, 2, 'system:permission', 'permission', 'system/permission/index', 'lock', 3, 0, 'system', 'system', '权限管理菜单'),
(101, '用户查询', 2, 3, 'system:user:list', NULL, NULL, NULL, 1, 0, 'system', 'system', '查询用户列表权限'),
(102, '用户新增', 2, 3, 'system:user:add', NULL, NULL, NULL, 2, 0, 'system', 'system', '新增用户权限'),
(103, '用户修改', 2, 3, 'system:user:edit', NULL, NULL, NULL, 3, 0, 'system', 'system', '修改用户权限'),
(104, '用户删除', 2, 3, 'system:user:remove', NULL, NULL, NULL, 4, 0, 'system', 'system', '删除用户权限'),
(105, '用户详情', 2, 3, 'system:user:detail', NULL, NULL, NULL, 5, 0, 'system', 'system', '查看用户详情权限'),
(201, '角色查询', 3, 3, 'system:role:list', NULL, NULL, NULL, 1, 0, 'system', 'system', '查询角色列表权限'),
(202, '角色新增', 3, 3, 'system:role:add', NULL, NULL, NULL, 2, 0, 'system', 'system', '新增角色权限'),
(203, '角色修改', 3, 3, 'system:role:edit', NULL, NULL, NULL, 3, 0, 'system', 'system', '修改角色权限'),
(204, '角色删除', 3, 3, 'system:role:remove', NULL, NULL, NULL, 4, 0, 'system', 'system', '删除角色权限'),
(301, '权限查询', 4, 3, 'system:permission:list', NULL, NULL, NULL, 1, 0, 'system', 'system', '查询权限列表权限');
-- Content Management Module (Example)
INSERT INTO `sys_permission` (`id`, `permission_name`, `parent_id`, `permission_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `status`, `created_by`, `updated_by`, `remark`) VALUES
(5, '内容管理', 0, 1, 'content', '/content', NULL, 'documentation', 2, 0, 'system', 'system', '内容管理顶级目录'),
(6, '文章管理', 5, 2, 'content:article', 'article', 'content/article/index', 'edit', 1, 0, 'system', 'system', '文章管理菜单'),
(401, '文章查询', 6, 3, 'content:article:list', NULL, NULL, NULL, 1, 0, 'system', 'system', '查询文章列表权限'),
(402, '文章新增', 6, 3, 'content:article:add', NULL, NULL, NULL, 2, 0, 'system', 'system', '新增文章权限'),
(403, '文章修改', 6, 3, 'content:article:edit', NULL, NULL, NULL, 3, 0, 'system', 'system', '修改文章权限'),
(404, '文章删除', 6, 3, 'content:article:remove', NULL, NULL, NULL, 4, 0, 'system', 'system', '删除文章权限');

-- Link Users and Roles
-- Clear existing links first if necessary
-- DELETE FROM `sys_user_role`; 
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1), -- admin -> superadmin
(2, 2), -- testuser -> user
(3, 3), -- editor -> editor
(4, 4); -- guest -> viewer

-- Link Roles and Permissions
-- Clear existing links first if necessary
-- DELETE FROM `sys_role_permission`;
-- Super Admin (role_id=1) gets all permissions implicitly or explicitly listed
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), 
(1, 101), (1, 102), (1, 103), (1, 104), (1, 105),
(1, 201), (1, 202), (1, 203), (1, 204),
(1, 301),
(1, 401), (1, 402), (1, 403), (1, 404);
-- Regular User (role_id=2) gets basic system view + user detail view
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(2, 1), (2, 2), (2, 101), (2, 105), (2, 3), (2, 201), (2, 4), (2, 301);
-- Editor (role_id=3) gets system view + content management edit permissions
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(3, 1), (3, 5), (3, 6), (3, 401), (3, 402), (3, 403), (3, 404);
-- Viewer (role_id=4) gets basic system view + content view
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(4, 1), (4, 2), (4, 101), (4, 105), (4, 3), (4, 201), (4, 4), (4, 301), (4, 5), (4, 6), (4, 401); 