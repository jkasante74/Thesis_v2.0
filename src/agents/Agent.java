package agents;

import java.util.ArrayList;
import java.util.Random;

import historicalInfo.HistoricalInfoMgr;


public class Agent {
	
	
	private String ADVANCE_COOPERATOR = "Advanced_C";
	private String ADVANCE_DEFECTOR = "Advanced_D";
	private String NAIVE_DEFECTOR = "Naive_D";
	private String NAIVE_COOPERATOR = "Naive_C";
	private String DUMMY = "Dummy";
	private ArrayList agents;
	HistoricalInfoMgr him;
	protected int numOfAgents;
	protected String[] agentStrategies;
	protected int infoRequestOption;
	private int currentTournament;
	protected double agentBeliefs[][], gameValue[][];
	public Agent() {
		
	}


	public Agent(ArrayList agents, String[] agentStrategies, int infoRequestOption, int numOfAgents, HistoricalInfoMgr him) {
		
		this.agents = agents;
		this.him = him;
		this.agentStrategies = agentStrategies;
		this.infoRequestOption = infoRequestOption;
		this.numOfAgents = numOfAgents;
		setAgentsBeliefs(numOfAgents);
		AgentStrategies ags = new AgentStrategies();
		
	}
	
	
	/**
	 * getMatchedAgentActions method returns the chosen actions of paired agents
	 * 
	 * @param requestingAgentID
	 *            : Requesting agent ID
	 * @param opponentID
	 *            : Opponent ID
	 * @param currentTournament
	 *            : Current Tournament
	 * @param currentRound
	 *            : Current Round
	 * 
	 * @return actions : Selected actions of both agent and opponent
	 * 
	 */
	public char getAgentAction(int requestingAgentID,
			int opponentID, int currentTournament, int currentRound) {
		Agent ags = new AgentStrategies();
		char agentAction = 0;
		
		this.currentTournament = currentTournament;

		String agentStrategy = agentStrategies[requestingAgentID];
		String opponentStrategy = agentStrategies[opponentID];

		// Get action of requesting agent
		if (agentStrategy.equalsIgnoreCase(NAIVE_COOPERATOR))
			agentAction = ags.cooperateAll();
		if (agentStrategy.equalsIgnoreCase(NAIVE_DEFECTOR))
			agentAction = ags.defectAll();
		if (agentStrategy.equalsIgnoreCase(ADVANCE_COOPERATOR))
			agentAction = ags.advanceCooperator(requestingAgentID,
					opponentID, currentTournament, currentRound);
		if (agentStrategy.equalsIgnoreCase(ADVANCE_DEFECTOR))
			agentAction = ags.advanceDefector(requestingAgentID,
					opponentID, currentTournament, currentRound);
		if (agentStrategy.equalsIgnoreCase(DUMMY))
			agentAction = 'A';
		
		/*
		// Get action of opponent
		if (opponentStrategy.equalsIgnoreCase(NAIVE_COOPERATOR))
			OpponentAction = ags.cooperateAll();
		if (opponentStrategy.equalsIgnoreCase(NAIVE_DEFECTOR))
			OpponentAction = ags.defectAll();
		if (opponentStrategy.equalsIgnoreCase(ADVANCE_COOPERATOR))
			OpponentAction = ags.advanceCooperator(opponentID,
					requestingAgentID, currentTournament, currentRound);
		if (opponentStrategy.equalsIgnoreCase(ADVANCE_DEFECTOR))
			OpponentAction = ags.advanceDefector(requestingAgentID,
					opponentID, currentTournament, currentRound);
		if (opponentStrategy.equalsIgnoreCase(DUMMY))
			OpponentAction = 'A';
*/
		

		return agentAction;

	}
	
	
	char advanceDefector(int requestingAgentID, int opponentID, int currentTournament, int currentRound) {
		
		return 0;
	}


	char defectAll() {
		return 0;
	}


	char cooperateAll() {
		return 0;
	}


	char advanceCooperator(int requestingAgentID, int opponentID, int currentTournament, int currentRound) {
		return 0;
	}


	/**
	 * createAgBeliefs creates and initializes the beliefs for both advanced
	 * cooperators and defectors about their ooponents.
	 * 
	 * @param numOfAgents
	 *            : Number of agents in current Experiment
	 * 
	 */
	private void setAgentsBeliefs(int numOfAgents) {

		agentBeliefs = new double[numOfAgents][numOfAgents];
		gameValue = new double[numOfAgents][numOfAgents];

		// Advanced agents assigns belief and game values about other agents
		for (int i = 0; i < agentStrategies.length; i++) {
			if ((agentStrategies[i] == ADVANCE_COOPERATOR)
					|| (agentStrategies[i] == ADVANCE_DEFECTOR)) {
				for (int j = 0; j < agentStrategies.length; j++) {
					agentBeliefs[i][j] = 0.0;
					gameValue[i][j] = 0.0;
				}
			}

		}
	}

	
	
	/**
	 * requestRandomTournament method provides the mechanism for advanced agents
	 * to randomly choose a tournament and request opponent's past information.
	 * 
	 * @return getRandomTournament : randomly selected tournament
	 */
	public int getRandomTournament() {

		// Generate Random tournament
		Random rand = new Random();
		int randomTournamentNumber = rand.nextInt((currentTournament - 0) + 1);

		return randomTournamentNumber;
	}

	
	
	
	
	

}
