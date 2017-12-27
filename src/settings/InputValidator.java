package settings;

import gui.GUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Stream;

import javax.swing.JOptionPane;


/**
 * InputValidator validates other simulation inputs such as Payoff matrix,
 * information acquisition uncertainties, information request limits and number
 * of tournaments that are stored in the setup repositories. These validated
 * inputs and assigned strategies are then sent to the simulation manager at the
 * begining of the simulation.
 * 
 * @author jonathanasante
 * 
 */
public class InputValidator {

	// Private InputValidator Parameters
	private static boolean startSim = false;
	public float[] currentSetup;
	public float[] payOff;
	public String []agentStrategies; 
	public float uncertaintyLevel;
	public int numOfTournament;
	public float infoRequestOption;
	public float numOfAgents;
	public float uncertaintyLimit;
	public long numOfExperiment;
	public int[] agentRequestLimit;
	public boolean validationStatus = true; 
	private String requestLimitOption;
	private final String ADVANCED_COOPERATOR = "Advanced_C";
	private final String ADVANCED_DEFECTOR = "Advanced_D";
	private final String ADVANCED_EXPLOITER = "Advanced_E";
	private String SETUP_LOCATION = "SR/SetupFile.csv";
	private final String FILE_NOT_FOUND = "File not found";
	public float param;
	
	/**
	 * Initiate method begins the parameter Configuration Manager's function
	 * of reading and validating setup values and also notify the simulation
	 * manager to begin simulations.
	 * @throws IOException
	 */
	public InputValidator() {
		// Begin a new simulation experiment
		numOfExperiment = 0;
		
		// Read and validate experiment values
		readAndValidate();
	//	setAgentStrategies();
	//	setRequestLimit();
	
	}

	
	/**
	 * Read the simulation setup file and validate
	 * 
	 */
	
	public void readAndValidate() {
		
		//Get number of experiments
		long numberOfLines = 0;
		String lineParam ="";
		    try (Stream<String> s = Files.lines(Paths.get(SETUP_LOCATION),
		            Charset.defaultCharset())) {

		        numberOfLines = s.count();

		    } catch (IOException e) {
		    	validationStatus = false;
		    	JOptionPane.showMessageDialog(null, FILE_NOT_FOUND);    
		  }
		
		numOfExperiment = numberOfLines;
		
		
		// Read setup inputs
		for(int i = 1; i < numOfExperiment; i++){
			try {
				lineParam = Files.readAllLines(Paths.get(SETUP_LOCATION)).get(i);
			} catch (IOException e) {
		    	JOptionPane.showMessageDialog(null, FILE_NOT_FOUND);    
		    	validationStatus = false;

			}
			
			int count = 0;	
			String[] setupParam = lineParam.split(",");
			currentSetup = new float[14];

			
			for (int j = 0; j < setupParam.length; j++) {
				try {
					currentSetup[j] =Float.valueOf((setupParam[j]));
				} catch (NumberFormatException ef) {
					JOptionPane.showMessageDialog(null,
							"PayOff inputs must be Integer ."+ count);
			    	validationStatus = false;

					
				}

			}
			

			// Validate conditions for cooperation
			if ((2 * currentSetup[1]) <= ((currentSetup[0]) + (currentSetup[3]))) {
				JOptionPane.showMessageDialog(null,
						"PayOff inputs must follow (2 * R) > (T + S)");
		    	validationStatus = false;

				
			}

			
			if (((currentSetup[0]) <= (currentSetup[1]))|| ((currentSetup[1]) <= (currentSetup[2])) || ((currentSetup[2]) <= (currentSetup[3]))) {
				JOptionPane.showMessageDialog(null,
						"PayOff inputs must follow T > R > P > S");
		    	validationStatus = false;

			}

			// Validate Number of tournaments input and info. request limit
			try {
				Math.round(Float.parseFloat(setupParam[4]));
				uncertaintyLevel = Float.parseFloat(setupParam[5]);

			} catch (NumberFormatException ef) {
				JOptionPane.showMessageDialog(null,
						"Tournament number must enter a number.");
		    	validationStatus = false;
			}

			// Validate Uncertainty limit as a probability value
			if ((Float.parseFloat(setupParam[5]) < 0)
					&& (Float.parseFloat(setupParam[5]) > 1)) {
				JOptionPane.showMessageDialog(null,
						"Uncertainty Level must be a probability value");
		    	validationStatus = false;

			}
	
		}
		
	}
	
	
	
	/**
	 * Seutp the current experiment
	 * @param i
	 */
	public void setCurrentExperiment(int experimentIndex){
			
			// Read current experiment
			String lineParam ="";
			try {
				lineParam = Files.readAllLines(Paths.get(SETUP_LOCATION)).get(experimentIndex);
			} catch (IOException e) {
		    	JOptionPane.showMessageDialog(null, FILE_NOT_FOUND);    
		    	validationStatus = false;
	
			}
			
			// Store current Setup as array
			String[] setupParam = lineParam.split(",");
			currentSetup = new float[14];
	
			for (int j = 0; j < setupParam.length; j++) {
				try {
					currentSetup[j] =Float.valueOf((setupParam[j]));
				} catch (NumberFormatException ef) {
					JOptionPane.showMessageDialog(null,
							"PayOff inputs must be Integer");
				}
			}
		
			setnumOfTournaments();
			setPayoff();
			setUncertaintyLimit();
			setRequestInfoOption();
			setAgentStrategies();
			setRequestLimit();
	}
	

	
	
	
	
	
	
	
	/******************** Getters and Setters **********************/
	
