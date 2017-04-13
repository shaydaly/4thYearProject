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
    username = event["username"]
    data = {}
    #query = """SELECT * FROM journey inner join overspeedlimit ON (journey.journeyid = overspeedlimit.journeyid);"""
    query = "SELECT incidentid, EXTRACT(day from age(current_date, (overspeedl).timeOfIncident )) from overspeedlimit where userid = (select userid from customer where (customer).username = '{0}') order by incidentid desc limit 1".format(username)
    try:
        with conn.cursor() as cur:
            cur.execute(query)
            conn.commit()
            for row in cur:
                data["daysOverSpeed"] = row[1]
                json_data = json.dumps(data)
                jsonlist = json.loads(json_data)
                return jsonlist
    except Exception as e:
        data["daysOverSpeed"] = -99
        json_data = json.dumps(data)
        jsonlist = json.loads(json_data)
        return jsonlist
    
    #return "Added %d items from RDS PostGRES table" %(item_count)
    #return journeyid