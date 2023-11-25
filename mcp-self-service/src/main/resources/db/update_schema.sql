ALTER TABLE `kcp`.`cloud_work_order_servervm_disk`
CHANGE COLUMN `apply_id` `work_order_id` int(11) NOT NULL COMMENT '工单ID' AFTER `id`;

ALTER TABLE `kcp`.`cloud_work_order_servervm_network`
CHANGE COLUMN `apply_id` `work_order_id` int(11) NOT NULL COMMENT '工单ID' AFTER `id`;



ALTER TABLE `kcp`.`cloud_work_order_servervm`
ADD COLUMN `system_type` varchar(255) NULL COMMENT '系统类型' AFTER `architecture`;


ALTER TABLE `kcp`.`cloud_work_order_modify_servervm`
ADD COLUMN `system_type` varchar(255) NULL COMMENT '系统类型' AFTER `architecture`;



ALTER TABLE `kcp`.`cloud_work_order_servervm_disk`
ADD COLUMN `apply_delete` bit(1) NULL DEFAULT 0 COMMENT '申请删除 0:否 1:删除' AFTER `type`,
MODIFY COLUMN `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:删除' AFTER `update_time`;

ALTER TABLE `kcp`.`cloud_work_order_servervm_disk`
ADD COLUMN `disk_id` int(11) NULL DEFAULT 0 COMMENT '磁盘ID' AFTER `type`;



ALTER TABLE `kcp`.`cloud_work_order_servervm_network`
ADD COLUMN `apply_delete` bit(1) NULL DEFAULT 0 COMMENT '申请删除 0:否 1:删除' AFTER `type`,
MODIFY COLUMN `delete_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除表中 0:未删除  1:删除' AFTER `update_time`;

ALTER TABLE `kcp`.`cloud_work_order_servervm_network`
ADD COLUMN `network_id` int(11) NULL DEFAULT 0 COMMENT 'mc中网卡ID' AFTER `type`;



//2021-10-25
ALTER TABLE `kcp`.`cloud_work_order_servervm_disk`
ADD COLUMN `disk_id` bigint(11) NOT NULL DEFAULT 0 COMMENT '磁盘ID' AFTER `work_order_id`;


ALTER TABLE `kcp`.`cloud_work_order_servervm_disk`
ADD COLUMN `disk_unit` tinyint(1) NOT NULL DEFAULT 0 COMMENT '硬盘单位 0:GB 1:TB' AFTER `disk_size`;

ALTER TABLE `kcp`.`cloud_work_order_servervm_disk`
ADD COLUMN `modify_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '变更类型  0:无变动 ，1:新增，2：变更，3：删除' AFTER `type`;

ALTER TABLE `kcp`.`cloud_work_order_servervm_disk`
ADD COLUMN `old_disk_size` int(11) NOT NULL DEFAULT 0 COMMENT '原来磁盘大小' AFTER `modify_type`,
ADD COLUMN `old_disk_unit` tinyint(1) NOT NULL DEFAULT 0 COMMENT '原来磁盘单位 0:GB 1:TB' AFTER `old_disk_size`;


ALTER TABLE `kcp`.`cloud_work_order_servervm_network`
ADD COLUMN `interface_id` bigint(11) NOT NULL DEFAULT 0 COMMENT '网卡ID' AFTER `work_order_id`;

ALTER TABLE `kcp`.`cloud_work_order_servervm_network`
ADD COLUMN `modify_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '变更类型  0:无变动 ，1:新增，2：变更，3：删除' AFTER `type`;



ALTER TABLE `kcp`.`cloud_work_order_servervm`
ADD COLUMN `modify_cpu` int(11) NOT NULL DEFAULT 0 COMMENT '变更后cpu' AFTER `cpu`,
ADD COLUMN `modify_mem` int(11) NULL COMMENT '变更后内存' AFTER `mem`;

update cloud_work_order_servervm set modify_cpu=cpu,modify_mem=mem;

