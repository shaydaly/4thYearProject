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
    name = event["body-json"]["name"]
    username = event["body-json"]["username"]
    email = event["body-json"]["email"]
    phoneNum = event["body-json"]["phoneNumber"]
    item_count = 0
    query ="""insert into journey values (nextval('customerSequence'), ROW(%s,%s,%s,%s))"""
    data = (name,username,email,phoneNumber)
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