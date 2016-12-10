import sys
import logging
import rds_config
import pymysql
import json
import boto3
import urllib2
#rds settings
rds_host  = "shaydbinstance.cuqx5uhbzyug.us-east-1.rds.amazonaws.com"
name = rds_config.db_username
password = rds_config.db_password
db_name = rds_config.db_name


logger = logging.getLogger()
logger.setLevel(logging.INFO)



try:
    conn = pymysql.connect(rds_host, user=name, passwd=password, db=db_name, connect_timeout=5)
except Exception as e:
    logger.error("ERROR: Unexpected error: Could not connect to MySql instance.")
    print str(e)
    sys.exit()

logger.info("SUCCESS: Connection to RDS mysql instance succeeded")
def handler(event, context):
    """
    This function fetches content from mysql RDS instance
    """
    longitude=""
    latitude=""
    my_list = []
    

    for i in event:
        my_list.append(event[i])
     
    #return len(my_list) 
    latitude = my_list[0]
    longitude = my_list[1]
    
    content = urllib2.urlopen("http://nominatim.openstreetmap.org/reverse?format=json&lat="+latitude+"&lon="+longitude+"&zoom=18&addressdetails=1").read()
    text = json.loads(content)
    node_id = text["osm_id"]
   
    #return latitude+"_"+longitude

    item_count = 0

    with conn.cursor() as cur:
        #cur.execute("create table SpeedLimits ( limitID  int NOT NULL, speed int NOT NULL, PRIMARY KEY (limitID))")  
        #cur.execute('insert into SpeedLimits (limitID, speed) values(1, 50)')
        #cur.execute('insert into SpeedLimits (limitID, speed) values(2, 50)')
        #cur.execute('insert into SpeedLimits (limitID, speed) values(3, 100)')
        #conn.commit()
        cur.execute("select speed_limit  from SpeedLimit where node_id ="+node_id)
        for row in cur:
            item_count += 1
            logger.info(row)
            return row

    

    #sns = boto3.client('sns')
    #number = '0851329485'
    #sns.publish(PhoneNumber = number, Message='example text message' )