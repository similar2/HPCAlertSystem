DROP TABLE IF EXISTS alert;
CREATE TABLE alert
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    description  VARCHAR(500)                       NOT NULL,
    solve_time   DATETIME                           NULL,
    solve_method VARCHAR(300)                       NULL,
    device_name  VARCHAR(50)                        NULL,
    alert_name   VARCHAR(100)                       NULL,
    severity     VARCHAR(100)                       NULL,
    type         VARCHAR(20)                        NULL
);

DROP TABLE IF EXISTS assignee;
CREATE TABLE assignee
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    device_id INT NULL,
    user_id   INT NULL
);

DROP TABLE IF EXISTS bmc;
CREATE TABLE bmc
(
    id       INT             NOT NULL PRIMARY KEY,
    user     VARCHAR(30)     NOT NULL,
    password VARCHAR(50)     NOT NULL,
    port     INT DEFAULT 623 NOT NULL
);

DROP TABLE IF EXISTS cluster;
CREATE TABLE cluster
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) NOT NULL
);

DROP TABLE IF EXISTS device;
CREATE TABLE device
(
    type       VARCHAR(30)          NOT NULL,    -- 设备类型，例如空调、UPS
    name       VARCHAR(30)          NULL,        -- 设备名称
    cluster_id INT                  NULL,        -- 集群ID
    id         INT AUTO_INCREMENT PRIMARY KEY,   -- 主键
    position   VARCHAR(30)          NULL,        -- 设备位置
    deleted    TINYINT(1) DEFAULT 0 NOT NULL,    -- 是否已删除，0表示未删除，1表示已删除
    alert      TINYINT(1) DEFAULT 0 NOT NULL,    -- 告警状态，0表示无告警，1表示有告警
    other      TINYINT(1) DEFAULT 0 NOT NULL     -- 其他字段，0表示无，1表示有
);


DROP TABLE IF EXISTS hardware;
CREATE TABLE hardware
(
    type      VARCHAR(30) NOT NULL,
    server_id INT         NOT NULL,
    id        INT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(50) NULL,
    ip        VARCHAR(15) NULL
);

DROP TABLE IF EXISTS prometheus_alert;
CREATE TABLE prometheus_alert
(
    alert_id        INT         NOT NULL PRIMARY KEY,
    last_occurrence DATETIME    NULL,
    ip              VARCHAR(15) NULL,
    alert_name      VARCHAR(50) NULL
);

DROP TABLE IF EXISTS server;
CREATE TABLE server
(
    id        INT         NOT NULL PRIMARY KEY,
    ip        VARCHAR(15) NOT NULL COMMENT '校园网IP',
    manage_ip VARCHAR(15) NULL COMMENT '管理网IP',
    public_ip VARCHAR(15) NULL COMMENT '公网IP'
);

DROP TABLE IF EXISTS user;
create table user
(
    `id`          int auto_increment
        primary key,
    `email`       varchar(40) null,
    `password`    char(60)    not null comment 'Bcrypt hashed password',
    `name`        varchar(20) null,
    `phone`       char(11)    null,
    `status`      int              default 1 not null comment '账号状态 0禁用 1启用',
    `create_time` timestamp   NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp   NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `deleted`     INT              DEFAULT '0' COMMENT '是否已经删除  0有效  1被删除'
);

DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`
(
    `id`          int(11)   NOT NULL AUTO_INCREMENT,
    `role_name`   varchar(20)    DEFAULT NULL COMMENT '角色名',
    `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     INT            default '1' null comment '是否删除  0有效 1删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = COMPACT COMMENT ='后台角色表';

DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`
(
    `id`      int(11) NOT NULL AUTO_INCREMENT,
    `user_id` int(11) NOT NULL COMMENT '用户id',
    `role_id` int(11) DEFAULT NULL COMMENT '角色id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 14
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户-角色关联表';


DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`
(
    `id`                  int(11) NOT NULL AUTO_INCREMENT COMMENT '自定id,主要供前端展示权限列表分类排序使用.',
    `menu_code`           varchar(255) DEFAULT '' COMMENT '归属菜单,前端判断并展示菜单使用,',
    `menu_name`           varchar(255) DEFAULT '' COMMENT '菜单的中文释义',
    `permission_code`     varchar(255) DEFAULT '' COMMENT '权限的代码/通配符,对应代码中@RequiresPermissions 的value',
    `permission_name`     varchar(255) DEFAULT '' COMMENT '本权限的中文释义',
    `required_permission` tinyint(1)   DEFAULT '2' COMMENT '是否本菜单必选权限, 1.必选 2非必选 通常是"列表"权限是必选',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = COMPACT COMMENT ='后台权限表';


DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission`
(
    `id`            int(11) NOT NULL AUTO_INCREMENT,
    `role_id`       int(11) DEFAULT NULL COMMENT '角色id',
    `permission_id` int(11) DEFAULT NULL COMMENT '权限id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 23
  DEFAULT CHARSET = utf8mb4 COMMENT ='角色-权限关联表';

DROP TABLE IF EXISTS `device_alert`;
CREATE TABLE device_alert (
    id INT AUTO_INCREMENT PRIMARY KEY,                     -- 主键
    device_id INT NOT NULL,                                -- 辅机设备ID（外键，关联device表）
    alert_time DATETIME NOT NULL,                          -- 告警发生时间
    resolve_time DATETIME,                                 -- 解决时间
    resolve_method VARCHAR(300),                           -- 解决方法
    alert_status ENUM('挂起', '处理中', '已解决') DEFAULT '挂起', -- 告警状态
    alert_level ENUM('低', '中', '高') NOT NULL,            -- 告警级别
    description VARCHAR(500),                              -- 告警描述
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,        -- 创建时间
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 更新时间
    deleted TINYINT DEFAULT 0,                             -- 是否删除（逻辑删除标志）
    responsible_person VARCHAR(100)                      -- 责任人
);

DROP TABLE IF EXISTS `other_device_alert`;
CREATE TABLE other_device_alert (
   id INT PRIMARY KEY,
   alert BOOLEAN
);

-- Insert the specified target servers into the server table
INSERT INTO server (id, ip, manage_ip, public_ip)
VALUES (4, '11.11.3.101', NULL, NULL),
       (5, '11.11.3.102', NULL, NULL),
       (6, '11.11.3.103', NULL, NULL),
       (7, '11.11.3.104', NULL, NULL);


-- Insert data into the hardware table
INSERT INTO hardware (type, server_id, name, ip)
VALUES ('BMC', 4, 'IPMI Hardware 101', '11.11.3.101'),
       ('BMC', 5, 'IPMI Hardware 102', '11.11.3.102'),
       ('BMC', 6, 'IPMI Hardware 103', '11.11.3.103'),
       ('BMC', 7, 'IPMI Hardware 104', '11.11.3.104');


# INSERT INTO device (id, name, type, cluster_id, position, deleted)
#     VALUES(1, 'Device 101', 'UPS', 4, 'Rack 1', 0),
#           (2, 'Device 102', 'BATTERY', 5, 'Rack 2', 0),
#           (3, 'Device 103', 'AIR-CONDITIONER', 6, 'Rack 3', 0);

-- Insert data into the device table
INSERT INTO device (type, name, cluster_id, id, position, deleted)
VALUES
    ('AIR_CONDITIONER', '空调03号', 1, 1, '太乙集群机房', 0),
    ('BATTERY', 'Device 106', 2, 2, 'Rack 2', 0),
    ('UPS', 'Device 107', 2, 3, 'Rack 3', 0),
    ('SERVER', 'Device 101', 1, 4, 'Rack 1', 0),
    ('SERVER', 'Device 102', 1, 5, 'Rack 2', 0),
    ('SERVER', 'Device 103', 1, 6, 'Rack 3', 0),
    ('SERVER', 'Device 104', 1, 7, 'Rack 4', 0),
    ('AIR_CONDITIONER', '空调03号', 1, 8, '太乙集群机房', 1),
    ('AIR_CONDITIONER', '1', 1, 9, '1', 1),
    ('BATTERY', 'Device 120', 1, 10, 'Rack 5', 0),
    ('UPS', '150', 1, 11, 'Rack 6', 0),
    ('UPS', 'Device 250', 1, 12, 'Rack 3', 0),
    ('UPS', 'Device 500', 1, 13, 'Rack 1', 0);

INSERT INTO device_alert (id, device_id, alert_time, resolve_time, resolve_method, alert_status, alert_level, description, created_at, updated_at, deleted, responsible_person)
VALUES(1, 3, '2024-11-21 10:00:00', '2024-11-22 16:00:00', '更换电池111', '处理中', '高', 'UPS 电池已更换', '2024-11-21 14:22:00', '2024-12-11 22:38:00', 0, '李四'),
      (2, 3, '2024-11-21 10:30:00', '2024-11-29 16:00:00', '我不道啊', '处理中', '高', '炸了', '2024-11-21 14:23:00', '2024-11-27 12:58:00', 0, '李四'),
      (3, 1, '2024-11-20 10:00:00', '2024-11-22 16:00:00', '换', '挂起', '高', 'UPS 电量不足，需换电池', '2024-11-21 14:24:00', '2024-11-27 12:19:00', 0, '王五'),
      (4, 2, '2024-11-20 11:00:00', '2024-11-20 16:15:00', '巡检它', '处理中', '中', '电池温度异常，需要巡检', '2024-11-21 14:24:00', '2024-11-27 12:14:00', 0, '赵六'),
      (5, 3, '2024-11-20 12:00:00', '2024-11-29 16:00:00', '调整', '已解决', '低', '水泵压力偏低，已调整', '2024-11-21 14:24:00', '2024-11-27 12:09:00', 0, '杂七'),
      (6, 10, '2024-11-27 12:09:00', '2024-11-29 16:00:00', '？？', '挂起', '低', '报警了', '2024-11-27 12:11:00', '2024-11-27 12:19:00', 0, 'wyh'),
      (7, 3, '2024-11-28 03:12:00', '2024-11-29 16:00:00', '11', '挂起', '低', '11', '2024-11-28 03:13:00', '2024-11-28 03:13:00', 0, '11'),
      (8, 11, '2024-11-28 12:33:00', '2024-12-06 16:00:00', '11', '挂起', '低', '11', '2024-11-28 12:35:00', '2024-11-28 12:35:00', 0, '1111111'),
      (9, 12, '2024-11-28 15:14:00', '2024-12-06 16:00:00', '222222222222', '已解决', '低', '1111111111111111111', '2024-11-28 15:16:00', '2024-12-11 02:47:00', 0, '2333333333333'),
      (10, 1, '2024-11-20 10:00:00', '2024-11-22 16:00:00', '换', '挂起', '高', 'UPS 电量不足，需换电池', '2024-12-05 15:42:00', '2024-12-05 15:42:00', 0, '王五'),
      (11, 1, '2024-11-20 10:00:00', '2024-11-22 16:00:00', '换', '挂起', '高', 'UPS 电量不足，需换电池', '2024-12-05 15:43:00', '2024-12-05 15:43:00', 0, '王五'),
      (12, 1, '2024-11-20 10:00:00', '2024-11-22 16:00:00', '换', '挂起', '高', 'UPS 电量不足，需换电池', '2024-12-05 15:43:00', '2024-12-05 15:43:00', 0, '王五'),
      (13, 1, '2024-11-20 10:00:00', '2024-11-22 16:00:00', '换', '挂起', '高', 'UPS 电量不足，需换电池', '2024-12-05 15:43:00', '2024-12-05 15:43:00', 0, '王五'),
      (14, 1, '2024-11-20 10:00:00', '2024-11-22 16:00:00', '换', '挂起', '高', 'UPS 电量不足，需换电池', '2024-12-05 15:44:00', '2024-12-05 15:44:00', 0, '王五');


INSERT INTO other_device_alert (id, alert) VALUES
      (1, 1),
      (2, 1),
      (3, 1),
      (4, 0),
      (5, 0),
      (6, 1),
      (7, 1),
      (8, 1),
      (9, 1),
      (10, 1),
      (11, 1),
      (12, 1),
      (13, 0);


INSERT INTO user (id, email, password, name, phone, status, create_time, update_time, deleted)
VALUES
    (1, '1602208987@qq.com', '$2a$10$O/86RAz9sEnZNF9TP9zCy.0FFMUJ8wx3KR.4qgEIaeO6k3sJdH0L2', 'admin', NULL, 1, '2024-11-06 20:09:00', '2024-11-06 20:09:00', 0),
    (2, '498463887@qq.com', '$2a$10$CmsYncvJpfgljplZp1bGkeWwz1pUJpM9pzTHKHBZE6UnGZNJ88AFG', '498463887@qq.com', '13622384071', 1, '2024-11-07 14:37:00', '2024-11-07 15:18:00', 0),
    (4, 'izhsf0.iq126@qq.com', '$2a$10$sekUuegqTFVbB1sTB5.EuOvONfCyhGD8FhbMl0dpXckXRjtidWd2e', 'test', '15868453763', 1, '2024-11-07 14:51:00', '2024-11-07 14:55:00', 0),
    (5, 'admin@sustech.edu.cn', '$2a$10$mHWoCbtQ56UfPuC3kTeMh.HxO61te8mCKk.gKwAS0LtIWHjdm1xh.', '123456', '13345678998', 1, '2024-11-14 15:21:00', '2024-11-14 15:21:00', 0);


INSERT INTO role (id, role_name, create_time, update_time, deleted)
VALUES
    (5, '超级管理', '2024-11-06 20:09:00', '2024-11-12 18:10:00', 0),
    (6, '1', '2024-11-07 00:59:00', '2024-11-07 00:59:00', 1),
    (7, '1', '2024-11-07 00:59:00', '2024-11-07 00:59:00', 1),
    (8, '管理', '2024-11-07 14:39:00', '2024-11-21 15:44:00', 0),
    (9, 'test', '2024-11-07 14:40:00', '2024-11-07 14:40:00', 0);

INSERT INTO user_role (id, user_id, role_id)
VALUES
    (16, 3, 5),
    (17, 3, 8),
    (26, 52, 16),
    (27, 52, 60),
    (28, 4, 5),
    (29, 4, 8),
    (31, 1, 8),
    (40, 2, 8),
    (41, 2, 5),
    (42, 5, 8);


INSERT INTO permission (id, menu_code, menu_name, permission_code, permission_name, required_permission)
VALUES
    (101, 'article', '告警信息', 'article:list', '列表', 1),
    (102, 'article', '告警信息', 'article:add', '添加', 2),
    (103, 'article', '告警信息', 'article:update', '修改', 2),
    (104, 'article', '告警信息', 'article:delete', '删除', 2),
    (601, 'user', '用户', 'user:list', '列表', 1),
    (602, 'user', '用户', 'user:add', '新增', 2),
    (603, 'user', '用户', 'user:update', '修改', 2),
    (604, 'user', '用户', 'user:delete', '删除', 2),
    (701, 'role', '角色权限', 'role:list', '列表', 1),
    (702, 'role', '角色权限', 'role:add', '新增', 2),
    (703, 'role', '角色权限', 'role:update', '修改', 2),
    (704, 'role', '角色权限', 'role:delete', '删除', 2),
    (705, 'cluster1', '集群1', 'cluster1:list', '列表', 1),
    (750, 'cluster2', '集群2', 'cluster2:list', '列表', 1),
    (800, 'rule', '规则', 'rule:list', '列表', 1),
    (801, 'rule', '规则', 'rule:update', '更新', 2),
    (802, 'rule', '规则', 'rule:delete', '删除', 2),
    (900, 'aux_monitor', '辅机监控', 'aux_monitor:list', '列表', 1),
    (901, 'aux_monitor', '辅机监控', 'aux_monitor:device', '设备管理', 2),
    (902, 'aux_monitor', '辅机监控', 'aux_monitor:alert', '告警管理', 2);

INSERT INTO role_permission (id, role_id, permission_id)
VALUES
    (315, 9, 101),
    (316, 9, 102),
    (317, 9, 103),
    (318, 9, 701),
    (319, 9, 702),
    (367, 11, 101),
    (368, 11, 102),
    (369, 11, 103),
    (370, 11, 701),
    (371, 11, 702),
    (372, 11, 704),
    (421, 5, 101),
    (457, 8, 705),
    (458, 8, 900),
    (459, 8, 701),
    (460, 8, 702),
    (461, 8, 703),
    (462, 8, 704),
    (463, 8, 800),
    (464, 8, 801),
    (465, 8, 802),
    (466, 8, 601),
    (467, 8, 602),
    (468, 8, 603),
    (469, 8, 101),
    (470, 8, 102),
    (471, 8, 103),
    (472, 8, 104),
    (473, 8, 750),
    (474, 8, 901),
    (475, 8, 902);

