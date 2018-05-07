package simulationEngine;

import java.io.IOException;
import java.util.ArrayList;
//import java.util.Random;
import agents.Agent;
import gui.GUI_Simulation;
import historicalInfo.HistoricalInfoMgr;
import settings.InputValidator;

public class SimulationManager {
	
	// Private Variables
	private String [] agentStrategies;
	private int numOfTournaments, numOfAgents, currentExperimentID, evolutionModelIndex;
	private float[] payOff;
	private char[] agentsAction;
	private final char COOPERATE = 'C';
	private final char DEFECT = 'D';
	private final char DUMMY = 'A';
	protected final String ADVANCE_COOPERATOR = "Advanced_C";
	protected final String ADVANCE_DEFECTOR = "Advanced_D";
	private final String NAIVE_DEFECTOR = "Naive_D";
	private final String NAIVE_COOPERATOR = "Naive_C";
	private String DUMMY_STRATEGY = "Dummy";
	
	// Parameters and Fields
	SimReport report;
	GUI_Simulation simLog;
	HistoricalInfoMgr him;
	ArrayList<Agent> agents;
	ArrayList<Integer> homeList = new ArrayList<Integer>();
	ArrayList<Integer> awayList = new ArrayList<Integer>();

	// Constructor
	public SimulationManager(int currentExperimentID, InputValidator input,GUI_Simulation simLog, ArrayList<Agent> agents, HistoricalInfoMgr him) {
		
		// Initialize Parameters
		this.agents = agents;
		this.agentStrategies = input.agentStrategies;
		this.numOfAgents = Math.round(input.numOfAgents);
		this.numOfTournaments = input.numOfTournament;
		this.currentExperimentID = currentExperimentID;
		this.payOff = input.payOff;
		this.simLog = simLog;
		this.him = him;
		this.evolutionModelIndex = input.getEvolutionModel();
		report = new SimReport(this.simLog, this.him);
		
		
	}
	
	
	
	/**
	 * runSimulation() method schedules the agents to begin simulation experiment
	 * 
	 */
	public void runSimulation(){
		report.printExperiment(currentExperimentID);
		report.printExperimentStats(currentExperimentID);
		
		// transfer control to tournament Manager
		tournamentManager();
		
	}

	
	
	
	/**
	 * tournamentManager method generates for each tournament a groups for the agents and handles their 
	 * performance in the current tournament.
	 * 
	 */
	
