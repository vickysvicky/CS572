
import java.util.LinkedList;


public class SearchTree {
	Node root;
	int depth;
	int size;
	
	/**
	 * Default constructor for empty tree
	 */
	public SearchTree(Board game) {
		root = new Node(game);
		depth = 0;
		size = 1;
	}
	
	
	public class Node{
		Node parent;
		LinkedList<Node> child = new LinkedList<Node>();
		Board state;
		int[] scores;
		
		public Node(Board game) {
			state = game;
			scores = game.updateScore();
		}
		
		public Node addChild(Board child) {
			Node nadd = new Node(child);
			nadd.parent = parent;
			if(size==0) {
				root = nadd;
				size++;
			}
			else {
				this.child.add(nadd);
				size++;
			}
			return nadd;
		}
	}
	
	
}
