PUT /device_user
{
  "mappings": {
    "properties": {
      "device_id":    { "type": "keyword" },
      "owner_id":  { "type": "keyword"  },
      "alias":       { "type": "keyword"  },
      "device_type":   { "type": "keyword"  },
      "device_name":   { "type": "keyword"  } ,
      "device_user_id":   { "type": "keyword" }
    }
  }
}