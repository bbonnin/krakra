# krakra

> Like Kafka, but more Krakra !

> Just a very simple broker of messages with HTTP API.
> Main constraint: no use of external libraries nor frameworks (just classes from the JDK)
> Why ? Because !!!!!!

## TODO

### Basic items

* [X] Topic - what kind of message queue ? (based on use cases, performance) -> basic map/list
* [X] Topic - message status -> none
* [ ] Topic - time to live in queue
* [X] Broker - automatic creation of topic
* [ ] Broker - handler for creating topic
* [ ] Broker - handler for deleting topic
* [ ] Broker - persistence storage (only memory in v1)
* [ ] Broker - ack the messages when they are consumed by all the known consumers
* [X] Broker - API: what kind of formats for the message ? JSON only ? Ignore type of messages as they are not interpreted (byte array)
* [X] Message - fields (header [id, content-type, post date], body)
  * [X] In Krakra, protocol, HTTP headers contains the meta-data of the message, HTTP body contains the message (stored as it is in memory (byte array))
* [X] Consumer - General API (subscribe to topic, read message)
* [ ] Consumer - API: consume message by groups
* [ ] Consumer group ?
* [X] Producer - General API (send message, send async)
* [ ] Producer - API: send multiple messages
* [ ] UI (to get some stats about the broker, the messages, ...)

### Use cases

(To ease the development :))
* [X] A consumer subscribes to a topic: POST /api/v1/topic/consumer?id=<a unique ID>&topic=<topic name>
  * At the moment, the ID is mandatory
  * Topic name is an exact name, no a wildcard
  * Why we need consumer to subscribe ? We need to have a subscription to remove the messages when they have been read by all the consumers of a topic !
* [X] A producer posts a message
  * The topic is automatically created (should be configurable)
  * The message in the topic is stored and current consumers can consume it
  * The message has a expiration date (ttl can be configured)
  * The message has a limited size (1024 ko, to be configurable)
* [X] A subscribed consumer read a message in a topic
  * The message is flagged as read by this consumer (consumer is removed from list of consumers)
  * The id of the consumer is in the query