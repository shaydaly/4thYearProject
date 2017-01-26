Create type cust as (
firstName text,
surname text,
DOB date,
username varchar(10),
email text,
password text,
phoneNum varchar
);


Create table Customer(
userID numeric primary key,
customer cust
);

ALTER TABLE Customer ADD CONSTRAINT uniqueConstraint UNIQUE (customer);
ALTER TABLE Journey ADD CONSTRAINT uniqueConstraintJourney UNIQUE (journey);

Create type journeyObject as(
startLatitude numeric,
startLongitude numeric,
endLatitude numeric,
endLongitude numeric,
journeyTime time
);

Create table Journey(
JourneyID numeric primary key,
journey journeyObject,
customer cust references Customer(customer)
);

Create type OverSpeed as(
timeOfIncident time,
speedTravelling numeric,
speedLimit numeric
);


Create table OverSpeedLimit(
incidentID numeric primary key,
customer cust references Customer(customer),
journey journeyObject REFerences Journey(journey),
overspeedL overspeed
);

Create type camera as(
startLatitude numeric,
startLongitude numeric,
endLatitude numeric,
endLongitude numeric
);

Create table SpeedCamera(
cameraID numeric primary key,
camera camera
);


Create type tempCamera as
(
longitude numeric,
latitude numeric
);

create table TemporaryCamera(
cameraID numeric primary key,
camera tempCamera
);

commit
