package gui;

import settings.InputValidator;
import settings.SetupManager;
import simulationEngine.SimReport;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.border.LineBorder;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;

import java.awt.Font;
import java.awt.SystemColor;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JSlider;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeListener;

import historicalInfo.HistoricalInfoMgr;

import javax.swing.event.ChangeEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class GUI {

	// Parameters of GUI component
	public static boolean radomRequest = false;
	public static JComboBox<Object> cmbExpSel;
	public static SimReport rep;

	// Private variables
	private JFrame frame;
	private JTextField txtTemptation;
	private JTextField txtReward;
	private JTextField txtSucker;
	private JTextField txtPunishment;
	private JTextField txtTournNum;
	private JTextField txtAdvanceCLimit;
	private JTextField txtAdvancedDLimit;
	private JTextField txtCNum;
	private JTextField txtDNum;
	private JTextField txtAdvancedCNum;
	private static int numOfTournament; 
	private boolean startSimulation = false;
	private JTextArea txtLeaderBoard = new JTextArea();
	private static final JTextArea txtStats = new JTextArea();
	private static JComboBox cmbThreshold; 
	private JRadioButton rdbtnRandom;
	private JSlider slider;
	private JTextField txtAdvancedDNum;
	private JLabel lblUncertainties; 
	private String SETUP_LOCATION = "SR/SetupFile.csv";
	protected static GUI_Simulation simLog;
	private static float eliminationThreshold = 0;
	private JTextField txtCommunicationCost;

	/**
	 * Main method that creates Application frame,
	 * 
	 * Main method is known to be the class' application entry point
	 * 
	 * @param args
	 *            Java main array of command- line arguments whose data type is
	 *            string passed to this method
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					
					GUI window = new GUI();
					window.frame.setVisible(true);
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
					simLog = new GUI_Simulation();
					simLog.frmSimulation.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	
	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 550, 550);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(192, 192, 192), 1, true));
		panel.setBounds(16, 468, 516, 54);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		frame.setLocation(800, 100); // Set the frame location
		JButton btnStart = new JButton("Start");
		final JRadioButton rdbtnLoadSetup = new JRadioButton(
				"Load Experiment Setup");
		final JRadioButton rdbtnAssign = new JRadioButton("Assign");
		rdbtnAssign.setForeground(SystemColor.controlShadow);
		final JComboBox<String> cmbInfoReqApproach = new JComboBox<String>();

		btnStart.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {

				
				
				// Initialize parameters to be loaded into Setup reopsitory
				String[] param = new String[15];
				
				// Reset for next experiment
				startSimulation = true;
				GUI.cmbExpSel.removeAllItems();
				

				// Check if we are not loading setup from batch file 
				if (!rdbtnLoadSetup.isSelected()) {
					
						// check if request limits textbox are empty 
						if ((txtAdvancedDLimit.getText().isEmpty())
								|| (txtAdvanceCLimit.getText().isEmpty())
								&& (!radomRequest)) {
							JOptionPane.showMessageDialog(null,
									"Request Limits must be entered");
							return;
						}
				else{

					// Store inputs of SetUp as an array 
					param[0] = (txtTemptation.getText());
					param[1] = (txtReward.getText());
					param[2] = (txtPunishment.getText());
					param[3] = (txtSucker.getText());
					param[4] = (txtTournNum.getText());
					param[5] = (lblUncertainties.getText());
					param[6] = (txtAdvanceCLimit.getText());
					param[7] = (txtAdvancedDLimit.getText());
					param[8] = (txtCNum.getText());
					param[9] = (txtDNum.getText());
					param[10] = (txtAdvancedCNum.getText());
					param[11] = (txtAdvancedDNum.getText());
					param[12] = String.valueOf((cmbInfoReqApproach.getSelectedIndex()));
					param[13] = String.valueOf((cmbThreshold.getSelectedIndex()));
					param[14] = txtCommunicationCost.getText();

					
					
					// Store agents SetUp array in the setup repository 
					try {
						String FileHeading = "T,R,P,S,NumOfTournament,Uncertainty,AdvancedCLimit,AdvancedDLimit,NaiveC,NaiveD,AdvancedC,AdvancedD, infoRequestApproach, EvolutionModel, CommunicationCost";
						writeFile(SETUP_LOCATION, FileHeading, param);
					} catch (IOException e1) {

						e1.printStackTrace();
					}
				}
			}		
				// Initialize Validator and Setup Manager
				InputValidator input = new InputValidator(); 
				
				SetupManager setup = new SetupManager(input, simLog);
				
				
		}

			/**
			 * writeFile method picks up the experimenter's simulation inputs
			 * and stores it in the setup repository to be accessed and
			 * validated by the setup manager
			 * 
			 * @param fileName
			 *            : Setup Repository
			 * @param fileHeading
			 *            : Title of various parameters in setup repository
			 * @param param
			 *            : Setup values
			 * @throws IOException
			 */
			private void writeFile(String fileName, String fileHeading,
					String[] param) throws IOException {
				BufferedWriter outputWriter = null;
				outputWriter = new BufferedWriter(new FileWriter(fileName));

				outputWriter.write(fileHeading);
				outputWriter.newLine();
				for (int i = 0; i < param.length; i++) {
					outputWriter.write((param[i]) + ",");

				}
				outputWriter.flush();
				outputWriter.close();
			}
		});
		btnStart.setBounds(6, 6, 99, 42);
		panel.add(btnStart);

		JButton btnStop = new JButton("Stop / Exit");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnStop.setBounds(411, 6, 99, 42);
		panel.add(btnStop);
		final JButton btnDefault = new JButton("Default");
		btnDefault.setBounds(148, 8, 90, 40);
		panel.add(btnDefault);
		final JButton btnClear = new JButton("Clear");
		btnClear.setBounds(281, 8, 90, 40);
		panel.add(btnClear);
		
				btnClear.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						txtTemptation.setText("");
						txtPunishment.setText("");
						txtReward.setText("");
						txtSucker.setText("");
						txtTournNum.setText("");
						txtAdvanceCLimit.setText("");
						txtAdvancedDLimit.setText("");
						rdbtnRandom.setSelected(false);
						rdbtnAssign.setSelected(false);
						rdbtnLoadSetup.setSelected(false);
						txtCNum.setText("");
						txtDNum.setText("");
						txtAdvancedCNum.setText("");
						txtAdvancedDNum.setText("");
						txtLeaderBoard.setText("");
						txtStats.setText("");
						btnDefault.setEnabled(true);
						btnStart.setEnabled(false);
		
					}
				});
		
				btnDefault.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						txtTemptation.setText("5");
						txtPunishment.setText("1");
						txtReward.setText("3");
						txtSucker.setText("0");
						txtTournNum.setText("1");
						txtAdvanceCLimit.setText("100");
						txtAdvancedDLimit.setText("100");
						rdbtnRandom.setSelected(true);
						rdbtnAssign.setSelected(false);
						rdbtnLoadSetup.setSelected(false);
						slider.setValue(100);
						txtCNum.setText("10");
						txtDNum.setText("10");
						txtAdvancedCNum.setText("10");
						txtAdvancedDNum.setText("10");
						btnStart.setEnabled(true);
		
					}
				});

		final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		tabbedPane.setBounds(6, 6, 538, 458);
		frame.getContentPane().add(tabbedPane);

		JPanel tbAbout = new JPanel();
		tbAbout.setBackground(Color.WHITE);
		tabbedPane.addTab("About", null, tbAbout, null);
		tbAbout.setLayout(null);

		JLabel lblNewLabel = new JLabel(
				"<html> \r<h3> \r<font color = red>Simulation of Iterated Round Robin Tournament </font>\r</h3>\r<br> \rBy Jonathan Asante\n<br>\n<br>\r<p align = \"justify\">\rWe consider population of agents in an Iterated Round Robin Tournament game where in each tournament every agent engages in a single prisoner's dilemma game against every other agent. \n<br>\n<br>\nFor every tournament with n agents, each agent will participate in (n - 1) matches with a total of n/2 (n - 1) number of matches play the prisoner'sdilemma game based on the action set A= {C , D}.\r</p>\r<p align = \"justify\">\rPlayer's strategy assignments  include; \r<ul>\r<li><font color = blue>Naive Cooperator</font>\r<li><font color = blue>Naive Defector</font>\n<li><font color = blue>Advanced Cooperator</font>\n<li><font color = blue>Advanced Defector</font>\n</ul>\n\r<p align = \"justify\">\rPlayers will compete in this simulation experiment against parameters; \r<ul>\r<li><font color = red>Information acquisition limits.</font>\r<li><font color = red>Different payoff Matrix.</font>\r<li><font color = red>Information acquisition uncertainties.</font>\r</ul>\r</p>\r<br>\r</html>\r");
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 12));

		lblNewLabel.setBounds(6, 6, 505, 390);
		tbAbout.add(lblNewLabel);

		JPanel tbSetUp = new JPanel();
		tabbedPane.addTab("SetUp", null, tbSetUp, null);
		tbSetUp.setLayout(null);

		JPanel panel_1 = new JPanel();
		panel_1.setLayout(null);
		panel_1.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panel_1.setBounds(19, 36, 481, 144);
		tbSetUp.add(panel_1);

		JLabel label = new JLabel("Temptation PayOff [D, C]");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setForeground(SystemColor.controlShadow);
		label.setBounds(6, 9, 163, 22);
		panel_1.add(label);

		JLabel label_1 = new JLabel("Reward PayOff [C, C]");
		label_1.setHorizontalAlignment(SwingConstants.RIGHT);
		label_1.setForeground(SystemColor.controlShadow);
		label_1.setBounds(23, 43, 146, 16);
		panel_1.add(label_1);

		JLabel lblSuckersPayoffc = new JLabel("Sucker's PayOff [C, D]");
		lblSuckersPayoffc.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSuckersPayoffc.setForeground(SystemColor.controlShadow);
		lblSuckersPayoffc.setBounds(23, 111, 146, 16);
		panel_1.add(lblSuckersPayoffc);

		JLabel lblPunishmentPayoffd = new JLabel("Punishment PayOff [D, D]");
		lblPunishmentPayoffd.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPunishmentPayoffd.setForeground(SystemColor.controlShadow);
		lblPunishmentPayoffd.setBounds(0, 83, 171, 16);
		panel_1.add(lblPunishmentPayoffd);

		txtTemptation = new JTextField();
		txtTemptation.setText("5");
		txtTemptation.setColumns(10);
		txtTemptation.setBounds(202, 6, 273, 28);
		panel_1.add(txtTemptation);

		txtReward = new JTextField();
		txtReward.setText("3");
		txtReward.setColumns(10);
		txtReward.setBounds(202, 39, 273, 28);
		panel_1.add(txtReward);

		txtSucker = new JTextField();
		txtSucker.setText("0");
		txtSucker.setColumns(10);
		txtSucker.setBounds(202, 105, 273, 28);
		panel_1.add(txtSucker);

		txtPunishment = new JTextField();
		txtPunishment.setText("1");
		txtPunishment.setColumns(10);
		txtPunishment.setBounds(202, 72, 273, 28);
		panel_1.add(txtPunishment);

		rdbtnRandom = new JRadioButton("Random");
		rdbtnRandom.setForeground(SystemColor.controlShadow);
		rdbtnRandom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (rdbtnRandom.isSelected()) {
					radomRequest = true;
					rdbtnAssign.setSelected(false);
					txtAdvanceCLimit.setEnabled(false);
					txtAdvancedDLimit.setEnabled(false);

				}

			}
		});

		rdbtnAssign.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (rdbtnAssign.isSelected()) {
					rdbtnRandom.setSelected(false);
					txtAdvanceCLimit.setEnabled(true);
					txtAdvanceCLimit.setText("");
					txtAdvancedDLimit.setEnabled(true);
					txtAdvancedDLimit.setText("");
					radomRequest = false;
				}

			}
		});

		rdbtnLoadSetup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (rdbtnLoadSetup.isSelected()) {
					txtTemptation.setEnabled(false);
					txtPunishment.setEnabled(false);
					txtReward.setEnabled(false);
					txtSucker.setEnabled(false);
					txtTournNum.setEnabled(false);
					txtAdvanceCLimit.setEnabled(false);
					txtAdvancedDLimit.setEnabled(false);
					rdbtnRandom.setEnabled(false);
					rdbtnAssign.setEnabled(false);
					slider.setEnabled(false);
					btnDefault.setEnabled(false);
					btnClear.setEnabled(false);
					txtCNum.setEnabled(false);
					txtDNum.setEnabled(false);
					txtAdvancedCNum.setEnabled(false);
					txtAdvancedDNum.setEnabled(false);

					cmbInfoReqApproach.setEnabled(false);

				} else {
					txtTemptation.setEnabled(true);
					txtPunishment.setEnabled(true);
					txtReward.setEnabled(true);
					txtSucker.setEnabled(true);
					txtTournNum.setEnabled(true);
					txtAdvanceCLimit.setEnabled(true);
					txtAdvancedDLimit.setEnabled(true);
					rdbtnRandom.setEnabled(true);
					rdbtnAssign.setEnabled(true);
					slider.setEnabled(true);
					btnDefault.setEnabled(true);
					btnClear.setEnabled(true);
					txtCNum.setEnabled(true);
					txtDNum.setEnabled(true);
					txtAdvancedCNum.setEnabled(true);
					txtAdvancedDNum.setEnabled(true);
					cmbInfoReqApproach.setEnabled(true);

				}

			}
		});
		rdbtnLoadSetup.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {

			}
		});

		JPanel panel_2 = new JPanel();
		panel_2.setLayout(null);
		panel_2.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panel_2.setBounds(19, 187, 481, 115);
		tbSetUp.add(panel_2);

		JLabel lblNumberOfTournaments = new JLabel("Number of Tournaments");
		lblNumberOfTournaments.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNumberOfTournaments.setForeground(SystemColor.controlShadow);
		lblNumberOfTournaments.setBounds(0, 12, 173, 16);
		panel_2.add(lblNumberOfTournaments);

		txtTournNum = new JTextField();
		txtTournNum.setHorizontalAlignment(SwingConstants.LEFT);
		txtTournNum.setText("4");
		txtTournNum.setColumns(10);
		txtTournNum.setBounds(201, 6, 274, 28);
		panel_2.add(txtTournNum);
		
		JLabel lblEliminationThreshold = new JLabel("Evolutionary Model");
		lblEliminationThreshold.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEliminationThreshold.setForeground(SystemColor.controlShadow);
		lblEliminationThreshold.setBounds(19, 48, 154, 16);
		panel_2.add(lblEliminationThreshold);
		
		cmbThreshold = new JComboBox();
		cmbThreshold.setModel(new DefaultComboBoxModel(new String[] {"No Elimination", "Eliminate 1 with 1 Replacement", "Eliminate 2 with 2 Replacements", "Least 4 Agents Adapt Top 4 Strategies", "Least 4 Agents Adapt Similar Native Strategies Top 4 "}));
		cmbThreshold.setSelectedIndex(3);
		cmbThreshold.setBounds(201, 44, 274, 27);
		panel_2.add(cmbThreshold);
		
		JLabel lblCommunicationCost = new JLabel("Communication Cost");
		lblCommunicationCost.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCommunicationCost.setForeground(SystemColor.controlShadow);
		lblCommunicationCost.setBounds(35, 76, 138, 16);
		panel_2.add(lblCommunicationCost);
		
		txtCommunicationCost = new JTextField();
		txtCommunicationCost.setHorizontalAlignment(SwingConstants.LEFT);
		txtCommunicationCost.setText("0");
		txtCommunicationCost.setColumns(10);
		txtCommunicationCost.setBounds(201, 70, 274, 28);
		panel_2.add(txtCommunicationCost);

		JPanel panel_3 = new JPanel();
		panel_3.setLayout(null);
		panel_3.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panel_3.setBounds(19, 312, 481, 94);
		tbSetUp.add(panel_3);

		JLabel lblInformationRequestLimit = new JLabel(
				"Information Request Limit");
		lblInformationRequestLimit.setHorizontalAlignment(SwingConstants.LEFT);
		lblInformationRequestLimit.setForeground(Color.RED);
		lblInformationRequestLimit.setBounds(21, 40, 176, 16);
		panel_3.add(lblInformationRequestLimit);

		JLabel lblAdvancedc = new JLabel("Advanced_C");
		lblAdvancedc.setHorizontalAlignment(SwingConstants.LEFT);
		lblAdvancedc.setForeground(SystemColor.controlShadow);
		lblAdvancedc.setBounds(246, 40, 86, 16);
		panel_3.add(lblAdvancedc);

		JLabel lblAdvancedd = new JLabel("Advanced_D");
		lblAdvancedd.setHorizontalAlignment(SwingConstants.LEFT);
		lblAdvancedd.setForeground(SystemColor.controlShadow);
		lblAdvancedd.setBounds(389, 40, 86, 16);
		panel_3.add(lblAdvancedd);

		rdbtnAssign.setBounds(6, 62, 86, 23);
		panel_3.add(rdbtnAssign);

		rdbtnRandom.setBounds(124, 62, 86, 23);
		panel_3.add(rdbtnRandom);

		txtAdvanceCLimit = new JTextField();
		txtAdvanceCLimit.setText("400000");
		txtAdvanceCLimit.setColumns(10);
		txtAdvanceCLimit.setBounds(256, 60, 45, 28);
		panel_3.add(txtAdvanceCLimit);

		txtAdvancedDLimit = new JTextField();
		txtAdvancedDLimit.setText("400000");
		txtAdvancedDLimit.setColumns(10);
		txtAdvancedDLimit.setBounds(399, 60, 45, 28);
		panel_3.add(txtAdvancedDLimit);
		slider = new JSlider();
		lblUncertainties = new JLabel("1.0");
		slider.setBounds(200, 6, 227, 20);
		panel_3.add(slider);
		panel_3.add(lblUncertainties);
		
				slider.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						String ticker = "";
						ticker = (String.valueOf((float) slider.getValue() / 100));
						lblUncertainties.setText(ticker.substring(0, 3));
					}
				});
				
						slider.setValue(100);
						slider.setPaintTicks(true);
						slider.setMajorTickSpacing(10);
						
								JLabel label_6 = new JLabel("Uncertainty Level");
								label_6.setBounds(35, 6, 137, 16);
								panel_3.add(label_6);
								label_6.setHorizontalAlignment(SwingConstants.RIGHT);
								label_6.setForeground(SystemColor.controlShadow);
								lblUncertainties.setBounds(436, 0, 25, 28);
								

		rdbtnLoadSetup.setForeground(Color.RED);
		rdbtnLoadSetup.setBounds(19, 7, 183, 23);
		tbSetUp.add(rdbtnLoadSetup);

		JPanel tbModel = new JPanel();
		tabbedPane.addTab("Agents", null, tbModel, null);
		tbModel.setLayout(null);

		JPanel panel_4 = new JPanel();
		panel_4.setLayout(null);
		panel_4.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panel_4.setBounds(26, 19, 468, 210);
		tbModel.add(panel_4);

		JLabel lblCooperateall = new JLabel("Naive_Cooperator");
		lblCooperateall.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCooperateall.setForeground(SystemColor.controlShadow);
		lblCooperateall.setBounds(16, 49, 188, 16);
		panel_4.add(lblCooperateall);

		JLabel lblDefectall = new JLabel("Naive_Defector");
		lblDefectall.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDefectall.setForeground(SystemColor.controlShadow);
		lblDefectall.setBounds(16, 89, 188, 16);
		panel_4.add(lblDefectall);

		JLabel lblAdvancedd_1 = new JLabel("Advanced_C");
		lblAdvancedd_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAdvancedd_1.setForeground(SystemColor.controlShadow);
		lblAdvancedd_1.setBounds(16, 123, 188, 16);
		panel_4.add(lblAdvancedd_1);

		txtCNum = new JTextField();
		txtCNum.setText("1");
		txtCNum.setColumns(10);
		txtCNum.setBounds(231, 43, 119, 28);
		panel_4.add(txtCNum);

		txtDNum = new JTextField();
		txtDNum.setText("1");
		txtDNum.setColumns(10);
		txtDNum.setBounds(231, 83, 119, 28);
		panel_4.add(txtDNum);

		txtAdvancedCNum = new JTextField();
		txtAdvancedCNum.setText("2");
		txtAdvancedCNum.setColumns(10);
		txtAdvancedCNum.setBounds(231, 117, 119, 28);
		panel_4.add(txtAdvancedCNum);

		JLabel lblStrategies = new JLabel("Strategies");
		lblStrategies.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		lblStrategies.setHorizontalAlignment(SwingConstants.RIGHT);
		lblStrategies.setForeground(new Color(199, 21, 133));
		lblStrategies.setBounds(16, 21, 188, 16);
		panel_4.add(lblStrategies);

		JLabel lblNumofplayers = new JLabel("Num Of Players");
		lblNumofplayers.setHorizontalAlignment(SwingConstants.LEFT);
		lblNumofplayers.setForeground(new Color(199, 21, 133));
		lblNumofplayers.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		lblNumofplayers.setBounds(234, 21, 116, 16);
		panel_4.add(lblNumofplayers);
		
		txtAdvancedDNum = new JTextField();
		txtAdvancedDNum.setText("2");
		txtAdvancedDNum.setColumns(10);
		txtAdvancedDNum.setBounds(231, 157, 119, 28);
		panel_4.add(txtAdvancedDNum);
		
		JLabel label_2 = new JLabel("Advanced_D");
		label_2.setHorizontalAlignment(SwingConstants.RIGHT);
		label_2.setForeground(SystemColor.controlShadow);
		label_2.setBounds(16, 161, 188, 16);
		panel_4.add(label_2);

		JPanel panel_5 = new JPanel();
		panel_5.setBounds(26, 247, 468, 148);
		tbModel.add(panel_5);
		panel_5.setLayout(null);
		panel_5.setBorder(new LineBorder(Color.LIGHT_GRAY));

		JLabel lblAgentsInfoRequest = new JLabel(
				"Agents' Info. Request Approach");
		lblAgentsInfoRequest.setHorizontalAlignment(SwingConstants.CENTER);
		lblAgentsInfoRequest.setForeground(new Color(255, 20, 147));
		lblAgentsInfoRequest.setBounds(6, 6, 456, 16);
		panel_5.add(lblAgentsInfoRequest);

		cmbInfoReqApproach.setModel(new DefaultComboBoxModel(new String[] {"Opponent's First Action", "First Time Opponent Defected", "All Opponent's Past Actions", "Opponent's Past Action in Random Tournament", "Past Actions of other Agents against Opponent", "Opponent's Strategy"}));
		cmbInfoReqApproach.setSelectedIndex(5);
		cmbInfoReqApproach.setBounds(17, 52, 434, 27);
		panel_5.add(cmbInfoReqApproach);

		JPanel tbDisplay = new JPanel();
		tabbedPane.addTab("Display", null, tbDisplay, null);
		tbDisplay.setLayout(null);

		final JTabbedPane tabbedPane_1 = new JTabbedPane(SwingConstants.TOP);
		tabbedPane_1.setBounds(6, 6, 505, 400);
		tbDisplay.add(tabbedPane_1);

		JPanel tbnLeaderboard = new JPanel();
		tabbedPane_1.addTab("Leaderboard", null, tbnLeaderboard, null);
		tbnLeaderboard.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 6, 472, 342);
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		tbnLeaderboard.add(scrollPane);
		txtLeaderBoard.setEditable(false);
		txtLeaderBoard.setForeground(new Color(105, 105, 105));

		scrollPane.setViewportView(txtLeaderBoard);

		JPanel tbnstats = new JPanel();
		tabbedPane_1.addTab("Statistics", null, tbnstats, null);
		tbnstats.setLayout(null);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setBounds(6, 6, 472, 342);
		tbnstats.add(scrollPane_1);
		txtStats.setEditable(false);
		txtStats.setForeground(new Color(105, 105, 105));

		scrollPane_1.setViewportView(txtStats);

		JPanel tbnGraph = new JPanel();
		tabbedPane_1.addTab("Graph", null, tbnGraph, null);
		tbnGraph.setLayout(null);

		final JButton btnLineChart = new JButton("Line Chart");
		btnLineChart.setEnabled(false);
		btnLineChart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String expIndex[] = { String.valueOf(cmbExpSel
						.getSelectedIndex()) };
				LineChart.main(expIndex);

			}
		});
		btnLineChart.setBounds(28, 111, 117, 66);
		tbnGraph.add(btnLineChart);

		final JButton btnBarChart = new JButton("Bar Chart");
		btnBarChart.setEnabled(false);
		btnBarChart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String expIndex[] = { String.valueOf(cmbExpSel
						.getSelectedIndex()) };
				BarChart.main(expIndex);
			}
		});
		btnBarChart.setBounds(333, 111, 117, 66);
		tbnGraph.add(btnBarChart);

		final JButton btnAreaChart = new JButton("Area Chart");
		btnAreaChart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String expIndex[] = { String.valueOf(cmbExpSel
						.getSelectedIndex()) };
				AreaChart.main(expIndex);
			}
		});
		btnAreaChart.setEnabled(false);
		btnAreaChart.setBounds(28, 202, 117, 66);
		tbnGraph.add(btnAreaChart);

		cmbExpSel = new JComboBox<Object>();
		cmbExpSel.setEnabled(false);
		cmbExpSel.setBounds(28, 32, 435, 27);
		tbnGraph.add(cmbExpSel);

		JLabel lblSelectExperiment = new JLabel("Select Experiment :");
		lblSelectExperiment.setForeground(Color.DARK_GRAY);
		lblSelectExperiment.setBounds(38, 16, 152, 16);
		tbnGraph.add(lblSelectExperiment);
		
		final JButton btnBarChart3D = new JButton("Bar Chart 3D");
		btnBarChart3D.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String expIndex[] = { String.valueOf(cmbExpSel
						.getSelectedIndex()) };
				BarChart3DDemo4.main(expIndex);
			}
		});
		btnBarChart3D.setEnabled(false);
		btnBarChart3D.setBounds(333, 202, 117, 66);
		tbnGraph.add(btnBarChart3D);

		tabbedPane.addChangeListener(new ChangeListener() { // add the Listener

					@Override
					public void stateChanged(ChangeEvent e) {
						String info = "";
						if (startSimulation) {
							if (tabbedPane.getSelectedIndex() == 3) // Index
																	// starts at
																	// 0, so
																	// Index 2 =
																	// Tab3
							{
							
								txtLeaderBoard.setText(HistoricalInfoMgr.experimentLeaderboard);
							}
						}
					}
				});

		// Actions for tabs under display clicked 
		tabbedPane_1.addChangeListener(new ChangeListener() { 
					@Override
					public void stateChanged(ChangeEvent e) {
						String info = "";
						if (startSimulation) {
							// Get leader board to observe players performance
							if (tabbedPane_1.getSelectedIndex() == 0) 
							{
								
								// print agents final scores
								txtLeaderBoard.setText(HistoricalInfoMgr.experimentLeaderboard);
							}

							// Get tournament Statistics
							if (tabbedPane_1.getSelectedIndex() == 1) 	
								txtStats.setText(HistoricalInfoMgr.agentsTournamentStatistics);

							

							// Get players performance as a graph
							if (tabbedPane_1.getSelectedIndex() == 2) {
								cmbExpSel.setEnabled(true);
								btnLineChart.setEnabled(true);
								btnBarChart.setEnabled(true);
								btnBarChart3D.setEnabled(true);

								btnAreaChart.setEnabled(true);

							}

						}

					}
				});

	}
}
