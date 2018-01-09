package DAG;

import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.Collections;
import java.util.Comparator;

public class Dag{
	public static HashMap<Integer, Node> all_node = new HashMap<Integer, Node>();

	public static Node random_generate(int n_node) //{{{ random_generate
	{
		//CHANGED NODE(0)
		Node head = new Node(0);
		all_node.put(0, head);

		HashMap<Integer, Node> p_list = new HashMap<Integer, Node>();
		//next_generate(head, n_node-1, 1, p_list);

		all_node.clear();
		return head;
	}	

	/*public static int next_generate(Node n, int n_total, int n_idx, HashMap<Integer, Node> p_list)
	{
		Node cur_node = n;

		if (n_idx > n_total) // No more Next
			return n_idx;
		System.out.println(cur_node.getId() + "Nodes n_idx idx : " + n_idx);

		p_list.put(cur_node.getId(), cur_node);

		int max = n_total-n_idx>4? 4 : n_total-n_idx; // Prev can have 4 childrun maximum
		int num_next = ThreadLocalRandom.current().nextInt(0, max+1); // # of Next for this node
		for(int i=0; i < num_next; ++i)
		{
			Node next = new Node();
			all_node.put(n_idx, next);

			cur_node.addNext(next);
			next.addPrev(cur_node);

			++n_idx;
		}

		int final_idx = n_idx;
		for(int i=0; i < num_next; ++i) // Generate Next of Next
		{
			Node Next_node = cur_node.getNext().get(i);
			final_idx = next_generate(Next_node, n_total, final_idx, p_list);
			System.out.println(cur_node.getId() + "Nodes final idx : " + final_idx);
		}

		int chance = ThreadLocalRandom.current().nextInt(0, 10); // Get Existed Node as Next 30%
		if (chance < 3)
		{
			int temp_Next = ThreadLocalRandom.current().nextInt(1, n_total+1);		
			if (!p_list.containsKey(temp_Next) && all_node.containsKey(temp_Next))
			{
				System.out.println(cur_node.getId() + "st Node Get " + temp_Next + "st Node as Next");
				Node new_Next = all_node.get(temp_Next);
				cur_node.addNext(new_Next);
				new_Next.addPrev(cur_node);
			}
		}

		return final_idx;	
	} */
	//}}}

	public static Node random_generate2(int n_node, int n_edge)	//{{{ random_generate2
	{
		// CHANGED NODE(0)
		Node head = new Node(0);
		ArrayList<Node> all_nodes = new ArrayList<Node>(n_node);
		all_nodes.add(head);

		for(int i=1; i < n_node; ++i) // make nodes
		{
			// CHANGED Node(i)
			Node node = new Node(i);
			all_nodes.add(node);
		}

		//// make edges
		HashMap<Integer,ArrayList<Integer>> p_list = new HashMap<Integer,ArrayList<Integer>>(n_node);
		for(int i=0; i<n_node; ++i) p_list.put(i, new ArrayList<Integer>());
		for(int i=0; i<n_node; ++i) p_list.get(i).add(i); // add themselves as prev to prevent loop

		for(int i=0; i<n_edge; ++i)
		{
			//randomization introduced to determine a random DAG structure
			int temp_prev = ThreadLocalRandom.current().nextInt(1, n_node);
		
			int cnt = 0;
			do
			{
				int temp_next = ThreadLocalRandom.current().nextInt(1, n_node);
				Node prev = all_nodes.get(temp_prev);
				Node next = all_nodes.get(temp_next);
	
				//// Check Loop
				if(temp_prev == temp_next) {/*System.out.println(temp_prev + "/" + temp_next + " : prev and next are same");*/ ++cnt; continue;}
				
				//prev and next shouldnt be next to each other because that means we already have an edge there
				if(prev.getNext().contains(next)) {/*System.out.println(temp_prev + " already has " + temp_next + " as Next");*/ ++cnt; continue;}
				if(prev.getPrev().contains(next)) {/*System.out.println(temp_prev + " already has " + temp_next + " as Prev");*/ ++cnt; continue;}
				
				//prevent loop since it must remain acyclic
				if(p_list.get(temp_prev).contains(temp_next)) {/*System.out.println(temp_prev + "/" + temp_next + " : don't make a loop");*/ ++cnt; continue;}
				//System.out.println(temp_prev + "/" + temp_next + " : Edge is established");

				prev.addNext(next);
				next.addPrev(prev);
			
				//// update prev list
				p_list.get(temp_next).add(temp_prev);
				p_list = updatePrev(p_list.get(temp_prev), next, p_list);
	
				break;
			}while(cnt<3);
		}

		//// make head
		for(int i=1; i < n_node; ++i)
		{
			Node cur_node = all_nodes.get(i);
			if(cur_node.getPrev().size() == 0)
			{
				head.addNext(cur_node);
				cur_node.addPrev(head);
			}
		}

		return head;
	}

