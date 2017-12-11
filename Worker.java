import java.util.*;
import org.apache.zookeeper.AsyncCallback.*;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.*;
import org.slf4j.*;
public class Worker implements org.apache.zookeeper.Watcher  {
	Random random=new Random();
	private static final Logger LOG = LoggerFactory.getLogger(Worker.class);
	ZooKeeper zk;
	String hostPort;
	String serverId = Integer.toHexString(random.nextInt());
	Worker(String hostPort) {
	this.hostPort = hostPort;
	}
	String status;
	String name="worker-";
void startZK() throws Exception
	{
		zk = new ZooKeeper(hostPort, 15000, this);
	}
	//Random random=new Random();
	public void process(WatchedEvent e) 
	{
		LOG.info(e.toString() + ", " + hostPort);
	}
	void register() {
		zk.create("/workers/worker-" + serverId,
		"Idle".getBytes(),
		Ids.OPEN_ACL_UNSAFE,
		CreateMode.EPHEMERAL,
		createWorkerCallback, null);
	}
StringCallback createWorkerCallback = new StringCallback() {
	public void processResult(int rc, String path, Object ctx,
	String name) {
		switch (Code.get(rc)) {
		case CONNECTIONLOSS:
		register();
		break;
		case OK:
		LOG.info("Registered successfully: " + serverId);
		break;
		case NODEEXISTS:
		LOG.warn("Already registered: " + serverId);
		break;
		default:
		LOG.error("Something went wrong: "
		+ KeeperException.create(Code.get(rc), path));
		}
	}
};
StatCallback statusUpdateCallback = new StatCallback() {
	public void processResult(int rc, String path, Object ctx, Stat stat) 
	{
		switch(Code.get(rc))
		 {
			case CONNECTIONLOSS:
			updateStatus((String)ctx);
			return;
		}
	}
};
synchronized private void updateStatus(String status) 
{
	if (status == this.status) 
	{
		zk.setData("/workers/" + name, status.getBytes(), -1,
		statusUpdateCallback, status);
		
	}
}

public void setStatus(String status) 
{
	this.status = status;
	updateStatus(status);
}
public static void main(String args[]) throws Exception {
		Worker w = new Worker(args[0]);
		w.startZK();
		w.register();
		w.setStatus("OK");
		Thread.sleep(30000);
}
}
