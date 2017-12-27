package historicalInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

import agents.Agent;
import agents.AgentStrategies;

public class HistoricalInfoMgr {
	private String TOURNAMENT_BOARD_FILE = "TB/TB.csv";
	private String SIMULATION_LOG_FILE = "HIR/SimLog.csv";
	private String FILE_NOT_FOUND = "File not found";
	public double[] agentScores;
	private int numOfAgents;
	private float uncertaintyLimit;
	//private int[] agentRequestLimit;
	private int numOfTournament;
	Agent agent;
	
	public HistoricalInfoMgr(int numOfAgents, int[] agentRequestLimit, float uncertaintyLimit, int numOfTournament, Agent agent){
		
		this.numOfAgents = numOfAgents;
		agentScores = new double[numOfAgents];
		this.numOfTournament  = numOfTournament;
		this.uncertaintyLimit = uncertaintyLimit;
		//this.agentRequestLimit = agentRequestLimit;
		this.agent = agent;
		HIR.agentActionsDbase = new char[numOfTournament][numOfAgents][numOfAgents];
		HIR.agentActs = new String[numOfAgents];
		HIR.agentsRequestLimit = agentRequestLimit;
	}
	
	
	/**
	 * updateLog method after every match reads from the Tournament Board and
	 * updates the SimLog data structure in HIR
	 * 
	 * @param tournamentBoardInfo
	 *            : Current information stored on TB by the Simulation Manager
	 * 
	 * @throws IOException
	 *             : Throw an input output exception when SimLog file is not
	 *             found
	 */
	public void updateLog(String tournamentBoardInfo) throws IOException {

		String expTitle = readTB();

		tournamentBoardInfo = tournamentBoardInfo + expTitle + "\n";
		Files.write(Paths.get(SIMULATION_LOG_FILE),
				tournamentBoardInfo.getBytes());

	}

	/**
	 * readTB method locates the Tournament Board and reads all information
	 * stored on it by the simulation manager.
	 * 
	 * Modified from original code [https://www.mkyong.com/java/how-to-read-file
	 * -from-java-bufferedreader-example/ ]
	 * 
	 * @return splited : All information read from TB
	 */
	private String readTB() {
		String boardInfo = "";
		
		try {
			File f = new File(TOURNAMENT_BOARD_FILE);
			BufferedReader b = new BufferedReader(new FileReader(f));
			String readLine = "";
			readLine = b.readLine();
			while ((readLine = b.readLine()) != null) {
				boardInfo = readLine;

			}
			b.close();
		} catch (IOException err) {
			JOptionPane.showMessageDialog(null, FILE_NOT_FOUND);
		}
		return boardInfo;

	}

	
	
	/**
	 * updateAgActionsInReposiory method updates all agents actions after every
	 * round of matches after invocation by round manager
	 * 
	 * @param currentTournamentIndex
	 *            : Current Tournament
	 * @param requestingAgentID
	 *            : Agent id
	 * @param opponentID
	 *            : Opponent Agent id
	 * @param agentsAction
	 *            : Actions of both agent and opponent
	 */
	public  void updateAgActionsInReposiory(int currentTournamentIndex,
			int requestingAgentID, int opponentID, char[] agentsAction) {

		// Parameter variables
		char agentMatchAct, opponentMatchAct;
		agentMatchAct = agentsAction[0];
		opponentMatchAct = agentsAction[1];

		// Store agents' actions in HIR based on ID
		HIR.agentActionsDbase[currentTournamentIndex][opponentID][requestingAgentID] = agentMatchAct;
		HIR.agentActionsDbase[currentTournamentIndex][requestingAgentID][opponentID] = opponentMatchAct;

		// Store agents' actions in HIR based on time taken
		if (HIR.agentActs[requestingAgentID] == null)
			HIR.agentActs[requestingAgentID] = String.valueOf(agentMatchAct);
		else
			HIR.agentActs[requestingAgentID] = HIR.agentActs[requestingAgentID]
					+ String.valueOf(agentMatchAct);

		if (HIR.agentActs[opponentID] == null)
			HIR.agentActs[opponentID] = String.valueOf(opponentMatchAct);

		else
			HIR.agentActs[opponentID] = HIR.agentActs[opponentID]
					+ String.valueOf(opponentMatchAct);
	}
	
	
	
