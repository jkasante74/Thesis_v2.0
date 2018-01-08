package agents;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JOptionPane;

import historicalInfo.HistoricalInfoMgr;


public class Agent {
	
	private String ADVANCE_COOPERATOR = "Advanced_C";
	private String ADVANCE_DEFECTOR = "Advanced_D";
	private String ADVANCE_EXPLOITER = "Advanced_E";
	private String NAIVE_DEFECTOR = "Naive_D";
	private String NAIVE_COOPERATOR = "Naive_C";
	private String DUMMY = "Dummy";
	private ArrayList agents;
	HistoricalInfoMgr him;
	protected int numOfAgents;
	protected String[] agentStrategies;
	protected int infoRequestOption;
	private int currentTournament;
	protected float TEMPT;
	protected float REWARD;
	protected float PUNISH;
	protected float SUCKER;
	public double agentBeliefs[][], gameValue[][];
	
	
	public Agent() {
	

	}

	/**
	 * Agent Constructor : sets up parameters and other variables
	 * @param agents
	 * @param agentStrategies
	 * @param infoRequestOption
	 * @param numOfAgents
	 * @param payOff
	 * @param him
	 */
	public Agent(ArrayList agents, String[] agentStrategies, int infoRequestOption, int numOfAgents, float []payOff, HistoricalInfoMgr him) {
		
		this.agents = agents;
		this.him = him;
		this.agentStrategies = agentStrategies;
		this.infoRequestOption = infoRequestOption;
		this.numOfAgents = numOfAgents;
		setBeliefs();	
		this.TEMPT =  payOff[0];
		this.REWARD = payOff[1];
		this.PUNISH = payOff[2];
		this.SUCKER = payOff[3];
				
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
		Agent ags = new AgentStrategies(him, agentBeliefs);
		char agentAction = 0;

		
		this.currentTournament = currentTournament;
		
		String agentStrategy = agentStrategies[requestingAgentID];
		String opponentStrategy = agentStrategies[opponentID];
		
		// Get action of agent based on strategy
		if (agentStrategy.equalsIgnoreCase(NAIVE_COOPERATOR))
			agentAction = ags.cooperateAll();
		if (agentStrategy.equalsIgnoreCase(NAIVE_DEFECTOR))
			agentAction = ags.defectAll();
		if (agentStrategy.equalsIgnoreCase(ADVANCE_COOPERATOR))
			agentAction = ags.advanceCooperator(requestingAgentID,agentStrategy,
					opponentID, opponentStrategy, currentTournament, currentRound, infoRequestOption);
		if ((agentStrategy.equalsIgnoreCase(ADVANCE_DEFECTOR))||(agentStrategy.equalsIgnoreCase(ADVANCE_EXPLOITER)))
			agentAction = ags.advanceDefector(requestingAgentID,agentStrategy,
					opponentID, opponentStrategy, currentTournament, currentRound, infoRequestOption);
		if (agentStrategy.equalsIgnoreCase(DUMMY))
			agentAction = 'A';
		

		return agentAction;

	}
	
	
	char advanceDefector(int requestingAgentID, String agentStrategy,
			int opponentID,String opponentStrategy, int currentTournament, int currentRound, int infoRequestOption) {
		
		return 0;
	}


	char defectAll() {
		return 0;
	}


	char cooperateAll() {
		return 0;
	}


	char advanceCooperator(int requestingAgentID, String agentStrategy,
			int opponentID,String opponentStrategy,  int currentTournament, int currentRound, int infoRequestOption) {
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
	protected void setBeliefs() {

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
