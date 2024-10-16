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
    type       VARCHAR(30)          NOT NULL,
    name       VARCHAR(30)          NULL,
    cluster_id INT                  NULL,
    id         INT AUTO_INCREMENT PRIMARY KEY,
    position   VARCHAR(30)          NULL,
    deleted    TINYINT(1) DEFAULT 0 NOT NULL
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
    id       int auto_increment
        primary key,
    name     varchar(20)   null,
    password char(60)      not null comment 'Bcrypt hashed password',
    phone    char(11)      null,
    email    varchar(40)   null,
    status   int default 1 not null comment '账号状态 0禁用 1启用'
);

DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`
(
    `id`            int(11)   NOT NULL AUTO_INCREMENT,
    `role_name`     varchar(20)    DEFAULT NULL COMMENT '角色名',
    `create_time`   timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `delete_status` varchar(1)     DEFAULT '1' COMMENT '是否有效  1有效  2无效',
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
    `id`                  int(11) NOT NULL DEFAULT '0' COMMENT '自定id,主要供前端展示权限列表分类排序使用.',
    `menu_code`           varchar(255)     DEFAULT '' COMMENT '归属菜单,前端判断并展示菜单使用,',
    `menu_name`           varchar(255)     DEFAULT '' COMMENT '菜单的中文释义',
    `permission_code`     varchar(255)     DEFAULT '' COMMENT '权限的代码/通配符,对应代码中@RequiresPermissions 的value',
    `permission_name`     varchar(255)     DEFAULT '' COMMENT '本权限的中文释义',
    `required_permission` tinyint(1)       DEFAULT '2' COMMENT '是否本菜单必选权限, 1.必选 2非必选 通常是"列表"权限是必选',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = COMPACT COMMENT ='后台权限表';


DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission`
(
    `id`            int(11)   NOT NULL AUTO_INCREMENT,
    `role_id`       int(11)        DEFAULT NULL COMMENT '角色id',
    `permission_id` int(11)        DEFAULT NULL COMMENT '权限id',
    `create_time`   timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `delete_status` varchar(1)     DEFAULT '1' COMMENT '是否有效 1有效     2无效',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 23
  DEFAULT CHARSET = utf8mb4 COMMENT ='角色-权限关联表';