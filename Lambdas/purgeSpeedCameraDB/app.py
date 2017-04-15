import sys
import logging
import rds_config
import json
import boto3
import urllib2
import psycopg2
from datetime import datetime, timedelta
#rds settings
#rds_host  = "shaydbinstance.cuqx5uhbzyug.us-east-1.rds.amazonaws.com"
rds  = rds_config.rds_host
name = rds_config.db_username
password = rds_config.db_password
db_name = rds_config.db_name


logger = logging.getLogger()
logger.setLevel(logging.INFO)
lambda_client = boto3.client('lambda')



conn_string = "host=%s dbname=%s user=%s password=%s "%(rds,db_name,name,password)

	# print the connection string we will use to connect
print "Connecting to database\n	->%s" % (conn_string)
 
	# get a connection, if a connect cannot be made an exception will be raised here
conn = psycopg2.connect(conn_string)
 
	# conn.cursor will return a cursor object, you can use this cursor to perform queries
cursor = conn.cursor()
print "Connected!\n"


logger.info("SUCCESS: Connection to RDS PGRES! instance succeeded")
def handler(event, context):
    """
    This function fetches content from mysql RDS instance
    """
    
    
    from firebase import firebase
    firebase = firebase.FirebaseApplication('https://carvis-a8d0c.firebaseio.com/', None)
    
    try:
        
        d1 = str(datetime.now()).split(".")[0]
        fmt = '%Y-%m-%d %H:%M:%S'
        d1 = datetime.strptime(d1, fmt)
        
        
        #return abs((d2 - d1).days)
        #print 
        url = str("/reportedSpeedCameras/")
        result = firebase.get(url, None)
        #return result
        if result:
            for i in result.items():
                #print i[1]["time"]
                datet = str(i[1]["time"])
                d2 = datetime.strptime(datet, fmt)
                #d2 = datetime.strptime(datet, '%Y-%m-%d %H:%M:%S')
                rowid = i[0]
                #return type(d2)
                duration = d1-d2
                duration  =  duration + timedelta(hours=1)
                seconds = duration.total_seconds()
                hours = seconds // 3600
                if hours >= 3:
                    firebase.delete(url, rowid)
                
                
        url = str("/reportedTrafficIncident/")
        result = firebase.get(url, None)
        #return result
        if result:
            for i in result.items():
                #print i[1]["time"]
                datet = str(i[1]["time"])
                d2 = datetime.strptime(datet, fmt)
                #d2 = datetime.strptime(datet, '%Y-%m-%d %H:%M:%S')
                rowid = i[0]
                #return type(d2)
                duration = d1-d2
                duration  =  duration + timedelta(hours=1)
                seconds = duration.total_seconds()
                hours = seconds // 3600
                print hours
                if hours >= 1:
                    print rowid
                    firebase.delete(url, rowid)

        
        #if speed is None:
         #   return "Unknown"
        #else:
         #   return speed
    except Exception as e:
        print str(e)
        return str(e)
        print(traceback.print_exc())


    #item_count = 0
    #with conn.cursor() as cur:
        #cur.execute("select speed_limit from SpeedLimitNew where way_id ="+node_id+" or node_id="+node_id)
        #for row in cur:
            #return row
import psycopg2