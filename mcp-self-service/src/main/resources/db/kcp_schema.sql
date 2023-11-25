/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 50730
 Source Host           : localhost:3306
 Source Schema         : kcp

 Target Server Type    : MySQL
 Target Server Version : 50730
 File Encoding         : 65001

 Date: 01/12/2021 17:42:23
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cloud_network_config
-- ----------------------------
DROP TABLE IF EXISTS `cloud_network_config`;
CREATE TABLE `cloud_network_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `network_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '网络名称',
  `interface_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '网络类型',
  `address_pool` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址池',
  `address_pool_id` int(11) NOT NULL DEFAULT 0 COMMENT '地址池ID',
  `virtual_switch` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '虚拟交换机',
  `model_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '网卡类型',
  `port_group` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '端口组',
  `port_group_uuid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '端口组uuid',
  `security_group_uuid` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '安全组uuid',
  `security_group` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '安全组',
  `create_by` int(11) NOT NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT NULL COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

INSERT INTO `cloud_user` VALUES (1, 'admin', '系统管理员', '25f9e794323b453885f5181f1b624d0b', '13000000000', '系统管理员', 1, 1, 1, 0, '2021-08-20 10:05:21', 0, NULL, b'0', NULL, 0);

-- ----------------------------
-- Table structure for cloud_organization
-- ----------------------------
DROP TABLE IF EXISTS `cloud_organization`;
CREATE TABLE `cloud_organization`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '组织ID',
  `organization_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '组织名称',
  `parent_id` int(11) NOT NULL DEFAULT 0 COMMENT '上级组织ID',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `index_name`(`organization_name`, `delete_flag`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '组织表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cloud_user
-- ----------------------------
DROP TABLE IF EXISTS `cloud_user`;
CREATE TABLE `cloud_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户姓名',
  `real_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '真实姓名',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `mobile` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `organization_id` int(11) NOT NULL DEFAULT 0 COMMENT '组织ID',
  `user_type` tinyint(4) NULL DEFAULT 0 COMMENT '用户类型 0:自服务用户， 1:云管用户',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '状态',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_org`(`organization_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cloud_user_machine
-- ----------------------------
DROP TABLE IF EXISTS `cloud_user_machine`;
CREATE TABLE `cloud_user_machine`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `machine_uuid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'mc中云服务器ID',
  `deadline_time` datetime(0) NOT NULL COMMENT '截止时间',
  `deadline_type` int(11) NULL DEFAULT 0 COMMENT '到期处理策略',
  `deadline_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否过期  0:没有 1:已过期',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:删除  1:未删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `index_uuid`(`machine_uuid`) USING BTREE,
  INDEX `index_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户拥有云服务器关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cloud_work_order
-- ----------------------------
DROP TABLE IF EXISTS `cloud_work_order`;
CREATE TABLE `cloud_work_order`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `type` int(2) NOT NULL COMMENT '工单类型',
  `target` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '工单对象',
  `apply_reason` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '申请原因',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '工单状态',
  `audit_by` int(11) NOT NULL DEFAULT 0 COMMENT '审核者',
  `audit_time` datetime(0) NULL DEFAULT NULL COMMENT '审核时间',
  `audit_opinion` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核时间',
  `create_by` int(11) NOT NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT NULL COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '工单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cloud_work_order_deferred_machine
-- ----------------------------
DROP TABLE IF EXISTS `cloud_work_order_deferred_machine`;
CREATE TABLE `cloud_work_order_deferred_machine`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `work_order_id` int(11) NOT NULL COMMENT '工单ID',
  `old_deadline_time` datetime(0) NOT NULL COMMENT '原过期时间',
  `deadline_time` datetime(0) NOT NULL COMMENT '截止时间',
  `user_machine_uuid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '用户拥有云服务器关联表ID',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `work_order_id`(`work_order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '工单-申请延期表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cloud_work_order_modify_servervm
-- ----------------------------
DROP TABLE IF EXISTS `cloud_work_order_modify_servervm`;
CREATE TABLE `cloud_work_order_modify_servervm`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `work_order_id` int(11) NOT NULL COMMENT '工单ID',
  `machine_uuid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '云服务器uuid',
  `servervm_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '虚拟机名称',
  `deadline_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '到期处理策略',
  `os_machine` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作系统',
  `architecture` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '架构',
  `system_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统类型',
  `cpu` int(11) NOT NULL DEFAULT 0 COMMENT 'cpu数',
  `mem` int(11) NOT NULL COMMENT '内存',
  `mem_unit` tinyint(1) NOT NULL DEFAULT 0 COMMENT '内存单位',
  `deadline_time` datetime(0) NULL DEFAULT NULL COMMENT '到期时间',
  `create_by` int(11) NOT NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `work_order_id`(`work_order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '工单-变更云服务器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cloud_work_order_servervm
-- ----------------------------
DROP TABLE IF EXISTS `cloud_work_order_servervm`;
CREATE TABLE `cloud_work_order_servervm`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `work_order_id` int(11) NOT NULL COMMENT '工单ID',
  `servervm_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '虚拟机名称',
  `apply_num` int(11) NOT NULL DEFAULT 0 COMMENT '申请个数',
  `use_month` int(11) NOT NULL DEFAULT 0 COMMENT '使用月数',
  `deadline_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '到期处理策略',
  `os_machine` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '操作系统',
  `architecture` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '架构',
  `system_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统类型',
  `template_id` int(11) NOT NULL COMMENT '模板ID',
  `cpu` int(11) NOT NULL DEFAULT 0 COMMENT 'cpu数',
  `modify_cpu` int(11) NOT NULL DEFAULT 0 COMMENT '变更后cpu',
  `mem` int(11) NOT NULL COMMENT '内存',
  `modify_mem` int(11) NULL DEFAULT NULL COMMENT '变更后内存',
  `mem_unit` tinyint(1) NOT NULL DEFAULT 0 COMMENT '内存单位',
  `create_by` int(11) NOT NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `work_order_id`(`work_order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '工单-申请云服务器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cloud_work_order_servervm_disk
-- ----------------------------
DROP TABLE IF EXISTS `cloud_work_order_servervm_disk`;
CREATE TABLE `cloud_work_order_servervm_disk`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `work_order_id` int(11) NOT NULL DEFAULT 0 COMMENT '工单ID',
  `disk_id` int(11) NOT NULL COMMENT '磁盘ID',
  `disk_size` int(11) NOT NULL COMMENT '硬盘大小',
  `disk_unit` tinyint(1) NOT NULL DEFAULT 0 COMMENT '硬盘单位 0:GB 1:TB',
  `purpose` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用途',
  `type` tinyint(4) NULL DEFAULT 0 COMMENT '类型 0:原始,1:自定义',
  `modify_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '变更类型  0:无变动 ，1:新增，2：变更，3：删除',
  `old_disk_size` int(11) NOT NULL DEFAULT 0 COMMENT '原来磁盘大小',
  `old_disk_unit` tinyint(1) NOT NULL DEFAULT 0 COMMENT '原来磁盘单位 0:GB 1:TB',
  `create_by` int(11) NOT NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '工单-申请服务器硬盘详情表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cloud_work_order_servervm_network
-- ----------------------------
DROP TABLE IF EXISTS `cloud_work_order_servervm_network`;
CREATE TABLE `cloud_work_order_servervm_network`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `work_order_id` int(11) NOT NULL COMMENT '工单ID',
  `interface_id` int(11) NOT NULL DEFAULT 0 COMMENT '网卡ID',
  `purpose` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用途',
  `type` tinyint(4) NULL DEFAULT 0 COMMENT '类型 0:原始,1 自定义',
  `modify_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '变更类型  0:无变动 ，1:新增，2：变更，3：删除',
  `create_by` int(11) NOT NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '工单-申请服务器网卡详情表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cloud_work_order_user
-- ----------------------------
DROP TABLE IF EXISTS `cloud_work_order_user`;
CREATE TABLE `cloud_work_order_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `work_order_id` int(11) NOT NULL COMMENT '工单ID',
  `old_real_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '旧真实姓名',
  `new_real_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '新真实姓名',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `work_order_id`(`work_order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '工单-变更账号详情表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
