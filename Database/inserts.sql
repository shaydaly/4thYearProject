insert into customer values (1,ROW('Seamus','Daly','1991-10-07','shayd','shaydaly20@gmail.com','shaypassword','0851329485'))
insert into journey values (nextval('journeySequence'),ROW(55.656,-6.78787,55.454,-7.565,'00:00:00'),ROW('Seamus','Daly','1991-10-07','shayd','shaydaly20@gmail.com','shaypassword','0851329485'))
INSERT INTO overspeedlimit VALUES (nextval('overspeedLimitSequence'),(select customer from customer where userid = 1),(select journey from journey where journeyid = 101),ROW(20, 20, '10:45:12', 55, 100));

select (customer).FirstName from customer