	protected void tournamentManager() {
		for (int currentTournament = 0; currentTournament < numOfTournaments; currentTournament++) {

			report.printTournamentResults(currentTournament);

			int totalRounds = (numOfAgents - 1); 
			int matchesPerRound = numOfAgents / 2; 

			// Group players into two for fair matching
			for (int j = 0; j < matchesPerRound; j++) {
				homeList.add(j);
				awayList.add(j);
			}
			
			// Round Manager
			roundMgr(currentTournament, totalRounds, matchesPerRound);
					
			try {
				him.getTournamentStats(currentTournament, agents);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			// Agents evolve before next tournament
			applyEvolutionModel();
			
			
			
			
	
		}
		
		
		

	}
	
	
	/**
	 * Applies the elimnation policy introduced by Kretz in his study
	 */
	private void applyEvolutionModel() {

		switch(evolutionModelIndex){
			case 1: eliminateOneWithOneReplacement();
			break;
			
			case 2: eliminateTwoWithTwoReplacements();
			break;
			
			case 3: least4AdoptTop4Strategies();
			break;
			
			case 4: least4AdoptTop4SimilarNativeStrategies();
			break;
			
		}
	}




	private void eliminateOneWithOneReplacement() {
				
		// Get agents scores in current tournament
		double []agentScores = him.getAgentScores();		
		
		// Get set temporal score
		double tempScore = agentScores[0];
		int lowestAgentID = 0, highestAgentID = 0;		
		double maxScore = agentScores[0];

		// To determine max and least score and get agents ids
		for(int i=0; i < agentScores.length;i++){
			if( tempScore > agentScores[i]){
				tempScore = agentScores[i];
				lowestAgentID = i;
			}
			
			if( maxScore < agentScores[i]){
				maxScore = agentScores[i];
				highestAgentID = i;
			}
		}
		
		
		// Change least scored agent strategy with most scored agent strategy
		agents.get(lowestAgentID).agentStrategy = agents.get(highestAgentID).agentStrategy;
		
		// New agent acquires half of mother's score.
		agentScores[lowestAgentID] = agentScores[highestAgentID] / 2;
		agentScores[highestAgentID] = agentScores[highestAgentID] / 2;

		
		System.out.println("Lowest score : "+ tempScore +"\t"+ "Agent ID "+lowestAgentID + "Maximum score : "+ maxScore +"\t"+ "Agent ID "+highestAgentID);
		
		System.out.println();
		
	}


	private void eliminateTwoWithTwoReplacements() {
		
		// Left blank intentionally
	}

	
	private void least4AdoptTop4Strategies() {
		String [][]tournamentResults;
		
		// Get Current Tournaments Results
		tournamentResults = him.getTournamentResults();
		
		int j = (tournamentResults.length - 1);

		// Losing agents replace their strategies with winning ones
		for(int i = 0; i < 4; i++){
	
			int agentID = (Integer.parseInt(tournamentResults[i][0].substring(6)) - 1);
			int replaceID = (Integer.parseInt(tournamentResults[j][0].substring(6)) - 1);
			agents.get(agentID).agentStrategy = agents.get(replaceID).agentStrategy;
			System.out.println("Agent : "+ (agentID+1)+" chooses \t Agent : "+(replaceID+1));
			j--;
		}
		
		//Update historical information strategies
		for(int k = 0; k < numOfAgents; k++){
			HistoricalInfoMgr.Strategies[k] = agents.get(k).agentStrategy;
		}		
		
	}
	
	

	private void least4AdoptTop4SimilarNativeStrategies() {
		
		String [][]tournamentResults;
		
		// Get Current Tournaments Results
		tournamentResults = him.getTournamentResults();
		
		

		// Losing agents replace their strategies with winning ones but of similar native strategies
		for(int i = 0; i < 4; i++){
	
			int agentID = (Integer.parseInt(tournamentResults[i][0].substring(6)) - 1);
			int limit = (tournamentResults.length - 5);
			for(int j = (tournamentResults.length - 1); j > limit; j--){
				int replaceID = (Integer.parseInt(tournamentResults[j][0].substring(6)) - 1);
				
				if(((agents.get(agentID).agentStrategy.equalsIgnoreCase(NAIVE_COOPERATOR))||(agents.get(agentID).agentStrategy.equalsIgnoreCase(ADVANCE_COOPERATOR)))&&((agents.get(replaceID).agentStrategy.equalsIgnoreCase(NAIVE_COOPERATOR))||(agents.get(replaceID).agentStrategy.equalsIgnoreCase(ADVANCE_COOPERATOR)))){
					agents.get(agentID).agentStrategy = agents.get(replaceID).agentStrategy;
				}
				
				
				else if(((agents.get(agentID).agentStrategy.equalsIgnoreCase(NAIVE_DEFECTOR))||(agents.get(agentID).agentStrategy.equalsIgnoreCase(ADVANCE_DEFECTOR)))&&((agents.get(replaceID).agentStrategy.equalsIgnoreCase(NAIVE_DEFECTOR))||(agents.get(replaceID).agentStrategy.equalsIgnoreCase(ADVANCE_DEFECTOR)))){
						agents.get(agentID).agentStrategy = agents.get(replaceID).agentStrategy;
				}
				
			}
		}
		
		//Update historical information strategies
		for(int k = 0; k < numOfAgents; k++){
			HistoricalInfoMgr.Strategies[k] = agents.get(k).agentStrategy;
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
			String roundHeader1 =  "\n\nRound " + (round + 1) + "\n" + "---------------------------------\n";
			String roundHeader2 = "\nRound " + (round + 1) + "\n";
			
			report.updateExperimentLog(roundHeader1, roundHeader2);

			
			
			// For each round store the agents' IDs and strategies on Tournament Board  
			for (int j = 0; j < matchesPerRound; j++) {

				int agentID = (homeList.get(j)) - 1;
				int opponentID = (awayList.get(j)) - 1;
				
				/*
					String agentStrategy = agentStrategies[agentID];
					String opponentStrategy = agentStrategies[opponentID];
				*/
				
				String agentStrategy = agents.get(agentID).agentStrategy;
				String opponentStrategy = agents.get(opponentID).agentStrategy;
				
				
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

		// Ignore the Dummy agents
		if(opponentStrategy!= (DUMMY_STRATEGY)){
			
			agentsAction[0] = agents.get(agentID).returnAction(agentID,agents.get(agentID).agentStrategy, opponentID,agents.get(opponentID).agentStrategy,currentTournament, currentRound);
			agentsAction[1] = agents.get(opponentID).returnAction(opponentID,agents.get(opponentID).agentStrategy, agentID, agents.get(agentID).agentStrategy,currentTournament, currentRound);
	
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

		else if ((agentsActions[0] == COOPERATE) && (agentsActions[1] == DEFECT)) {
			agentScore = payOff[3];
			opponentScore = payOff[0];
		}

		else if ((agentsActions[0] == DEFECT) && (agentsActions[1] == COOPERATE)) {
			agentScore = payOff[0];
			opponentScore = payOff[3];
		}

		else if ((agentsActions[0] == DEFECT) && (agentsActions[1] == DEFECT)) {
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
