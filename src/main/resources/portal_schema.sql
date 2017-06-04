drop table if exists tb_user;

create table tb_user(
	id bigint(20) not null auto_increment,
	username varchar(50) not null,
	password varchar(32) not null,
	phone varchar(20) default null,
	email varchar(50) default null,
	created datetime not null,
	updated datetime not null,
	primary key(id),
	unique key username(username) using btree,
	unique key phone(phone) using btree,
	unique key email(email) using btree
);

insert into tb_user(username, password, phone, email, created, updated)
values("zhangsan", "zhangsan", 13888888888, "zhangsan@sina.com", now(), now());
insert into tb_user(username, password, phone, email, created, updated)
values("lisi", "lisi", 13666666666, "lisi@sina.com", now(), now());
insert into tb_user(username, password, phone, email, created, updated)
values("wangwu", "wangwu", 13999999999, "wangwu@sina.com", now(), now());