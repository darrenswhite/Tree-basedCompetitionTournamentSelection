package cs21120.assignment.solution;

import cs21120.assignment.IBinaryTree;
import cs21120.assignment.IManager;
import cs21120.assignment.Match;
import cs21120.assignment.NoNextMatchException;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/**
 * The competition tree uses a binary search tree to store all of
 * the upcoming matches as well as previous matches with their
 * associated scores. Each node in the tree may consists of up to
 * two children and a node may contain a player and a score. If a
 * node has two children which each contain a player then that will
 * be a match between the two children and the winner will be moved
 * to the parent ready for the next match.
 * <p>
 * The tree is created by adding a sufficient amount of nodes to the
 * tree so that all matches will take place. The amount of nodes in
 * the tree needs to be equal to 2^log2(p) - 1 where p is the number
 * of players. log2(p) will result in the height of the tree which can
 * then be used to get the total number of nodes and adding all of the
 * nodes.
 * <p>
 * Now that the tree is built all of the players can be added. The number
 * of players to append to the bottom of the tree is 2*p - h where p is
 * the number of player and h is the height of the tree. These players
 * will be added as new leaves. The remaining number of players to be added
 * are added at already existing empty leaves and therefore will not play
 * a match until another match has been completed (its parents other child
 * match to be precise).
 * <p>
 * The choice of how matches are played is simply from right to left. So if
 * there are a total of 10 nodes then the first match is between nodes 10 and
 * 9, then 8 and 7, then 5 and 6, and so on until 0 is reached (as the root is
 * the winner).
 * <p>
 * Setting the score of the current match is to keep the current match stored
 * and later set the score values for each node and then move the winner player
 * to the parent node.
 * <p>
 * Correct functionality (60%): The program has the correct functionality
 * with all methods implemented from IManager and work as expected. I would
 * award myself 50 marks for this section as the methods work as expected
 * but are not perfect as I believe they could be more efficient.
 * <p>
 * Class structure (20%): I believe that this single class has a clear
 * structure with appropriate fields and methods where needed. All of
 * the code is properly labelled, documented and easy to read. For this
 * section I would award myself 15 marks as some variables could have
 * used better naming.
 * <p>
 * Documentation (20%): The documentation contains detailed descriptions
 * of the algorithm used and is correctly formatted JavaDoc. I would
 * award myself 15 marks for this section due to no references cited.
 * <p>
 * Overall I would award myself 80 marks.
 *
 * @author Darren White
 */
public class BTSingleElimDaw48 implements IManager {

	/**
	 * The root of the competition tree - used to store the competition
	 */
	private final BinaryTreeImpl root = new BinaryTreeImpl();

	/**
	 * The queue of matches to be played (used in pairs). Stores the indices
	 * of nodes to be chosen
	 */
	private final Queue<Integer> matches = new LinkedList<>();

	/**
	 * The two nodes for the current match being played
	 */
	private BinaryTreeImpl currLeft, currRight;

	/**
	 * Adds two players to the competition tree in the next available position.
	 * This will add both players to a sub tree with the least amount
	 * of nodes (this is recursive).
	 *
	 * @param curr    The node to append the players to (usually root)
	 * @param player1 The first player to add
	 * @param player2 The second player to add
	 */
	private void addPlayers(BinaryTreeImpl curr, String player1, String player2) {
		if (curr.right == null && curr.left == null) {
			BinaryTreeImpl right = new BinaryTreeImpl();
			BinaryTreeImpl left = new BinaryTreeImpl();

			right.player = player2;
			left.player = player1;

			curr.right = right;
			curr.left = left;
		} else if (size(curr.left) < size(curr.right)) {
			addPlayers(curr.left, player1, player2);
		} else {
			addPlayers(curr.right, player1, player2);
		}
	}

	/**
	 * Appends a node to the end of the tree in the next available position.
	 * This will add the given node to the sub tree with the least amount of
	 * nodes (recursively).
	 *
	 * @param curr The node to append to (usually root)
	 * @param node The node to append
	 */
	private void appendNode(BinaryTreeImpl curr, BinaryTreeImpl node) {
		if (curr.left == null) {
			curr.left = node;
		} else if (curr.right == null) {
			curr.right = node;
		} else if (size(curr.left) < size(curr.right)) {
			appendNode(curr.left, node);
		} else {
			appendNode(curr.right, node);
		}
	}

