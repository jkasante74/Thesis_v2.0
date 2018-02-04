package settings;

import java.io.IOException;
import java.util.ArrayList;


import agents.Agent;
import gui.BarChart;
import gui.GUI;
import gui.GUI_Simulation;
import historicalInfo.HistoricalInfoMgr;
import simulationEngine.SimulationManager;

/**
 * SetupManager sets up and controls the configuration 
 * values required to execute the simulation experiment
 * 
 * @author jonathanasante
 * 
 */
public class SetupManager {

	GUI_Simulation simLog;
	private final String ACTIVE_STATUS = "active"; 

	// Constructor
	public SetupManager(InputValidator input, GUI_Simulation simLog) {
		
		this.simLog = simLog;
		
		// Begin experimentation.
		for(int currentExperimentID = 1; currentExperimentID < input.numOfExperiment; currentExperimentID++){
			
			input.setCurrentExperiment(currentExperimentID);
			
			HistoricalInfoMgr him = new HistoricalInfoMgr(Math.round(input.numOfAgents), input.agentRequestLimit, input.getUncertaintyLimit(), input.getnumOfTournaments(), input.payOff, input.agentStrategies, currentExperimentID);
			
			
			// Create the required number of agents for this experiment
			ArrayList<Agent> agents = new ArrayList<Agent>();
			for(int i = 0; i < input.numOfAgents; i++){		 
				agents.add(new Agent((i+1), input.agentStrategies[i], Math.round(input.infoRequestOption)));
			}
			
			// Advanced agents create their beliefs
			for(int i = 0; i < input.numOfAgents; i++){		 
				agents.get(i).setBeliefs(i, agents.get(i).agentStrategy, agents.size(),input.payOff, him);
			}
			
			
			//Begin the simulation of current experiment
			SimulationManager sim = new SimulationManager(currentExperimentID, input,simLog, agents, him);
			
			sim.runSimulation();
			
			// Show leadership board
			try {
				him.displayAgentsTournamentPerformance(currentExperimentID, agents);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			String name = "Experiment " + (currentExperimentID);
			GUI.cmbExpSel.addItem(name);
		}
	}


	

}
