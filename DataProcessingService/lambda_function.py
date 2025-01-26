import json
import urllib.parse
import boto3
import re
from botocore.exceptions import ClientError
import networkx as nx

print('Loading function')

s3 = boto3.client('s3')


def lambda_handler(event, context):
    message = event['Records'][0]['Sns']['Message'] 
    destination_bucket = "s3-wo2w-data-processing"
    
    data = ""

    try:
        processed_content = []


        # Parsear el mensaje si es string
        
        if isinstance(message, str):
            data = json.loads(message)

        else:
            data = message

        object_key = data["request_id"]

        #comprobar si data tiene un campo paths
        if "paths" in data:
        
            G = nx.DiGraph()

            for path in data["paths"]:
                for segment in path:
                    # Peso invertido
                    inverted_weight = 1 / float(segment["averageOccurrences"])
                    
                    G.add_edge(
                        segment["from"], 
                        segment["to"], 
                        weight=inverted_weight,
                        averageOccurrences=float(segment["averageOccurrences"])
                    )

            key = data['algorithm']   

            source = data['from']
            target = data['to'] 

            if "astart" in key:
                def heuristic(u, target):
                    return abs(G.nodes[u].get('averageOccurrences', 0) - G.nodes[target].get('averageOccurrences', 0))
                shortest_path = nx.astar_path(G, source=source, target=target, heuristic=heuristic, weight="weight")
                shortest_path_length = nx.astar_path_length(G, source=source, target=target, heuristic=heuristic, weight="weight")
            elif "dijkstra" in key:
                shortest_path = nx.dijkstra_path(G, source=source, target=target, weight="weight")
                shortest_path_length = nx.dijkstra_path_length(G, source=source, target=target, weight="weight")
            else:
                raise ValueError("El archivo no contiene 'astart' o 'dijkstra' en su nombre.")
            
            total_average_occurrences = 0
            for i in range(len(shortest_path) - 1):
                u = shortest_path[i]
                v = shortest_path[i + 1]
                total_average_occurrences += G[u][v]['averageOccurrences']

            data['result'] = {
                'shortestPath': shortest_path,
                'shortestPathLength': shortest_path_length,
                'totalAverageOccurrences': total_average_occurrences
            }


        
        else:
            G = nx.Graph()

            for node in data["nodes"]:
                node_value = node["value"]
                connected_nodes = node["connectedNodes"]
                for connected in connected_nodes:
                    G.add_edge(node_value, connected)

            connected_components = list(nx.connected_components(G))

            subgraphs = [G.subgraph(component).copy() for component in connected_components]

            list_subgraphs = []

            for i, sg in enumerate(subgraphs):
                list_subgraph = []

                for node in sg.nodes():
                    list_subgraph.append(node)
                
                list_subgraphs.append(list_subgraph)

            dictionary_nodes = {}
            dictionary_nodes["subgraphs"] = list_subgraphs
            cleaned_key = "nodes_in_clusters"

            data = dictionary_nodes


        result_key = f'{object_key}.json'
        s3.put_object(Bucket=destination_bucket, Key=result_key, Body=json.dumps(data).encode('utf-8'))

        return {
            'statusCode': 200,
            'body': json.dumps(f'Archivo {object_key} procesado exitosamente.')
        }
    
    except Exception as e:
        print(e)
        raise e
