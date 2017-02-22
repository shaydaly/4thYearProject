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
    longitude = event["body-json"]["longitude"]
    latitude = event["body-json"]["latitude"]
    journeyID = event["body-json"]["journeyID"]
    username = event["body-json"]["username"]
    speedTravelling = event["body-json"]["speedTravelling"]
    speedLimit = event["body-json"]["speedLimit"]
    time = event["body-json"]["time"]
    #userID = event["body-json"]["userID"]
    item_count = 0
    #journeyid = -1
    
    #query = """insert into testJson(longitude,latitude) VALUES(%s,%s)"""
    query ="""insert into overspeedlimit values (nextval('overspeedlimitsequence'), ROW(%s,%s,%s,%s,%s),%s, (select userid from customer where (customer).username=%s))"""
    data = (latitude,longitude,time,speedTravelling,speedLimit,journeyID,username)
    try:
        with conn.cursor() as cur:
            cur.execute(query, data)
            conn.commit()

            #cur.execute('select journey from journey')
            #for row in cur:
            #    item_count += 1
            #    logger.info(row)
            #    journeyid = row
            #print cur.lastrowid   
    except Exception as e:
        logger.error("could not insert into test table")
        print str(e)
        sys.exit()
    

    #return "Added %d items from RDS PostGRES table" %(item_count)
    #return journeyid