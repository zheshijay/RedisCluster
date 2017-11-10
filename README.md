# RedisCluster Setup

## 1. Install redis and create nodes

 ### 1.1 Cluster Structure

```
Master node A: 0 ~ 5460
Master node B: 5461 ~ 10922
Master node C: 10923 ~ 16383
```

 ### 1.2 Install redis
Admins-MBP:redis-4.0.2 jay$ tar xzf redis-4.0.2.tar.gz  
Admins-MBP:redis-4.0.2 jay$ cd redis-4.0.2 
Admins-MBP:redis-4.0.2 jay$ make  
Admins-MBP:redis-4.0.2 jay$ make install PREFIX=/usr/local/redis/redis-cluster/

in redis-cluster folder, modify bin folder to redis01, and copy redis.conf from original redis folder



Modify redis.conf in redis01:
 		daemonize yes #run as a daemon process
        port 7001 #assign port numbers 7001 ~ 7006
        cluster-enabled yes # enable cluster
        cluster-config-file nodes.conf
        cluster-node-timeout 15000
        appendonly yes

### 1.3 Create nodes
Create 6 copies and modify port number.
Copy src/redis-trib.rb from redis original folder

```
Admins-MBP:redis-cluster jay$ ls -ltr
total 296
-rwxr-xr-x@  1 jay  bidder  75776 Nov  3 11:31 redis-3.2.2.gem
-rwxr-xr-x   1 jay  bidder  60843 Nov  3 16:20 redis-trib.rb
-rwxrwxrwx   1 jay  bidder    292 Nov  3 16:29 start-all.sh
-rwxr-xr-x   1 jay  bidder     19 Nov  6 09:57 redis-process.sh
-rwxrwxrwx   1 jay  bidder    337 Nov  9 16:13 clear-all-dump.sh
drwxr-xr-x  11 jay  bidder    374 Nov  9 16:14 redis06
drwxr-xr-x  11 jay  bidder    374 Nov  9 16:14 redis05
drwxr-xr-x  11 jay  bidder    374 Nov  9 16:14 redis04
drwxr-xr-x  12 jay  bidder    408 Nov  9 16:16 redis01
drwxr-xr-x  12 jay  bidder    408 Nov  9 16:17 redis02
drwxr-xr-x  12 jay  bidder    408 Nov  9 16:17 redis03
```

## 2. Install Dependencies
### 2.1 install ruby:
Admins-MBP:redis-4.0.2 jay$ yum install ruby  
Admins-MBP:redis-4.0.2 jay$ yum install rubygems
 
### 2.2 install dependency redis-3.2.2.gem, it is needed when run redis-trib.rb.
https://rubygems.global.ssl.fastly.net/gems/redis-3.2.2.gem

Admins-MBP:redis-4.0.2 jay$ gem install redis-3.2.2.gem  


## 3. Start Nodes

### 3.1 Start all nodes
Admins-MBP:redis-cluster jay$ cat start-all.sh
cd redis01
./redis-server redis.conf
cd ..
cd redis02
./redis-server redis.conf
cd ..
cd redis03
./redis-server redis.conf
cd ..
cd redis04
./redis-server redis.conf
cd ..
cd redis05
./redis-server redis.conf
cd ..
cd redis06
./redis-server redis.conf
cd ..

3.2 Make sure all nodes started
```
Admins-MBP:redis-cluster jay$ ps auwx|grep redis
root             28075   0.1  0.0  2478236   1216   ??  Ss   Mon02PM   1:58.56 ./redis-server 127.0.0.1:7002 [cluster]
root             27667   0.1  0.0  2476184   1200   ??  Ss   Mon11AM   2:19.10 ./redis-server 127.0.0.1:7006 [cluster]
root             27665   0.0  0.0  2476184   1192   ??  Ss   Mon11AM   2:21.42 ./redis-server 127.0.0.1:7005 [cluster]
root             27663   0.0  0.0  2476184   1184   ??  Ss   Mon11AM   2:19.75 ./redis-server 127.0.0.1:7004 [cluster]
root             27661   0.0  0.0  2476184   1212   ??  Ss   Mon11AM   2:21.43 ./redis-server 127.0.0.1:7003 [cluster]
root             27657   0.0  0.0  2476184   1304   ??  Ss   Mon11AM   2:21.78 ./redis-server 
```


## 4. Create and Start Cluster

