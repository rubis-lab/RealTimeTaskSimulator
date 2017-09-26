package DAG;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
	
public class NodeGroup{
	private static AtomicInteger cnt = new AtomicInteger(-1);

	private int id;
	private ArrayList<Node> nodes;
	private NodeGroup next;

	public NodeGroup()
	{
		this.id = cnt.incrementAndGet();
		this.nodes = new ArrayList<Node>();
	}

	public NodeGroup(NodeGroup t, boolean is_all)
	{
		this.id = cnt.incrementAndGet();
		this.nodes = new ArrayList<Node>(t.getNodes());

		if(is_all)
		{
			NodeGroup n = t.getNext();
			if(n!=null)
			{
				NodeGroup temp = new NodeGroup(n, is_all);
				this.next = temp;
			}
		}
	}

	public int getId() {return id;}
	public ArrayList<Node> getNodes() {return nodes;}
	public NodeGroup getNext() {return next;}

	public void addNode(Node n)
	{
		this.nodes.add(n);
	}

	public void setNodes(ArrayList<Node> ns)
	{
		this.nodes = ns;
	}

	public void setNext(NodeGroup n)
	{
		this.next = n;
	}
}
