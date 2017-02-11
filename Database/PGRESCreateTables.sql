Create type cust as (
name text,
username varchar(10),
email text,
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
journeyStartTime timestamp,
journeyEndTime timestamp
);

Create table Journey(
JourneyID numeric primary key,
journey journeyObject,
customer cust references Customer(customer)
);

create table JourneyFragment(
journeyfragid numeric,
latitude  numeric,
longitude numeric,
currentSpeed numeric, 
speedLimit numeric,
timeFragment timestamp,
journeyid numeric references Journey(journeyid),
customer numeric references Customer(userID)
)

Create type OverSpeed as(
latitude numeric,
longitude numeric,
timeOfIncident time,
speedTravelling numeric,
speedLimit numeric
);


Create table OverSpeedLimit(
incidentID numeric primary key,
overspeedL overspeed,
journey journeyObject REFerences Journey(journey),
customer cust references Customer(customer)
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
