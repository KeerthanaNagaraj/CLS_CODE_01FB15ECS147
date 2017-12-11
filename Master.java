import java.util.*;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.AsyncCallback.*;
import org.apache.zookeeper.AsyncCallback.*;
import org.apache.zookeeper.OpResult.ErrorResult;
import org.apache.zookeeper.client.ConnectStringParser;
import org.apache.zookeeper.client.HostProvider;
import org.apache.zookeeper.client.StaticHostProvider;
import org.apache.zookeeper.common.PathUtils;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.proto.*;
import org.apache.zookeeper.server.DataTree;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;
import org.apache.zookeeper.KeeperException.Code;
//import java.util.logging.Logger;
import org.apache.log4j.*;
//import org.apache.commons.logging;
//import org.apache.zookeeper.server;


public class Master implements org.apache.zookeeper.Watcher 
{
	ZooKeeper zk;
	String hostPort;
	Master(String hostPort) 
	{
		this.hostPort = hostPort;
	}
	void startZK() throws Exception 
	{
		zk = new ZooKeeper(hostPort, 15000, this);
	}
	public void process(WatchedEvent e)
	{
		System.out.println(e);
	}
	
	void stopZK() throws Exception 
	{
	 zk.close();
	}

	//String serverId = Integer.toHexString(random.nextInt());
	Random r=new Random();
	 String serverId = Integer.toString(r.nextInt());
	//static boolean isLeader = false;
	// returns true if there is a master
	boolean checkMaster()
	 {
		while (true) 
		{
			try 
			{
				Stat stat = new Stat();
				byte data[] = zk.getData("/master", false, stat);
				isLeader = new String(data).equals(serverId);
				return true;
			} 
			catch (Exception e) 
			{
			// no master, so try create again
			return false;
			}
			
		}
	}
	void runForMaster() throws InterruptedException //if there is no master then create
	{
		while (true) 
		{
			try 
			{
				zk.create("/master", serverId.getBytes(),Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
				isLeader = true;
				break;
			} 
			catch (Exception e)
			{
				isLeader = false;
				break;
			}
			finally{
				if (checkMaster()) break;}
	     }
	}
	
	
	static boolean isLeader;
	 DataCallback masterCheckCallback = new DataCallback() //check if master present after rerun
	{
		public void processResult(int rc, String path, Object ctx, byte[] data,Stat stat) 
		{
			switch(Code.get(rc)) 
			{
				case CONNECTIONLOSS:
				checkMaster();
				return;
				case NONODE:
				runForMaster();
				return;
			}
		}
	

	};
	 void checkMaster() 
	{
		zk.getData("/master", false, masterCheckCallback, null);
	}
	 StringCallback masterCreateCallback = new StringCallback() 
	{
		public void processResult(int rc, String path, Object ctx, String name) 
		{
			switch(Code.get(rc))
			 {
				case CONNECTIONLOSS:
				checkMaster();
			//	System.out.println("CONNECTIONLOSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSs");
				return;
				case OK:
			//	System.out.println("OKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");
				isLeader = true;
				break;
				default:
			//	System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
				isLeader = false;
			}
			System.out.println("I'm " + (isLeader ? "" : "not ") +"the leader");
		}
	};
	void runForMaster() //ephemeral nodes creation
	{
		zk.create("/master", serverId.getBytes(), Ids.OPEN_ACL_UNSAFE,
		CreateMode.EPHEMERAL, masterCreateCallback, null);
		isLeader=true;
	}
	public void bootstrap() {
		createParent("/workers", new byte[0]);
		createParent("/assign", new byte[0]);
		createParent("/tasks", new byte[0]);
		createParent("/status", new byte[0]);
		}
	void createParent(String path, byte[] data) {
			//System.out.println("inParentCreateeeeeeeeeeeeeeeeeeeeeeeee");
			zk.create(path,
			data,
			Ids.OPEN_ACL_UNSAFE,
			CreateMode.PERSISTENT,
			createParentCallback,
			data);
			
	}
	/*StringCallback createParentCallback = new StringCallback() {
		public void processResult(int rc, String path, Object ctx, String name) {
			//System.out.println("CPCccCCCccccccccccccccccccccccccccccccccccccccc");
			org.apache.log4j.Logger LOG = Logger.getLogger(Master.class.getName());
			switch (Code.get(rc)) {
				case CONNECTIONLOSS:
					createParent(path, (byte[]) ctx);
					break;
				case OK:
					LOG.info("Parent created");
					break;
				case NODEEXISTS:
					LOG.warn("Parent already registered: " + path);
					break;
				default:
					LOG.error("Something went wrong: ",KeeperException.create(Code.get(rc), path));
			}
}
	}; */
	public static void main(String args[])throws Exception 
	{
		Master m = new Master(args[0]);
		m.startZK();
		// wait for a bit
		m.runForMaster();
		m.checkMaster();
		//isLeader=true;
		if (isLeader) 
		{
			//System.out.println("I'm the leader");
		
			Thread.sleep(60000);
			
		} 
		else 
		{
			System.out.println("Someone else is the leader");
		}
		m.bootstrap();
		//m.stopZK();
		//Thread.sleep(60000);
	}
	//void create(String path,byte[] data,List<ACL> acl,CreateMode createMode,AsyncCallback.StringCallback cb,Object ctx)
} 
