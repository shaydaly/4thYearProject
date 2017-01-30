import sys
import logging
import rds_config
import psycopg2
#rds settings
rds_host  = "carvis-pgres-db.cuqx5uhbzyug.us-east-1.rds.amazonaws.com"
name = rds_config.db_username
password = rds_config.db_password
db_name = rds_config.db_name


logger = logging.getLogger()
logger.setLevel(logging.INFO)

try:
    #Define our connection string
	conn_string = "host='carvis-pgres-db.cuqx5uhbzyug.us-east-1.rds.amazonaws.com' dbname='CARVIS_DB' user='shayAWS' password='ShayVisCar'"
 
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

    longitude = event["longitude"]
    latitude = event["latitude"]
    currentSpeed = event["currentSpeed"]
    speedLimit = event["speedLimit"]
    time ="10:00:00"
    sequence =""
    
    query =  "INSERT INTO overspeedlimit VALUES (nextval('overspeedLimitSequence'),(select customer from customer where userid = 1),(select journey from journey where journeyid = 101),ROW(%s, %s, %s, %s, %s));"
    data = (latitude, longitude, time , currentSpeed, speedLimit)

    item_count = 0
    with conn.cursor() as cur:
        cur.execute(query, data)
        conn.commit()
        #for row in cur:
         #   item_count += 1
         #   logger.info(row)
            #print(row)
    

    #return "Added %d items from RDS MySQL table" %(item_count)