from flask import Flask, request, jsonify
import requests

app = Flask(__name__)

elb = "External-LB-reader-685281967.us-east-1.elb.amazonaws.com"
base_url = f"http://{elb}/read"


def fetch_response_from_api(method_name, base_url):

    params = {"method": method_name}

    try:
        response = requests.get(url=base_url, params=params)
        response.raise_for_status()
        return response.json(), response.status_code
    
    except requests.exceptions.RequestException as e:
        return {"error": str(e)}, 500

@app.route('/findTotalNodesCount', methods=['GET'])
def findTotalNodesCount():
    method = "findTotalNodesCount"
    return fetch_response_from_api(method, base_url)

@app.route("/findTotalConnectionsCount", methods=['GET'])
def findTotalConnectionsCount():
    method = "findTotalConnectionsCount"
    return fetch_response_from_api(method, base_url)

@app.route('/findAverageNodeDegreeCount', methods=['GET'])
def findAvergageNodeDegreeCount():
    method = "findAverageNodeDegreeCount"
    return fetch_response_from_api(method, base_url)

@app.route('/findIsolatedNodesCount', methods=['GET'])
def findIsolatedNodesCount():
    method = "findIsolatedNodesCount"
    return fetch_response_from_api(method, base_url)

@app.route('/findConnectedNodesCount', methods=['GET'])
def findConnectedNodesCount():
    method = "findConnectedNodesCount"
    return fetch_response_from_api(method, base_url)

@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({"status": "healthy"}), 200

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=8080, debug=True)