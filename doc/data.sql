PUT /device
{
  "mappings": {
    "properties": {
      "device_id":    { "type": "keyword" },
      "account_id":  { "type": "keyword"  },
      "alias":       { "type": "keyword"  },
      "bind_code":  { "type": "keyword"},
      "bind_region":   { "type": "keyword"  },
      "device_type":   { "type": "keyword"  },
      "device_model":  { "type": "keyword"},
      "hardware_version":   { "type": "keyword"  } ,
      "locale":   { "type": "keyword"  },
      "mac":  { "type": "keyword"},
      "oem_id":   { "type": "keyword"  } ,
      "region":   { "type": "keyword"  },
      "region_code":  { "type": "keyword"},
      "version":   { "type": "keyword"  }
    }
  }
}

POST /device/_search
{
  "query":{
    "match_all": {}
  }
}

POST /device/_doc
{
   "device_id" : "device_id_1"
}

POST /device/_refresh


POST device/_update_by_query
{
  "script":{
  "source": "ctx._source['account_id']=params['account_id'];ctx._source['device_type']=params['device_type'];ctx._source['mac']=params['mac']",
    "lang": "painless",
    "params" : {
        "account_id" : "1",
        "device_type": "deviceType1",
        "mac": "mac1"
    }
  },
  "query": {
    "match": {
      "device_id": "device_id_1"
    }
  }
}
