package settings;


import java.util.ArrayList;


import agents.Agent;
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

	
	public SetupManager(InputValidator input, GUI_Simulation simLog) {
		
		this.simLog = simLog;
		
		// Begin experimentation.
		for(int currentExperimentID = 1; currentExperimentID < input.numOfExperiment; currentExperimentID++){
			
			
			input.setCurrentExperiment(currentExperimentID);
			
			Agent agent = new Agent();
			
			HistoricalInfoMgr him = new HistoricalInfoMgr(Math.round(input.numOfAgents), input.agentRequestLimit, input.getUncertaintyLimit(), input.getnumOfTournaments(), agent);
			
			// Create the required number of agents for this experiment
			ArrayList<Agent> agents = new ArrayList<Agent>();
			for(int i = 0; i < input.numOfAgents; i++){		 
				agents.add(new Agent());
			}

			Agent ag = new Agent(agents, input.agentStrategies, Math.round(input.infoRequestOption), Math.round(input.numOfAgents), him);
			
			//Begin the simulation of current experiment
			SimulationManager sim = new SimulationManager(currentExperimentID, input,simLog, agents, him, ag);
			
			sim.runSimulation();
			
		}
	}


	

}
