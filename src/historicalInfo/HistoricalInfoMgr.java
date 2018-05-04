package historicalInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import javax.swing.JOptionPane;
import agents.Agent;
import gui.AreaChart;
import gui.BarChart;
import gui.BarChart3DDemo4;
import gui.LineChart;

public class HistoricalInfoMgr {

	// Private variables
	private static String TOURNAMENT_BOARD_FILE = "TB/TB.csv";
	private static String SIMULATION_STATS_FILE = "HIR/SimStats.csv";
	private static String STRATEGY_STATS_FILE = "HIR/StrategyStats.csv";
	private static String STRATEGY_NUM_GRAPH_FILE = "HIR/StrategyNumGraph.txt";
	private static String PAYOFF_GRAPH_FILE = "HIR/PayOffGraph.txt";
	private static String COOPERATIONS_GRAPH_FILE = "HIR/CooperationsGraph.csv";
	private static String SIMULATION_LOG_FILE = "HIR/SimLog.csv";
	private static String SIMULATION_LEADERBOARD_FILE = "HIR/SimLeaderBoard.csv";
	private static String CHARTS_FILE = "HIR/chartInfo.csv";
	private static char DEFECT_ACTION = 'D';
	private static String FILE_NOT_FOUND = "File not found";
	private String requestLimitOptions;
	private float[] experimentPayOff;
	private double []strategyPayOff = new double[4];

	private String chartsInfo = "", agentTournamentStat = "", tournamentBoardInfo = "",
			currentExperimentResults = "", DUMMY = "Dummy";
	private double sum = 0, min = 0, max = 0;
	private int numOfAgents, numOfTournament, currentExperimentID;
	private float uncertaintyLevel, communicationCost;
	public static String[] Strategies;

	// Public Fields
	public static String agentsTournamentStatistics = "", experimentLeaderboard = "", strategyStats="",
						 strategyNumGraphs="", payoffGraphs="", cooperationsGraphs="";
	public double[] agentScores;
	Agent agent;

	// Constructor
	public HistoricalInfoMgr(int numOfAgents, int[] agentRequestLimit, float uncertaintyLevel, int numOfTournament, float[] experimentPayOff, String[] Strategies, int currentExperimentID, float communicationCost) {

		this.numOfAgents = numOfAgents;
		agentScores = new double[numOfAgents];
		this.numOfTournament = numOfTournament;
		this.uncertaintyLevel = uncertaintyLevel;
		HIR.agentActionsDbase = new char[numOfTournament][numOfAgents][numOfAgents];
		HIR.agentActs = new String[numOfAgents];
		HIR.agentsRequestLimit = agentRequestLimit;
		this.experimentPayOff = experimentPayOff;
		HistoricalInfoMgr.Strategies = Strategies;
		this.currentExperimentID = currentExperimentID;
		this.communicationCost = communicationCost;
	}

	public HistoricalInfoMgr() {

	}

