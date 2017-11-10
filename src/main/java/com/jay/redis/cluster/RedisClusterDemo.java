package com.jay.redis.cluster;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;


/**
* Warriors Redis Cluster Demo
* 
* 
* <P>Redis and Redis Cluster DEMO
* 
*  
*  
* @author Jay Shi
* @version 1.0
*/
public class RedisClusterDemo {

	/** 
	 * Connection pool settings
	 */  
	private static JedisPoolConfig getJedisConfig() {  
		JedisPoolConfig config = new JedisPoolConfig();  
		//Max threads
		config.setMaxTotal(100000);  

		//Max idel threads  
		config.setMaxIdle(100);  

		//Max connection wait time when borrow (get jedis instance), throw JedisConnectionException if timeout
		config.setMaxWaitMillis(180);  

		//validate connection before borrow 
		config.setTestOnBorrow(true); 

		return config;
	}  

	//Redis
	public static JedisCluster getRedisCluster() {
		JedisCluster redisCluster = null;
		Set<HostAndPort> clusterNodes = new HashSet<HostAndPort>();
		
		//3 Master nodes, 3 slave nodes
		clusterNodes.add(new HostAndPort("127.0.0.1", 7001));
		clusterNodes.add(new HostAndPort("127.0.0.1", 7002));
		clusterNodes.add(new HostAndPort("127.0.0.1", 7003));
		clusterNodes.add(new HostAndPort("127.0.0.1", 7004));
		clusterNodes.add(new HostAndPort("127.0.0.1", 7005));
		clusterNodes.add(new HostAndPort("127.0.0.1", 7006));
		
		JedisPoolConfig config = getJedisConfig();
		redisCluster = new JedisCluster(clusterNodes, 5000, config);
		return redisCluster;
	}

	public static void main(String[] args) {

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
//		Map<String, Double> scores = new HashMap<>();
//		scores.put("PlayerOne", 3000.0);
//		scores.put("PlayerTwo", 1500.0);
//		scores.put("PlayerThree", 8200.0);
//		 
//		scores.keySet().forEach(playerKey -> {
//			cluster.zadd("ranking", scores.get(playerKey), playerKey);
//		});
//		         
//		String player = jedis.zrevrange("ranking", 0, 1).iterator().next();
//		long rank = jedis.zrevrank("ranking", "PlayerOne");
//		
//		cluster.lpush("playerCurry", "40");
//		cluster.lpush("playerDurant", "41");
//		cluster.lpush("playerGreen", "42");
////		String listJerseyNumbers = cluster.rpop("listJerseyNumbers");
//		List<String> allNumbers = cluster.lrange("listJerseyNumbers", 0, -1);
		
		System.out.println("-- getting values");
		System.out.println("player: " + cluster.get("player"));
		System.out.println("team: " + cluster.get("team"));
		System.out.println("1. Sets - jerseyNumbers: " + setJerseyNumbers );
//		System.out.println("2. Lists - jeryseyNum: " + listJerseyNumbers );
		System.out.println("3. Lists - allNumbers: " + allNumbers );
	}

}
