import sys
import logging
import rds_config
import json
import boto3
import urllib2
import psycopg2
import geocoder
import traceback
import psycopg2
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
    
    longitude = event["longitude"]
    latitude = event["latitude"]
    
    g = geocoder.osm([latitude, longitude], method='reverse')
    city =  ((g.json['county']).split(None, 1)[1])
    
    try:
        #content = urllib2.urlopen("http://nominatim.openstreetmap.org/reverse?format=json&osm_type=W&lat="+latitude+"&lon="+longitude+"&zoom=16").read()
        #content = urllib2.urlopen("http://open.mapquestapi.com/nominatim/v1/reverse.php?key=1TGjV92dKzvrvSbIqHmVxsn0hvRWACF0&format=json&lat="+latitude+"&lon="+longitude+"&zoom=16").read()
        #text = json.loads(content)
        #osm_id = text["osm_id"]
        #content2 = urllib2.urlopen("http://overpass-api.de/api/interpreter?data=[out:json];way("+osm_id+");out;").read()
        #nodeInfo = json.loads(content2)
        #speed = nodeInfo["elements"][0]["tags"]["maxspeed"]
        
        content = urllib2.urlopen("http://overpass-api.de/api/interpreter?data=[out:json];way(around:10,"+latitude+","+longitude+")[maxspeed];out;").read()
        text = json.loads(content)
        osm_id = text["elements"][0]["id"]
        speed = text["elements"][0]["tags"]["maxspeed"]
        roadName = text["elements"][0]["tags"]["name"]
        
        query ="""insert into road values ('{0}','{1}')""".format(osm_id, roadName)
        try:
            with conn.cursor() as cur:
                    cur.execute(query)
                    conn.commit()
              
        except Exception as e:
            print str(e)
            traceback.print_exc()
        
        
        #print speed
        x = {"latitude" : latitude, "longitude" : longitude, "speedLimit" : speed }
        #print x
        #print str(x)
        #invoke_response = lambda_client.invoke(FunctionName="CreateSpeedLimits", InvocationType='Event', Payload=json.dumps(x))
        
        data = {}
        data['speed'] = speed
        data['latitude'] = latitude
        data['longitude'] = longitude
        data['osm_id'] = osm_id
        data['locale'] = city
        json_data = json.dumps(data)
        jsonlist = json.loads(json_data)
        
        url = str("/speedLimits/{0}/{1}").format(city, osm_id)
        print url
        result = firebase.post(url, x)
        
        return jsonlist
        
        #if speed is None:
         #   return "Unknown"
        #else:
         #   return speed
    except Exception as e:
        print str(e)
        traceback.print_exc()
        data = {}
        data['speed'] = -99
        json_data = json.dumps(data)
        jsonlist = json.loads(json_data)
        return jsonlist
        


    #item_count = 0
    #with conn.cursor() as cur:
        #cur.execute("select speed_limit from SpeedLimitNew where way_id ="+node_id+" or node_id="+node_id)
        #for row in cur:
            #return row