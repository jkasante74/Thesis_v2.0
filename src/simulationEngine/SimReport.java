package simulationEngine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.JOptionPane;
import gui.GUI_Simulation;
import historicalInfo.HistoricalInfoMgr;

public class SimReport {
	
	GUI_Simulation simLog;
	HistoricalInfoMgr him;
	private final String TOURNAMENTBOARD = "TB/TB.csv";
	private final String FILENOTFOUND = "File not found";
	public static String agentsTournamentStatistics = "";

	
	/**Constructor to initialize
	 * 
	 * @param simLog : Instance of GUI_Simulation
	 * @param him	 : HistoricalInfoMgr  
	 */
	SimReport(GUI_Simulation simLog, HistoricalInfoMgr him){
		this.simLog = simLog;
		this.him = him;
	}
	
	
	public SimReport(){
		
	}
	
	
	/**
	 * printExperiment method reports on a statistics of current Experiment
	 * index agents' payoffs, strategies and positions .
	 * 
	 * @param currentExperimentID
	 *            : Current Tournament index
	 * 
	 */
	
	protected void printExperiment(int currentExperimentID) {

		// Save to TB and signal HIM
		String experimentTitle = "\n\nEXPERIMENT " + (currentExperimentID)
				+ "\n-----------------------\n";
		String experimentTitle2 = "\nEXPERIMENT " + (currentExperimentID) + "\n";
		simLog.txtSim.append(experimentTitle);

		try {
			Files.write(Paths.get(TOURNAMENTBOARD), experimentTitle2.getBytes());
			him.updateLog();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, FILENOTFOUND);
		}

	}
	
	
	
	/**
	 * printTournamentStats method reports on a statistics of agents' payoffs,
	 * strategies and positions for every tournament.
	 * 
	 * @param currentTournamentIndex
	 *            : Current Tournament index
	 * 
	 */
	protected void printTournament(float currentTournamentIndex) {

		String tournamentStats = "\nRound-Robin Tournament " + (currentTournamentIndex + 1) + "\n" + "===================\n";;
		simLog.txtSim.append(tournamentStats);
		
		String tx =  "\nRound-Robin Tournament " + (currentTournamentIndex + 1)+ "\n";
		
		// Store current tournament title in tournament board
		try {
			Files.write(Paths.get(TOURNAMENTBOARD), tx.getBytes());
			him.updateLog();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, FILENOTFOUND);
		}

	}
	
	
	
	protected void updateExperimentLog(String windowLog, String logMsg) {

		simLog.txtSim.append(windowLog);
		
		// Store current tournament title in tournament board
		try {
			Files.write(Paths.get(TOURNAMENTBOARD), logMsg.getBytes());
			him.updateLog();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, FILENOTFOUND);
		}

	}
	
	
	

	/**
	 * getExperimentResults accepts display request from the GUI component and
	 * based on the request returns the appropriate results stored in Historical
	 * Information Repository
	 * 
	 * @param requestOption
	 *            : Query from GUI component
	 * 
	 * @return requestInfo : Response given to GUI component
	 * 
	 */

	public String getExperimentResults(int requestOption) {
		String requestInfo = "";

		switch (requestOption) {
		case 0:
			requestInfo = him.experimentLeaderboard;
			break;

		case 1:
			requestInfo = him.agentsTournamentStatistics;
			break;
		}

		return requestInfo;
	}

/**
 * displayAgentsExperimentStats method displays a statistic of agents performance
 * @param currentExperimentID	: current Experiment index
 */
	protected void displayAgentsExperimentStats(int currentExperimentID) {
		him.displayAgentsExperimentStats(currentExperimentID);
		
	}


	
	
	
	
	

}
