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
    journeyID = "journeyid"
    startLongitude ="(journey).startlongitude"
    startLatitude = "(journey).startlatitude"
    endLongitude ="(journey).endlongitude"
    endLatitude = "(journey).endlatitude"
    startTime = "(journey).journeystarttime"
    endTime = "(journey).journeyendtime"
    query = """select {0}, {1},{2},{3},{4},{5},{7} from journey,customer where journey.userid = customer.userid AND (customer).username='{6}' order by journeyid desc""".format(startLatitude,startLongitude,endLatitude,endLongitude,startTime,endTime, username,journeyID)
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
                #data["startLat"] = str(row[0])  
                #data["startLon"] = str(row[1])
                #data["endLat"] = str(row[2])
                #data["endLon"] = str(row[3])
                #data["starttime"] = str(row[4])
                #data["endtime"] = str(row[5])
                
                data.append({"startLat": str(row[0]), "startLon": str(row[1]), "endLat": str(row[2]), "endLon": str(row[3]), "starttime" : str(row[4]), "endtime" : str(row[5]), "journeyID" : str(row[6])})
                
            json_data = (json.dumps(data))
            jsonlist = json.loads(json_data)  
            return jsonlist
            

    except Exception as e:
        logger.error("db error")
        print str(e)
        sys.exit()
    

    #return "Added %d items from RDS PostGRES table" %(item_count)
    #return journeyid