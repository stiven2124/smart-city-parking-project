import azure.functions as func
import logging
import json
import re
from azure.cosmos import CosmosClient
import os

app = func.FunctionApp(http_auth_level=func.AuthLevel.ANONYMOUS)
COSMOS_CONN = os.environ.get("CosmosDBConnectionString")
DB_NAME = "SmartCityDb"
CONTAINER_NAME = "ParkingSlots"


# Get SENSOR STATUS FUNCTION
@app.route(route="getSensorStatus")
def getSensorStatus(req: func.HttpRequest) -> func.HttpResponse:
    logging.info('Python HTTP trigger function processed a request.')

    try:
        client = CosmosClient.from_connection_string(COSMOS_CONN)
        database = client.get_database_client(DB_NAME)
        container = database.get_container_client(CONTAINER_NAME)

        query = "SELECT * FROM c"
        items = list(container.query_items(query=query, enable_cross_partition_query=True))

        for item in items:
            for key in ["_rid", "_self", "_etag", "_attachments", "_ts"]:
                item.pop(key, None) 

        return func.HttpResponse(
            json.dumps(items, ensure_ascii=False),
            status_code = 200
        )  
    except Exception as e:
        return func.HttpResponse(
            f"Error: {str(e)}",
            status_code = 500
        )   
      
# Update SENSON STATUS FUNCTION
@app.function_name(name="updateSensorStatus")
@app.event_hub_message_trigger(arg_name="azeventHub",
                       event_hub_name="SensorsHub",
                       connection="IoTHubConnectionString")

def updateSensorStatus(azeventHub: func.EventHubEvent):
    try:
        message_body = azeventHub.get_body().decode('utf-8')
    
        outer_data = json.loads(message_body)

        inner_data_str = outer_data.get('data', "") 

        # Καθαρισμός του string από το "#10" χρησιμοποιώντας Regex
        clean_json_match = re.search(r'\{.*\}', inner_data_str)
        
        if clean_json_match:
            clean_json_str = clean_json_match.group(0)
            data = json.loads(clean_json_str)
            
            parking_id = str(data.get('parkingId'))
            new_status = data.get('status')
            
            logging.info(f"Processing update for ID: {parking_id} with new status: {new_status}")

            client = CosmosClient.from_connection_string(COSMOS_CONN)
            database = client.get_database_client(DB_NAME)
            container = database.get_container_client(CONTAINER_NAME)

            item = container.read_item(item=parking_id, partition_key=parking_id)
            item['status'] = new_status

            container.replace_item(item=parking_id, body=item)
            logging.info(f"SUCCESS: Updated ID {parking_id} to status {new_status}")
        else:
            logging.error(f"FAIL: Could not find valid JSON in data field: {inner_data_str}")

    except Exception as e:
        logging.error(f"Error in updateSensorStatus: {str(e)}")