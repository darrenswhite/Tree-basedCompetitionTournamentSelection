package cs21120.assignment;

import java.util.List;

/**
 * Interface that defines the behaviour of a competition
 *
 * @author ncm
 */
public interface IManager {

	/**
	 * Get the competition tree
	 * For a single elimination type of competition this will be the root of the tree representing
	 * the matches played or still to be played
	 *
	 * @return returns the root of the competition tree
	 */
	IBinaryTree getCompetitionTree();

	/**
	 * Get the name of the player/team that finished in position n.
	 * The returned value should be null if the competition is still running, or if the competition hasn't
	 * determined who came in place n.  e.g. a single elimination competition can only (validly) return the
	 * winner (n=0).
	 *
	 * @param n the position to return
	 * @return returns the name of the team/player, or null if competition still running or n too large
	 */
	String getPosition(int n);

	/**
	 * Return true if there is another match in the competition that can be fetched using nextMatch
	 *
	 * @return returns true if the competition is still going
	 */
	boolean hasNextMatch();

	/**
	 * Returns the nextMatch to play
	 *
	 * @return returns the next match
	 * @throws NoNextMatchException if the competition is over and no more matches
	 */
	Match nextMatch() throws NoNextMatchException;

	/**
	 * Sets the score for the last retrieved Match.  The scores should be different i.e. no draws are allowed
	 *
	 * @param p1 should be the score of player1
	 * @param p2 should be the score of player2
	 */
	void setMatchScore(int p1, int p2);

	/**
	 * Set the players or teams to use in the competition
	 *
	 * @param players the players or teams
	 */
	void setPlayers(List<String> players);
}
