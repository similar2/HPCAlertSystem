DROP TABLE IF EXISTS alert;
CREATE TABLE alert
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    description  VARCHAR(500) NOT NULL,
    solve_time   DATETIME NULL,
    solve_method VARCHAR(300) NULL,
    device_name  VARCHAR(50) NULL,
    alert_name   VARCHAR(100) NULL,
    severity     VARCHAR(100) NULL,
    type         VARCHAR(20) NULL
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
    id       INT NOT NULL PRIMARY KEY,
    user     VARCHAR(30) NOT NULL,
    password VARCHAR(50) NOT NULL,
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
    type       VARCHAR(30) NOT NULL,
    name       VARCHAR(30) NULL,
    cluster_id INT NULL,
    id         INT AUTO_INCREMENT PRIMARY KEY,
    position   VARCHAR(30) NULL,
    deleted    TINYINT(1) DEFAULT 0 NOT NULL
);

DROP TABLE IF EXISTS hardware;
CREATE TABLE hardware
(
    type      VARCHAR(30) NOT NULL,
    server_id INT NOT NULL,
    id        INT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(50) NULL,
    ip        VARCHAR(15) NULL
);

DROP TABLE IF EXISTS prometheus_alert;
CREATE TABLE prometheus_alert
(
    alert_id        INT NOT NULL PRIMARY KEY,
    last_occurrence DATETIME NULL,
    ip              VARCHAR(15) NULL,
    alert_name      VARCHAR(50) NULL
);

DROP TABLE IF EXISTS role;
CREATE TABLE role
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) NOT NULL
);

DROP TABLE IF EXISTS server;
CREATE TABLE server
(
    id        INT NOT NULL PRIMARY KEY,
    ip        VARCHAR(15) NOT NULL COMMENT '校园网IP',
    manage_ip VARCHAR(15) NULL COMMENT '管理网IP',
    public_ip VARCHAR(15) NULL COMMENT '公网IP'
);

DROP TABLE IF EXISTS user;
CREATE TABLE user
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(20) NULL,
    password CHAR(60) NOT NULL COMMENT 'Bcrypt hashed password',
    phone    CHAR(11) NULL,
    email    VARCHAR(40) NULL,
    role     INT CHECK (role IN (1, 2, 3))
);

DROP TABLE IF EXISTS user_with_role;
CREATE TABLE user_with_role
(
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);