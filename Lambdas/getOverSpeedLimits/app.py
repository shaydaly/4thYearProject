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
    # query = "SELECT COUNT(*) FROM journey where userid = (select userid from customer where (customer).username='{0}');".format(username)
    # try:
    #     with conn.cursor() as cur:
    #         cur.execute(query)
    #         conn.commit()
    #         for row in cur:
    #             data["numJourneys"] = str(row[0])
    # except Exception as e:
    #     logger.error("db error")
    #     print str(e)
    #     #sys.exit()
    
    query = "SELECT COUNT(distinct(journeyid)) FROM overspeedlimit where userid = (select userid from customer where (customer).username='{0}');".format(username)
    try:
        with conn.cursor() as cur:
            cur.execute(query)
            conn.commit()
            for row in cur:
                data["journeysWithOverSpeed"] = str(row[0])

    except Exception as e:
        logger.error("db error")
        print str(e)
        #sys.exit()
        
    query = "SELECT COUNT(distinct(journeyid)) FROM overspeedlimit where userid = (select userid from customer where (customer).username = '{0}');".format(username)
    try:
        with conn.cursor() as cur:
            cur.execute(query)
            conn.commit()
            for row in cur:
                data["journeysWithOverSpeed"] = str(row[0])

    except Exception as e:
        logger.error("db error")
        print str(e)
        #sys.exit()
        
    query = "select (journey).journeyStartTime, (journey).journeyendTime,(journey).startlatitude,(journey).startlongitude, (journey).endlatitude, (journey).endLongitude from journey where userid= (select userid from customer where (customer).username = '{0}') and (journey).endLongitude != 0".format(username)
    journeys = []
    try:
        with conn.cursor() as cur:
            cur.execute(query)
            conn.commit()
            for row in cur:
                journeys.append({"startTime": str(row[0]), "endTime": str(row[1]), "startLatitude" : str(row[2]), "startLongitude" : str(row[3]), "endLatitude" : str(row[4]), "endLongitude" : str(row[5])})
        data["journeys"] = journeys
    except Exception as e:
        logger.error("db error")
        print str(e)
        #sys.exit()

    roadid = "99"
    query = "select roadid, count (roadid) as value_occurence from overspeedlimit where userid = (select userid from customer where (customer).username = '{0}') group by roadid order by value_occurence desc limit 1".format(username)
    try:
        with conn.cursor() as cur:
            cur.execute(query)
            conn.commit()
            data["overSpeedRoad"] = 0
            for row in cur:
                data["overSpeedRoad"] = str(row[0])
                roadid = str(row[0])
    except Exception as e:
        logger.error("db error")
        print str(e)
        #sys.exit()
        
    query = "select address from road where roadid = '{0}'".format(roadid)
    try:
        with conn.cursor() as cur:
            cur.execute(query)
            conn.commit()
            data["roadAddress"] = "NA"
            for row in cur:
                data["roadAddress"] = str(row[0])
    except Exception as e:
        logger.error("db error")
        print str(e)
        #sys.exit()
        
    query = "select distinct(overspeedl).timeOfIncident::date from overspeedlimit where userid = (select userid from customer where (customer).username = '{0}')".format(username)
    overSpeedDates = []
    try:
        with conn.cursor() as cur:
            cur.execute(query)
            conn.commit()
            for row in cur:
                overSpeedDates.append({"overSpeedDate": str(row[0])})
        data["overSpeedDates"] = overSpeedDates
    except Exception as e:
        logger.error("db error")
        print str(e)
        #sys.exit()
        
    # query = "select joined::date from customer where userid = (select userid from customer where (customer).username = '{0}')".format(username)
    # try:
    #     with conn.cursor() as cur:
    #         cur.execute(query)
    #         conn.commit()
    #         for row in cur:
    #             data["memberSince"] = str(row[0])
    # except Exception as e:
    #     logger.error("db error")
    #     print str(e)
    #     #sys.exit()
    query = "select round(AVG(currentspeed),0) from journeyfragment where userid = (select userid from customer where (customer).username = '{0}')".format(username)
    try:
        with conn.cursor() as cur:
            cur.execute(query)
            conn.commit()
            for row in cur:
                data["averageSpeed"] = str(row[0])
    except Exception as e:
        data["averageSpeed"] = str(row[0])
        print str(e)
        #sys.exit()  


    json_data = json.dumps(data)
    jsonlist = json.loads(json_data)
    return jsonlist
    #return "Added %d items from RDS PostGRES table" %(item_count)
    #return journeyid