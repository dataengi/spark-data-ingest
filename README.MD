# Data import 

Spark application for ingest data from data providers into data lake.  
Application take data as csv files stored at hdfs or s3, transform it and put into hive tables. 
Could be run in two different modes:
- as periodical Oozie job for batch processing all available files in directory
- as permanent streaming service controlled via kafka tasks
  

## Batch processing mode

Run as spark application on cluster and process all files in hdfs directory.

### run 

Main class `com.ImportBatch`

Run spark job specifying directory with files to process. File names should be prepared according to pattern

````
<DataProvider>#<Country>#<DataProfile>#<TimeStamp>#<FileName>.csv
````


## Streaming service mode

Run as spark-streaming application and listening kafka topic for tasks.

### run 

Main class `com.ImportStreaming`

Run spark job with parameters: 

```
--packages org.apache.spark:spark-streaming-kafka-0-10_2.11:2.2.0"
```


### kafka console producer and consumer

Kafka producer:

```
cd /usr/hdp/current/kafka-broker/bin &&
./kafka-console-producer.sh --broker-list cluster-name.com:6667 --topic data-import-in
```

Kafka consumer:

```
cd /usr/hdp/current/kafka-broker/bin &&
./kafka-console-consumer.sh --bootstrap-server cluster-name.com:6667 --topic data-import-out --from-beginning

``` 

### Json Task

Task description should be put in kafka topic

```json
{
    "jobID": 4,
    "timeStamp": 1531831890,
    "owner": "user",
    "link": "s3a://files/providername#UA#UA_files#2018-06-25#data.csv",
    "origin": {
    	"provider": "providername",
    	"profile": "UA_files",
    	"country":	"UA",
    	"date":	1529042345,
    	"source":	"/providername/UA_files/2018-06-25/data.csv"
    },
    "remap":{
    	"columns":"data->0,firstname->4,lastname->5,ip->3,url->7,date->1",
    	"other":"6->lang"
    }
}
``` 

## AWS S3 support

To work with S3 as files storage, include path for appropriate CredentialProvider in spark-submit call 

```
--conf spark.hadoop.hadoop.security.credential.provider.path=jceks://hdfs@hadoop/user/conf/s3.jceks
```

Path to S3 files should looks like `s3a://bucket-name/file-path/file-name.csv`  


Have fun! Team [@DataEngi](https://github.com/dataengi)