	/**
	 * Performs breadth-first search on the competition tree. This will
	 * iterate over all nodes and test the given predicate on each node
	 * encountered. The search is terminated early if the predicate returns
	 * true for any node.
	 *
	 * @param p The Predicate to test on each node
	 */
	private void bfs(Predicate<BinaryTreeImpl> p) {
		bfs(root, p, false);
	}

	/**
	 * Performs breadth-first search on the competition tree. This will
	 * iterate over all nodes and test the given predicate on each node
	 * encountered. The search is terminated early if the predicate returns
	 * true for any node. The search can be performed in both directions,
	 * left-to-right or right-to-left which is given by the boolean parameter.
	 *
	 * @param p   The Predicate to test on each node
	 * @param rtl True to perform the search right-to-left
	 */
	private void bfs(Predicate<BinaryTreeImpl> p, boolean rtl) {
		bfs(root, p, rtl);
	}

	/**
	 * Performs breadth-first search on the given node. This will
	 * iterate over all nodes and test the given predicate on each node
	 * encountered. The search is terminated early if the predicate returns
	 * true for any node. The search can be performed in both directions,
	 * left-to-right or right-to-left which is given by the boolean parameter.
	 *
	 * @param root The node to start the search at
	 * @param p    The Predicate to test on each node
	 * @param rtl  True to perform the search right-to-left
	 */
	private void bfs(BinaryTreeImpl root, Predicate<BinaryTreeImpl> p, boolean rtl) {
		Queue<BinaryTreeImpl> queue = new LinkedList<>();

		queue.add(root);

		while (!queue.isEmpty()) {
			BinaryTreeImpl node = queue.poll();

			if (p.test(node)) {
				break;
			}

			if (rtl) {
				if (node.right != null) {
					queue.add(node.right);
				}
				if (node.left != null) {
					queue.add(node.left);
				}
			} else {
				if (node.left != null) {
					queue.add(node.left);
				}
				if (node.right != null) {
					queue.add(node.right);
				}
			}
		}
	}

	/**
	 * Builds the competition tree. This will create all necessary nodes
	 * required for all of the matches that will take place. All of the
	 * players are then added equally on each side of the tree (left & right,
	 * evenly) and then the remaining players are added to empty leaves.
	 * A queue is then used to store which matches will be played by simply
	 * storing the index of each node in reverse order (excluding the root).
	 *
	 * @param players The list of players to add to the tree
	 */
	private void buildTree(List<String> players) {
		int numPlayers = players.size();
		int treeHeight = (int) Math.ceil(Math.log(numPlayers) / Math.log(2));
		int nodesToAdd = (int) Math.pow(2, treeHeight) - 2;

		while (nodesToAdd-- > 0) {
			appendNode(root, new BinaryTreeImpl());
		}

		int extraPlayers = (int) Math.pow(2, treeHeight) - numPlayers;
		int totalPlayers = numPlayers - extraPlayers;

		for (int i = 0; i < totalPlayers - 1; i++) {
			addPlayers(root, players.get(i++), players.get(i));
		}

		for (int i = totalPlayers; i < numPlayers; i++) {
			getEmptyLeaf().player = players.get(i);
		}

		int totalNodes = (int) (Math.pow(2, treeHeight + 1) - 2 - extraPlayers * 2);

		for (int i = totalNodes; i > 0; i--) {
			matches.add(i);
		}
	}

	/**
	 * The competition tree root node
	 *
	 * @return The root node
	 */
	@Override
	public IBinaryTree getCompetitionTree() {
		return root;
	}

	/**
	 * Gets the first leaf without an associated player using
	 * breadth-first search.
	 *
	 * @return An empty leaf (node without a player)
	 */
	private BinaryTreeImpl getEmptyLeaf() {
		AtomicReference<BinaryTreeImpl> node = new AtomicReference<>();

		bfs(n -> {
			if (n.left == null && n.right == null && n.player == null) {
				node.set(n);
				return true;
			}

			return false;
		}, true);

		return node.get();
	}

