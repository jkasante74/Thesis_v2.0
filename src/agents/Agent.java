package agents;


import javax.swing.JOptionPane;

import historicalInfo.HistoricalInfoMgr;

public class Agent {

	protected final String ADVANCE_COOPERATOR = "Advanced_C";
	protected final String ADVANCE_DEFECTOR = "Advanced_D";
	private final String NAIVE_DEFECTOR = "Naive_D";
	private final String NAIVE_COOPERATOR = "Naive_C";
	private final String FIRST_ACT_COOPERATE = "C";
	private final String FIRST_ACT_DEFECT = "D";
	private final String DUMMY = "Dummy";
	public int agentID, infoRequestOption;
	HistoricalInfoMgr him;
	protected int numOfAgents;
	public String agentStrategy;
	protected float tempt, reward, punish, sucker;
	private static char NON_APPLICABLE = 'N';
	public double agentBeliefs[][];

	// Parameters for Agent strategies
	public static boolean infoAcquired = false;

	// Private Variables
	private final char COOPERATE = 'C';
	private final char DEFECT = 'D';
	private String opponentFirstAct = "";

	private double DISCOUNTFACTOR = 0.9;

	public Agent() {

	}

	/**
	 * Agent Constructor : sets up parameters and other variables
	 * 
	 * @param agentID
	 *            : Agent's current experiment ID
	 * @param agentStraggy
	 *            : Assigned strategy to the agent
	 * @param infoRequestOption
	 *            : Agent's approach to request for opponent's past information
	 * 
	 */
	public Agent(int agentID, String agentStrategy, int infoRequestOption) {
		this.agentID = agentID;
		this.agentStrategy = agentStrategy;
		this.infoRequestOption = infoRequestOption;

	}

	/**
	 * getAgentAction method returns the chosen action of the agent
	 * 
	 * @param requestingAgentID
	 *            : Requesting agent ID
	 * @param opponentID
	 *            : Opponent ID
	 * @param opponentStrategy 
	 * @param currentTournament
	 *            : Current Tournament
	 * @param currentRound
	 *            : Current Round
	 * 
	 * @return actions : Selected actions of both agent and opponent
	 * 
	 */
	public char returnAction(int requestingAgentID, String agentStrategy, int opponentID, String opponentStrategy, int currentTournament,
			int currentRound) {

		char agentAction;

		// Get action of agent based on strategy
		switch (agentStrategy) {
		case NAIVE_COOPERATOR:
			agentAction = cooperateAll();
			break;

		case NAIVE_DEFECTOR:
			agentAction = defectAll();
			break;

		case ADVANCE_COOPERATOR:
			//agentAction = advanceCooperator(requestingAgentID, agentStrategy, opponentID, currentTournament,
			//		currentRound, infoRequestOption, communicationTournament);
			agentAction = advanceC(currentTournament, currentRound, requestingAgentID, opponentID, infoRequestOption);
			break;

		case ADVANCE_DEFECTOR:
			//agentAction = advanceDefector(requestingAgentID, agentStrategy, opponentID, currentTournament, currentRound,
			//		infoRequestOption, communicationTournament);
			agentAction = advanceD(currentTournament, currentRound, requestingAgentID, opponentID, infoRequestOption);
			break;

		case DUMMY:
			agentAction = 'A';
			break;

		default:
			agentAction = NON_APPLICABLE;
		}

		return agentAction;

	}

	
	
	private char advanceD(int currentTournament, int currentRound, int requestingAgentID, int opponentID, int infoRequestOption) {
		
		// Set natural action
		char matchAction;
		
		// First action in a new experiment
		if (((currentTournament == 0) && (currentRound == 0))) {
				matchAction = DEFECT;
		}

		
		else{ 
			if(((him.requestOppPastInfo(requestingAgentID, opponentID, infoRequestOption)).equalsIgnoreCase(NAIVE_COOPERATOR))||((him.requestOppPastInfo(requestingAgentID, opponentID, infoRequestOption)).equalsIgnoreCase(NAIVE_DEFECTOR)))
				matchAction = DEFECT;
			else{
				double opponentCooperateRatio = getOpponentPastInfo(requestingAgentID, opponentID, 2, agentStrategy);

				// Update agent's belief about opponent cooperating ratio
				setOpponentCooperateRatio(requestingAgentID, opponentID, opponentCooperateRatio);
			
				// Get opponent's updated cooperating ratio
				double opponentCooperatingRating = getOpponentCooperateRatio(requestingAgentID, opponentID);
			matchAction = superRationalWithDiscountFactor(opponentCooperateRatio, requestingAgentID, agentStrategy,
					opponentID);
		}
		}
		
		return matchAction;
	}

	
	
