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
    #Define our connection string
	#conn_string = "host='carvis-pgres-db.cuqx5uhbzyug.us-east-1.rds.amazonaws.com' dbname='CARVIS_DB' user='shayAWS' password='ShayVisCar'"
	#conn_string = "host='carvis-pgres-db.cuqx5uhbzyug.us-east-1.rds.amazonaws.com' dbname='CARVIS_DB' user='shayAWS' password='ShayVisCar'"
	#print conn_string
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
    longitude = event["body-json"]["longitude"]
    latitude = event["body-json"]["latitude"]
    startTime = event["body-json"]["startTime"]
    endTime = event["body-json"]["endTime"]
    username = event["body-json"]["username"]
    item_count = 0
    #query = """insert into testJson(longitude,latitude) VALUES(%s,%s)"""
    query ="""insert into journey values (nextval('journeySequence'), ROW(%s,%s,%s,%s,%s,%s) , (select customer from customer where (customer).username=%s))"""
    data = (latitude,longitude,latitude,longitude,startTime,endTime,username)
    try:
        with conn.cursor() as cur:
            cur.execute(query, data)
            conn.commit()
            #for row in cur:
            #    item_count += 1
            #    logger.info(row)
            #    print(row)
    except Exception as e:
        logger.error("could not insert into test table")
        print str(e)
        sys.exit()
    

    return "Added %d items from RDS PostGRES table" %(item_count)