	public static HashMap<Integer,ArrayList<Integer>> updatePrev(ArrayList<Integer> prevs, Node next, HashMap<Integer,ArrayList<Integer>> p_list)
	{
		int cur_id = next.getId();

		///// Update prevs of cur node;
		for(int i=0; i<prevs.size(); ++i)
		{
			int p_idx = prevs.get(i);
			p_list.get(cur_id).add(p_idx);
			//System.out.println(cur_id + " has " + p_idx + " as Prev");
		}
				

		///// Update childrun
		for(int i=0; i<next.getNext().size(); ++i)
		{
			p_list = updatePrev(prevs, next.getNext().get(i), p_list);
		}

		return p_list;
	}
	//}}}
	
	public static void checkDepth(Node node, int d) //{{{
	{
		if(node.depth < d)
			node.depth = d;

		for(int i=0; i < node.getNext().size(); ++i)
			checkDepth(node.getNext().get(i), d+1);
	}
	//}}}
	
/// make Node Group with depth	
	public static NodeGroup makeGroup(Node node) //{{{
	{
		ArrayList<NodeGroup> groups = new ArrayList<NodeGroup>();
		ArrayList<ArrayList<Node>> depth_Node;
		depth_Node = sumupDepth(node);

		for(int i=0; i<depth_Node.size(); ++i)
		{
			NodeGroup group = new NodeGroup();
			group.setNodes(depth_Node.get(i));
			groups.add(group);
		}
		
		for(int i=0; i<groups.size()-1; ++i)
			groups.get(i).setNext(groups.get(i+1));

		return groups.get(0);
	}

	public static ArrayList<ArrayList<Node>> sumupDepth(Node head)
	{
		Stack<Node> st = new Stack<Node>();
		HashSet<Integer> visit = new HashSet<Integer>();
		ArrayList<ArrayList<Node>> depth_Node = new ArrayList<ArrayList<Node>>();
		
		st.push(head);
		
		while(!st.empty())
		{
			Node cur_node = st.pop();
			int cur_depth = cur_node.depth;
			if(visit.contains(cur_node.getId()))
				continue;

			for(int i=0; i<cur_node.getNext().size(); ++i)
				st.push(cur_node.getNext().get(i));

			if(cur_depth > depth_Node.size())
				continue;
			else if(cur_depth == depth_Node.size())
			{
				ArrayList<Node> nodes = new ArrayList<Node>();
				nodes.add(cur_node);
				depth_Node.add(nodes);
			}
			else
				depth_Node.get(cur_depth).add(cur_node);
			
			visit.add(cur_node.getId());
		}

		return depth_Node;
	}
	//}}}


	public static ArrayList<NodeGroup> getAllCase(NodeGroup group) //{{{
	{
		ArrayList<NodeGroup> all = new ArrayList<NodeGroup>();
		
		NodeGroup cur_group = group;

		while(cur_group != null)
		{
			all = Multi_List(all, getGroups(cur_group));
			cur_group = cur_group.getNext();
		}

		return all;
	}

	public static ArrayList<NodeGroup> getGroups(NodeGroup n)
	{
		ArrayList<NodeGroup> temp = new ArrayList<NodeGroup>();
		ArrayList<Node> nodes = n.getNodes();

		//// Combination
		for(int i=1; i<nodes.size()+1; ++i)
		{
			Stack<Node> st = new Stack<Node>();
			ArrayList<NodeGroup> comb = getComb(n, nodes.size(), i, 0, st);
			temp.addAll(comb);
		}

		return temp;
	}
	
	public static ArrayList<NodeGroup> getComb(NodeGroup node, int n, int r, int idx, Stack<Node> st)
	{
		ArrayList<NodeGroup> temp = new ArrayList<NodeGroup>();

		if(r==0 || n==r)
		{
			////// make combination
			NodeGroup t = new NodeGroup();
			ArrayList<Node> t_nodes = new ArrayList<Node>();

			if(r==0)
			{
				for(int i=0; i<st.size(); ++i)
					t_nodes.add(st.get(i));
			}
			else
			{
				for(int i=0; i<r; ++i)
					t_nodes.add(node.getNodes().get(idx+i));
			}
			
			t.setNodes(t_nodes);
			temp.add(t);

			// recursive
			NodeGroup t2 = new NodeGroup(node, false);
			t2.getNodes().removeAll(t_nodes);
			temp = Multi_List(temp,	getGroups(t2));
		}
		else
		{
			st.push(node.getNodes().get(idx));
			temp.addAll(getComb(node, n-1, r-1, idx+1, st));

			st.pop();
			temp.addAll(getComb(node, n-1, r, idx+1, st));
		}
		return temp;
	}
	//}}}