	private char advanceC(int currentTournament, int currentRound, int requestingAgentID, int opponentID, int infoRequestOption) {
		
		char matchAction;
				
		// First action in a new experiment
		if (((currentTournament == 0) && (currentRound == 0))) {
				matchAction = COOPERATE;
		}
		
		else{ 
			if(((him.requestOppPastInfo(requestingAgentID, opponentID, infoRequestOption)).equalsIgnoreCase(NAIVE_COOPERATOR))||((him.requestOppPastInfo(requestingAgentID, opponentID, infoRequestOption)).equalsIgnoreCase(ADVANCE_COOPERATOR)))
				matchAction = COOPERATE;
			else
				matchAction = DEFECT;
		}
		
		return matchAction;
	}

	/**
	 * advancedDefector strategy cooperates or defects based on a strategic
	 * analysis of acquired past information and updated belief on opponent with
	 * a higher inclination to defect
	 * @param requestingAgentID
	 *            : Requesting agent ID
	 * @param opponentID
	 *            : Opponent ID
	 * @param currentTournament
	 *            : Current Tournament
	 *            
	 * @param agentStraggy
	 *            : Assigned strategy to the agent
	 * @param infoRequestOption
	 *            : Agent's approach to request for opponent's past information
	    
	 * @return matchAction : Action taken after strategic decision
	 * 
	 */

	public char advanceDefector(int requestingAgentID, String agentStrategy, int opponentID, int currentTournament,
			int currentRound, int infoRequestOption, float communicationTournament) {

		char matchAction; // Set default action
		

		// First action in a new experiment
		if (((currentTournament == 0) && (currentRound == 0))) {
			matchAction = DEFECT;
		}

		// Act based on updated beliefs from past information
		else {
			double opponentCooperateRatio = 0.0;
			if(((currentTournament !=0)&&((currentTournament+1) % communicationTournament == 0.0 ))||(communicationTournament == 0.0 )){
				opponentCooperateRatio = getOpponentPastInfo(requestingAgentID, opponentID, infoRequestOption,
					agentStrategy);

				// Update agent's belief about opponent cooperating ratio
				setOpponentCooperateRatio(requestingAgentID, opponentID, opponentCooperateRatio);
			}
				// Get opponent's updated cooperating ratio
				double opponentCooperatingRating = getOpponentCooperateRatio(requestingAgentID, opponentID);

				// Exploit naive cooperators and protect from defectors
				if (((opponentCooperatingRating) <= 0.1) || ((opponentCooperatingRating) >= 0.9)) {
					matchAction = DEFECT;
				}

			// Decide using returns-based beliefs
			else
				matchAction = superRationalWithDiscountFactor(opponentCooperateRatio, requestingAgentID, agentStrategy,
						opponentID);
		}
		return matchAction;
	}

	
	/**
	 * advanceCooeprator strategy cooperates or defect based on acquired past
	 * information on opponents.
	 * 
	 * @param requestingAgentID
	 *            : Requesting agent ID
	 * @param opponentID
	 *            : Opponent ID
	 * @param currentTournament
	 *            : Current Tournament
	 *            
	 * @param agentStraggy
	 *            : Assigned strategy to the agent
	 *            
	 * @param infoRequestOption
	 *            : Agent's approach to request for opponent's past information
	
	 * @return matchAction : Action taken after strategic decision
	 * 
	 */

