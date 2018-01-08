package simulationEngine;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import agents.Agent;
import gui.GUI_Simulation;
import historicalInfo.HistoricalInfoMgr;
import settings.InputValidator;

public class SimulationManager {
	
	ArrayList<Agent> agents;
	private String [] agentStrategies;
	private int numOfTournaments;
	private int numOfAgents;
	private int currentExperimentID;
	private float[] payOff;
	private char[] agentsAction;
	private final char COOPERATE = 'C';
	private final char DEFECT = 'D';
	private final char DUMMY = 'A';
	private String DUMMY_STRATEGY = "Dummy";
	SimReport report;
	GUI_Simulation simLog;
	HistoricalInfoMgr him;
	Agent agent;
	ArrayList<Object> homeList = new ArrayList<Object>();
	ArrayList<Object> awayList = new ArrayList<Object>();
	
	
	
	public SimulationManager(int currentExperimentID, InputValidator input,GUI_Simulation simLog, ArrayList<Agent> agents, HistoricalInfoMgr him, Agent agent) {
		
		// Initialize Parameters
		this.agents = agents;
		this.agentStrategies = input.agentStrategies;
		this.numOfAgents = Math.round(input.numOfAgents);
		this.numOfTournaments = input.numOfTournament;
		this.currentExperimentID = currentExperimentID;
		this.payOff = input.payOff;
		this.simLog = simLog;
		this.him = him;
		this.agent = agent;
		report = new SimReport(this.simLog, this.him);
		
		
	
	}
	
	
	/**
	 * scheduler method schedules the agents to begin simulation experiment
	 * 
	 */
	public void runSimulation(){
		
		report.printExperiment(currentExperimentID);
		report.displayAgentsExperimentStats(currentExperimentID);
		
		// transfer control to tournament Manager
		tournamentManager();
		
	}

	
	
	
	/**
	 * tournHandler method generates for each tournament a groups for the ageents.
	 * 
	 * @param homeList
	 *            : Group of Agents that forms the first half
	 * @param awayList
	 *            : Group of Agents that forms the last half
	 * 
	 */
	