	/**
	 * updateMatchedAgentsScores method updates the score for each agent
	 * participating in the current match
	 * 
	 * @param requestingAgentID
	 *            : Requesting agent ID
	 * 
	 * @param opponentID
	 *            : Opponent ID
	 * 
	 * @param matchScores
	 *            : Scores of both agents and opponents
	 */
	public void updateMatchedAgentsScores(int requestingAgentID,
			int opponentID, float[] matchScores) {
		agentScores[requestingAgentID] += matchScores[0];
		agentScores[opponentID] += matchScores[1];
	}

	
	

	public void displayAgentsTournamentStats(int currentTournament) {
	
		
	}
	
	
	
	/**
	 * requestOppPastInfo accepts information request about an opponent from the
	 * agent and based on limitations return the requested info after applying uncertainty
	 * limit
	 * 
	 * @param requestingAgentID
	 *            : Experiment identity for the agent
	 * @param opponentID
	 *            : Experiment identity for the opponent
	 * @param option
	 *            : Approach for information request
	 * 
	 * @return opponentPastInfo : Retrieved information on opponent past action
	 * 
	 */
	public  String requestOppPastInfo(int requestingAgentID,
			int opponentID, int requestOption) {
		String opponentPastInfo = "";

		// If agent has exceeded limit
		if (HIR.agentsRequestLimit[requestingAgentID] == 0) {
			opponentPastInfo = "";
			AgentStrategies.infoAcquired = false;
		}

		// Honour request based on type of info reqired
		else {
			AgentStrategies.infoAcquired = true;
			switch (requestOption) {

			case 0:
				opponentPastInfo = getOppFirstAction(requestingAgentID,
						opponentID);
				break;

			case 1:
				opponentPastInfo = getOpponentFirstDefection(requestingAgentID,
						opponentID);
				break;

			case 2:
				opponentPastInfo = getOpponentPastInfo(requestingAgentID,
						opponentID);
				break;

			case 3:
				opponentPastInfo = getOpponentActionsInRandomTournament(
						requestingAgentID, opponentID);
				break;

			case 4:
				opponentPastInfo = get2ndLevelAction(requestingAgentID,
						opponentID);
				break;
			}

			HIR.agentsRequestLimit[requestingAgentID] = HIR.agentsRequestLimit[requestingAgentID] - 1;
		}
		return opponentPastInfo;
	}

	/**
	 * get2ndLevelAction method returns past actions of the secondary level
	 * opponents that were taken against current opponent.
	 * 
	 * @param requestingAgentID
	 *            : Experiment identity for the agent
	 * @param opponentID
	 *            : Experiment identity for the opponent
	 * @return pastInfoAfterUncertainty : past actions of all previous opponents
	 *         against current opponent after uncertainty limit has been applied
	 */
	private  String get2ndLevelAction(int requestingAgentID,
			int opponentID) {

		String opponentPastInfo = "", pastInfoAfterUncertainty = "";

		for (int i = 0; i < numOfTournament; i++) {
			for (int j = 0; j < numOfAgents; j++)
				opponentPastInfo = opponentPastInfo
						+ HIR.agentActionsDbase[i][opponentID][j];
		}
		pastInfoAfterUncertainty = applyUncertaintyLimit(opponentPastInfo);
		return pastInfoAfterUncertainty;
	}

