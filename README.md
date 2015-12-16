# Github events info
Githubarchive provides information for every event registered.

This application focuses on extract part of this information and send it to *elasticsearch* so the user can elaborate some analysis.

Information selected:
- Event name
- Event creation timestamp
- Programming language

## Tested working environment
- A virtual machine with 3 nodes having OS *CentOS 6.5*, *HDFS*, *YARN* and *Spark*.
- *Elasticsearch* and *Kibana* running on localhost.

## How to run the application
- Copy to a HDFS folder the Githubarchive JSON files to be processed.
- Copy the fat-jar file containing the application to the VM. Step into the folder containing the .jar file.
- Run the application with the below command:
```
spark-submit --class github.GhEvent \
--master yarn-cluster \
--num-executors 3 \
--driver-memory 512m \
--executor-memory 512m \
--executor-cores 1 \
ing-gh-event-info-assembly-0.1.0.jar \
<path/to/json/dataset> <elasticsearchIP> <elasticsearchPort>
```
Adapt executors and memory as per your needs.

Provide the path for the JSON files stored in HDFS, as well as the IP and port for elasticsearch service running in your localhost.

## Check results
In Kibana you will find all the information regarding to the files provided.
- Elasticsearch index: *github*
- Elasticsearch type: *evinfo*
