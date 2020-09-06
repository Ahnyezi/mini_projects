create table rent_member(
mid varchar2(20) primary key,
pwd varchar2(20) not null,
license varchar2(20) not null,
name varchar2(20) not null);

create table rent_car(
num number(38) not null,
name varchar2(20),
price number(38));

insert into rent_car values(1,'suv',10000);
insert into rent_car values(2,'tyco',10);
insert into rent_car values(3,'bmw',50000);
insert into rent_car values(4,'genesis',60000);
insert into rent_car values(5,'ferarri',100000);

create table rent_order(
order_num number(38) primary key,
order_id varchar2(20) references rent_member(mid) on delete cascade,
order_carname varchar2(20),
order_date date,
order_time varchar2(20),
order_flag char(1)
);

commit;
