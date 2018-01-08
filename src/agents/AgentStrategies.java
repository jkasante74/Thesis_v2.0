

package agents;

import javax.swing.JOptionPane;

import historicalInfo.HistoricalInfoMgr;


public class AgentStrategies extends Agent {

	// Parameters for Agent strategies
	public static boolean infoAcquired = false;

	// Private Variables
	private final char COOPERATE = 'C';
	private final char DEFECT = 'D';
	private String opponentFirstAct="";
	private double ALPHA = 0.5, BETA = 0.5,DISCOUNTFACTOR = 0.9;
	
	// Parameter variables
	protected double [][] agentBeliefs;
	

	
	
	public AgentStrategies(HistoricalInfoMgr him, double[][] agentBeliefs) {
		
		this.him = him;
		this.agentBeliefs = agentBeliefs;
		
	}



	/**
	 * DefectAll strategy defects at all times no matter what the opponent does
	 * 
	 * @return DEFECT : 'D'
	 * 
	 *         The algorithm and code structure for the DefectAll Strategy was
	 *         taken and modified from
	 *         http://www.prisoners-dilemma.com/java/ipdlx/ipdlx_javadocs/
	 * 
	 */
	@Override
	char defectAll() {

		return DEFECT;
	}

	
	
	
	/**
	 * CooperateAll strategy cooperates at all times no matter what the opponent
	 * does
	 * 
	 * @return COOPERATE : 'C'
	 * 
	 *         The algorithm and code structure for the CooperateAll Strategy
	 *         could be located here
	 *         http://www.prisoners-dilemma.com/java/ipdlx/ipdlx_javadocs/
	 * 
	 */
	@Override
	char cooperateAll() {

		return COOPERATE;
	}

	
	
	
	/**
	 * advanceCooeprator strategy cooperates or defect based on acquired past
	 * information on opponents.
	 * 
	 * @return matchAction : Action taken after strategic decision
	 * 
	 */
	@Override
	char advanceCooperator(int requestingAgentID, String agentStrategy,
			int opponentID,String opponentStrategy, int currentTournament, int currentRound, int infoRequestOption) {

		char matchAction = COOPERATE; // Set default action
		
		// First action in a new experiment
		if (((currentTournament == 0) && (currentRound == 0))) {
			matchAction = COOPERATE;
		}

		// Act based on updated beliefs from past information
		else {
			double opponentCooperateRatio = getOpponentPastInfo(requestingAgentID,
					opponentID, infoRequestOption);
			
			
			//Update agent's belief about opponent cooperating ratio
			setOpponentCooperateRatio(requestingAgentID,opponentID,opponentCooperateRatio);

			
			// Get opponent's updated cooperating ratio
			double opponentCooperatingRating = 	getOpponentCooperateRatio(requestingAgentID,opponentID,opponentCooperateRatio);

			
			
			// Promote cooperation with Naive Cooperators
			if (opponentCooperatingRating >= 0.9){
				matchAction = COOPERATE;
			}
			
			// Protect against Naive exploiters
			else if(opponentCooperatingRating <= 0.1){
				matchAction = DEFECT;
			}
			
			
			// Make strategic decision if agent is advanced 
			else
				matchAction = superRationalWithDiscountFactor(opponentCooperateRatio,requestingAgentID,agentStrategy, opponentID);
		}

		return matchAction;
	}

	
	//advance Defector here
	/**
	 * advancedDefector strategy cooperates or defects based on a strategic
	 * analysis of acquired past information and updated belief on opponent with
	 * a higher inclination to defect
	 * 
	 * @return matchAction : Action taken after strategic decision
	 * 
	 */
	@Override
	char advanceDefector(int requestingAgentID, String agentStrategy,
			int opponentID,String opponentStrategy,
			int currentTournament, int currentRound, int infoRequestOption) {

		char matchAction = DEFECT; // Set default action
		
		// First action in a new experiment
		if (((currentTournament == 0) && (currentRound == 0))) {
			matchAction = DEFECT;
		}

		// Act based on updated beliefs from past information
		else {
			double opponentCooperateRatio = getOpponentPastInfo(requestingAgentID,
					opponentID, infoRequestOption);

			//Update agent's belief about opponent cooperating ratio
			setOpponentCooperateRatio(requestingAgentID,opponentID,opponentCooperateRatio);

			
			// Get opponent's updated cooperating ratio
			double opponentCooperatingRating = 	getOpponentCooperateRatio(requestingAgentID,opponentID,opponentCooperateRatio);

			
			// Exploit naive cooperators and protect from defectors
			if (((opponentCooperatingRating) <= 0.1)
					|| ((opponentCooperatingRating) >= 0.9)) {
				matchAction = DEFECT;
			}
			
			
			// Decide using returns-based beliefs
			else
				matchAction = superRationalWithDiscountFactor(opponentCooperateRatio, requestingAgentID,agentStrategy, opponentID);
		}
		return matchAction;
	}

