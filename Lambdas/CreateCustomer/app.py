import sys
import logging
import rds_config
import psycopg2
import json
import datetime
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
except Exception as e:
    logger.error("could not insert into test table")
    print str(e)

logger.info("SUCCESS: Connection to RDS PGRES! instance succeeded")
def handler(event, context):
    name = event["request"]["userAttributes"]["given_name"]
    username = event["userName"]
    email = event["request"]["userAttributes"]["email"]
    phoneNumber = event["request"]["userAttributes"]["phone_number"]
    date = datetime.datetime.now()
   
    item_count = 0
    query ="""insert into customer values (nextval('customerSequence'), ROW('{0}','{1}','{2}','{3}'), '{4}')""".format(name, username, email, phoneNumber, date)
    #data = (name,username,email,phoneNumber)
    try:
        with conn.cursor() as cur:
            cur.execute(query)
            conn.commit()
            #for row in cur:
            #    item_count += 1
            #    logger.info(row)
            #    print(row)
            return event
            #return response
          
    except Exception as e:
        logger.error("could not insert into test table")
        print str(e)
        return event
    

    