# 数据库初始化

-- 创建库
create database if not exists wisdom;

-- 切换库
use wisdom;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (id)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 图表表
create table if not exists chart
(
    id           bigint auto_increment comment 'id' primary key,
    name         varchar(128) null comment '图表名称',
    goal         text null comment '分析目标',
    chartData    text null comment '图表数据',
    chartType    varchar(128) null comment '图表类型',
    genChart     text null comment  '生成的图表类型',
    genResult    text null comment '生成的图表数据',
    status       varchar(128)   not null    default  'wait' comment 'wait succeed failed running',
    execMessage  text null comment '执行信息',
    userName     varchar(256)                           null comment '用户昵称',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (id)
) comment '图表表' collate = utf8mb4_unicode_ci;

