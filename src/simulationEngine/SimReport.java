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
	
	/**Constructor to initialize
	 * 
	 * @param simLog : Instance of GUI_Simulation
	 * @param him2 
	 */
	SimReport(GUI_Simulation simLog, HistoricalInfoMgr him){
		this.simLog = simLog;
		this.him = him;
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
			him.updateLog(experimentTitle2);
		//	HIM.startExp(currentExperimentID);
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
			him.updateLog(tournamentStats);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, FILENOTFOUND);
		}

	}
	
	
	
	

}
