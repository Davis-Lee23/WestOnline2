create table city(
    id int primary key,
    name varchar(255) not null,
    lat double not null,
    lon double not null
)

create table weather(
    id int,
    fxDate  varchar(255) not null,
    tempMax varchar(255) not null,
    tempMin varchar(255) not null,
    textDay varchar(255) not null,
    primary key (id,fxDate),
    foreign key (id) references city (id)
)

