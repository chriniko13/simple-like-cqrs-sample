### Simple Like Cqrs Sample

#### Description
A simple app which shows how a cqrs behaves.

* Write Side = Kafka
* Read Side = Heap or Elastic Search


#### Prerequisites - How to run app

* Setup local environment by executing: `docker-compose up` (clean up: `docker-compose down`)


* Create kafka topic posts with 20 partitions and replication factor 3
    * Command: 
        `./kafka-topics.sh --create --zookeeper localhost:2181 --topic posts --partitions 20 --replication-factor 3`


* Download Payara application server in order to deploy WAR
    * https://www.payara.fish/software/payara-server/
    
* Create WAR: `mvn clean install` (it will be created inside target folder with the name: `simple-like-cqrs-sample.war`)

* Deploy WAR where is inside target folder with the name: `simple-like-cqrs-sample.war` to Payara Application Server or any other JavaEE7 compliant server.

* Open web browser at: `http://localhost:8080/simple-like-cqrs-sample/`


#### Access Elastic Search
* Visit: `http://localhost:9200/`


#### Access Kibana
* Visit: `http://localhost:5601/app/kibana`


#### Storage Selection
* Use/choose in `PostManager.java` the annotation in ctor injection (line: 29)
    * `@Materialization(MaterializationType.ELASTIC_SEARCH)`
    * `@Materialization(MaterializationType.HEAP)`
    
    
#### Useful commands

* List kafka topics
    * `./kafka-topics.sh --list --zookeeper localhost:2181`

* Describe specified kafka topic (in this example we have 20 partitions but replication factor is 1)
    * `./kafka-topics.sh --describe --zookeeper localhost:2181 --topic posts`
        
        ```
        Topic:posts	PartitionCount:20	ReplicationFactor:1	Configs:
            Topic: posts	Partition: 0	Leader: 3	Replicas: 3	Isr: 3
            Topic: posts	Partition: 1	Leader: 1	Replicas: 1	Isr: 1
            Topic: posts	Partition: 2	Leader: 2	Replicas: 2	Isr: 2
            Topic: posts	Partition: 3	Leader: 3	Replicas: 3	Isr: 3
            Topic: posts	Partition: 4	Leader: 1	Replicas: 1	Isr: 1
            Topic: posts	Partition: 5	Leader: 2	Replicas: 2	Isr: 2
            Topic: posts	Partition: 6	Leader: 3	Replicas: 3	Isr: 3
            Topic: posts	Partition: 7	Leader: 1	Replicas: 1	Isr: 1
            Topic: posts	Partition: 8	Leader: 2	Replicas: 2	Isr: 2
            Topic: posts	Partition: 9	Leader: 3	Replicas: 3	Isr: 3
            Topic: posts	Partition: 10	Leader: 1	Replicas: 1	Isr: 1
            Topic: posts	Partition: 11	Leader: 2	Replicas: 2	Isr: 2
            Topic: posts	Partition: 12	Leader: 3	Replicas: 3	Isr: 3
            Topic: posts	Partition: 13	Leader: 1	Replicas: 1	Isr: 1
            Topic: posts	Partition: 14	Leader: 2	Replicas: 2	Isr: 2
            Topic: posts	Partition: 15	Leader: 3	Replicas: 3	Isr: 3
            Topic: posts	Partition: 16	Leader: 1	Replicas: 1	Isr: 1
            Topic: posts	Partition: 17	Leader: 2	Replicas: 2	Isr: 2
            Topic: posts	Partition: 18	Leader: 3	Replicas: 3	Isr: 3
            Topic: posts	Partition: 19	Leader: 1	Replicas: 1	Isr: 1
        ```
    
* Alter partitions of specified kafka topic
    * `./kafka-topics.sh --alter --zookeeper localhost:2181 --topic posts --partitions 20`

