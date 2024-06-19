create table if not exists alert
(
    id           int auto_increment
        primary key,
    create_time  datetime default CURRENT_TIMESTAMP not null,
    description  varchar(500)                       not null,
    solve_time   datetime                           null,
    solve_method varchar(300)                       null,
    device_name  varchar(50)                        null,
    alert_name   varchar(100)                       null,
    severity     varchar(100)                       null,
    type         varchar(20)                        null
);

create table if not exists assignee
(
    id        int auto_increment
        primary key,
    device_id int null,
    user_id   int null
);

create table if not exists bmc
(
    id       int             not null
        primary key,
    user     varchar(30)     not null,
    password varchar(50)     not null,
    port     int default 623 not null
);

create table if not exists cluster
(
    id   int auto_increment
        primary key,
    name varchar(30) not null
);

create table if not exists device
(
    type       varchar(30)          not null,
    name       varchar(30)          null,
    cluster_id int                  null,
    id         int auto_increment
        primary key,
    position   varchar(30)          null,
    deleted    tinyint(1) default 0 not null
);

create table if not exists hardware
(
    type      varchar(30) not null,
    server_id int         not null,
    id        int auto_increment
        primary key,
    name      varchar(50) null,
    ip        varchar(15) null
);

create table if not exists prometheus_alert
(
    alert_id        int         not null
        primary key,
    last_occurrence datetime    null,
    ip              varchar(15) null,
    alert_name      varchar(50) null
);

create table if not exists role
(
    id   int auto_increment
        primary key,
    name varchar(30) not null
);

create table if not exists server
(
    id        int         not null
        primary key,
    ip        varchar(15) not null comment '校园网IP',
    manage_ip varchar(15) null comment '管理网IP',
    public_ip varchar(15) null comment '公网IP'
);

create table if not exists user
(
    id       int auto_increment
        primary key,
    name     varchar(20) null,
    password char(60)    not null comment 'Bcrypt hashed password',
    phone    char(11)    null,
    email    varchar(40) null
);

create table if not exists user_with_role
(
    user_id int not null,
    role_id int not null,
    primary key (user_id, role_id)
);

