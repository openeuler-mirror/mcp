/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50730
 Source Host           : 127.0.0.1:3306
 Source Schema         : kcp-818server-3

 Target Server Type    : MySQL
 Target Server Version : 50730
 File Encoding         : 65001

 Date: 25/11/2023 14:37:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cloud_alarm_config
-- ----------------------------
DROP TABLE IF EXISTS `cloud_alarm_config`;
CREATE TABLE `cloud_alarm_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `resource_type` tinyint(1) NOT NULL COMMENT '资源类型',
  `general_alarm` int(11) NOT NULL DEFAULT 0 COMMENT '一般告警值',
  `severity_alarm` int(11) NOT NULL DEFAULT 0 COMMENT '严重告警',
  `urgent_alarm` int(11) NOT NULL DEFAULT 0 COMMENT '紧急告警',
  `duration_time` int(11) NOT NULL DEFAULT 0 COMMENT '持续时间',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'VDC告警设置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_alarm_config
-- ----------------------------
INSERT INTO `cloud_alarm_config` VALUES (1, 0, 70, 80, 90, 3, 1, '2022-04-06 14:44:10', 1, '2022-04-08 13:56:48', b'0', NULL, 0);
INSERT INTO `cloud_alarm_config` VALUES (2, 1, 70, 80, 90, 5, 1, '2022-04-06 14:44:29', 1, '2022-04-06 16:08:39', b'0', NULL, 0);
INSERT INTO `cloud_alarm_config` VALUES (3, 2, 70, 80, 90, 5, 1, '2022-04-06 14:44:45', 1, '2022-04-06 16:08:38', b'0', NULL, 0);

-- ----------------------------
-- Table structure for cloud_alarm_log
-- ----------------------------
DROP TABLE IF EXISTS `cloud_alarm_log`;
CREATE TABLE `cloud_alarm_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `alarm_level` tinyint(1) NOT NULL DEFAULT 0 COMMENT '告警等级',
  `resource_type` tinyint(1) NOT NULL COMMENT '类型',
  `alarm_target` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '告警对象',
  `target_type` tinyint(1) NULL DEFAULT NULL,
  `alarm_detail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '告警详情',
  `alarm_target_id` int(11) NOT NULL DEFAULT 0 COMMENT '告警对象ID',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_targetId`(`alarm_target_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '平台告警日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_alarm_log
-- ----------------------------

-- ----------------------------
-- Table structure for cloud_cluster
-- ----------------------------
DROP TABLE IF EXISTS `cloud_cluster`;
CREATE TABLE `cloud_cluster`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '集群名称',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '集群描述',
  `type` tinyint(1) NOT NULL COMMENT '集群类型',
  `cluster_admin_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '集群管理员名称',
  `cluster_admin_password` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '集群管理员密码',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '集群管理' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_cluster
-- ----------------------------
INSERT INTO `cloud_cluster` VALUES (1, '58集群', '58集群', 0, 'mcadmin1', '8fba933048e8369321a71c7852c75a07', 4, '2023-02-01 16:34:51', 0, NULL, b'0', NULL, 0);

-- ----------------------------
-- Table structure for cloud_cluster_node
-- ----------------------------
DROP TABLE IF EXISTS `cloud_cluster_node`;
CREATE TABLE `cloud_cluster_node`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cluster_id` int(11) NOT NULL COMMENT '集群ID',
  `http_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT 'http类型',
  `ip_address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'ip地址',
  `port` int(10) NULL DEFAULT 0 COMMENT '端口号',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_clusterId`(`cluster_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '集群主节点' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_cluster_node
-- ----------------------------
INSERT INTO `cloud_cluster_node` VALUES (1, 1, 'https://', '10.90.6.58', 8443, 4, '2023-02-01 16:34:51', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_cluster_node` VALUES (2, 1, 'https://', '10.90.6.76', 8443, 4, '2023-02-01 16:34:51', 0, NULL, b'0', NULL, 0);

-- ----------------------------
-- Table structure for cloud_network_config
-- ----------------------------
DROP TABLE IF EXISTS `cloud_network_config`;
CREATE TABLE `cloud_network_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `vdc_id` int(11) NOT NULL DEFAULT 0,
  `cluster_id` int(11) NOT NULL DEFAULT 0,
  `network_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '网络名称',
  `interface_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '网络类型',
  `address_pool` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址池',
  `address_pool_id` int(11) NOT NULL DEFAULT 0 COMMENT '地址池ID',
  `virtual_switch` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '虚拟交换机',
  `model_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '网卡类型',
  `port_group` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '端口组',
  `port_group_uuid` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '端口组uuid',
  `security_policy` tinyint(1) NULL DEFAULT 0 COMMENT '安全策略',
  `security_group_uuid` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '安全组uuid',
  `security_group` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '安全组',
  `virtual_firewall_id` int(11) NULL DEFAULT 0 COMMENT '虚拟防火墙ID',
  `virtual_firewall_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '虚拟防火墙名称',
  `create_by` int(11) NOT NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT NULL COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_cdv`(`vdc_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_network_config
-- ----------------------------

-- ----------------------------
-- Table structure for cloud_org_vdc
-- ----------------------------
DROP TABLE IF EXISTS `cloud_org_vdc`;
CREATE TABLE `cloud_org_vdc`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `org_id` int(11) NOT NULL DEFAULT 0 COMMENT '组织id',
  `vdc_id` int(11) NOT NULL COMMENT 'vdc虚拟数据中心Id',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_orgId`(`org_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = '组织关联VDC关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_org_vdc
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '组织表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_organization
-- ----------------------------
INSERT INTO `cloud_organization` VALUES (1, 'kylinsec', 0, '顶级组织', 1, '2022-04-14 15:27:58', 1, '2022-04-20 17:22:03', b'0', NULL, 0);

-- ----------------------------
-- Table structure for cloud_permission
-- ----------------------------
DROP TABLE IF EXISTS `cloud_permission`;
CREATE TABLE `cloud_permission`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) NOT NULL COMMENT '父ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '权限名称',
  `icon` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '权限图标',
  `route_key` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT '权限标识',
  `platform_role_permission` bit(1) NULL DEFAULT b'0' COMMENT '平台管理权限  0:不是 1:是',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `index_routeKey`(`route_key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 63 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_permission
-- ----------------------------
INSERT INTO `cloud_permission` VALUES (1, 0, '运营', NULL, 'operator', b'1', 0, '2021-11-24 11:36:02', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (2, 1, '组织管理', NULL, 'org', b'1', 0, '2021-11-24 11:37:41', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (3, 2, '查询组织', NULL, 'search_org', b'1', 0, '2021-11-24 11:38:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (4, 2, '添加组织', NULL, 'create_org', b'1', 0, '2021-11-24 11:38:38', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (5, 2, '删除组织', NULL, 'delete_org', b'1', 0, '2021-11-24 11:38:57', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (6, 2, '编辑组织', NULL, 'modify_org', b'1', 0, '2021-11-24 11:39:20', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (7, 2, '组织详情', NULL, 'org_info', b'1', 0, '2021-11-24 11:40:26', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (8, 1, '权限管理', NULL, 'permission', b'1', 0, '2021-11-24 11:42:25', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (9, 8, '角色管理', NULL, 'role', b'1', 0, '2021-11-24 11:42:52', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (10, 9, '查询角色', NULL, 'search_role', b'1', 0, '2021-11-24 11:43:27', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (11, 9, '添加角色', NULL, 'create_role', b'1', 0, '2021-11-24 11:43:52', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (12, 9, '编辑角色', NULL, 'modify_role', b'1', 0, '2021-11-24 11:44:39', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (13, 9, '删除角色', NULL, 'delete_role', b'1', 0, '2021-11-24 11:45:00', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (14, 9, '查看角色详情', NULL, 'role_info', b'1', 0, '2021-11-24 11:45:34', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (15, 8, '用户管理', NULL, 'user', b'1', 0, '2021-11-24 11:45:55', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (16, 15, '查询用户', NULL, 'search_user', b'1', 0, '2021-11-24 11:46:14', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (17, 15, '添加用户', NULL, 'create_user', b'1', 0, '2021-11-24 11:46:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (18, 15, '编辑用户', NULL, 'modify_user', b'1', 0, '2021-11-24 11:47:34', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (19, 15, '删除用户', NULL, 'delete_user', b'1', 0, '2021-11-24 11:47:54', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (20, 15, '激活用户', NULL, 'active_user', b'1', 0, '2021-11-24 11:48:23', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (21, 0, '资源', NULL, 'resource', b'1', 0, '2021-11-24 11:49:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (22, 21, '物理集群', NULL, 'cluster', b'0', 0, '2021-12-16 16:04:05', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (23, 22, '添加物理集群', NULL, 'create_cluster', b'0', 0, '2021-12-16 16:04:33', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (24, 22, '查询物理集群', NULL, 'search_cluster', b'0', 0, '2021-12-16 16:04:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (25, 22, '物理集群详情', NULL, 'cluster_info', b'0', 0, '2021-12-16 16:05:22', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (26, 22, '编辑物理集群', NULL, 'modify_cluster', b'0', 0, '2021-12-16 16:05:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (27, 22, '删除物理集群', NULL, 'delete_cluster', b'0', 0, '2021-12-16 16:06:02', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (28, 1, '工单管理', NULL, 'workorder', b'1', 0, '2021-12-16 16:27:08', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (29, 28, '工单审核', NULL, 'check_workorder', b'1', 0, '2021-12-16 16:28:08', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (30, 28, '网络设置', NULL, 'networkconfig', b'1', 0, '2021-12-16 16:29:09', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (31, 21, '可用区管理', NULL, 'zone', b'0', 0, '2022-01-21 10:04:21', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (32, 31, '添加可用区', NULL, 'create_zone', b'0', 0, '2022-01-21 10:04:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (33, 31, '查询可用区', NULL, 'search_zone', b'0', 0, '2022-01-21 10:05:34', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (34, 31, '编辑可用区', NULL, 'modify_zone', b'0', 0, '2022-01-21 10:06:20', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (35, 31, '可用区详情', NULL, 'zone_info', b'0', 0, '2022-01-21 10:07:02', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (36, 31, '删除可用区', NULL, 'delete_zone', b'0', 0, '2022-01-21 10:07:38', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (37, 21, '虚拟数据中心VDC', NULL, 'vdc', b'1', 0, '2022-01-21 10:08:17', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (38, 37, '添加vdc', NULL, 'create_vdc', b'1', 0, '2022-01-21 10:10:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (39, 37, 'vdc列表查询', NULL, 'search_vdc', b'1', 0, '2022-01-21 10:11:28', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (40, 37, '编辑vdc', NULL, 'modify_vdc', b'1', 0, '2022-01-21 10:11:58', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (41, 37, 'vdc详情', NULL, 'vdc_info', b'1', 0, '2022-01-21 10:12:21', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (42, 37, '删除vdc', NULL, 'delete_vdc', b'1', 0, '2022-01-21 10:12:45', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (43, 21, '云服务器', NULL, 'servervm', b'1', 0, '2022-02-16 10:17:17', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (44, 43, '查询云服务器', NULL, 'search_servervm', b'1', 0, '2022-02-16 10:17:45', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (45, 43, '开机', NULL, 'start_servervm', b'1', 0, '2022-02-16 10:18:14', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (46, 43, '关机', NULL, 'shutdown_servervm', b'1', 0, '2022-02-16 10:18:38', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (47, 43, '强制关机', NULL, 'force_shutdown_servervm', b'1', 0, '2022-02-16 10:19:48', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (48, 43, '重启', NULL, 'restart_servervm', b'1', 0, '2022-02-16 10:20:15', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (49, 43, '强制重启', NULL, 'force_restart_servervm', b'1', 0, '2022-02-16 10:21:22', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (50, 43, '云服务器详情', NULL, 'servervm_info', b'1', 0, '2022-02-16 10:22:09', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (51, 43, '删除云服务器', NULL, 'delete_servervm', b'1', 0, '2022-02-16 10:26:33', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (52, 0, '监控', NULL, 'monitor', b'1', 0, '2022-02-24 17:54:35', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (53, 52, '告警事件', NULL, 'alarmEvent', b'1', 0, '2022-02-24 17:55:36', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (54, 53, '告警日志', NULL, 'alarmLog', b'1', 0, '2022-02-24 17:56:26', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (55, 54, '导出', NULL, 'exportAlarmLog', b'1', 0, '2022-02-24 17:56:49', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (56, 53, '告警设置', NULL, 'settingAlarm', b'1', 0, '2022-02-24 17:57:15', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (57, 52, '操作日志', NULL, 'operateLog', b'1', 0, '2022-02-24 19:02:44', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (58, 57, '物理主机事件', NULL, 'serverEvent', b'1', 0, '2022-02-24 19:03:46', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (59, 58, '导出', NULL, 'ecportServerEvent', b'1', 0, '2022-02-24 19:04:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (60, 57, '云服务器时间', NULL, 'cloudServerEvent', b'1', 0, '2022-02-24 19:04:46', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (61, 60, '导出', NULL, 'exportCloudServerEvent', b'1', 0, '2022-02-24 19:05:08', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_permission` VALUES (62, 37, '变更VDC', NULL, 'apply_vdc', b'1', 0, '2022-04-01 10:49:25', 0, NULL, b'0', NULL, 0);

-- ----------------------------
-- Table structure for cloud_role
-- ----------------------------
DROP TABLE IF EXISTS `cloud_role`;
CREATE TABLE `cloud_role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '角色名称',
  `role_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '角色类型 \r\n0：平台管理，1：组织管理， 2:  自服务',
  `default_role` bit(1) NULL DEFAULT b'0' COMMENT '是否系统内置默认角色  0:不是 1:是',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_role
-- ----------------------------
INSERT INTO `cloud_role` VALUES (1, '系统管理员', 0, b'1', '系统管理员：仅能执行系统业务维护，以及创建/删除帐号的操作', 0, '2021-11-22 16:35:08', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role` VALUES (2, '安全管理员', 0, b'1', '安全管理员：仅能执行用户、角色的权限管理；', 0, '2021-11-22 16:35:08', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role` VALUES (3, '安全审计员', 0, b'1', '安全审计员：仅能执行系统日志管理，对其他用户的操作进行审查', 0, '2021-11-22 16:35:08', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role` VALUES (4, '组织管理员', 1, b'1', '组织管理员：主要是对组织信息以及资源分配，方便使用VDC资源', 0, '2021-11-22 16:35:08', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role` VALUES (5, '自服务用户', 2, b'1', '自服务用户：通过机构建立的portal平台，实现对云服务器申请、工单管理、账号管理', 0, '2021-11-22 16:35:08', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role` VALUES (6, '超级管理员', 0, b'1', '超级管理员：拥有管理整个平台的权限', 0, '2022-04-20 20:36:56', 0, NULL, b'0', NULL, 0);

-- ----------------------------
-- Table structure for cloud_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `cloud_role_permission`;
CREATE TABLE `cloud_role_permission`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  `permission_id` int(11) NOT NULL COMMENT '权限ID',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_role`(`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1188 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = '角色权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_role_permission
-- ----------------------------
INSERT INTO `cloud_role_permission` VALUES (1, 1, 1, 0, '2022-03-08 19:27:17', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (2, 1, 2, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (3, 1, 3, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (4, 1, 4, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (5, 1, 5, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (6, 1, 6, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (7, 1, 7, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (8, 1, 8, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (9, 1, 9, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (10, 1, 10, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (11, 1, 11, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (12, 1, 12, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (13, 1, 13, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (14, 1, 14, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (15, 1, 15, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (16, 1, 16, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (17, 1, 17, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (18, 1, 18, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (19, 1, 19, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (20, 1, 20, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (21, 1, 21, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (22, 1, 22, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (23, 1, 23, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (24, 1, 24, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (25, 1, 25, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (26, 1, 26, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (27, 1, 27, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (28, 1, 28, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (29, 1, 29, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (30, 1, 30, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (31, 1, 31, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (32, 1, 32, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (33, 1, 33, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (34, 1, 34, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (35, 1, 35, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (36, 1, 36, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (37, 1, 37, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (38, 1, 38, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (39, 1, 39, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (40, 1, 40, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (41, 1, 41, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (42, 1, 42, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (43, 1, 43, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (44, 1, 44, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (45, 1, 45, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (46, 1, 46, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (47, 1, 47, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (48, 1, 48, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (49, 1, 49, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (50, 1, 50, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (51, 1, 51, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (52, 1, 52, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (53, 1, 53, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (54, 1, 54, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (55, 1, 55, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (56, 1, 56, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (57, 1, 57, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (58, 1, 58, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (59, 1, 59, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (60, 1, 60, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (61, 1, 61, 1, '2022-03-08 19:33:42', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (62, 2, 1, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (63, 2, 2, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (64, 2, 3, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (65, 2, 8, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (66, 2, 9, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (67, 2, 10, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (68, 2, 11, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (69, 2, 12, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (70, 2, 13, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (71, 2, 14, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (72, 2, 15, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (73, 2, 16, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (74, 2, 18, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (75, 2, 19, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (76, 2, 20, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (77, 2, 52, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (78, 2, 53, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (79, 2, 54, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (80, 2, 55, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (81, 2, 56, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (82, 2, 57, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (83, 2, 58, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (84, 2, 59, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (85, 2, 60, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (86, 2, 61, 1, '2022-03-08 19:40:11', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (87, 3, 52, 1, '2022-03-08 19:40:44', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (88, 3, 53, 1, '2022-03-08 19:40:44', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (89, 3, 54, 1, '2022-03-08 19:40:44', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (90, 3, 55, 1, '2022-03-08 19:40:44', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (91, 3, 56, 1, '2022-03-08 19:40:44', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (92, 3, 57, 1, '2022-03-08 19:40:44', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (93, 3, 58, 1, '2022-03-08 19:40:44', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (94, 3, 59, 1, '2022-03-08 19:40:44', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (95, 3, 60, 1, '2022-03-08 19:40:44', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (96, 3, 61, 1, '2022-03-08 19:40:44', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (402, 1, 62, 1, '2022-04-01 10:50:09', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (977, 4, 1, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (978, 4, 2, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (979, 4, 3, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (980, 4, 4, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (981, 4, 5, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (982, 4, 6, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (983, 4, 7, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (984, 4, 8, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (991, 4, 15, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (992, 4, 16, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (993, 4, 17, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (994, 4, 18, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (995, 4, 19, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (996, 4, 20, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (997, 4, 21, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (998, 4, 28, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (999, 4, 29, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1000, 4, 30, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1001, 4, 37, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1002, 4, 38, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1003, 4, 39, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1004, 4, 40, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1005, 4, 41, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1006, 4, 42, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1007, 4, 43, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1008, 4, 44, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1009, 4, 45, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1010, 4, 46, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1011, 4, 47, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1012, 4, 48, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1013, 4, 49, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1014, 4, 50, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1015, 4, 51, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1016, 4, 52, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1017, 4, 53, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1018, 4, 54, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1019, 4, 55, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1020, 4, 56, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1021, 4, 57, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1022, 4, 58, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1023, 4, 59, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1024, 4, 60, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1025, 4, 61, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1026, 4, 62, 37, '2022-04-15 17:54:50', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1126, 6, 1, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1127, 6, 2, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1128, 6, 3, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1129, 6, 4, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1130, 6, 5, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1131, 6, 6, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1132, 6, 7, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1133, 6, 8, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1134, 6, 9, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1135, 6, 10, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1136, 6, 11, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1137, 6, 12, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1138, 6, 13, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1139, 6, 14, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1140, 6, 15, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1141, 6, 16, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1142, 6, 17, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1143, 6, 18, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1144, 6, 19, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1145, 6, 20, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1146, 6, 21, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1147, 6, 22, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1148, 6, 23, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1149, 6, 24, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1150, 6, 25, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1151, 6, 26, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1152, 6, 27, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1153, 6, 28, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1154, 6, 29, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1155, 6, 30, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1156, 6, 31, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1157, 6, 32, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1158, 6, 33, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1159, 6, 34, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1160, 6, 35, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1161, 6, 36, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1162, 6, 37, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1163, 6, 38, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1164, 6, 39, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1165, 6, 40, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1166, 6, 41, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1167, 6, 42, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1168, 6, 43, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1169, 6, 44, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1170, 6, 45, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1171, 6, 46, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1172, 6, 47, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1173, 6, 48, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1174, 6, 49, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1175, 6, 50, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1176, 6, 51, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1177, 6, 52, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1178, 6, 53, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1179, 6, 54, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1180, 6, 55, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1181, 6, 56, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1182, 6, 57, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1183, 6, 58, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1184, 6, 59, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1185, 6, 60, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1186, 6, 61, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_role_permission` VALUES (1187, 6, 62, 1, '2022-04-21 10:43:47', 0, NULL, b'0', NULL, 0);

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
  `super_user` bit(1) NULL DEFAULT b'0' COMMENT '是否是超级管理员  0:不是 1:是',
  `default_user` bit(1) NULL DEFAULT b'0' COMMENT '是否系统内置默认用户  0:不是 1:是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_org`(`organization_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_user
-- ----------------------------
INSERT INTO `cloud_user` VALUES (1, 'sysadmin', '系统管理员', '25f9e794323b453885f5181f1b624d0b', NULL, '仅能执行系统业务维护，一级用户管理的操作', 1, 1, 1, 0, '2022-03-08 19:18:55', 0, NULL, b'0', NULL, 0, b'0', b'1');
INSERT INTO `cloud_user` VALUES (2, 'secadmin', '安全管理员', '25f9e794323b453885f5181f1b624d0b', NULL, '仅能执行角色管理,密码策略管理，用户激活/锁定/解锁的操作，一级系统日志的查看操作', 1, 1, 1, 0, '2022-03-08 19:24:13', 0, NULL, b'0', NULL, 0, b'0', b'1');
INSERT INTO `cloud_user` VALUES (3, 'secauditor', '审计管理员', '25f9e794323b453885f5181f1b624d0b', NULL, '仅能执行角色管理,密码策略管理，用户激活/锁定/解锁的操作，一级系统日志的查看操作', 1, 1, 1, 0, '2022-03-08 19:24:48', 0, NULL, b'0', NULL, 0, b'0', b'1');
INSERT INTO `cloud_user` VALUES (4, 'admin', '超级管理员', '25f9e794323b453885f5181f1b624d0b', NULL, '超级管理员', 1, 1, 1, 0, '2022-04-14 15:52:00', 0, NULL, b'0', NULL, 0, b'1', b'1');

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
  `cluster_id` int(11) NOT NULL DEFAULT 0 COMMENT '集群id',
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户拥有云服务器关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_user_machine
-- ----------------------------

-- ----------------------------
-- Table structure for cloud_user_role
-- ----------------------------
DROP TABLE IF EXISTS `cloud_user_role`;
CREATE TABLE `cloud_user_role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT 0,
  `role_id` int(11) NOT NULL,
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:删除  1:未删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_userid`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_user_role
-- ----------------------------
INSERT INTO `cloud_user_role` VALUES (1, 1, 1, 0, '2022-03-08 19:26:47', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_user_role` VALUES (2, 2, 2, 0, '2022-03-08 19:26:56', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_user_role` VALUES (3, 3, 3, 0, '2022-03-08 19:27:08', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_user_role` VALUES (4, 4, 6, 1, '2022-03-11 10:59:43', 0, NULL, b'0', NULL, 0);

-- ----------------------------
-- Table structure for cloud_vcenter
-- ----------------------------
DROP TABLE IF EXISTS `cloud_vcenter`;
CREATE TABLE `cloud_vcenter`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '名称',
  `remark` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '描述',
  `vcenter_ip` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'vcenterIp',
  `vcenter_port` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'vcenter端口',
  `vcenter_account` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'vcenter账号',
  `vcenter_password` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'vcenter账号',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '状态',
  `http_type` tinyint(1) NULL DEFAULT NULL COMMENT 'http类型',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'vcenter管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_vcenter
-- ----------------------------

-- ----------------------------
-- Table structure for cloud_vdc
-- ----------------------------
DROP TABLE IF EXISTS `cloud_vdc`;
CREATE TABLE `cloud_vdc`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `vdc_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'vdc名称',
  `parent_id` int(11) NOT NULL DEFAULT 0 COMMENT '父VDC-ID',
  `zone_id` int(11) NOT NULL DEFAULT 0 COMMENT '可用区ID',
  `remark` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '描述',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = '虚拟数据中心VDC' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_vdc
-- ----------------------------
INSERT INTO `cloud_vdc` VALUES (1, '研发一部VDC', 0, 1, '', 1, '2022-09-08 14:46:08', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_vdc` VALUES (2, '虚拟化VDC', 1, 1, '', 1, '2022-09-08 14:46:43', 0, NULL, b'0', NULL, 0);

-- ----------------------------
-- Table structure for cloud_vdc_cpu
-- ----------------------------
DROP TABLE IF EXISTS `cloud_vdc_cpu`;
CREATE TABLE `cloud_vdc_cpu`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `vdc_id` int(11) NOT NULL COMMENT 'vdc ID',
  `architecture` tinyint(1) NOT NULL COMMENT '架构类型',
  `vcpus` int(11) NULL DEFAULT NULL COMMENT 'cpu',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_vdc`(`vdc_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'vdc-cpu关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_vdc_cpu
-- ----------------------------
INSERT INTO `cloud_vdc_cpu` VALUES (1, 1, 0, 1000, 1, '2022-09-08 14:46:08', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_vdc_cpu` VALUES (2, 2, 0, 500, 1, '2022-09-08 14:46:43', 0, NULL, b'0', NULL, 0);

-- ----------------------------
-- Table structure for cloud_vdc_mem
-- ----------------------------
DROP TABLE IF EXISTS `cloud_vdc_mem`;
CREATE TABLE `cloud_vdc_mem`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `vdc_id` int(11) NOT NULL COMMENT 'vdc ID',
  `architecture` tinyint(1) NOT NULL COMMENT '架构类型',
  `mem` int(11) NULL DEFAULT NULL COMMENT '内存大小',
  `mem_unit` tinyint(1) NULL DEFAULT NULL COMMENT '内存单位',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_vdc`(`vdc_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'vdc-内存关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_vdc_mem
-- ----------------------------
INSERT INTO `cloud_vdc_mem` VALUES (1, 1, 0, 2000, 1, 1, '2022-09-08 14:46:08', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_vdc_mem` VALUES (2, 2, 0, 1000, 1, 1, '2022-09-08 14:46:43', 0, NULL, b'0', NULL, 0);

-- ----------------------------
-- Table structure for cloud_vdc_storage
-- ----------------------------
DROP TABLE IF EXISTS `cloud_vdc_storage`;
CREATE TABLE `cloud_vdc_storage`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `vdc_id` int(11) NOT NULL COMMENT 'vdc ID',
  `storage` int(11) NULL DEFAULT NULL COMMENT '存储大小',
  `unit` tinyint(1) NULL DEFAULT NULL COMMENT '单位',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_vdc`(`vdc_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'vdc-存储' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_vdc_storage
-- ----------------------------
INSERT INTO `cloud_vdc_storage` VALUES (1, 1, 3000, 0, 1, '2022-09-08 14:46:08', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_vdc_storage` VALUES (2, 2, 2000, 0, 1, '2022-09-08 14:46:43', 0, NULL, b'0', NULL, 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '工单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_work_order
-- ----------------------------
INSERT INTO `cloud_work_order` VALUES (1, 9, 2, 'test-disk', 'test-disk', 1, 1, '2022-09-08 15:38:33', 'pass', 9, '2022-09-08 15:38:23', 1, '2022-09-08 15:38:33', b'0', NULL, NULL);
INSERT INTO `cloud_work_order` VALUES (2, 9, 3, 'test-disk_3', 'ddd', 1, 1, '2022-09-08 15:57:33', 'pass', 9, '2022-09-08 15:56:51', 1, '2022-09-08 15:57:33', b'0', NULL, NULL);
INSERT INTO `cloud_work_order` VALUES (3, 9, 3, 'test-disk_3', '啛啛喳喳', 1, 1, '2022-09-08 15:58:13', 'pass', 9, '2022-09-08 15:57:45', 1, '2022-09-08 15:58:13', b'0', NULL, NULL);
INSERT INTO `cloud_work_order` VALUES (4, 9, 3, 'test-disk_0', '存储', 1, 1, '2022-09-08 15:59:36', 'pass', 9, '2022-09-08 15:59:26', 1, '2022-09-08 15:59:36', b'0', NULL, NULL);
INSERT INTO `cloud_work_order` VALUES (5, 9, 3, 'test-disk_0', '侧耳', 1, 1, '2022-09-08 16:00:18', 'pass', 9, '2022-09-08 16:00:08', 1, '2022-09-08 16:00:18', b'0', NULL, NULL);
INSERT INTO `cloud_work_order` VALUES (6, 9, 3, 'test-disk_0', '上试试', 1, 1, '2022-09-08 16:01:21', 'psss', 9, '2022-09-08 16:01:09', 1, '2022-09-08 16:01:21', b'0', NULL, NULL);
INSERT INTO `cloud_work_order` VALUES (7, 9, 3, 'test-disk_0', 'ccc', 1, 1, '2022-09-08 16:09:54', 'pass', 9, '2022-09-08 16:09:46', 1, '2022-09-08 16:09:54', b'0', NULL, NULL);
INSERT INTO `cloud_work_order` VALUES (8, 9, 3, 'test-disk_0', 'paass', 1, 1, '2022-09-08 16:10:43', 'pass', 9, '2022-09-08 16:10:26', 1, '2022-09-08 16:10:43', b'0', NULL, NULL);
INSERT INTO `cloud_work_order` VALUES (9, 9, 3, 'test-disk_0', 'rrrr', 1, 1, '2022-09-08 16:14:44', '擦擦擦', 9, '2022-09-08 16:12:59', 1, '2022-09-08 16:14:44', b'0', NULL, NULL);
INSERT INTO `cloud_work_order` VALUES (10, 9, 3, 'test-disk_0', 'ddd', 1, 1, '2022-09-08 19:40:28', 'pass', 9, '2022-09-08 19:39:27', 1, '2022-09-08 19:40:28', b'0', NULL, NULL);
INSERT INTO `cloud_work_order` VALUES (11, 9, 2, '45545545', '434', 1, 1, '2022-09-16 16:38:02', 'pass', 9, '2022-09-16 16:35:52', 1, '2022-09-16 16:38:02', b'0', NULL, NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '工单-申请延期表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_work_order_deferred_machine
-- ----------------------------

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
  `original_cpu` int(11) NOT NULL DEFAULT 0 COMMENT '变更前cpu数',
  `mem` int(11) NOT NULL COMMENT '内存',
  `original_mem` int(11) NOT NULL COMMENT '变更前内存',
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
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '工单-变更云服务器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_work_order_modify_servervm
-- ----------------------------
INSERT INTO `cloud_work_order_modify_servervm` VALUES (1, 2, '75e2ed6d-44aa-3e7b-79c0-ea0055f3c567', 'test-disk_3', 0, 'KylinsecOS', 'x86_64', '麒麟信安', 1, 1, 1, 1, 1, '2022-10-08 23:59:59', 9, '2022-09-08 15:56:51', 1, '2022-09-08 15:57:33', b'0', NULL, 0);
INSERT INTO `cloud_work_order_modify_servervm` VALUES (2, 3, '75e2ed6d-44aa-3e7b-79c0-ea0055f3c567', 'test-disk_3', 0, 'KylinsecOS', 'x86_64', '麒麟信安', 1, 1, 1, 1, 1, '2022-10-08 23:59:59', 9, '2022-09-08 15:57:45', 1, '2022-09-08 15:58:13', b'0', NULL, 0);
INSERT INTO `cloud_work_order_modify_servervm` VALUES (3, 4, '229718d1-e633-3d20-cd46-22ce24e08403', 'test-disk_0', 0, 'KylinsecOS', 'x86_64', '麒麟信安', 1, 1, 1, 1, 1, '2022-10-08 23:59:59', 9, '2022-09-08 15:59:26', 1, '2022-09-08 15:59:36', b'0', NULL, 0);
INSERT INTO `cloud_work_order_modify_servervm` VALUES (4, 5, '229718d1-e633-3d20-cd46-22ce24e08403', 'test-disk_0', 0, 'KylinsecOS', 'x86_64', '麒麟信安', 1, 1, 1, 1, 1, '2022-10-08 23:59:59', 9, '2022-09-08 16:00:08', 1, '2022-09-08 16:00:18', b'0', NULL, 0);
INSERT INTO `cloud_work_order_modify_servervm` VALUES (5, 6, '229718d1-e633-3d20-cd46-22ce24e08403', 'test-disk_0', 0, 'KylinsecOS', 'x86_64', '麒麟信安', 1, 1, 1, 1, 1, '2022-10-08 23:59:59', 9, '2022-09-08 16:01:09', 1, '2022-09-08 16:01:21', b'0', NULL, 0);
INSERT INTO `cloud_work_order_modify_servervm` VALUES (6, 7, '229718d1-e633-3d20-cd46-22ce24e08403', 'test-disk_0', 0, 'KylinsecOS', 'x86_64', '麒麟信安', 1, 1, 1, 1, 1, '2022-10-08 23:59:59', 9, '2022-09-08 16:09:46', 1, '2022-09-08 16:09:54', b'0', NULL, 0);
INSERT INTO `cloud_work_order_modify_servervm` VALUES (7, 8, '229718d1-e633-3d20-cd46-22ce24e08403', 'test-disk_0', 0, 'KylinsecOS', 'x86_64', '麒麟信安', 1, 1, 1, 1, 1, '2022-10-08 23:59:59', 9, '2022-09-08 16:10:26', 1, '2022-09-08 16:10:43', b'0', NULL, 0);
INSERT INTO `cloud_work_order_modify_servervm` VALUES (8, 9, '229718d1-e633-3d20-cd46-22ce24e08403', 'test-disk_0', 0, 'KylinsecOS', 'x86_64', '麒麟信安', 1, 1, 1, 1, 1, '2022-10-08 23:59:59', 9, '2022-09-08 16:12:59', 1, '2022-09-08 16:14:44', b'0', NULL, 0);
INSERT INTO `cloud_work_order_modify_servervm` VALUES (9, 10, '229718d1-e633-3d20-cd46-22ce24e08403', 'test-disk_0', 0, 'KylinsecOS', 'x86_64', '麒麟信安', 1, 1, 1, 1, 1, '2022-10-08 23:59:59', 9, '2022-09-08 19:39:27', 1, '2022-09-08 19:40:28', b'0', NULL, 0);

-- ----------------------------
-- Table structure for cloud_work_order_servervm
-- ----------------------------
DROP TABLE IF EXISTS `cloud_work_order_servervm`;
CREATE TABLE `cloud_work_order_servervm`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `work_order_id` int(11) NOT NULL COMMENT '工单ID',
  `apply_servervm_type` tinyint(11) NULL DEFAULT 0 COMMENT '申请类型',
  `clone_type` tinyint(11) NULL DEFAULT 0 COMMENT '创建类型  0:完整克隆 1：链接克隆',
  `servervm_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '虚拟机名称',
  `apply_num` int(11) NOT NULL DEFAULT 0 COMMENT '申请个数',
  `modify_apply_num` int(11) NOT NULL DEFAULT 0 COMMENT '变更后申请个数',
  `use_month` int(11) NOT NULL DEFAULT 0 COMMENT '使用月数',
  `deadline_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '到期处理策略',
  `os_machine` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '操作系统',
  `architecture` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '架构',
  `system_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统类型',
  `template_id` int(11) NOT NULL COMMENT '模板ID',
  `cluster_id` int(11) NULL DEFAULT 0 COMMENT '集群ID',
  `cpu` int(11) NOT NULL DEFAULT 0 COMMENT 'cpu数',
  `modify_cpu` int(11) NOT NULL DEFAULT 0 COMMENT '变更后cpu',
  `mem` int(11) NOT NULL COMMENT '内存',
  `modify_mem` int(11) NULL DEFAULT NULL COMMENT '变更后内存',
  `mem_unit` tinyint(1) NOT NULL DEFAULT 0 COMMENT '内存单位',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  `create_by` int(11) NOT NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `work_order_id`(`work_order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '工单-申请云服务器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_work_order_servervm
-- ----------------------------
INSERT INTO `cloud_work_order_servervm` VALUES (1, 1, 0, 1, 'test-disk', 4, 4, 1, 0, 'KylinsecOS', 'X86_64', '麒麟信安', 12, 1, 1, 1, 1, 1, 1, 'test-disk', 9, '2022-09-08 15:38:23', 1, '2022-09-08 15:38:33', b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm` VALUES (2, 11, 0, 1, '45545545', 2, 2, 1, 0, 'KylinsecOS', 'X86_64', '麒麟信安', 4, 1, 1, 1, 1, 1, 1, '34343', 9, '2022-09-16 16:35:52', 1, '2022-09-16 16:38:02', b'0', NULL, 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '工单-申请服务器硬盘详情表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_work_order_servervm_disk
-- ----------------------------
INSERT INTO `cloud_work_order_servervm_disk` VALUES (1, 1, 17, 100, 0, '', 0, 0, 100, 0, 9, '2022-09-08 15:38:23', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (2, 1, 0, 200, 0, 'ces', 1, 1, 200, 0, 9, '2022-09-08 15:38:23', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (3, 2, 25, 100, 0, NULL, 0, 0, 100, 0, 9, '2022-09-08 15:56:51', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (4, 2, 26, 200, 0, NULL, 0, 0, 200, 0, 9, '2022-09-08 15:56:51', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (5, 2, 0, 300, 0, '测试', 1, 1, 300, 0, 9, '2022-09-08 15:56:51', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (6, 3, 25, 100, 0, NULL, 0, 0, 100, 0, 9, '2022-09-08 15:57:45', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (7, 3, 26, 200, 0, NULL, 0, 3, 200, 0, 9, '2022-09-08 15:57:45', 1, '2022-09-08 15:58:13', b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (8, 3, 27, 300, 0, NULL, 0, 0, 300, 0, 9, '2022-09-08 15:57:45', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (9, 4, 19, 100, 0, NULL, 0, 0, 100, 0, 9, '2022-09-08 15:59:26', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (10, 4, 20, 200, 0, NULL, 0, 0, 200, 0, 9, '2022-09-08 15:59:26', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (11, 4, 0, 300, 0, '测', 1, 1, 300, 0, 9, '2022-09-08 15:59:26', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (12, 5, 19, 100, 0, NULL, 0, 0, 100, 0, 9, '2022-09-08 16:00:08', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (13, 5, 20, 200, 0, NULL, 0, 3, 200, 0, 9, '2022-09-08 16:00:08', 1, '2022-09-08 16:00:18', b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (14, 5, 28, 300, 0, NULL, 0, 0, 300, 0, 9, '2022-09-08 16:00:08', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (15, 6, 19, 100, 0, NULL, 0, 0, 100, 0, 9, '2022-09-08 16:01:09', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (16, 6, 28, 300, 0, NULL, 0, 3, 300, 0, 9, '2022-09-08 16:01:09', 1, '2022-09-08 16:01:21', b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (17, 7, 19, 100, 0, NULL, 0, 0, 100, 0, 9, '2022-09-08 16:09:46', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (18, 7, 0, 300, 0, 'ces', 1, 1, 300, 0, 9, '2022-09-08 16:09:46', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (19, 7, 0, 300, 0, 'ce', 1, 1, 300, 0, 9, '2022-09-08 16:09:46', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (20, 8, 19, 100, 0, NULL, 0, 0, 100, 0, 9, '2022-09-08 16:10:26', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (21, 8, 29, 300, 0, NULL, 0, 3, 300, 0, 9, '2022-09-08 16:10:26', 1, '2022-09-08 16:10:43', b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (22, 8, 30, 300, 0, NULL, 0, 3, 300, 0, 9, '2022-09-08 16:10:26', 1, '2022-09-08 16:10:43', b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (23, 8, 0, 150, 0, 'ces', 1, 1, 150, 0, 9, '2022-09-08 16:10:26', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (24, 8, 0, 160, 0, 'ces', 1, 1, 160, 0, 9, '2022-09-08 16:10:26', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (25, 9, 19, 100, 0, NULL, 0, 0, 100, 0, 9, '2022-09-08 16:12:59', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (26, 9, 31, 150, 0, NULL, 0, 3, 150, 0, 9, '2022-09-08 16:12:59', 1, '2022-09-08 16:14:44', b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (27, 9, 32, 160, 0, NULL, 0, 3, 160, 0, 9, '2022-09-08 16:12:59', 1, '2022-09-08 16:14:44', b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (28, 9, 0, 300, 0, 'eeee', 1, 1, 300, 0, 9, '2022-09-08 16:12:59', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (29, 9, 0, 350, 0, '', 1, 1, 350, 0, 1, '2022-09-08 16:14:44', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (30, 10, 19, 100, 0, NULL, 0, 0, 100, 0, 9, '2022-09-08 19:39:27', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (31, 10, 33, 300, 0, NULL, 0, 3, 300, 0, 9, '2022-09-08 19:39:27', 1, '2022-09-08 19:40:28', b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (32, 10, 34, 350, 0, NULL, 0, 3, 350, 0, 9, '2022-09-08 19:39:27', 1, '2022-09-08 19:40:28', b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (33, 10, 0, 120, 0, '测试', 1, 1, 120, 0, 9, '2022-09-08 19:39:27', 0, NULL, b'0', NULL, 0);
INSERT INTO `cloud_work_order_servervm_disk` VALUES (34, 11, 8, 100, 0, '', 0, 0, 100, 0, 9, '2022-09-16 16:35:52', 0, NULL, b'0', NULL, 0);

-- ----------------------------
-- Table structure for cloud_work_order_servervm_iso
-- ----------------------------
DROP TABLE IF EXISTS `cloud_work_order_servervm_iso`;
CREATE TABLE `cloud_work_order_servervm_iso`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `work_order_id` int(11) NOT NULL DEFAULT 0 COMMENT '工单ID',
  `iso_file` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'iso文件名',
  `modify_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '变更类型  0:无变动 ，1:新增，2：变更，3：删除',
  `old_iso_file` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '旧iso文件名',
  `create_by` int(11) NOT NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_work_order_servervm_iso
-- ----------------------------

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
  `ip_bind_mac` bit(1) NOT NULL DEFAULT b'0' COMMENT 'ip和mac绑定  0:未绑定  1:已绑定',
  `manual_set_ip` bit(1) NOT NULL DEFAULT b'0' COMMENT '手动设置ip    0:未设置  1:已设置',
  `automatic_acq_ip` bit(1) NOT NULL DEFAULT b'0' COMMENT '自动DHP    0:未设置  1:已设置',
  `ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'ip地址',
  `mask` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '掩码',
  `gw` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '网关',
  `dns1` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'dns1',
  `dns2` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'dns2',
  `create_by` int(11) NOT NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '工单-申请服务器网卡详情表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_work_order_servervm_network
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '工单-变更账号详情表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_work_order_user
-- ----------------------------

-- ----------------------------
-- Table structure for cloud_work_order_vdc
-- ----------------------------
DROP TABLE IF EXISTS `cloud_work_order_vdc`;
CREATE TABLE `cloud_work_order_vdc`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `work_order_id` int(11) NOT NULL COMMENT '工单ID',
  `vdc_id` int(11) NOT NULL COMMENT 'vdcId',
  `old_storage` int(11) NOT NULL COMMENT '存储大小',
  `storage_unit` tinyint(1) NULL DEFAULT NULL COMMENT '存储单位',
  `apply_storage` int(11) NULL DEFAULT NULL COMMENT '申请存储大小',
  `real_storage` int(11) NULL DEFAULT NULL COMMENT '审核后实际存储大小',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_work_order_vdc
-- ----------------------------

-- ----------------------------
-- Table structure for cloud_work_order_vdc_cpu_mem
-- ----------------------------
DROP TABLE IF EXISTS `cloud_work_order_vdc_cpu_mem`;
CREATE TABLE `cloud_work_order_vdc_cpu_mem`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `work_order_id` int(11) NOT NULL COMMENT '变更vdc工单ID',
  `resource_type` tinyint(1) NOT NULL COMMENT '资源类型',
  `architecture` tinyint(1) NOT NULL COMMENT '架构',
  `old_size` int(11) NOT NULL DEFAULT 0 COMMENT '原始大小',
  `apply_size` int(11) NOT NULL DEFAULT 0 COMMENT '申请大小',
  `real_size` int(11) NOT NULL DEFAULT 0 COMMENT '审核后大小',
  `unit` tinyint(1) NULL DEFAULT NULL COMMENT '单位',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_work_order_vdc_cpu_mem
-- ----------------------------

-- ----------------------------
-- Table structure for cloud_zone
-- ----------------------------
DROP TABLE IF EXISTS `cloud_zone`;
CREATE TABLE `cloud_zone`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '可用区名称',
  `type` tinyint(1) NOT NULL COMMENT '集群类型',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '可用区' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_zone
-- ----------------------------

-- ----------------------------
-- Table structure for cloud_zone_cluster
-- ----------------------------
DROP TABLE IF EXISTS `cloud_zone_cluster`;
CREATE TABLE `cloud_zone_cluster`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `zone_id` int(11) NOT NULL COMMENT '可用区ID',
  `cluster_id` int(11) NOT NULL COMMENT '集群ID',
  `create_by` int(11) NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` int(11) NULL DEFAULT 0 COMMENT '变更者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '变更时间',
  `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:已删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_by` int(11) NULL DEFAULT 0 COMMENT '删除者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '可用区集群管理关系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cloud_zone_cluster
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
