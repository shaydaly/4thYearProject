import sys
import logging
import rds_config
import psycopg2
import psycopg2.extras
import json

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
    journeyID = event["journeyID"]
    username = event["username"]
    query = """select * from journeyFragment where journeyid='{0}' order by journeyfragid desc""".format(journeyID)
    try:
        with conn.cursor(cursor_factory=psycopg2.extras.DictCursor) as cur:
            cur.execute(query)
            conn.commit()
            journey=""
            data = []
            json_data=""
            jsonlist=""
            js=""
            #cur.execute('select journey from journey')
            for row in cur:
                data.append({"journeyFragID": str(row[0]), "latitude": str(row[1]), "longitude": str(row[2]), "currentSpeed": str(row[3]), "speedLimit" : str(row[4]), "time" : str(row[5])})
                
            json_data = (json.dumps(data))
            jsonlist = json.loads(json_data)  
            return jsonlist
            

    except Exception as e:
        logger.error("db error")
        print str(e)
        sys.exit()
    

    #return "Added %d items from RDS PostGRES table" %(item_count)
    #return journeyid