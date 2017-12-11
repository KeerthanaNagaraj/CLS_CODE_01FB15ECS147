import java.io.IOException;
import java.io.*;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.*;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.CreateMode;
//import org.apache.zookeeper.ZooDefs;
import java.lang.Math;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.KeeperException.Code;
import java.util.*;


class ZooKeeperConnection{

   // declare zookeeper instance to access ZooKeeper ensemble
   private ZooKeeper zoo;
   final CountDownLatch connectedSignal = new CountDownLatch(1);

   // Method to connect zookeeper ensemble.
   public ZooKeeper connect(String host) throws IOException,InterruptedException {
	
      zoo = new ZooKeeper(host,5000,new Watcher() {
		
         public void process(WatchedEvent we) {

            if (we.getState() == KeeperState.SyncConnected) {
               connectedSignal.countDown();
            }
         }
      });

//zoo=new ZooKeeper(host,5000,this);	
      connectedSignal.await();
      return zoo;
   }

   // Method to disconnect from zookeeper server
   public void close() throws InterruptedException {
      zoo.close();
   }
}

public class ZKCreate{
   // create static instance for zookeeper class.
   private static ZooKeeper zk;

   // create static instance for ZooKeeperConnection class.
   private static ZooKeeperConnection conn;

   // Method to create znode in zookeeper ensemble
   public static void create(String path, byte[] data) throws 
      KeeperException,InterruptedException {
      zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE,
      CreateMode.PERSISTENT);
   }

   public static void main(String[] args) throws IOException {

      // znode path
       String path = "/zk-demo/my-node"; // Assign path to znode
      File file = new File("META_ip.txt");
   	File file1 = new File("META.txt");
     
      
      // creates a FileWriter Object
      FileWriter writer = new FileWriter(file); 
     FileWriter writer1 = new FileWriter(file1);
     int ip = Math.abs("master".hashCode());
     ip = ip%255;
      String ip_addr = "127.0.0." + String.valueOf(ip);
      // data in byte array
      byte[] data = (ip_addr).getBytes(); // Declare data
		
      try {
         conn = new ZooKeeperConnection();
         zk = conn.connect("localhost");
         create(path, data); // Create the data to the specified path
         conn.close();
      } catch (Exception e) {
         System.out.println(e.getMessage()); //Catch error message
      }
       writer.write(ip_addr+"\n");
      writer1.write("0 " + ip_addr+"\n"); 
      writer.flush();
      writer.close();
    writer1.flush();
    writer1.close();
   }
  
}




//org.apache.log4j.PropertyConfigurator.configure(/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/log4j.properties)
