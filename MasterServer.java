import java.io.IOException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.KeeperException.Code;
import java.util.concurrent.CountDownLatch;


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

public class ZKCreate1{
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

   public static void main(String[] args) {

      // znode path
     int ip = "second".hashCode();
     ip = ip%255;
      String path = "/zk-demo/my-node/" + ip.toString(); // Assign path to znode
      String ip_addr = "127.0.0." + ip.toString();
      // data in byte array
      byte[] data = (ip_addr).getBytes(); // Declare data
      System.out.println("1 " + ip_addr);
		
      try {
         conn = new ZooKeeperConnection();
         zk = conn.connect("localhost");
         create(path, data); // Create the data to the specified path
         conn.close();
      } catch (Exception e) {
         System.out.println(e.getMessage()); //Catch error message
      }
   }
   ip = "secondR".hashCode();
   ip = ip%255;
    String path = "/zk-demo/my-node/" + ip.toString(); // Assign path to znode
     ip_addr = "127.0.0." + ip.toString();
      // data in byte array
      byte[] data = (ip_addr).getBytes(); // Declare data
      System.out.println("1R " + ip_addr);
		
      try {
         conn = new ZooKeeperConnection();
         zk = conn.connect("localhost");
         create(path, data); // Create the data to the specified path
         conn.close();
      } catch (Exception e) {
         System.out.println(e.getMessage()); //Catch error message
      }
   }
}




//org.apache.log4j.PropertyConfigurator.configure(/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/log4j.properties)
