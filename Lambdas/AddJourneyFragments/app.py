import sys
import logging
import rds_config
import psycopg2
#rds settings
rds_host  = rds_config.rds_host
name = rds_config.db_username
password = rds_config.db_password
db_name = rds_config.db_name


logger = logging.getLogger()
logger.setLevel(logging.INFO)

try:
	conn_string = "host=%s dbname=%s user=%s password=%s "%(rds_host,db_name,name,password)
	#print newcon
	#conn_params = (rds_host, db_name, name, password)
 
	# print the connection string we will use to connect
	print "Connecting to database\n	->%s" % (conn_string)
 
	# get a connection, if a connect cannot be made an exception will be raised here
	conn = psycopg2.connect(conn_string)
 
	# conn.cursor will return a cursor object, you can use this cursor to perform queries
	cursor = conn.cursor()
	print "Connected!\n"
except:
    logger.error("ERROR: Unexpected error: Could not connect to PGRES instance.")
    sys.exit()

logger.info("SUCCESS: Connection to RDS PGRES! instance succeeded")
def handler(event, context):
    #return event
    for i in event["body-json"]:
    #    print i["journeyID"]
        longitude = i["longitude"]
        latitude = i["latitude"]
        speed = i["currentSpeed"]
        speedLimit = i["speedLimit"]
        time = i["time"]
        username = i["username"]
        journeyID = i["journeyID"]
        osmid = i["osmID"]

        #query = """insert into TestingFragment values (%s,%s)"""
        #data = (latitude,longitude)                
        query ="""insert into JourneyFragment values (nextval('journeyFragmentSequence'),%s,%s,%s,%s,%s,%s,(select userid from customer where (customer).username=%s),%s)"""
        data = (latitude,longitude,speed,speedLimit,time,journeyID, username, osmid)
 
        try:
            with conn.cursor() as cur:
                cur.execute(query, data)
                conn.commit()
        except Exception as e:
            logger.error("ERROR: Unexpected error: Could not connect to MySql instance.")
            print str(e)
            sys.exit()