	public static ArrayList<NodeGroup> Multi_List(ArrayList<NodeGroup> m, ArrayList<NodeGroup> n) //{{{
	{
		if(m.size()==0)
			return n;
		else if(n.size()==0)
			return m;

		ArrayList<NodeGroup> temp = new ArrayList<NodeGroup>();

		for(int i=0; i < m.size(); ++i)
		{
			for(int j=0; j < n.size(); ++j)
			{
				NodeGroup x = new NodeGroup(m.get(i), true);
				NodeGroup last = getLastGroup(x);

				NodeGroup y = new NodeGroup(n.get(j), true);
				last.setNext(y);
				temp.add(x);
			}
		}
		m = null;
		n = null;

		return temp;
	}

	public static NodeGroup getLastGroup(NodeGroup head)
	{
		while(head.getNext()!=null)
		{
			head = head.getNext();
		}
		return head;
	}
//}}}

	public static void removeDuplicate(ArrayList<NodeGroup> l)//{{{
	{
		HashMap<Integer, ArrayList<ArrayList<ArrayList<Integer>>>> dupli = new HashMap<Integer, ArrayList<ArrayList<ArrayList<Integer>>>>();
		ArrayList<Boolean> reserve = new ArrayList<Boolean>(l.size());
		boolean is_first = true;

		for(int i=0; i<l.size(); ++i)
		{
			int key = 1;
			NodeGroup cur = l.get(i);
			NodeGroup test = null;
			while(cur != null)
			{
				if(cur.getNodes().size()>1)
				{
					//// Sort
					Collections.sort(cur.getNodes(), new Comparator<Node>(){
							public int compare(Node n1, Node n2) { return (n1.getId() < n2.getId())? -1 : ((n1.getId() > n2.getId())? 1:0);}});

					key += cur.getNodes().size();

					NodeGroup t = new NodeGroup(cur, false);
					if(test == null)
						test = t;
					else
						test.setNext(t);
				}

				cur = cur.getNext();
			}
			
			if(key > 1)
			{
				ArrayList<ArrayList<ArrayList<Integer>>> target = dupli.get(key);
				
				if(target == null)
				{
					target = new ArrayList<ArrayList<ArrayList<Integer>>>();
					dupli.put(key, target);
				}
				boolean is_reserve = check_Dupli(test, target);
				reserve.add(is_reserve);
			}
			else
			{
				reserve.add(is_first);
				is_first = false;
			}
		}

		for(int i=l.size()-1; i>=0; --i)
		{
			if(reserve.get(i)==false)
				l.remove(i);
		}
	}

	public static boolean check_Dupli(NodeGroup n, ArrayList<ArrayList<ArrayList<Integer>>> list)
	{
		ArrayList<ArrayList<Integer>> group = new ArrayList<ArrayList<Integer>>();
		NodeGroup cur = n;
		while(cur!=null)
		{
			ArrayList<Integer> gr = new ArrayList<Integer>();
			for(int i=0; i<cur.getNodes().size(); ++i)
				gr.add(cur.getNodes().get(i).getId());
	
			group.add(gr); /// insert sort
			cur = cur.getNext();
		}

		Collections.sort(group, new Comparator<ArrayList<Integer>>() {
				public int compare(ArrayList<Integer> a, ArrayList<Integer> b) { return a.get(0).compareTo(b.get(0)); }});

		for(int i=0; i<list.size(); ++i)
		{
			ArrayList<ArrayList<Integer>> t = list.get(i);
			if(group.size() != t.size())
				continue;

			if(t.equals(group))
				return false;
		}

		list.add(group);

		return true;
	}
//}}}

	public static ArrayList<NodeGroup> getAllCase2(Node graph) //{{{
	{
		ArrayList<Node> prev = new ArrayList<Node>();

		NodeGroup head = new NodeGroup();
		head.addNode(graph);

		ArrayList<NodeGroup> all = getGroup2(head, prev);

		//System.out.println("All Size : " + all.size());

		/*
		for(int i=0; i<all.size(); ++i)
		{
			System.out.println(i + "st NodeGroup");
			Dag.DrawNodeGroup(all.get(i));
			System.out.println();
		}
		System.out.println("\n\n\n\n\n");
		*/

		removeDuplicate(all);

		// The number of actual groupings - after duplicate grouping patterns removed.
		//System.out.println("After Size : " + all.size());

		return all;
	}