	/**
	 * superRationalWithDiscountFactor method defines algorithm for identifying opponent's expected cooperating ratio
	 * in order to make a decision that maximize its expected return.
	 * 							 
	 * @param opponentCooperateRatio	:	Expected ratio at which opponent cooperates	
	 * @param requestingAgentID			:	agent experiment ID
	 * @param agentStrategy				:	opponent Strategy
	 * @param opponentID				: 	opponent experiment ID
	 * @return							:	agent's action
	 */
	private char superRationalWithDiscountFactor(double opponentCooperateRatio, int requestingAgentID,String agentStrategy, int opponentID) {
		char matchAction; // Set default action

		if(opponentFirstAct != "D"){
			//JOptionPane.showMessageDialog(null, agentStrategy);

			if(agentStrategy.equalsIgnoreCase("Advanced_E")){	
				// Advance Exploiters obstructs cooperation and exploit advance cooperators 
				matchAction = DEFECT;
			}
			else
				// Advance Cooperators and Advance Defectors promotes cooperation
				matchAction = COOPERATE;
		}
		
		else{
		
			// Find opponent defect ratio
			double opponentDefectRatio = 1.0 - opponentCooperateRatio;
		
		
			//Calculate opponent cooperate expected function
			double opponentCooperateExpectation = opponentCooperateRatio * (REWARD / (1 - DISCOUNTFACTOR));
			
			//Calculate opponent defect expected function
			double opponentDefectExpectation = opponentDefectRatio * (TEMPT + ((DISCOUNTFACTOR * PUNISH) / (1 - DISCOUNTFACTOR)));
		
				
			// Make decision based on opponent Expectations
			if(opponentCooperateExpectation >= opponentDefectExpectation)
				matchAction = COOPERATE;
			else
				matchAction = DEFECT;
		}
		
		return matchAction;
	}

	
	
	
	/**
	 * dummy strategy returns a non-computable strategy 'A'
	 * 
	 * @return DUMMY : 'D'
	 * 
	
	 */
	char dummy() {

		return 'A';
	}

	
	/**
	 * getOpponentPastInfo request for past opponent action from the HIM and
	 * calculate the cooperating level of opponent which is stored in the
	 * agent's belief base
	 * 
	 * @param requestingAgentID
	 *            : Requesting agent ID
	 * 
	 * @param opponentID
	 *            : Opponent ID
	 * 
	 * 
	 */
	private double getOpponentPastInfo(int requestingAgentID,
			int opponentID, int infoRequestOption) {

		String opponentInformation = "";
		
		double opponentCooperateRatio = 0;

		switch (infoRequestOption) {

		// Get opponent's first action
		case 0:
			opponentInformation = him.requestOppPastInfo(requestingAgentID,
					opponentID, infoRequestOption);
			
			if (infoAcquired) {
				if (opponentInformation.equalsIgnoreCase("D"))
					opponentCooperateRatio = 0.0;
				else
					opponentCooperateRatio = 1.0;

			}
			break;

		// Get opponent's first defection
		case 1:
			opponentInformation = him.requestOppPastInfo(requestingAgentID,
					opponentID, infoRequestOption);
			if (infoAcquired) {
				int pastInfo = Integer.parseInt(opponentInformation);
				opponentCooperateRatio = pastInfo / 10;
			}
			break;

		// Request all opponent's past actions
		case 2:
			opponentInformation = him.requestOppPastInfo(requestingAgentID,
					opponentID, infoRequestOption);
			if ((infoAcquired)&&(!opponentInformation.isEmpty())){
				opponentCooperateRatio = calcOppRating(opponentInformation);
				opponentFirstAct = opponentInformation.substring(0, 1);
			}
			break;

		// Request all opponent's past actions from a random tournament
		case 3:
			opponentInformation = him.requestOppPastInfo(requestingAgentID,
					opponentID, infoRequestOption);
			if (infoAcquired) {
				opponentCooperateRatio = calcOppRating(opponentInformation);
				opponentFirstAct = opponentInformation.substring(0, 1);
			}
			break;

		// Request past actions of those who played against opponent
		case 4:
			opponentInformation = him.requestOppPastInfo(requestingAgentID,
					opponentID, infoRequestOption);
			if (infoAcquired)
				opponentCooperateRatio = calcOppRating(opponentInformation);
			break;
		}
		
		

		return opponentCooperateRatio;
	}

	/**
	 * calcOppRating method calculates the current rating of the opponent based
	 * on the past information received from HIM component
	 * 
	 * @param opponentInformation
	 *            : list of past actions of opponent or secondary opponents
	 *            received from HIM.
	 * @return opponentCooperateRatio : agent's calculated rating of the opponent.
	 * 
	 */
	private double calcOppRating(String opponentInformation) {

		double numOfCooperations = 0.0, numOfDefections = 0.0;
		double opponentCooperateRatio = 0.0;

		for (int i = 0; i < opponentInformation.length(); i++) {
			if (opponentInformation.charAt(i) == DEFECT)
				numOfDefections++;
			else
				numOfCooperations++;
		}

		if ((numOfCooperations + numOfDefections) == 0)
			opponentCooperateRatio = 0;
		else
			opponentCooperateRatio = numOfCooperations
					/ (numOfCooperations + numOfDefections);
		
		return opponentCooperateRatio;
	}


	
	/**
	 * setOpponentCooperateRatio method updates cooperate ratio of opponent in agent's belief base
	 * @param requestingAgentID		:	agent ID
	 * @param opponentID			:	opponent ID
	 * @param opponentCooperateRatio	: opponent Cooperating ratio
	 */
	protected void setOpponentCooperateRatio(int requestingAgentID, int opponentID, double opponentCooperateRatio) {
		agentBeliefs[requestingAgentID][opponentID] = opponentCooperateRatio;		
	}

	
	/**
	 * getOpponentCooperateRatio method returns opponent current cooperating ratio
	 * @param requestingAgentID		:	agent ID
	 * @param opponentID			:	opponent ID
	 * @param opponentCooperateRatio	: opponent Cooperating ratio
	 * @return current opponent cooperating ratio
	 */
	protected double getOpponentCooperateRatio(int requestingAgentID, int opponentID, double opponentCooperateRatio) {
		
		return agentBeliefs[requestingAgentID][opponentID];
	}

	
	
	
	
	
	
}

