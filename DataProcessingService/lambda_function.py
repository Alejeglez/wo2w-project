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
    destination_bucket = "s3-wo2w-results"
    
    try:
        processed_content = []

        print(message)

        if isinstance(message, str):
            # Si es una cadena, lo cargamos como JSON
            data = json.loads(message)
        else:
            # Si no es una cadena, asumimos que ya es un objeto JSON
            data = message

        G = nx.DiGraph()

        for path in data["paths"]:
            for segment in path:
                # Peso invertido
                inverted_weight = 1 / int(segment["averageOccurrences"])
                
                G.add_edge(
                    segment["from"], 
                    segment["to"], 
                    weight=inverted_weight,
                    averageOccurrences=int(segment["averageOccurrences"])
                )

        key = data['algorithm']    

        if "astart" in key:
            def heuristic(u, target):
                return abs(G.nodes[u].get('averageOccurrences', 0) - G.nodes[target].get('averageOccurrences', 0))
            shortest_path = nx.astar_path(G, source="pero", target="peras", heuristic=heuristic, weight="weight")
            shortest_path_length = nx.astar_path_length(G, source="pero", target="peras", heuristic=heuristic, weight="weight")
        elif "dijkstra" in key:
            shortest_path = nx.dijkstra_path(G, source="pero", target="peras", weight="weight")
            shortest_path_length = nx.dijkstra_path_length(G, source="pero", target="peras", weight="weight")
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

        # Escribimos el resultado en el bucket de destino
        cleaned_key = data['from'] + "_" + data['to']
        result_key = f'{cleaned_key}_result.json'
        s3.put_object(Bucket=destination_bucket, Key=result_key, Body=json.dumps(data).encode('utf-8'))

        return {
            'statusCode': 200,
            'body': json.dumps(f'Archivo {key} procesado exitosamente.')
        }
    
    except Exception as e:
        print(e)
        raise e