	/**
	 * getOppFirstAction method returns the first action of current opponent
	 * 
	 * @param requestingAgentID
	 *            : Experiment identity for the agent
	 * @param opponentID
	 *            : Experiment identity for the opponent
	 * 
	 * @return pastInfoAfterUncertainty : opponent past actions after
	 *         uncertainty limit has been applied
	 */
	private  String getOppFirstAction(int requestingAgentID,
			int opponentID) {

		String opponentPastInfo = "", pastInfoAfterUncertainty = "";

		if (HIR.agentActs[opponentID] != null) {
			opponentPastInfo = String.valueOf(HIR.agentActs[opponentID]
					.substring(0, 1));
			pastInfoAfterUncertainty = applyUncertaintyLimit(opponentPastInfo);

		}
		return pastInfoAfterUncertainty;
	}

	
	/**
	 * getOpponentPastInfo method returns all past actions of the current
	 * opponent
	 * 
	 * @param requestingAgentID
	 *            : Experiment identity for the agent
	 * @param opponentID
	 *            : Experiment identity for the opponent
	 * 
	 * @return pastInfoAfterUncertainty : opponent past actions after
	 *         uncertainty limit has been applied
	 */
	private  String getOpponentPastInfo(int requestingAgentID,
			int opponentID) {
		String opponentPastInfo = "", pastInfoAfterUncertainty = "";
		if (HIR.agentActs[opponentID] != null) {
			opponentPastInfo = HIR.agentActs[opponentID];
			pastInfoAfterUncertainty = applyUncertaintyLimit(opponentPastInfo);
		}

		return pastInfoAfterUncertainty;

	}

	/**
	 * getOpponentFirstDefection method returns the first time it defected
	 * 
	 * @param requestingAgentID
	 *            : Experiment identity for the agent
	 * @param opponentID
	 *            : Experiment identity for the opponent
	 * 
	 * @return pastInfoAfterUncertainty : opponent past actions after
	 *         uncertainty limit has been applied
	 */
	private  String getOpponentFirstDefection(int requestingAgentID,
			int opponentID) {

		String oppActions = "";
		String pastInfoAfterUncertainty = "";
		String opponentPastInfo = "";
		oppActions = HIR.agentActs[opponentID];

		// Determine the first time opponent defected
		for (int i = 0; i < oppActions.length(); i++) {

			
			if (oppActions.charAt(i) == 'D') {
				opponentPastInfo = String.valueOf(i);
				break;
			}

			else {
				opponentPastInfo = "0";
			}

		}
		pastInfoAfterUncertainty = opponentPastInfo;

		return pastInfoAfterUncertainty;
	}

	
	/**
	 * getOpponentActionsInRandomTournament method returns past actions of the
	 * opponent from a randomly chosen tournament
	 * 
	 * @param requestingAgentID
	 *            : Experiment identity for the agent
	 * @param opponentID
	 *            : Experiment identity for the opponent
	 * 
	 * @return pastInfoAfterUncertainty opponent past actions after uncertainty
	 *         limit has been applied
	 */
	private  String getOpponentActionsInRandomTournament(
			int requestingAgentID, int opponentID) {
		String opponentPastInfo = "", pastInfoAfterUncertainty = "";

		// Request agent to submit random tournament index
		int randomTournamentIndex = agent.getRandomTournament();
		
		// Get past action of opponent in tournament
		for (int j = 0; j < numOfAgents; j++) {
			if (opponentPastInfo == null)
				opponentPastInfo = String
						.valueOf(HIR.agentActionsDbase[randomTournamentIndex][j][opponentID]);
			else
				opponentPastInfo = opponentPastInfo
						+ HIR.agentActionsDbase[randomTournamentIndex][j][opponentID];
		}

		pastInfoAfterUncertainty = applyUncertaintyLimit(opponentPastInfo);

		return pastInfoAfterUncertainty;
	}

	
	
	
	/**
	 * applyUncertaintyLimit method applies the uncertainty limit on queried
	 * opponent past information
	 * 
	 * @param opponentPastInfo
	 *            : Information on opponent's past actions before
	 *            uncertainty limit is applied
	 * @return pastInfoAfterUncertainty : Requested Information on opponent's
	 *         past actions after uncertainty limit application
	 */
	private String applyUncertaintyLimit(String opponentPastInfo) {
		String pastInfoAfterUncertainty = opponentPastInfo.substring(0,
				(int) ((opponentPastInfo.length()) * uncertaintyLimit));

		return pastInfoAfterUncertainty;
	}

	
	
	
	

}
