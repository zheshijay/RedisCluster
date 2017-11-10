package com.jay.redis.cluster;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JRedisClient {
	private static String RESULT_OK = "OK";  
	private static Set<HostAndPort> clusterNodes = null;  
	private static JedisPoolConfig config = null;  
	private static JedisCluster jedisCluster=null;  

	private JRedisClient() {}    
	private static JRedisClient jredisClient=null;   

	//Thread Safe Singleton    
	public static synchronized  JRedisClient getInstance() {    
		if (jredisClient == null) {      
			jredisClient = new JRedisClient();    
			jredisClient.clusterInit();  
		} else{  
			System.out.println("JRedisClient already initialized");  
		}     
		return jredisClient;    
	}    
	
	/** 
	 * Get redis cluster 
	 * @return 
	 */  
	public JedisCluster getJedisCluster(){  
		if(jedisCluster==null){  
			JRedisClient.getInstance();  
		}  
		return jedisCluster;  
	}  

	
	/** 
	 * Add nodes
	 */  
	private void genClusterNode() {  
		//Master nodes
		clusterNodes = new HashSet<HostAndPort>();  
		clusterNodes.add(new HostAndPort("localhost", 7001));
		clusterNodes.add(new HostAndPort("localhost", 7002));  
		clusterNodes.add(new HostAndPort("localhost", 7003));  
		
		//Slave nodes
		clusterNodes.add(new HostAndPort("localhost", 7004));  
		clusterNodes.add(new HostAndPort("localhost", 7005));  
		clusterNodes.add(new HostAndPort("localhost", 7006)); 
	}  

	/** 
	 * Connection pool settings
	 */  
	private void genJedisConfig() {  
		config = new JedisPoolConfig();  
		//Max threads
		config.setMaxTotal(100000);  
		
		//Max idel threads  
		config.setMaxIdle(100);  
		
		//Max connection wait time when borrow (get jedis instance), throw JedisConnectionException if timeout
		config.setMaxWaitMillis(180);  
		
		//validate connection before borrow 
		config.setTestOnBorrow(true);  
	}  

	/** 
	 * JRedis cluster init
	 */  
	private void clusterInit() {  
		if(jedisCluster == null){  
			//init nodes
			genClusterNode();  
			
			//init connection pool
			genJedisConfig();  

			jedisCluster = new JedisCluster(clusterNodes, 5000, config);  
		}  
	}  

	public boolean set(String key, String value)  
	{  
		return this.set(key,0,value);  
	}  

	public boolean set(String key, int seconds, String value)  
	{  
		boolean isOk = false;  
		String returnResult = "";  
		if(seconds>0){  
			returnResult= jedisCluster.setex(key, seconds, value);  
		}else{  
			returnResult = jedisCluster.set(key, value);  
		}  
		if(RESULT_OK.equals(returnResult)){  
			isOk = true;  
		}  
		return isOk;  
	}  

	public String get(String key) {  
		return jedisCluster.get(key);  
	}  

	public boolean hmset(String key, Map<String,String> hashMap){  
		return this.hmset(key, 0, hashMap);  
	}  

	public boolean hmset(String key,int seconds, Map<String,String> hashMap){  
		boolean isOk = false;  
		String returnResult = "";  
		returnResult = jedisCluster.hmset(key, hashMap);  
		if(seconds>0){  
			this.expire(key, seconds);  
		}  
		if(RESULT_OK.equals(returnResult)){  
			isOk = true;  
		}  
		return isOk;  
	}  
  
	public String  hget(String key,String field){  
		return jedisCluster.hget(key, field);  
	}  

	public Map  hget(String key){  
		return jedisCluster.hgetAll(key);  
	}  
 
	public long incr(String key){  

		return jedisCluster.incr(key);  
	}  

	public long decr(String key){  
		return  jedisCluster.decr(key);  
	}  
	
	public long expire(String key,int seconds){  
		return jedisCluster.expire(key, seconds);  
	}  

	public long del(String key){  
		return jedisCluster.del(key);  
	}  

	public long list(String key,List<String> list){  
		return this.list(key, 0, list);  
	}  

	public long list(String key,int seconds,List<String> list){  
		long returnResult = 0;  
		String[] strArray = null;  
		if(list!=null&&!list.isEmpty()){  
			strArray = new String[list.size()];  
			for(int i=0;i<list.size();i++){  
				strArray[i]=list.get(i).toString();  
			}  
		}  
		returnResult = jedisCluster.lpush(key,strArray);  
		if(seconds>0){  
			this.expire(key, seconds);  
		}  
		return returnResult;  
	}  

	public List<String> lrange(String key,long start,long end){  
		return jedisCluster.lrange(key, start, end);  
	}  

	public TreeSet<String> keys(String pattern){    
		TreeSet<String> keys = new TreeSet<String>();    
		Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();    
		for(String k : clusterNodes.keySet()){    
			JedisPool jp = clusterNodes.get(k);    
			Jedis connection = jp.getResource();    
			try {    
				keys.addAll(connection.keys(pattern));    
			} catch(Exception e){    
				e.printStackTrace();  
			} finally{    
				connection.close(); 
			}    
		}    
		return keys;    
	}
	
	/** 
	 * @param args 
	 */  
	public static void main(String[] args) {  
		/*//JRedisClient jRedisClient =new JRedisClient("192.168.113.134",7006); 
	        System.out.println("set===>"+jRedisClient.set("key3", "key3333333333333333333")); 
	        System.out.println("get===>"+jRedisClient.get("key3"));*/  

		JRedisClient jredisClient =JRedisClient.getInstance();  
		jredisClient =JRedisClient.getInstance();  
		/*System.out.println(jredisClient.set("FP_100000","FP_100000000000000")); 
	        System.out.println(jredisClient.get("FP_100000")); 
	        List<String> list = new ArrayList<String>(); 
	        list.add("LIST1"); 
	        list.add("LIST2"); 
	        list.add("LIST3"); 
	        jredisClient.list("LIST", 3600, list);*/  

		/*  System.out.println("===>"+jredisClient.lrange("LIST", 0, -1)); 
	        System.out.println("===>"+jredisClient.lrange("LIST", 0, 1)); 
	        List<String> listGet = jredisClient.lrange("LIST",0, 2); 
	        for(int i=0;i<listGet.size();i++){ 
	            System.out.println("===>"+listGet.get(i).toString()); 
	        } 
		 */  
		for(int i=1;i<=10;i++){  
			jredisClient.set("kEY_"+i,1000,i+"");  
		}  

		for(int i=1;i<=10;i++){  
			System.out.println(i+"==>"+jredisClient.get("kEY_"+i));  
		}  

		/*for(int i=1;i<=10000;i++){ 
	            System.out.println(i+"==>"+jredisClient.del("kEY_"+i)); 
	        }*/  

		/*TreeSet<String> keys = jredisClient.keys("kEY_*");  
	        if(keys.size()>0){   
	           System.out.println("SIZE:"+keys.size());   
	           for(String str:keys){ 
	               System.out.println("==============>"+str);    
	           } 
	        } else{ 
	            System.out.println("No keys");   
	        }*/  


	}
	
	
}
