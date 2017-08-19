import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {

	/**
	 * Collection of nodes this node follows
	 */
	private boolean[] followees;

	/**
	 * Set of transactions this node will propose to the network
	 */
	private Set<Transaction> txsToPropose;

	/**
	 * Set of transactions upon which the network has reach consensus
	 */
	private Set<Transaction> acceptedTxs;

	/**
	 * Set of probable malicious nodes that this node follows
	 */
	private Set<Integer> badFollowees;

	/**
	 * A mapping of nodes to the set of transactions each has proposed
	 * 
	 * @formatter:off NOTE: a node is considered a valid candidate if (1) This node
	 *                follows it, i.e follwees[i] must be true, where {i} is the
	 *                index of the corresponding candidate node
	 * @formatter:on
	 */
	private HashMap<Integer, Set<Transaction>> proposedTxs;
	/**
	 * A mapping of nodes to the number of rounds since that that node proposed a
	 * "new" transaction. "new" in this case refers to a any valid transaction, that
	 * was not previously proposed by the corresponding node.
	 */
	private HashMap<Integer, Integer> roundsSinceNewTx;
	/**
	 * Number of rounds remaining before consensus should be reached
	 */
	private int roundsLeft;

	public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
		roundsLeft = numRounds;
		txsToPropose = new HashSet<Transaction>();
		acceptedTxs = new HashSet<Transaction>();
		badFollowees = new HashSet<Integer>();
		proposedTxs = new HashMap<>();
		roundsSinceNewTx = new HashMap<>();

	}

	public void setFollowees(boolean[] followees) {
		this.followees = followees;
	}

	public void setPendingTransaction(Set<Transaction> pendingTransactions) {
		txsToPropose = pendingTransactions;
	}

	public Set<Transaction> sendToFollowers() {
		if (roundsLeft <= 0) {
			return acceptedTxs;
		}

		return txsToPropose;
	}

	public void receiveFromFollowees(Set<Candidate> candidates) {

		for (Candidate candidate : candidates) {

			if (!proposedTxs.containsKey(candidate.sender)) {

				proposedTxs.put(candidate.sender, new HashSet<Transaction>());
				roundsSinceNewTx.put(candidate.sender, 0);
			}
		}

		for (Candidate candidate : candidates) {
			txsToPropose.add(candidate.tx);
		}

		if (roundsLeft == 1) {
			acceptedTxs.addAll(txsToPropose);
		}

		roundsLeft--;

	}
}