	/**
	 * updateLog method after every match reads from the Tournament Board and
	 * updates the SimLog data file with agents simulation actions in HIR
	 * 
	 * 
	 * @throws IOException
	 *             : Throw an input output exception when SimLog file is not
	 *             found
	 */
	public void updateLog() throws IOException { /// unfound

		String expTitle = readTounamentBoard();

		tournamentBoardInfo = tournamentBoardInfo + expTitle + "\n";
		Files.write(Paths.get(SIMULATION_LOG_FILE), tournamentBoardInfo.getBytes());

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
	private String readTounamentBoard() {
		String boardInfo = "";

		try {
			File f = new File(TOURNAMENT_BOARD_FILE);
			BufferedReader b = new BufferedReader(new FileReader(f));
			String readLine = "";
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

	public void updateAgActionsInReposiory(int currentTournamentIndex, int requestingAgentID, int opponentID,
			char[] agentsAction) {

		if (agentsAction.length == 2) {

			// Store agents' actions in HIR based on ID
			HIR.agentActionsDbase[currentTournamentIndex][opponentID][requestingAgentID] = agentsAction[0];
			HIR.agentActionsDbase[currentTournamentIndex][requestingAgentID][opponentID] = agentsAction[1];

			// Store agents' actions in HIR based on time taken
			if (HIR.agentActs[requestingAgentID] == null)
				HIR.agentActs[requestingAgentID] = String.valueOf(agentsAction[0]);
			else
				HIR.agentActs[requestingAgentID] = HIR.agentActs[requestingAgentID] + String.valueOf(agentsAction[0]);

			if (HIR.agentActs[opponentID] == null)
				HIR.agentActs[opponentID] = String.valueOf(agentsAction[1]);

			else
				HIR.agentActs[opponentID] = HIR.agentActs[opponentID] + String.valueOf(agentsAction[1]);
		}
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

	public void updateMatchedAgentsScores(int requestingAgentID, int opponentID, float[] matchScores) {
		agentScores[requestingAgentID] += matchScores[0];
		agentScores[opponentID] += matchScores[1];
	}

	/**
	 * requestOppPastInfo accepts information request about an opponent from the
	 * agent and based on limitations return the requested info after applying
	 * uncertainty limit
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
	public String requestOppPastInfo(int requestingAgentID, int opponentID, int requestOption) { // Not on class diagram
		String opponentPastInfo = "";

		// If agent has exceeded limit
		if (HIR.agentsRequestLimit[requestingAgentID] == 0) {
			Agent.infoAcquired = false;
		}

		// Honour request based on type of info required
		else {
			Agent.infoAcquired = true;
			switch (requestOption) {

			case 0:
				opponentPastInfo = getOppFirstAction(requestingAgentID, opponentID);
				agentScores[requestingAgentID] = agentScores[requestingAgentID] - communicationCost;
				break;

			case 1:
				opponentPastInfo = getOpponentFirstDefection(requestingAgentID, opponentID);
				agentScores[requestingAgentID] = agentScores[requestingAgentID] - communicationCost;

				break;

			case 2:
				opponentPastInfo = getOpponentInfo(requestingAgentID, opponentID);
				agentScores[requestingAgentID] = agentScores[requestingAgentID] - communicationCost;
				break;

			case 3:
				opponentPastInfo = getOpponentActionsInRandomTournament(requestingAgentID, opponentID);
				agentScores[requestingAgentID] = agentScores[requestingAgentID] - communicationCost;
				break;

			case 4:
				opponentPastInfo = get2ndLevelAction(requestingAgentID, opponentID);
				agentScores[requestingAgentID] = agentScores[requestingAgentID] - communicationCost;
				break;
			

			case 5:
				opponentPastInfo = getOpponentStrategy(requestingAgentID, opponentID);
				agentScores[requestingAgentID] = agentScores[requestingAgentID] - communicationCost;
				break;
			}

			HIR.agentsRequestLimit[requestingAgentID] -= 1;
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
	private String get2ndLevelAction(int requestingAgentID, int opponentID) {

		String opponentPastInfo = "", pastInfoAfterUncertainty = "";

		for (int i = 0; i < numOfTournament; i++) {
			for (int j = 0; j < numOfAgents; j++)
				opponentPastInfo = opponentPastInfo + HIR.agentActionsDbase[i][opponentID][j];
		}
		pastInfoAfterUncertainty = applyuncertaintyLevel(opponentPastInfo);
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
	private String getOppFirstAction(int requestingAgentID, int opponentID) {

		String opponentPastInfo = "", pastInfoAfterUncertainty = "";

		if (HIR.agentActs[opponentID] != null) {
			opponentPastInfo = String.valueOf(HIR.agentActs[opponentID].substring(0, 1));
			pastInfoAfterUncertainty = applyuncertaintyLevel(opponentPastInfo);

		}
		return pastInfoAfterUncertainty;
	}

	/**
	 * getOpponentInfo method returns all past actions of the current
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
	private String getOpponentInfo(int requestingAgentID, int opponentID) {
		String opponentPastInfo = "", pastInfoAfterUncertainty = "";
		if (HIR.agentActs[opponentID] != null) {
			opponentPastInfo = HIR.agentActs[opponentID];
			pastInfoAfterUncertainty = applyuncertaintyLevel(opponentPastInfo);
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
	private String getOpponentFirstDefection(int requestingAgentID, int opponentID) {

		String oppActions = "";
		String pastInfoAfterUncertainty = "";
		String opponentPastInfo = "";
		oppActions = HIR.agentActs[opponentID];

		// Determine the first time opponent defected
		for (int i = 0; i < oppActions.length(); i++) {

			if (oppActions.charAt(i) == DEFECT_ACTION) {
				opponentPastInfo = String.valueOf(i);
				break;
			}

			else {
				opponentPastInfo = "0";
			}

		}
		pastInfoAfterUncertainty = applyuncertaintyLevel(opponentPastInfo);

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
	private String getOpponentActionsInRandomTournament(int requestingAgentID, int opponentID) {
		String opponentPastInfo = "", pastInfoAfterUncertainty = "";
		
		// Generate Random tournament
		Random rand = new Random();
				
		// Request agent to submit random tournament index
		int randomTournamentIndex = rand.nextInt((numOfTournament - 0) + 1);

		// Get past action of opponent in tournament
		for (int j = 0; j < numOfAgents; j++) {
			if (opponentPastInfo == null)
				opponentPastInfo = String.valueOf(HIR.agentActionsDbase[randomTournamentIndex][j][opponentID]);
			else
				opponentPastInfo = opponentPastInfo + HIR.agentActionsDbase[randomTournamentIndex][j][opponentID];
		}

		pastInfoAfterUncertainty = applyuncertaintyLevel(opponentPastInfo);

		return pastInfoAfterUncertainty;
	}

	/**
	 * getOpponentStrategy method returns strategy of the current opponent 
	 * @param requestingAgentID
	 * @param opponentID
	 * @return
	 */
	private String getOpponentStrategy(int requestingAgentID, int opponentID) {
		//JOptionPane.showMessageDialog(null, Strategies[opponentID]);
		
		return Strategies[opponentID];
	}
	
	/**
	 * applyuncertaintyLevel method applies the uncertainty limit on queried
	 * opponent past information
	 * 
	 * @param opponentPastInfo
	 *            : Information on opponent's past actions before uncertainty
	 *            limit is applied
	 * @return pastInfoAfterUncertainty : Requested Information on opponent's
	 *         past actions after uncertainty limit application
	 */
	private String applyuncertaintyLevel(String opponentPastInfo) {
		String pastInfoAfterUncertainty = opponentPastInfo.substring(0,
				(int) (Math.ceil(opponentPastInfo.length() * uncertaintyLevel)));

		return pastInfoAfterUncertainty;
	}

	/**
	 * displayAgentsTournamentPerformance method performs the arrangement of the
	 * player's tournament standing at the end of each tournament in a leader
	 * board format showing the following;
	 * <ul>
	 * <li>player name / id
	 * <li>player's chosen strategy at the start of the tournament
	 * <li>Agent's final score </u>
	 * 
	 * @return experimentLeaderboard : Agents actions, strategy and pay-offs
	 * 
	 * @throws IOException
	 * 
	 */
	public String displayAgentsTournamentPerformance(int currentExpIndex, ArrayList<Agent> agents) throws IOException {
		experimentLeaderboard = experimentLeaderboard + "\nExperiment " + currentExpIndex
				+ " Simulation Leaderboard\n=========================\n"
				+ "Agent_ID \t Strategy \tPay_Off    Cooperations       "
				+ "Defections \n---------------------------------------------------\n";

		currentExperimentResults = currentExperimentResults + "\nExperiment " + currentExpIndex
				+ "\n==========================\n" + "Agent_ID \t Strategy \tPay_Off    Cooperations "
				+ "Defections \n\n";
		currentExperimentResults = currentExperimentResults + "Number of Tournament : ," + numOfTournament + "\n";
		currentExperimentResults = currentExperimentResults + "Payoffs : ,T = " + experimentPayOff[0] + ", R = " + experimentPayOff[1]
				+ ", P = " + experimentPayOff[2] + ", S = " + experimentPayOff[3] + "\n";
		currentExperimentResults = currentExperimentResults + "Uncertainty Level : ," + uncertaintyLevel + "\n";
		currentExperimentResults = currentExperimentResults + "Request Limit : ," + requestLimitOptions + "\n";
		currentExperimentResults = currentExperimentResults + "\n\n Agent_ID, Strategy , Pay_Off , Cooperations , "
				+ "Defections \n";
		
		HIR.data = new String[HIR.agentsRequestLimit.length][3];

		for (int i = 0; i < (Strategies.length); i++) {
			HIR.data[i][0] = "Agent " + (i + 1);
			HIR.data[i][1] = agents.get(i).agentStrategy;
			HIR.data[i][2] = String.valueOf((agentScores[i]));
		}

		// Arrange performance based on total scores
		Arrays.sort(HIR.data, new Comparator<String[]>() {
			@Override
			public int compare(final String[] entry1, final String[] entry2) {
				final String time1 = entry1[2];
				final String time2 = entry2[2];
				return Float.valueOf(time1).compareTo(Float.valueOf(time2));
			}
		});

		for (int i = HIR.data.length - 1; i >= 0; i--) {
			if (HIR.data[i][1] != DUMMY) {
				experimentLeaderboard = experimentLeaderboard + HIR.data[i][0] + "\t" + HIR.data[i][1] + "\t"
						+ HIR.data[i][2] + "\t" + numOfPastActions(HIR.data[i][0], 1) + "\t"
						+ numOfPastActions(HIR.data[i][0], 2) + "\n \n";

				currentExperimentResults = currentExperimentResults + HIR.data[i][0] + "," + HIR.data[i][1] + ","
						+ HIR.data[i][2] + "," + numOfPastActions(HIR.data[i][0], 1) + ","
						+ numOfPastActions(HIR.data[i][0], 2) + "\n \n";
				;
			}
		}

		updateHistoricalRepository(currentExperimentResults);
		return experimentLeaderboard;

	}

	/**
	 * updateHistoricalRepository performs the function of saving the
	 * currentExperimentResults from every experiment into a text file.
	 * 
	 * @param leaderboard2
	 *            : updated leadership board
	 * @throws IOException
	 *             exception
	 * 
	 */
	private void updateHistoricalRepository(String currentExperimentResults) throws IOException {

		Files.write(Paths.get(SIMULATION_LEADERBOARD_FILE), currentExperimentResults.getBytes());
		Files.write(Paths.get(STRATEGY_NUM_GRAPH_FILE), strategyNumGraphs.getBytes()); 
		Files.write(Paths.get(PAYOFF_GRAPH_FILE), payoffGraphs.getBytes()); 

		
	}

	/**
	 * numOfPastActions retuns the number of cooperative actions performed by
	 * the current agent
	 * 
	 * @param agentName : Requesting agent name
	 * @return
	 */
	private int numOfPastActions(String agentName, int option) { // Not in class diagram
		int numOfDefections = 0, numOfCooperations = 0;

		// Get Opponent Id
		int agentID = Integer.parseInt(agentName.substring(6));

		// Acquire past opponent action
		String agentPastInformation = HIR.agentActs[(agentID - 1)];

		for (int i = 0; i < agentPastInformation.length(); i++) {
			if (agentPastInformation.charAt(i) == DEFECT_ACTION)
				numOfDefections++;
			else if (agentPastInformation.charAt(i) == 'C')
				numOfCooperations++;

		}

		// Return total number of actions based on request type
		if (option == 1)
			return numOfCooperations;
		else
			return numOfDefections;
	}

	/**
	 * getTournamentStats method develops a statistics of players
	 * performance after every tournament in the current experiment.Statistics
	 * will indicate the following;
	 * <ul>
	 * <li>player name / id
	 * <li>player's chosen strategy at the start of the tournament
	 * <li>Agent's final tournament payoff
	 * <li>Tournament Statistics [Maximum, Average, Minimum payoff]
	 * </ul>
	 * 
	 * @throws IOException
	 * 
	 * 
	 */
	public void getTournamentStats(int currentTournamentIndex, ArrayList<Agent> agents) throws IOException {

		sum = 0;
		agentsTournamentStatistics = agentsTournamentStatistics + "\n\n\nTounament " + (currentTournamentIndex + 1)
				+ "\n-----------------------\n";
		agentTournamentStat = agentTournamentStat + "\n\n\nTounament " + (currentTournamentIndex + 1)
				+ "\n-----------------------\n";
		HIR.data = new String[HIR.agentsRequestLimit.length][3];

		// update agents' tournament information
		for (int i = 0; i < agents.size(); i++) {
			HIR.data[i][0] = "Agent " + (i + 1);
			HIR.data[i][1] = agents.get(i).agentStrategy;
		//	Strategies[i] = agents.get(i).agentStrategy;
			HIR.data[i][2] = String.valueOf((agentScores[i]));

			// store parameters in chart for later display
			getChartDataset(currentTournamentIndex, i, HIR.data);
		}
		
		setStrategyStats(HIR.data, currentTournamentIndex + 1, agentScores);

		// Arrange performance based on total Experiment/Tournament scores
		Arrays.sort(HIR.data, new Comparator<String[]>() {
			@Override
			public int compare(final String[] entry1, final String[] entry2) {
				final String time1 = entry1[2];
				final String time2 = entry2[2];
				return Float.valueOf(time1).compareTo(Float.valueOf(time2));
			}
		});

		// Arrange information in descending order and calculate statistics
		for (int i = HIR.data.length - 1; i >= 0; i--) {
			if (HIR.data[i][1] != DUMMY) {
				agentsTournamentStatistics = agentsTournamentStatistics + HIR.data[i][0] + "\t" + HIR.data[i][1] + "\t"
						+ HIR.data[i][2] + "\n";
				agentTournamentStat = agentTournamentStat + HIR.data[i][0] + "," + HIR.data[i][1] + ","
						+ HIR.data[i][2] + "\n";

				sum = sum + Double.parseDouble(HIR.data[i][2]);
				min = Double.parseDouble(HIR.data[0][2]);
				max = Double.parseDouble(HIR.data[(HIR.data.length - 1)][2]);

			}
		}

		agentsTournamentStatistics = agentsTournamentStatistics + "\n" + "Statistics \n Min : " + min + "\nAverage : "
				+ (sum / HIR.data.length) + "\nMax : " + max;
		agentTournamentStat = agentTournamentStat + "\n" + "Statisticsts \n Min : " + "," + min + "\nAverage : " + ","
				+ (sum / HIR.data.length) + "\nMax : " + "," + max;

		// save Stats
		Files.write(Paths.get(SIMULATION_STATS_FILE), agentTournamentStat.getBytes());
		Files.write(Paths.get(STRATEGY_STATS_FILE), strategyStats.getBytes());
		
	}

	

	private void setStrategyStats(String[][] data, int currentTournamentIndex, double []agentScores) {
		
		strategyStats = strategyStats + "Tournament "+currentTournamentIndex+"\n------------------------";
		int []totalStrategyNum = new int[4];
		
		for(int i = 0; i < HIR.data.length; i++){
			
			switch(HIR.data[i][1]){
				case "Advanced_C":
					totalStrategyNum[0]++;
					strategyPayOff[0] = strategyPayOff[0] +  agentScores[i];
					break;
					
				case "Advanced_D":
					totalStrategyNum[1]++;
					strategyPayOff[1] = strategyPayOff[1] +  agentScores[i];
					break;
					
					
				case "Naive_C":
					totalStrategyNum[2] ++;
					strategyPayOff[2] = strategyPayOff[2] +  agentScores[i];
					break;
					
				case "Naive_D":
					totalStrategyNum[3] ++;
					strategyPayOff[3] = strategyPayOff[3] +  agentScores[i];
					break;
				
			}
			
		}
		
		// Store information
		strategyStats = strategyStats + "\n" + "Advanced_C,"+ totalStrategyNum[0]+","+strategyPayOff[0]+"\n"+
				"Advanced_D,"+ totalStrategyNum[1]+","+strategyPayOff[1]+"\n"+
				"Naive_C,"+ totalStrategyNum[2]+","+strategyPayOff[2]+"\n"+
				"Naive_D,"+ totalStrategyNum[3]+","+strategyPayOff[3]+"\n \n";
		
		if(currentTournamentIndex==numOfTournament){
			//JOptionPane.showMessageDialog(null, experimentPayOff[1]+","+ experimentPayOff[3]);
			strategyNumGraphs = strategyNumGraphs + "\n" + experimentPayOff[0]+","+ experimentPayOff[2]+","+totalStrategyNum[0]+","+ totalStrategyNum[1]+","+totalStrategyNum[2]+","+totalStrategyNum[3];
			payoffGraphs = payoffGraphs + "\n" + experimentPayOff[0]+","+ experimentPayOff[2]+","+strategyPayOff[0]+","+ strategyPayOff[1]+","+strategyPayOff[2]+","+strategyPayOff[3];

		}
		
		
	}

	
	
	
	/**
	 * getChartDataset stores chart dataset to file for later use in making
	 * different charts
	 * 
	 * @param currentTournamentIndex
	 *            : Current tournament number
	 * @param i
	 *            : agent ID
	 * @param data
	 *            : Stored Agent's id, and total experiment payoffs
	 * @throws IOException
	 */
	private void getChartDataset(int currentTournamentIndex, int i, String[][] data) throws IOException {
		String x_axis = "T " + (currentTournamentIndex + 1);
		chartsInfo = chartsInfo + String.valueOf(currentExperimentID) + "," + HIR.data[i][2] + "," + HIR.data[i][0]
				+ "(" + HIR.data[i][1] + ")" + " ," + HIR.data[i][1] + "," + x_axis + "\n";

		Files.write(Paths.get(CHARTS_FILE), chartsInfo.getBytes());
	}

	/**
	 * getChartDataset reads all the information necessary to make a graph depending
	 * on the experiment query from GUI component
	 * 
	 * @param selectedExperimentIndex
	 *            : index for experiment for which dataset are required to make
	 *            a graph.
	 */
	public static void getChartDataset(int selectedExperimentIndex) {

		ArrayList<String> myAgentStrategies = new ArrayList<String>(Arrays.asList(Strategies));
		
		BarChart.agentStrategies = myAgentStrategies;
		BarChart3DDemo4.agentStrategies = myAgentStrategies;

		readLines(selectedExperimentIndex);

	}

	/// All not on Class Diagram
	
	/**
	 * readLines method locates the chartInfo file in HIR and reads all
	 * information for the requested experiment index and then returns to
	 * getChartDataset method
	 * 
	 * Modified from original code [https://www.mkyong.com/java/how-to-read-file
	 * -from-java-bufferedreader-example/ ]
	 * 
	 * @param currentExperimentIndex
	 *            : Dataset info of requested experiment stored in HIR
	 * @return splited : All information read from HIR/chartInfo required for
	 *         making graphs
	 */
	private static String[][] readLines(int currentExperimentIndex) {

		String[] chartDataset = null;
		try {

			File f = new File(CHARTS_FILE);

			BufferedReader b = new BufferedReader(new FileReader(f));

			String readLine = "";
			// readLine = b.readLine();
			while ((readLine = b.readLine()) != null) {

				chartDataset = readLine.split(",");

				if (chartDataset[0].equalsIgnoreCase(String.valueOf((currentExperimentIndex + 1)))) {
					LineChart.dataset.addValue(Float.parseFloat(chartDataset[1]), chartDataset[2], chartDataset[4]);
					BarChart.dataset.addValue(Float.parseFloat(chartDataset[1]), chartDataset[2], chartDataset[4]);

					BarChart3DDemo4.dataset.addValue(Float.parseFloat(chartDataset[1]), chartDataset[2],
							chartDataset[4]);
					AreaChart.dataset.addValue(Float.parseFloat(chartDataset[1]), chartDataset[2], chartDataset[4]);

				}
			}

			b.close();
		} catch (IOException err) {
			JOptionPane.showMessageDialog(null, FILE_NOT_FOUND);
		}

		return null;
	}

	/**
	 * The clarChartInfo method Cleans up the chartsInfo file to begin a new set
	 * of experiments
	 * 
	 * @throws FileNotFoundException
	 * 
	 */
	public void clearChartInfo() throws FileNotFoundException {
		chartsInfo = "";
		PrintWriter writer = new PrintWriter(CHARTS_FILE);
		writer.print("");
		writer.close();

	}

	public void printExperimentStats(int currentExperimentID) {
		agentsTournamentStatistics = agentsTournamentStatistics + "\nExperiment " + (currentExperimentID)
				+ "\n=================================\n";
		
		strategyStats = strategyStats + "\nExperiment " + (currentExperimentID)
				+ "\n=================================\n";
	}
	
	
	public double getTournamentPayoff(){
		return sum;
	}

	public double[] getAgentScores() {
		
		return agentScores;
	}

	public String[][] getTournamentResults() {
		
		return HIR.data;
	}

}
