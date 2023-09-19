delete from users;
alter table users alter column id restart with 1;
alter table items alter column id restart with 1;
alter table bookings alter column id restart with 1;
alter table comments alter column id restart with 1;
alter table requests alter column id restart with 1;
