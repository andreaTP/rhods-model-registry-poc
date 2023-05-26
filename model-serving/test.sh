
# kubectl port-forward --address 0.0.0.0 service/modelmesh-serving 8008 -n modelmesh-serving

curl -v -H "Host: modelmesh-serving.modelmesh-serving:8033" http://localhost:8008/v2/models/basic/infer -d \
  '{"model_name": "basic", "inputs": [{"name": "predict", "shape": [1, 2], "datatype": "FP32", "data": [[2.0, 1.0]]}]}'


curl 'http://localhost:8008/v2/models/simple-latest/infer' \
  --data-raw '{"model_name":"basic","inputs":[{"name":"predict","shape":[1,2],"datatype":"FP32","data":[[0, 0]]}]}'
