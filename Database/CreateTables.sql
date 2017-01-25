Create or replace type cust as object(
userID numeric,
firstName varchar2(255),
surname varchar2(255),
DOB date,
username varchar2(255),
password varchar2(255),
phoneNum varchar2(10),
);

Create table Customer OF cust;


Create or replace type journeyObject as Object(
JourneyID varchar2(255),
startLatitude numeric,
startLongitude numeric,
endLatitude numeric,
endLongitude numeric,
journeyTime datetime,
journeyUser cust
);

Create table Journey of JourneyObject;

Create or replace type OverSpeed as Object(
OverSpeedUser cust,
journey journeyObject,
timeOfIncident datetime);

Create table OverSpeedLimit of overspeed;

Create or replace type camera as object(
cameraID numeric,
startLatitude numeric,
startLongitude numeric,
endLatitude numeric,
endLongitude numeric,
);

Create table SpeedCamera of camera;

Create or replace type tempCamera as object
(
camera ID numeric,
longitude numeric,
latitude numeric
);

create table TemporaryCamera of tempCamera;