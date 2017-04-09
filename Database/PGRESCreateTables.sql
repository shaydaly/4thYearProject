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
userid numeric references customer(userid)
);

create table JourneyFragment(
journeyfragid numeric,
latitude  numeric,
longitude numeric,
currentSpeed numeric, 
speedLimit numeric,
timeFragment timestamp,
journeyid numeric references Journey(journeyid),
userid numeric references Customer(userID)
);

Create type OverSpeed as(
latitude numeric,
longitude numeric,
timeOfIncident timestamp,
speedTravelling numeric,
speedLimit numeric
);



##########Create table OverSpeedLimit(
incidentID numeric primary key,
overspeedL overspeed,
journey journeyObject REFerences Journey(journey),
customer cust references Customer(customer)
);##########

Create table OverSpeedLimit(
incidentID numeric primary key,
overspeedL overspeed,
journeyid numeric REFerences Journey(journeyid),
userid numeric references Customer(userid),
roadid numeric
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

Create table SpeedLimits(
osm_road_id numeric primary key, 
longitude numeric,
latitude numeric,
speedLimit numeric
);

Create table road (
roadid numeric primary key,
address varchar
);

create table trafficincidents(
incidentid numeric primary key,
roadid numeric references road(roadid) on delete set null,
incidentdate timestamp,
userid numeric references customer(userid)
);
