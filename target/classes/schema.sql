drop table t_users if exists
drop table t_items if exists
drop table t_orders if exists
create table t_users (user_name varchar(255) not null, user_prop integer, primary key (user_name))
create table t_items (item_name varchar(255) not null, item_prop integer, item_type varchar(255), primary key (item_name))
create table t_orders (ord_id bigint generated by default as identity, ord_user varchar(255), ord_item varchar(255), primary key (ord_id))
alter table t_orders add constraint order_item_fk foreign key (ord_item) references t_items
alter table t_orders add constraint order_user_fk foreign key (ord_user) references t_users
