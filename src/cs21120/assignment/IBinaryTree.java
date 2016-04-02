package cs21120.assignment;

/**
 * The tree implementation to follow the following specification to allow it to be printed
 *
 * @author ncm
 */
public interface IBinaryTree {

	/**
	 * Gets the left sub tree, or null if there isn't a left subtree
	 *
	 * @return returns the left subtree
	 */
	IBinaryTree getLeft();

	/**
	 * Gets the name of the player or team at the top of this tree
	 *
	 * @return returns the name of the player or team as a String
	 */
	String getPlayer();

	/**
	 * Gets the right sub tree, or null if there isn't a right subtree
	 *
	 * @return returns the right subtree
	 */
	IBinaryTree getRight();

	/**
	 * Returns the score scored by this player or team in this round
	 *
	 * @return returns the score
	 */
	int getScore();
}