	/**
	 * Gets a node at the given position in the competition tree using bread-first
	 * search.
	 *
	 * @param i The index to get the node at
	 * @return The node at the index
	 */
	private BinaryTreeImpl getNodeAt(int i) {
		AtomicReference<BinaryTreeImpl> node = new AtomicReference<>();
		AtomicInteger index = new AtomicInteger();

		bfs(n -> {
			if (i == index.getAndIncrement()) {
				node.set(n);
				return true;
			}

			return false;
		});

		return node.get();
	}

	/**
	 * Gets the parent node of the given node in the competition tree.
	 *
	 * @param node The node to get the parent node of
	 * @return The parent of the given node
	 */
	private BinaryTreeImpl getParent(BinaryTreeImpl node) {
		AtomicReference<BinaryTreeImpl> parent = new AtomicReference<>();

		bfs(n -> {
			if (n.left != null && n.left.equals(node) || n.right != null && n.right.equals(node)) {
				parent.set(n);
				return true;
			}

			return false;
		});

		return parent.get();
	}

	/**
	 * Gets the player at the given index position in the competition tree
	 *
	 * @param n The index to get the node at
	 * @return The node at the index
	 */
	@Override
	public String getPosition(int n) {
		BinaryTreeImpl node = getNodeAt(n);

		return node != null ? node.player : null;
	}

	/**
	 * Determines if another match can be played in the competition tree
	 *
	 * @return If another match is to be played
	 */
	@Override
	public boolean hasNextMatch() {
		return !matches.isEmpty();
	}

	/**
	 * Gets the next match to be played if any
	 *
	 * @return The next match to be played
	 * @throws NoNextMatchException If there is no playable match to be returned
	 */
	@Override
	public Match nextMatch() throws NoNextMatchException {
		int rightIdx = matches.poll();
		int leftIdx = matches.poll();

		currRight = getNodeAt(rightIdx);
		currLeft = getNodeAt(leftIdx);

		if (currRight == null) {
			throw new NoNextMatchException("Unable to find player at index: " + rightIdx);
		} else if (currLeft == null) {
			throw new NoNextMatchException("Unable to find player at index: " + leftIdx);
		}

		return new Match(currLeft.player, currRight.player);
	}

	/**
	 * Sets the score for the current match being played
	 *
	 * @param p1 The score of the first player
	 * @param p2 The score of the second player
	 */
	@Override
	public void setMatchScore(int p1, int p2) {
		currLeft.score = p1;
		currRight.score = p2;

		getParent(currLeft).player = p1 > p2 ? currLeft.player : currRight.player;
	}

	/**
	 * Sets the list of players to be used for the competition tree and
	 * will initialize the binary search tree
	 *
	 * @param players The list of players
	 */
	@Override
	public void setPlayers(List<String> players) {
		if (players.size() < 2) {
			System.err.println("Must have at least two players!");
			return;
		}

		buildTree(players);
	}

	/**
	 * Calculates the size of the tree given. A minimum of 1 will be returned.
	 * All children of the node will add 1 to the total size.
	 *
	 * @param node The node to calculate the size of
	 * @return The size of the node and its children
	 */
	private int size(BinaryTreeImpl node) {
		int size = 1;

		if (node.right != null) {
			size += size(node.right);
		}

		if (node.left != null) {
			size += size(node.left);
		}

		return size;
	}

	/**
	 * This class is used to store the binary search tree. It represents a single
	 * node which can have two children at most, a score (int) and a player (string)
	 */
	private class BinaryTreeImpl implements IBinaryTree {

		private BinaryTreeImpl left, right;
		private String player;
		private int score = 0;

		/**
		 * Gets the left child of this node
		 *
		 * @return The left child
		 */
		@Override
		public IBinaryTree getLeft() {
			return left;
		}

		/**
		 * Gets the current player associated with this node (can be null)
		 *
		 * @return The player for this node
		 */
		@Override
		public String getPlayer() {
			return player;
		}

		/**
		 * Gets the right child of this node
		 *
		 * @return The right child
		 */
		@Override
		public IBinaryTree getRight() {
			return right;
		}

		/**
		 * Gets the current score of this node (0 is default)
		 *
		 * @return The score for this node
		 */
		@Override
		public int getScore() {
			return score;
		}
	}
}