* To increase the number of replicas.
    1) Specify the extra replicas in a custom reassignment json file (For example, you could create increase-replication-factor.json and put this content in it)
     
     ```json5
    
    {"version":1,
          "partitions":[
             {"topic":"posts","partition":0,"replicas":[1,2,3]},
             {"topic":"posts","partition":1,"replicas":[1,2,3]},
             {"topic":"posts","partition":2,"replicas":[1,2,3]},
             {"topic":"posts","partition":3,"replicas":[1,2,3]},
             {"topic":"posts","partition":4,"replicas":[1,2,3]},
             {"topic":"posts","partition":5,"replicas":[1,2,3]},
             {"topic":"posts","partition":6,"replicas":[1,2,3]},
             {"topic":"posts","partition":7,"replicas":[1,2,3]},
             {"topic":"posts","partition":8,"replicas":[1,2,3]},
             {"topic":"posts","partition":9,"replicas":[1,2,3]},
             {"topic":"posts","partition":10,"replicas":[1,2,3]},
             {"topic":"posts","partition":11,"replicas":[1,2,3]},
             {"topic":"posts","partition":12,"replicas":[1,2,3]},
             {"topic":"posts","partition":13,"replicas":[1,2,3]},
             {"topic":"posts","partition":14,"replicas":[1,2,3]},
             {"topic":"posts","partition":15,"replicas":[1,2,3]},
             {"topic":"posts","partition":16,"replicas":[1,2,3]},
             {"topic":"posts","partition":17,"replicas":[1,2,3]},
             {"topic":"posts","partition":18,"replicas":[1,2,3]},
             {"topic":"posts","partition":19,"replicas":[1,2,3]}
        ]}
    
    ````
    
    2) Use the file with the --execute option of the kafka-reassign-partitions tool
    `./kafka-reassign-partitions --zookeeper localhost:2181 --reassignment-json-file increase-replication-factor.json --execute`

    3) Verify the replication factor with the kafka-topics tool
    `./kafka-topics.sh --describe --zookeeper localhost:2181 --topic posts`
    
    Output:
    ```
    Topic:posts	PartitionCount:20	ReplicationFactor:3	Configs:
    	Topic: posts	Partition: 0	Leader: 3	Replicas: 1,2,3	Isr: 3,2,1
    	Topic: posts	Partition: 1	Leader: 1	Replicas: 1,2,3	Isr: 1,2,3
    	Topic: posts	Partition: 2	Leader: 2	Replicas: 1,2,3	Isr: 2,1,3
    	Topic: posts	Partition: 3	Leader: 3	Replicas: 1,2,3	Isr: 3,1,2
    	Topic: posts	Partition: 4	Leader: 1	Replicas: 1,2,3	Isr: 1,2,3
    	Topic: posts	Partition: 5	Leader: 2	Replicas: 1,2,3	Isr: 2,1,3
    	Topic: posts	Partition: 6	Leader: 3	Replicas: 1,2,3	Isr: 3,1,2
    	Topic: posts	Partition: 7	Leader: 1	Replicas: 1,2,3	Isr: 1,2,3
    	Topic: posts	Partition: 8	Leader: 2	Replicas: 1,2,3	Isr: 2,1,3
    	Topic: posts	Partition: 9	Leader: 3	Replicas: 1,2,3	Isr: 3,1,2
    	Topic: posts	Partition: 10	Leader: 1	Replicas: 1,2,3	Isr: 1,2,3
    	Topic: posts	Partition: 11	Leader: 2	Replicas: 1,2,3	Isr: 2,1,3
    	Topic: posts	Partition: 12	Leader: 3	Replicas: 1,2,3	Isr: 3,1,2
    	Topic: posts	Partition: 13	Leader: 1	Replicas: 1,2,3	Isr: 1,2,3
    	Topic: posts	Partition: 14	Leader: 2	Replicas: 1,2,3	Isr: 2,1,3
    	Topic: posts	Partition: 15	Leader: 3	Replicas: 1,2,3	Isr: 3,1,2
    	Topic: posts	Partition: 16	Leader: 1	Replicas: 1,2,3	Isr: 1,2,3
    	Topic: posts	Partition: 17	Leader: 2	Replicas: 1,2,3	Isr: 2,1,3
    	Topic: posts	Partition: 18	Leader: 3	Replicas: 1,2,3	Isr: 3,1,2
    	Topic: posts	Partition: 19	Leader: 1	Replicas: 1,2,3	Isr: 1,2,3

    ```