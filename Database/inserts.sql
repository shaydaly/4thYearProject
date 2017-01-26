insert into customer values (1,ROW('Seamus','Daly','1991-10-07','shayd','shaydaly20@gmail.com','shaypassword','0851329485'))
insert into journey values (nextval('journeySequence'),ROW(55.656,-6.78787,55.454,-7.565,'00:00:00'),ROW('Seamus','Daly','1991-10-07','shayd','shaydaly20@gmail.com','shaypassword','0851329485'))


select (customer).FirstName from customer

