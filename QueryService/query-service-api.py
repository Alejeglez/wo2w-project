from flask import Flask, request, jsonify
from prometheus_client import Counter, Gauge, Histogram, Summary, start_http_server
import math
import time
import requests
import os
import boto3
app = Flask(__name__)



PATH_REQUEST_COUNT = Counter('path_requests_total', 'Total de solicitudes recibidas (paths)')
SHORTEST_PATH_REQUEST_COUNT = Counter('shortest_path_requests_total', 'Total de solicitudes recibidas (shortest path)')
NODES_WITH_CONNECTIVITY_DEGREE_REQUEST_COUNT = Counter('nodes_with_connectivity_degree_requests_total', 'Total de solicitudes recibidas (nodes with connectivity degree)')
MAX_DISTANCE_PATH_REQUEST_COUNT = Counter('max_distance_path_requests_total', 'Total de solicitudes recibidas (max distance path)')
NODES_WITH_HIGHEST_CONNECTIONS_REQUEST_COUNT = Counter('nodes_with_highest_connections_requests_total', 'Total de solicitudes recibidas (nodes with highest connections)')
ISOLATES_NODES_REQUEST_COUNT = Counter('isolates_nodes_requests_total', 'Total de solicitudes recibidas (isolates nodes)')
NODES_CLUSTER_REQUEST_COUNT = Counter('nodes_cluster_requests_total', 'Total de solicitudes recibidas (nodes cluster)')

REQUEST_ID = 0

REQUEST_IN_PROGRESS = Gauge('query_requests_in_progress', 'Solicitudes en progreso')
REQUEST_LATENCY = Histogram('query_request_latency_seconds', 'Distribución de latencias')
REQUEST_STATS = Summary('query_request_summary_seconds', 'Estadísticas de latencias')

elb_dns = "external-lb-768934270.us-east-1.elb.amazonaws.com"
sns_arn = "arn:aws:sns:us-east-1:152286733504:request-processing-topic"
s3_bucket = "s3-wo2w-data-processing"
s3 = boto3.client('s3', region_name='us-east-1')
sns = boto3.client('sns', region_name='us-east-1')

@app.route('/findPaths', methods=['GET'])
def findPaths():
    global REQUEST_ID
    id = "1" + str(REQUEST_ID)
    REQUEST_ID += 1
    PATH_REQUEST_COUNT.inc()
    REQUEST_IN_PROGRESS.inc()  # Incrementar gauge de solicitudes en progreso
    
    
    start_time = time.time()
    try:
        origin = request.args.get('from')
        to = request.args.get('to')
        if not origin or not to:
            return {400: "Debe especificar un origen y un destino"}, 400
        response = requests.get(f'http://{elb_dns}/read?method=findPaths&start={origin}&end={to}')
        response = response.json()
        response['data']['request_id'] = id
        return response, 200
    except Exception as e:
        return {"message": str(e)}, 500
    finally:
        latency = time.time() - start_time
        REQUEST_LATENCY.observe(latency)  # Registrar latencia en histogram
        REQUEST_STATS.observe(latency)  # Registrar latencia en summary
        REQUEST_IN_PROGRESS.dec()  # Decrementar gauge de solicitudes en progreso

@app.route('/findShortestPaths', methods=['GET'])
def findShortestPath():
    global REQUEST_ID
    id = "1" + str(REQUEST_ID)
    REQUEST_ID += 1
    SHORTEST_PATH_REQUEST_COUNT.inc()
    REQUEST_IN_PROGRESS.inc()  # Incrementar gauge de solicitudes en progreso
    
    
    start_time = time.time()
    try:
        origin = request.args.get('from')
        to = request.args.get('to')
        algorithm = request.args.get('method')
        if not origin or not to:
            return {400: "Debe especificar un origen y un destino"}, 400
        response = requests.get(f'http://{elb_dns}/read?method=findPaths&start={origin}&end={to}')
        response = response.json()
        response['data']['request_id'] = id
        response['data']['algorithm'] = algorithm
        sns.publish(TopicArn=sns_arn, Message=str(response["data"]).replace("'", '"'))
        while True:
            try:
                content = s3.get_object(Bucket=s3_bucket, Key=f'{id}.json')
                data = content['Body'].read().decode('utf-8')
                break
            except Exception as e:
                continue
        return data, 200
    except Exception as e:
        return {"message": str(e)}, 500
    finally:
        latency = time.time() - start_time
        REQUEST_LATENCY.observe(latency)  # Registrar latencia en histogram
        REQUEST_STATS.observe(latency)  # Registrar latencia en summary
        REQUEST_IN_PROGRESS.dec()  # Decrementar gauge de solicitudes en progreso

