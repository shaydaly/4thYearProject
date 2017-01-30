import sys
import logging
import rds_config
import pymysql
import json
import boto3
import urllib2
#rds settings
#rds_host  = "shaydbinstance.cuqx5uhbzyug.us-east-1.rds.amazonaws.com"
#name = rds_config.db_username
#password = rds_config.db_password
#db_name = rds_config.db_name


logger = logging.getLogger()
logger.setLevel(logging.INFO)



#try:
#    conn = pymysql.connect(rds_host, user=name, passwd=password, db=db_name, connect_timeout=5)
#except Exception as e:
#    logger.error("ERROR: Unexpected error: Could not connect to MySql instance.")
#    print str(e)
#    sys.exit()

#logger.info("SUCCESS: Connection to RDS mysql instance succeeded")
def handler(event, context):
    """
    This function fetches content from mysql RDS instance
    """
    longitude = event["longitude"]
    latitude = event["latitude"]
    
    try:
        #content = urllib2.urlopen("http://nominatim.openstreetmap.org/reverse?format=json&osm_type=W&lat="+latitude+"&lon="+longitude+"&zoom=16").read()
        content = urllib2.urlopen("http://open.mapquestapi.com/nominatim/v1/reverse.php?key=SloiHBpLJDvIOPDkBAWjbansNvWLPnQU&format=json&lat="+latitude+"&lon="+longitude+"&zoom=16").read()
        text = json.loads(content)
        osm_id = text["osm_id"]
        
        content2 = urllib2.urlopen("http://overpass-api.de/api/interpreter?data=[out:json];way("+osm_id+");out;").read()
        nodeInfo = json.loads(content2)
        speed = nodeInfo["elements"][0]["tags"]["maxspeed"]
        
        data = {}
        data['speed'] = speed
        json_data = json.dumps(data)
        jsonlist = json.loads(json_data)
        return jsonlist
        
        #if speed is None:
         #   return "Unknown"
        #else:
         #   return speed
    except Exception as e:
        return "NA!"
    #return latitude+"_"+longitude

    #item_count = 0
    #with conn.cursor() as cur:
        #cur.execute("select speed_limit from SpeedLimitNew where way_id ="+node_id+" or node_id="+node_id)
        #for row in cur:
            #return row