ALTER TABLE `kcp`.`cloud_user`
CHANGE COLUMN `nick_name` `real_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '真实姓名' AFTER `user_name`;

ALTER TABLE `kcp`.`cloud_work_order_user`
CHANGE COLUMN `old_nick_name` `old_real_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '旧真实姓名' AFTER `work_order_id`,
CHANGE COLUMN `new_nick_name` `new_real_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '新真实姓名' AFTER `old_real_name`;

ALTER TABLE `kcp`.`cloud_work_order_servervm`
ADD COLUMN `description` varchar(255) NULL COMMENT '备注信息' AFTER `mem_unit`;





/*
 * 2021-12-08
 */
ALTER TABLE `kcp`.`cloud_work_order_modify_servervm`
ADD COLUMN `original_cpu` int(11) NOT NULL DEFAULT 0 COMMENT '变更前cpu数' AFTER `cpu`,
ADD COLUMN `original_mem` int(11) NOT NULL COMMENT '变更前内存' AFTER `mem`;


update cloud_work_order_modify_servervm set original_cpu=cpu,original_mem=mem;


ALTER TABLE `kcp`.`cloud_work_order_servervm`
ADD COLUMN `modify_apply_num` int(11) NOT NULL DEFAULT 0 COMMENT '变更后申请个数' AFTER `apply_num`;

update cloud_work_order_servervm set modify_apply_num=apply_num;



ALTER TABLE `kcp`.`cloud_work_order_servervm`
ADD COLUMN `modify_cpu` int(11) NOT NULL DEFAULT 0 COMMENT '变更后cpu' AFTER `cpu`,
ADD COLUMN `modify_mem` int(11) NULL COMMENT '变更后内存' AFTER `mem`;

update cloud_work_order_servervm set modify_cpu=cpu,modify_mem=mem;

ALTER TABLE `kcp`.`cloud_user`
CHANGE COLUMN `nick_name` `real_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '真实姓名' AFTER `user_name`;

ALTER TABLE `kcp`.`cloud_work_order_user`
CHANGE COLUMN `old_nick_name` `old_real_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '旧真实姓名' AFTER `work_order_id`,
CHANGE COLUMN `new_nick_name` `new_real_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '新真实姓名' AFTER `old_real_name`;

ALTER TABLE `kcp`.`cloud_work_order_servervm`
ADD COLUMN `description` varchar(255) NULL COMMENT '备注信息' AFTER `mem_unit`;





/*
 * 2021-12-08
 */
ALTER TABLE `kcp`.`cloud_work_order_modify_servervm`
ADD COLUMN `original_cpu` int(11) NOT NULL DEFAULT 0 COMMENT '变更前cpu数' AFTER `cpu`,
ADD COLUMN `original_mem` int(11) NOT NULL COMMENT '变更前内存' AFTER `mem`;


update cloud_work_order_modify_servervm set original_cpu=cpu,original_mem=mem;


ALTER TABLE `kcp`.`cloud_work_order_servervm`
ADD COLUMN `modify_apply_num` int(11) NOT NULL DEFAULT 0 COMMENT '变更后申请个数' AFTER `apply_num`;

update cloud_work_order_servervm set modify_apply_num=apply_num;



#2021-12-23
ALTER TABLE `kcp`.`cloud_work_order_servervm`
ADD COLUMN `apply_servervm_type` tinyint(11) NULL DEFAULT 0 COMMENT '申请类型' AFTER `work_order_id`;

ALTER TABLE `kcp`.`cloud_work_order_servervm_network`
ADD COLUMN `ip_bind_mac` bit(1) NOT NULL DEFAULT b'0' COMMENT 'ip和mac绑定  0:未绑定  1:已绑定' AFTER `modify_type`,
ADD COLUMN `manual_set_ip` bit(1) NOT NULL DEFAULT b'0' COMMENT '手动设置ip    0:未设置  1:已设置' AFTER `ip_bind_mac`,
ADD COLUMN `automatic_acq_ip` bit(1) NOT NULL DEFAULT b'0' COMMENT '自动DHP    0:未设置  1:已设置' AFTER `manual_set_ip`,
ADD COLUMN `ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'ip地址' AFTER `automatic_acq_ip`,
ADD COLUMN `mask` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '掩码' AFTER `ip`,
ADD COLUMN `gw` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '网关' AFTER `mask`,
ADD COLUMN `dns1` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'dns1' AFTER `gw`,
ADD COLUMN `dns2` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'dns2' AFTER `dns1`;