	public void setnumOfTournaments(){
		numOfTournament = Math.round(currentSetup[4]);
		
	}
	
	public int getnumOfTournaments(){
		return numOfTournament;
	}
	
	
	public void setPayoff(){
		payOff = new float[4];
		payOff[0] = currentSetup[0];
		payOff[1] = currentSetup[1];
		payOff[2] = currentSetup[2];
		payOff[3] = currentSetup[3];



	}
	
	public float[] getPayoff(){
		return payOff;
	}


	public void setUncertaintyLimit(){
		uncertaintyLimit = currentSetup[5];
	}
	
	public float getUncertaintyLimit(){
		return uncertaintyLimit;
	}


	public void setRequestInfoOption(){
		infoRequestOption = currentSetup[13];
	}
	
	public float getRequestInfoOption(){
		return uncertaintyLimit;
	}

	/**
	 * getAgentStrategies Sets up the strategy of each competing player n the
	 * tournament.
	 * 
	 * @param strategiesNum
	 *            : Agents strategies in the current experiment
	 */
	public void setAgentStrategies() {
		int[] strategiesNum = new int[5];
		strategiesNum[0] = Math.round(currentSetup[8]);
		strategiesNum[1] = Math.round(currentSetup[9]);
		strategiesNum[2] = Math.round(currentSetup[10]);
		strategiesNum[3] = Math.round(currentSetup[11]);
		strategiesNum[4] = Math.round(currentSetup[12]);
		

		for (int i = 0; i < (strategiesNum.length); i++) 
			numOfAgents = numOfAgents + (strategiesNum[i]);

		// Add a dummy player to odd number of agents 
		if (numOfAgents % 2 != 0) {
			numOfAgents = numOfAgents + 1;
			agentStrategies = new String[(int) numOfAgents];
			agentStrategies[(int) (numOfAgents - 1)] = "Dummy"; 
		}
	
		// Set allocation for even number of agents 
		else {

			agentStrategies = new String[Math.round(numOfAgents)];

		}

		int count = 0;

		// Generate agent strategies and store in array 
		for (int i = 0; i < 5; i++) {
			
			for (int j = 0; j < strategiesNum[i]; j++) {

				switch(i){
					case 0:		
						agentStrategies[(count)] = "Naive_C";
						break;
	
					case 1:			
						agentStrategies[(count)] = "Naive_D";
						break;
	
					case 2:
						agentStrategies[(count)] = "Advanced_C";
						break;
	
					case 3:
						agentStrategies[(count)] = "Advanced_D";
						break;
					
					case 4:
						agentStrategies[(count)] = "Advanced_E";
						break;
				}
				count++;
			}
		}

		
		 // Shuffle agent strategies so that position doesn't favor outcome in first round
		reshuffleStrategies();

	}

	public void reshuffleStrategies() {
		Collections.shuffle(Arrays.asList(agentStrategies));
		
		for(int i = 0; i < agentStrategies.length; i++){
			if(agentStrategies[i]=="Dummy"){
				String temp = agentStrategies[i];
				agentStrategies[i] = agentStrategies[agentStrategies.length-1];
				agentStrategies[agentStrategies.length-1] = temp;
			}
		}
	}


	
	
	
	
	/**
	 * setRequestLimit() returns the current experiment request limits for
	 * agents to Historical Information Manager upon request.
	 * 
	 * @return agentRequestLimit : Request limit if all agents in the current
	 *         experiment
	 */
	public int[] setRequestLimit() {

		agentRequestLimit = new int[Math.round(numOfAgents)];
	//	JOptionPane.showMessageDialog(null, numOfAgents);
		// Generate random limits for agents if random option is selected
		if (GUI.radomRequest) {
			Random r = new Random();
			requestLimitOption = "Random";

			for (int i = 0; i < agentRequestLimit.length; i++)
				agentRequestLimit[i] = r.nextInt((4000 - 0) + 1) + 0;
		} 
		
		
		// Used assigned request limits
		else {
			requestLimitOption = "Assigned";

			for (int i = 0; i < agentRequestLimit.length; i++) {
				if (agentStrategies[i].equalsIgnoreCase(ADVANCED_COOPERATOR)) {
					agentRequestLimit[i] = Math.round(currentSetup[6]);
				}

				else if (agentStrategies[i].equalsIgnoreCase(ADVANCED_DEFECTOR)) {
					agentRequestLimit[i] = (int) currentSetup[7];
				}
				
				else if (agentStrategies[i].equalsIgnoreCase(ADVANCED_EXPLOITER)) {
					agentRequestLimit[i] = (int) currentSetup[7];
				}

				else {
					agentRequestLimit[i] = 0;
				}
			}

		}
		return agentRequestLimit;

	}

	

}

