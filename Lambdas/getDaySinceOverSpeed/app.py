import sys
import logging
import rds_config
import psycopg2
import psycopg2.extras
import json
from collections import defaultdict
import operator

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
    query = """SELECT * FROM journey inner join overspeedlimit ON (journey.journeyid = overspeedlimit.journeyid);"""
    
    query = "SELECT incidentid, EXTRACT(day from age(current_date, (overspeedl).timeOfIncident )) from overspeedlimit where userid = (select userid from customer where (customer).username = '{0}') order by incidentid desc limit 1".format(username)
    try:
        with conn.cursor() as cur:
            cur.execute(query)
            conn.commit()
            rowcount = cur.rowcount;
            if rowcount >0:
                for row in cur:
                    data["daysOverSpeed"] = row[1]
            else:
                data["daysOverSpeed"] =-99
    except Exception as e:
        data["daysOverSpeed"] = -99
        print(str(e))
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
    
    query = " select count(userid) from trafficincidents where userid = (select userid from customer where (customer).username = '{0}')".format(username)
    overSpeedDates = []
    try:
        with conn.cursor() as cur:
            cur.execute(query)
            conn.commit()
            for row in cur:
                data["numTrafficIncidentsReported"] = row[0]
    except Exception as e:
        logger.error("db error")
        print str(e)
    query = """select distinct on (address) address, t.roadid from trafficincidents t, road r WHERE incidentdate > current_date - interval '0' day and t.roadid = r.roadid"""
    roadWithTraffic = []
    try:
        with conn.cursor() as cur:
            cur.execute(query)
            conn.commit()
            for row in cur:
                roadWithTraffic.append({"address" : str(row[0])})
        data["roadWithTraffic"] = roadWithTraffic       
    except Exception as e:
        logger.error("db error")
        print str(e)
    
    query = "select jf.journeyid, roadid from journeyfragment jf where jf.userid = (select userid from customer where (customer).username = '{0}') and roadid != -99 and timefragment > current_date - interval '7' day group by jf.journeyid, roadid order by journeyid".format(username)
    urls_d = defaultdict(int)
    try:
        with conn.cursor() as cur:
            cur.execute(query)
            conn.commit()
            for row in cur:
                urls_d[row[1]] += 1
        sorted_x = sorted(urls_d.items(), key=operator.itemgetter(1), reverse =True)[:5]
        
      # urls =  Counter(roads)
        #return urls
              
    except Exception as e:
        logger.error("db error")
        print str(e)
    
    roadsToAvoid = [] 
    query = """SELECT COUNT(t.roadid) as thecount, t.roadid, r.address FROM trafficincidents t, road r WHERE t.incidentdate > current_date - interval '7' day 
                and r.roadid = t.roadid group by t.roadid, address order by thecount desc limit 5 """
    try:
        with conn.cursor() as cur:
            cur.execute(query)
            conn.commit()
            for row in cur:
                for i in sorted_x:
                    if i[0] == row[1]:
                        roadsToAvoid.append({"roadAddress" : row[2]})
            # data["roadsToAvoid"] = roadsToAvoid
        data["roadsToAvoid"]  = roadsToAvoid
    except Exception as e:
        logger.error("db error")
        print str(e)
        
        
    # query = """select count(distinct (userid)) from overspeedlimit where (overspeedl).timeofincident> current_date - interval '7' day  """
    # try:
    #     with conn.cursor() as cur:
    #         cur.execute(query)
    #         conn.commit()
    #         for row in cur:
    #             numUsers = row[0]
    #             print numUsers
    #             if numUsers >0:
    #                 query = """select count (distinct(userid)) from customer"""
    #                 try:
    #                     with conn.cursor() as cur:
    #                         cur.execute(query)
    #                         conn.commit()
    #                         for row in cur:
    #                             num = row[0]
    #                             print(numUsers, num)
    #                             return (numUsers / num * 100)
    #                 except Exception as e:
    #                     logger.error("db error")
    #                     print str(e) 
    # except Exception as e:
    #     logger.error("db error")
    #     print str(e)    
        
    json_data = json.dumps(data)
    jsonlist = json.loads(json_data)
    return jsonlist
    #return "Added %d items from RDS PostGRES table" %(item_count)
    #return journeyid