@app.route('/findNodesWithConnectivityDegree', methods=['GET'])
def findNodesWithConnectivityDegree():
    global REQUEST_ID
    id = "1" + str(REQUEST_ID)
    REQUEST_ID += 1
    NODES_WITH_CONNECTIVITY_DEGREE_REQUEST_COUNT.inc()
    REQUEST_IN_PROGRESS.inc()  # Incrementar gauge de solicitudes en progreso
    
    start_time = time.time()
    try:
        number = request.args.get('number')
        if not number:
            return {400: "Debe especificar un número"}, 400
        response = requests.get(f'http://{elb_dns}/read?method=findNodesWithConnectivityDegree&number={number}')
        return jsonify(response.json()), 200
    except Exception as e:
        return {"message": str(e)}, 500
    finally:
        latency = time.time() - start_time
        REQUEST_LATENCY.observe(latency)  # Registrar latencia en histogram
        REQUEST_STATS.observe(latency)  # Registrar latencia en summary
        REQUEST_IN_PROGRESS.dec()  # Decrementar gauge de solicitudes en progreso

@app.route('/findMaxDistancePath', methods=['GET'])
def maxDistancePath():
    global REQUEST_ID
    id = "1" + str(REQUEST_ID)
    REQUEST_ID += 1
    MAX_DISTANCE_PATH_REQUEST_COUNT.inc()
    REQUEST_IN_PROGRESS.inc()  # Incrementar gauge de solicitudes en progreso
    
    start_time = time.time()
    try:
        response = requests.get(f'http://{elb_dns}/read?method=findMaxDistancePath')
        return jsonify(response.json()), 200
    except Exception as e:
        return {"message": str(e)}, 500
    finally:
        latency = time.time() - start_time
        REQUEST_LATENCY.observe(latency)  # Registrar latencia en histogram
        REQUEST_STATS.observe(latency)  # Registrar latencia en summary
        REQUEST_IN_PROGRESS.dec()  # Decrementar gauge de solicitudes en progreso

@app.route('/findNodesWithHighestConnections', methods=['GET'])
def nodesWithHighestConnections():
    global REQUEST_ID
    id = "1" + str(REQUEST_ID)
    REQUEST_ID += 1
    NODES_WITH_HIGHEST_CONNECTIONS_REQUEST_COUNT.inc()
    REQUEST_IN_PROGRESS.inc()  # Incrementar gauge de solicitudes en progreso
    
    start_time = time.time()
    try:
        response = requests.get(f'http://{elb_dns}/read?method=findNodesWithHighestConnections')
        return jsonify(response.json()), 200
    except Exception as e:
        return {"message": str(e)}, 500
    finally:
        latency = time.time() - start_time
        REQUEST_LATENCY.observe(latency)  # Registrar latencia en histogram
        REQUEST_STATS.observe(latency)  # Registrar latencia en summary
        REQUEST_IN_PROGRESS.dec()  # Decrementar gauge de solicitudes en progreso

@app.route('/findIsolatedNodes', methods=['GET'])
def isolatesNodes():
    global REQUEST_ID
    id = "1" + str(REQUEST_ID)
    REQUEST_ID += 1
    ISOLATES_NODES_REQUEST_COUNT.inc()
    REQUEST_IN_PROGRESS.inc()  # Incrementar gauge de solicitudes en progreso
    start_time = time.time()
    try:
        response = requests.get(f'http://{elb_dns}/read?method=findIsolatedNodes')
        return jsonify(response.json()), 200
    except Exception as e:
        return {"message": str(e)}, 500
    finally:
        latency = time.time() - start_time
        REQUEST_LATENCY.observe(latency)  # Registrar latencia en histogram
        REQUEST_STATS.observe(latency)  # Registrar latencia en summary
        REQUEST_IN_PROGRESS.dec()  # Decrementar gauge de solicitudes en progreso

@app.route('/findNodesCluster', methods=['GET'])
def nodesCluster():
    global REQUEST_ID
    id = "1" + str(REQUEST_ID)
    REQUEST_ID += 1
    NODES_CLUSTER_REQUEST_COUNT.inc()
    REQUEST_IN_PROGRESS.inc()  # Incrementar gauge de solicitudes en progreso
    
    start_time = time.time()
    try:
        response = requests.get(f'http://{elb_dns}/read?method=findNodesCluster')
        response = response.json()
        response['data']['request_id'] = id
        sns.publish(TopicArn=sns_arn, Message=str(response["data"]).replace("'", '"'))
        while True:
            try:
                content = s3.get_object(Bucket=s3_bucket, Key=f'{id}.json')
                data = content['Body'].read().decode('utf-8')
                break
            except Exception as e:
                continue
        return data, 200
    except Exception as e:
        return {"message": str(e)}, 500
    finally:
        latency = time.time() - start_time
        REQUEST_LATENCY.observe(latency)  # Registrar latencia en histogram
        REQUEST_STATS.observe(latency)  # Registrar latencia en summary
        REQUEST_IN_PROGRESS.dec()  # Decrementar gauge de solicitudes en progreso

@app.route('/health', methods=['GET'])
def health_check():
    global REQUEST_ID
    id = "1" + str(REQUEST_ID)
    REQUEST_ID += 1
    return jsonify({"status": "healthy", "request_id": id}), 200

if __name__ == '__main__':
    start_http_server(8900) #metrics
    app.run(host='0.0.0.0', port=8080, debug=False) #factorial