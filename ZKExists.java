import java.io.IOException;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;
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
public class ZKExists {
   private static ZooKeeper zk;
   private static ZooKeeperConnection conn;

   // Method to check existence of znode and its status, if znode is available.
   public static Stat znode_exists(String path) throws
      KeeperException,InterruptedException {
      return zk.exists(path, true);
   }

   public static void main(String[] args) throws InterruptedException,KeeperException {
      String path = "/zk-demo/my-node"; // Assign znode to the specified path
			
      try {
         conn = new ZooKeeperConnection();
         zk = conn.connect("localhost");
         Stat stat = znode_exists(path); // Stat checks the path of the znode
	
         if(stat != null) {
            System.out.println("MASTER NODE EXIST");
         } else {
            System.out.println("0");
         }
				
      } catch(Exception e) {
         System.out.println(e.getMessage()); // Catches error messages
      }
   }
}