	public static ArrayList<NodeGroup> getGroup2(NodeGroup cur, ArrayList<Node> prev)
	{
		ArrayList<NodeGroup> all = new ArrayList<NodeGroup>();
		all.add(cur);
		
//		System.out.println("\n\ngetGroup2 NodeGroup id : " + cur.getId());
		for(int i=0; i<cur.getNodes().size(); ++i)
		{
//			System.out.print(cur.getNodes().get(i).getId() + ", ");
			prev.add(cur.getNodes().get(i));
		}
//		System.out.println("prev size : " + prev.size());

		Stack<NodeGroup> avail = getNextGroups2(prev);
/*
		System.out.println("---Avail List---");
		for(int i=0; i<avail.size(); ++i)
		{
			for(int j=0; j<avail.get(i).getNodes().size(); ++j)
				System.out.print(avail.get(i).getNodes().get(j).getId() + ", ");
			System.out.println();
		}
*/
		ArrayList<NodeGroup> temp = new ArrayList<NodeGroup>();
		while(!avail.empty())
		{
			NodeGroup t = avail.pop();
			temp.addAll(getGroup2(t, prev));
		}

		all = Multi_List(all, temp);

		for(int i=0; i<cur.getNodes().size(); ++i)
			prev.remove(prev.size()-1);

		return all;
	}

	// find available nodes and make combination
	public static Stack<NodeGroup> getNextGroups2(ArrayList<Node> prev)
	{
		Stack<NodeGroup> all = new Stack<NodeGroup>();
		HashSet<Integer> visit = new HashSet<Integer>();
		ArrayList<Node> avail = new ArrayList<Node>();

		for(int i=0; i<prev.size(); ++i)
			visit.add(prev.get(i).getId());		
			
		for(int i=0; i<prev.size(); ++i)
		{
			Node cur = prev.get(i);
			for(int j=0; j<cur.getNext().size(); ++j)
			{
				Node next = cur.getNext().get(j);

				if(visit.contains(next.getId())) // already in group
					continue;
				
				boolean is_possible = true;
				for(int k=0; k<next.getPrev().size(); ++k) // not yet possible
				{
					is_possible = visit.contains(next.getPrev().get(k).getId());
						if(!is_possible)
							break;
				}
				
				if(is_possible && !avail.contains(next))
					avail.add(next);
			}
		}

		Stack<NodeGroup> st;
		for(int i=1; i<avail.size()+1; ++i)
		{
			Stack<Node> st2 = new Stack<Node>();
			st = getComb2(avail, avail.size(), i, 0, st2);
			while(!st.empty())
				all.push(st.pop());
		}

		return all;
	}

	public static Stack<NodeGroup> getComb2(ArrayList<Node> node, int n, int r, int idx, Stack<Node> st)
	{
		Stack<NodeGroup> temp = new Stack<NodeGroup>();

		if(r==0 || n==r)
		{
			////// make combination
			NodeGroup t = new NodeGroup();
			ArrayList<Node> t_nodes = new ArrayList<Node>();

			for(int i=0; i<st.size(); ++i)
					t_nodes.add(st.get(i));
			if(n==r)
			{
				for(int i=0; i<r; ++i)
					t_nodes.add(node.get(idx+i));
			}
			
			t.setNodes(t_nodes);
			temp.push(t);
		}
		else
		{
			Stack<NodeGroup> tmp;
			st.push(node.get(idx));
			tmp = getComb2(node, n-1, r-1, idx+1, st);
			while(!tmp.empty())
				temp.push(tmp.pop());

			st.pop();
			tmp = getComb2(node, n-1, r, idx+1, st);
			while(!tmp.empty())
				temp.push(tmp.pop());
		}
		return temp;
	}
	//}}}
	

//{{{ DRAW TOOLS
	public static void DrawDag(Node node)
	{
		ArrayList<Node> nexts = node.getNext();

		if(nexts.size()==0)
			return;

		if(node.visit == true)
			return;
		node.visit = true;

		System.out.print(node.getId() + "'d" + node.depth + "'" + "--");
		for(int i = 0; i < nexts.size(); ++i)
		{
			System.out.print(nexts.get(i).getId() + "(" + nexts.get(i).depth + ") ");
		}

		System.out.println();
		for(int i = 0; i < nexts.size(); ++i)
			DrawDag(nexts.get(i));
	}

	public static void DrawNodeGroup(NodeGroup node)
	{
		int count = 0;
		while(node!=null)
		{
			
			System.out.print("(" + node.getId() + ") - ");
			for(int i=0; i<node.getNodes().size(); ++i)
				System.out.print(node.getNodes().get(i).getId() + " ");
			System.out.println();
			count ++;
			node = node.getNext();
		}
		System.out.println(count);
	}
	public static int MinNumSegments(ArrayList<NodeGroup> seq)
	{
		int MinCount = 10000;
		for (int i=0; i<seq.size(); ++i) {
			int count = 0;
			NodeGroup node = seq.get(i);
			while(node!=null)
			{
				count ++;
				node = node.getNext();
			}
			if (count < MinCount) {
				MinCount = count;
			}
			
		}
		return MinCount;
	}

	//}}}
}