	char advanceCooperator(int requestingAgentID, String agentStrategy, int opponentID, int currentTournament,
			int currentRound, int infoRequestOption, float communicationTournament) {

		char matchAction; 
		
		// First action in a new experiment
		if (((currentTournament == 0) && (currentRound == 0))) {
			matchAction = COOPERATE;
		}

		// Act based on updated beliefs from past information
		else {
			double opponentCooperateRatio = 0.0;
			if(((currentTournament !=0)&&((currentTournament+1) % communicationTournament == 0.0 ))||(communicationTournament == 0.0 )){
				//JOptionPane.showMessageDialog(null,+currentTournament+ "yes");
				opponentCooperateRatio = getOpponentPastInfo(requestingAgentID, opponentID, infoRequestOption,
					agentStrategy);

				// Update agent's belief about opponent cooperating ratio
				setOpponentCooperateRatio(requestingAgentID, opponentID, opponentCooperateRatio);
			}
			
			// Get opponent's up	dated cooperating ratio
			double opponentCooperatingRating = getOpponentCooperateRatio(requestingAgentID, opponentID);

			// Promote cooperation with Naive and advanced Cooperators
			if (opponentFirstAct.equalsIgnoreCase(FIRST_ACT_COOPERATE)) {
				matchAction = COOPERATE;
			}

			// Promote cooperation among advanced defectors ready to cooperate
			else if ((opponentFirstAct.equalsIgnoreCase(FIRST_ACT_DEFECT))&&(opponentCooperatingRating >= 0.5)) {
				matchAction = COOPERATE;
			}
			
			else
				// Protect against Naive exploiters and exploiting advanced defectors
				matchAction = DEFECT;

		}

		return matchAction;
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

	char cooperateAll() {

		return COOPERATE;
	}



	/**
	 * createAgBeliefs creates and initializes the beliefs for both advanced
	 * cooperators and defectors about their ooponents.
	 * 
	 * @param numOfAgents
	 *            : Number of agents in current Experiment
	 * 
	 */
	public void setBeliefs(int agentID, String agentStrategy, int numOfAgents, float[] payOff, HistoricalInfoMgr him) {

		this.him = him;
		tempt = payOff[0];
		reward = payOff[1];
		punish = payOff[2];
		sucker = payOff[3];
		agentBeliefs = new double[numOfAgents][numOfAgents];

		// All agents assigns belief and game values about other agents
		for (int j = 0; j < numOfAgents; j++) {
			agentBeliefs[agentID][j] = 0.0;

		}

	}

	/**
	 * superRationalWithDiscountFactor method defines algorithm for identifying
	 * opponent's expected cooperating ratio in order to make a decision that
	 * maximize its expected return.
	 * 
	 * @param opponentCooperateRatio
	 *            : Expected ratio at which opponent cooperates
	 * @param requestingAgentID
	 *            : agent experiment ID
	 * @param agentStrategy
	 *            : opponent Strategy
	 * @param opponentID
	 *            : opponent experiment ID
	 * @return : agent's action
	 */
	private char superRationalWithDiscountFactor(double opponentCooperateRatio, int requestingAgentID,
			String agentStrategy, int opponentID) {
		
		char matchAction; 
		
		// Determine probability of opponent defecting 
		double opponentDefectRatio = 1.0 - opponentCooperateRatio;

		// Calculate opponent cooperate expected function
		double opponentCooperateExpectation = opponentCooperateRatio * (reward / (1 - DISCOUNTFACTOR));

		// Calculate opponent defect expected function
		double opponentDefectExpectation = opponentDefectRatio
					* (tempt + ((DISCOUNTFACTOR * punish) / (1 - DISCOUNTFACTOR)));

		// Make decision based on opponent Expectations
		if (opponentCooperateExpectation >= opponentDefectExpectation)
			matchAction = COOPERATE;
		else
			matchAction = DEFECT;

		return matchAction;
	}


	
	/**
	 * superRationalWithDiscountFactor method defines algorithm for identifying
	 * opponent's expected cooperating ratio in order to make a decision that
	 * maximize its expected return.
	 * 
	 * @param opponentCooperateRatio
	 *            : Expected ratio at which opponent cooperates
	 * @param requestingAgentID
	 *            : agent experiment ID
	 * @param agentStrategy
	 *            : opponent Strategy
	 * @param opponentID
	 *            : opponent experiment ID
	 * @return : agent's action
	 */
	private char subjectedExpectedUtility(double opponentCooperateProb, int requestingAgentID,
			String agentStrategy, int opponentID) {
		
		char matchAction; // Set default action
	
		// Find opponentDefectProb 
		double opponentDefectProb = 1.0 - opponentCooperateProb;
		
		// Calculate weighted payoffs.
		
		
		
		
		// Calculate opponent cooperate expected function
		double opponentCooperateExpectation = (reward * opponentCooperateProb) + (sucker * opponentDefectProb);
				
		// Calculate opponent defect expected function
		double opponentDefectExpectation = (tempt * opponentCooperateProb) + (punish * opponentDefectProb);

		// Make decision based on opponent Expectations
		if (opponentCooperateExpectation >= opponentDefectExpectation)
			matchAction = COOPERATE;
		else
			matchAction = DEFECT;

		return matchAction;
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
	 * @param infoRequestOption : Agent's approach to request for opponent's past information
	 * 
	 * @param agentStrategy : Agent's current adopted tournament strategy.
	 * 
	 * 
	 */
	private double getOpponentPastInfo(int requestingAgentID, int opponentID, int infoRequestOption,
			String agentStrategy) {
		
		double opponentCooperateRatio = 0;
		
		String opponentInformation = him.requestOppPastInfo(requestingAgentID, opponentID, infoRequestOption);
		switch (infoRequestOption) {

		// Get opponent's first action
		case 0:
			

			if (infoAcquired) {
				if (opponentInformation.equalsIgnoreCase(FIRST_ACT_DEFECT))
					opponentCooperateRatio = 0.0;
				else
					opponentCooperateRatio = 1.0;

			}
			break;

		// Get opponent's first defection
		case 1:
			
			if (infoAcquired) {
				int pastInfo = Integer.parseInt(opponentInformation);
				opponentCooperateRatio = pastInfo / 10;
			}
			break;

		// Request all opponent's past actions
		case 2:
			if ((infoAcquired)) {
				if ((!opponentInformation.isEmpty())) {
					opponentCooperateRatio = calcOppRating(opponentInformation);
					opponentFirstAct = opponentInformation.substring(0, 1);
				}

				else {
					if (agentStrategy.equalsIgnoreCase(ADVANCE_COOPERATOR)) {
						opponentCooperateRatio = 1.0;
					} else
						opponentCooperateRatio = 0.0;
				}

			}
			break;

		// Request all opponent's past actions from a random tournament
		case 3:
			if (infoAcquired) {
				opponentCooperateRatio = calcOppRating(opponentInformation);
				opponentFirstAct = opponentInformation.substring(0, 1);
			}
			break;

		// Request past actions of those who played against opponent
		case 4:
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
	 * @return opponentCooperateRatio : agent's calculated rating of the
	 *         opponent.
	 * 
	 */
	private double calcOppRating(String opponentInformation) {

		int numOfCooperations = 0, numOfDefections = 0; 
		double opponentCooperateRatio = 0.0;

		for (int i = 0; i < opponentInformation.length(); i++) {
			if (opponentInformation.charAt(i) == DEFECT)
				numOfDefections++;
			else
				numOfCooperations++;
		}

		if ((numOfCooperations + numOfDefections) == 0)
			opponentCooperateRatio = 0;
		else{
				try{
					opponentCooperateRatio = numOfCooperations / (numOfCooperations + numOfDefections);
				}
			
				catch (ArithmeticException e) { 
					/* This block will only execute if any Arithmetic exception 
					 * occurs in try block
					 */
					System.out.println("You should not divide a number by zero");
					System.out.println(e.getMessage());
				}
		}
		
		return opponentCooperateRatio;
	
	}

	
	/**
	 * setOpponentCooperateRatio method updates cooperate ratio of opponent in
	 * agent's belief base
	 * 
	 * @param requestingAgentID
	 *            : agent ID
	 * @param opponentID
	 *            : opponent ID
	 * @param opponentCooperateRatio
	 *            : opponent Cooperating ratio
	 */
	protected void setOpponentCooperateRatio(int requestingAgentID, int opponentID, double opponentCooperateRatio) {
		agentBeliefs[requestingAgentID][opponentID] = opponentCooperateRatio;
	}

	/**
	 * getOpponentCooperateRatio method returns opponent current cooperating
	 * ratio
	 * 
	 * @param requestingAgentID
	 *            : agent ID
	 * @param opponentID
	 *            : opponent ID
	 * @return current opponent cooperating ratio
	 */
	protected double getOpponentCooperateRatio(int requestingAgentID, int opponentID) {

		return agentBeliefs[requestingAgentID][opponentID];
	}

}