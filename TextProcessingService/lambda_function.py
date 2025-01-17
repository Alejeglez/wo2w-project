import json
import urllib.parse
import boto3
import re
from collections import defaultdict
from botocore.exceptions import ClientError
import os

print('Loading function')

s3 = boto3.client('s3')


def lambda_handler(event, context):
    word_count = defaultdict(int)
    bucket = event['Records'][0]['s3']['bucket']['name']
    key = urllib.parse.unquote_plus(event['Records'][0]['s3']['object']['key'], encoding='utf-8')
    destination_bucket = 's3-wo2w-words-22'
    words_set = set()
    
    try:
        processed_content = []
        response = s3.get_object(Bucket=bucket, Key=key)
        content = response['Body'].read().decode('utf-8')
        lines = content.splitlines()
        
        # Procesar línea por línea
        for line in lines:
            processed_content.append(line)
            line = line.lower()
            # Eliminamos caracteres especiales
            line = re.sub(r'[^a-z\s]', ' ', line)
            words = line.split()

            for word in words:
                if 3 <= len(word) <= 5 and is_pronounceable(word):
                    word_count[word] += 1
        
        # Ahora escribimos los resultados en el bucket de destino
        for word, count in word_count.items():
            file_key = f'{word}.txt'  # Nombre del archivo para la palabra
            existing_count = 0  # Asumimos que la palabra no existe en el bucket
            words_set.add(word)
            
            # Intentamos leer el archivo si ya existe
            try:
                existing_object = s3.get_object(Bucket=destination_bucket, Key=file_key)
                existing_content = existing_object['Body'].read().decode('utf-8')
                existing_count = int(existing_content)  # Leer el conteo existente si ya existe
            except ClientError as e:
                if e.response['Error']['Code'] == 'NoSuchKey':
                    # Si el archivo no existe, seguimos como si el conteo fuera 0
                    a=0
                else:
                    raise  # Si hay otro tipo de error, lo levantamos
            
            # Actualizamos el conteo sumando el nuevo conteo al existente
            updated_count = existing_count + count
            s3.put_object(Bucket=destination_bucket, Key=file_key, Body=str(updated_count).encode('utf-8'))
            
        sns = boto3.client('sns')
        sns.publish(
            TopicArn=os.environ['SNS_TOPIC'],
            Message=json.dumps(",".join(words_set)),
            Subject='Palabras procesadas'
        )

        
        return {
            'statusCode': 200,
            'body': json.dumps(f'Archivo {key} procesado exitosamente.')
        }
    
    except Exception as e:
        print(f"Error al procesar el objeto {key} del bucket {bucket}. Error: {e}")
        raise e


def is_pronounceable(word):
    vowels = 'aeiou'
    if not any(vowel in word for vowel in vowels):
        return False
    return not re.match(r'(?i)[^aeiou]{3,}', word)
