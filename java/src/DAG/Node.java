package DAG;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Node {
	private static AtomicInteger cnt = new AtomicInteger(-1);


	private int id;
	private ArrayList<Node> prev;
	private ArrayList<Node> next;

	// for utility
	public int depth;
	public boolean visit;

	public Node(int idnum) {
		id = idnum;
		//this.id = cnt.incrementAndGet();
		this.prev = new ArrayList<Node>();
		this.next = new ArrayList<Node>();
		this.depth = 0;
		this.visit = false;
	}

	public int getId() {return id;}
	public ArrayList<Node> getPrev() {return prev;}
	public ArrayList<Node> getNext() {return next;}

	public void addNext(Node node)
	{
		this.next.add(node);
	}

	public void addPrev(Node node)
	{
		this.prev.add(node);
	}
}