```
Admins-MBP:redis-cluster jay$ ./redis-trib.rb create --replicas 1 127.0.0.1:7001 127.0.0.1:7002 127.0.0.1:7003 127.0.0.1:7004 127.0.0.1:7005 127.0.0.1:7006 


Admins-MBP:redis-cluster jay$ sudo ./redis-trib.rb create --replicas 1 127.0.0.1:7001 127.0.0.1:7002 127.0.0.1:7003 127.0.0.1:7004 127.0.0.1:7005 127.0.0.1:7006
>>> Creating cluster
/usr/local/lib/ruby/gems/2.4.0/gems/redis-3.2.2/lib/redis/client.rb:441: warning: constant ::Fixnum is deprecated
>>> Performing hash slots allocation on 6 nodes...
Using 3 masters:
127.0.0.1:7001
127.0.0.1:7002
127.0.0.1:7003
Adding replica 127.0.0.1:7004 to 127.0.0.1:7001
Adding replica 127.0.0.1:7005 to 127.0.0.1:7002
Adding replica 127.0.0.1:7006 to 127.0.0.1:7003
M: 64f2e52640e3588e6e9ea3e9cfacacd937b4302a 127.0.0.1:7001
   slots:0-5460 (5461 slots) master
M: 2fe945bcfdba4e96628251e100fe9a5a73aca519 127.0.0.1:7002
   slots:5461-10922 (5462 slots) master
M: 55ee0c2d3299d60b06659b39f7ef408dcd88989d 127.0.0.1:7003
   slots:10923-16383 (5461 slots) master
S: bbd4de25cad21c57459078a705d3b90e2f59bb88 127.0.0.1:7004
   replicates 64f2e52640e3588e6e9ea3e9cfacacd937b4302a
S: 10e90225c478fc23bfd33a585869cb3cde3b4afd 127.0.0.1:7005
   replicates 2fe945bcfdba4e96628251e100fe9a5a73aca519
S: f9e6159b72821662701d17db9370c5247075beed 127.0.0.1:7006
   replicates 55ee0c2d3299d60b06659b39f7ef408dcd88989d
Can I set the above configuration? (type 'yes' to accept): yes
>>> Nodes configuration updated
>>> Assign a different config epoch to each node
>>> Sending CLUSTER MEET messages to join the cluster
Waiting for the cluster to join...
>>> Performing Cluster Check (using node 127.0.0.1:7001)
M: 64f2e52640e3588e6e9ea3e9cfacacd937b4302a 127.0.0.1:7001
   slots:0-5460 (5461 slots) master
   1 additional replica(s)
M: 55ee0c2d3299d60b06659b39f7ef408dcd88989d 127.0.0.1:7003
   slots:10923-16383 (5461 slots) master
   1 additional replica(s)
M: 2fe945bcfdba4e96628251e100fe9a5a73aca519 127.0.0.1:7002
   slots:5461-10922 (5462 slots) master
   1 additional replica(s)
S: 10e90225c478fc23bfd33a585869cb3cde3b4afd 127.0.0.1:7005
   slots: (0 slots) slave
   replicates 2fe945bcfdba4e96628251e100fe9a5a73aca519
S: f9e6159b72821662701d17db9370c5247075beed 127.0.0.1:7006
   slots: (0 slots) slave
   replicates 55ee0c2d3299d60b06659b39f7ef408dcd88989d
S: bbd4de25cad21c57459078a705d3b90e2f59bb88 127.0.0.1:7004
   slots: (0 slots) slave
   replicates 64f2e52640e3588e6e9ea3e9cfacacd937b4302a
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
```



5. Testing
```
case01: set value and get value from one node
case02: set value and get value from another node
case03: set value and bring down one node, test slave node   
```


```
=========== Useful command for debug ===========
pkill -9 redis  
ps auwx|grep redis
```


## JRedis Examples
```
		JedisCluster cluster = RedisClusterDemo.getRedisCluster();
		
		//remove old data
		cluster.del("setJerseyNumbers");
		cluster.del("listJerseyNumbers");
		
		System.out.println("-- Setting values");
		cluster.set("team", "warriors");
		cluster.set("player", "Stephen Curry");
		
		System.out.println("----- 1. Sets");
		cluster.sadd("setJerseyNumbers", "30");
		cluster.sadd("setJerseyNumbers", "11");
		cluster.sadd("setJerseyNumbers", "23");
		Set<String> setJerseyNumbers = cluster.smembers("setJerseyNumbers");
		
		
		System.out.println("----- 2. Lists");
		cluster.lpush("listJerseyNumbers", "30");
		cluster.lpush("listJerseyNumbers", "11");
		cluster.lpush("listJerseyNumbers", "23");
//		String listJerseyNumbers = cluster.rpop("listJerseyNumbers");
		List<String> allNumbers = cluster.lrange("listJerseyNumbers", 0, -1);
		
		System.out.println("----- 3. Sorted Sets");
  //To be Continued
```