	protected void tournamentManager() {
		int matchesPerRound = 0;
		for (int currentTournament = 0; currentTournament < numOfTournaments; currentTournament++) {

			report.printTournament(currentTournament);

			int totalRounds = (numOfAgents - 1); 
			 matchesPerRound = numOfAgents / 2; 

			// Group players into two for fair matching
			for (int j = 0; j < matchesPerRound; j++) {
				homeList.add(agents.get(j));
				awayList.add(agents.get(j));
			}
			
			// Round Manager
			roundMgr(currentTournament, totalRounds, matchesPerRound);
					
			try {
				him.displayAgentsTournamentStats(currentTournament);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
	
		}		

	}
	
	
	
	/**
	 * roundMgr method initiates the function of managing all agentsTotal
	 * activities in the current round
	 * 
	 * @param currentTournament
	 *            : Current Tournament index
	 * @param totalRounds
	 *            : Total number of rounds in current Tounrament
	 * @param matchesPerRound
	 *            : Total number of matches to be played in every round
	 * @param homeList
	 *            : Group of Agents that forms the first half
	 * @param awayList
	 *            : Group of Agents that forms the last half
	 * 
	 */
	
	public void roundMgr(int currentTournament, int totalRounds, int matchesPerRound) {

		for (int round = 0; round < totalRounds; round++) {

			// Divide agents into two groups to play one on one
			for (int match = 0; match < matchesPerRound; match++) {
				int home = (round + match) % (numOfAgents - 1);
				int away = (numOfAgents - 1 - match + round) % (numOfAgents - 1);

				if (match == 0)
					away = numOfAgents - 1;

				homeList.set(match, (home + 1));
				awayList.set(match, (away + 1));
			}
			
			// Update round Information
			String text =  "\n\nRound " + (round + 1) + "\n" + "---------------------------------\n";
			String textx = "\nRound " + (round + 1) + "\n";
			
			report.updateExperimentLog(text, textx);

			
			
			// For each round store the agents' IDs and strategies on Tournament Board  
			for (int j = 0; j < matchesPerRound; j++) {

				int agentID = (int) (homeList.get(j)) - 1;
				int opponentID = (int) (awayList.get(j)) - 1;

				String agentStrategy = agentStrategies[agentID];
				String opponentStrategy = agentStrategies[opponentID];
					
				if(!opponentStrategy.equalsIgnoreCase(DUMMY_STRATEGY))
				{	

					// Update matched agent ID in report and log
					String matchedAgentID =  "Agent " + (agentID + 1) + "\t \t vrs \t Agent " + (opponentID + 1) + "\n";
					String matchedAgentID2 = "\nAgent " + homeList.get(j) + "\t \t vrs \t Agent " + awayList.get(j) + "\n";
					report.updateExperimentLog(matchedAgentID, matchedAgentID2);
					
					
					// Update matched strategies in report and log
					String matchedAgentStrategies = agentStrategy + "\t \t vrs \t " + opponentStrategy + "\n";
					report.updateExperimentLog(matchedAgentStrategies, matchedAgentStrategies);
				
					
					// Transfer control to MatchManager
					matchMgr(agentStrategy, opponentStrategy, agentID, opponentID, currentTournament, round);
				}
				

			}
		}
	}

	
	
	/**
	 * matchMgr method initiates the function of managing all agents activities
	 * in various matches in current round
	 * 
	 * @param agentStrategy
	 *            : Strategy of agent in current Experiment
	 * @param opponentStrategy
	 *            : Strategy of opponent in current Experiment
	 * @param agentID
	 *            : Agent id in current experiment
	 * @param opponentID
	 *            : Opponent id in current experiment
	 * @param currentTournament
	 *            : current Tournament
	 * @param currentRound
	 *            : Current Round
	 */
	
	protected void matchMgr(String agentStrategy,
			String opponentStrategy, int agentID, int opponentID,
			int currentTournament, int currentRound) {
		agentsAction = new char[2];
		Agent ic = agents.get(agentID);
		Agent rc = agents.get(opponentID);

		if(opponentStrategy!= (DUMMY_STRATEGY)){
			agentsAction[0] = agent.getAgentAction(agentID, opponentID,currentTournament, currentRound);
			agentsAction[1] = agent.getAgentAction(opponentID,agentID, currentTournament, currentRound);
	
			// Update actions of matched agents
			String text = agentsAction[0] + "\t \t vrs \t "+ agentsAction[1] + "\n";
			report.updateExperimentLog(text, text);
	
			
			float[] matchScores = calcMatchedAgentsScores(agentsAction);
	
			him.updateMatchedAgentsScores(agentID, opponentID, matchScores);
	
			him.updateAgActionsInReposiory(currentTournament, agentID, opponentID, agentsAction);
		}
	}

	

	/**
	 * calcMatchedAgentsScores method calculates the payoffs associated with the
	 * actions of the matched agents	 
	 * @param agentsActions
	 *            : Selected actions of both player and his opponent
	 * 
	 * @return matchScores : Scores for both player and his opponent based on
	 *         their actions
	 * 
	 */
	private float[] calcMatchedAgentsScores(char[] agentsActions) {

		float agentScore = 0, opponentScore = 0;
		float[] matchScores = new float[2];

		if ((agentsActions[0] == COOPERATE) && (agentsActions[1] == COOPERATE)) {
			agentScore = payOff[1];
			opponentScore = payOff[1];
		}

		if ((agentsActions[0] == COOPERATE) && (agentsActions[1] == DEFECT)) {
			agentScore = payOff[3];
			opponentScore = payOff[0];
		}

		if ((agentsActions[0] == DEFECT) && (agentsActions[1] == COOPERATE)) {
			agentScore = payOff[0];
			opponentScore = payOff[3];
		}

		if ((agentsActions[0] == DEFECT) && (agentsActions[1] == DEFECT)) {
			agentScore = payOff[2];
			opponentScore = payOff[2];
		}
		
		// No score given to agents matched against Dummies
		if (agentsActions[1] == DUMMY) {
			agentScore = 0;
			opponentScore = 0;
		}
		
		matchScores[0] = agentScore;
		matchScores[1] = opponentScore;

		if (agentsActions[1] != DUMMY) {
			String calculatedScores =  matchScores[0] + "\t \t vrs \t " + matchScores[1] + "\n\n";
			String calculatedScores2 =  matchScores[0] + "\t \t vrs \t " + matchScores[1] + "\n";
			
			// Update scores of matched agents
			report.updateExperimentLog(calculatedScores, calculatedScores2);

		}
		 
		return matchScores;

	}
	
	
	
	
	
	
	
	
	
	

}
