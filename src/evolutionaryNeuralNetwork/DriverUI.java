package evolutionaryNeuralNetwork;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class DriverUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textFieldOutput;
	private JTextField textFieldNInputs;
	private JTextField textFieldNOutputs;
	private JTextField textFieldLayers;
	private JTextField textFieldNeuronsLayer;
	private JTextField textFieldPopSize;
	private JTextField textFieldCrossover;
	private JTextField textFieldMutation;
	private JTextField textFieldTournamentSize;
	private JTextField textFieldInput;
	private JButton btnStop;
	private JComboBox<String> comboBoxAF;
	private JButton btnTrain;
	private JTextField textFieldFileName;
	private JButton btnFind;
	private JButton btnLoad;

	private DataSet learningData;
	public NeuralNetwork result;
	public GeneticAlgorithm moon;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DriverUI frame = new DriverUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DriverUI() {
		NeuralNetwork result;
		GeneticAlgorithm moon;
		int nInputs=0;
		int nOutputs;
		
		setTitle("Evolutionary Neural Network");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 614, 389);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblDataEntry = new JLabel("Data Entry");
		lblDataEntry.setBounds(239, 84, 72, 14);
		contentPane.add(lblDataEntry);
		
		JLabel lblOutput = new JLabel("Output");
		lblOutput.setBounds(239, 11, 46, 14);
		contentPane.add(lblOutput);
		
		textFieldOutput = new JTextField();
		textFieldOutput.setEditable(false);
		textFieldOutput.setBounds(278, 8, 140, 20);
		contentPane.add(textFieldOutput);
		textFieldOutput.setColumns(10);
		
		comboBoxAF = new JComboBox<String>();
		comboBoxAF.setModel(new DefaultComboBoxModel<String>(new String[] {"sigmoid", "step", "tanh", "sigmoid-step"}));
		comboBoxAF.setBounds(122, 303, 86, 20);
		contentPane.add(comboBoxAF);
		
		btnTrain = new JButton("Train");
		btnTrain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new TrainWorker().execute();
			}
		});
		btnTrain.setEnabled(false);
		btnTrain.setBounds(265, 277, 89, 23);
		contentPane.add(btnTrain);
		
		btnStop = new JButton("Stop");
		btnStop.setBounds(265, 252, 89, 23);
		btnStop.setEnabled(false);
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				GeneticAlgorithm.stop();
			}
		});
		contentPane.add(btnStop);
		
		btnFind = new JButton("Find");
		btnFind.setBounds(285, 178, 89, 23);
		btnFind.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				int action = JFileChooser.ERROR_OPTION;
				action = fileChooser.showOpenDialog(null);
				if (action == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					textFieldFileName.setText(selectedFile.getPath());
				}
				btnTrain.setEnabled(true);
			}
		});
		contentPane.add(btnFind);
		
		btnLoad = new JButton("Load Data");
		btnLoad.setBounds(285, 203, 89, 23);
		btnLoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (textFieldFileName.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "No file selected");
					return;
				}
			
				if (textFieldNInputs.getText().isEmpty() || textFieldNOutputs.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Invalid Inputs");
					return;
				}
				
				int nInputs = Integer.parseInt(textFieldNInputs.getText());
				int nOutputs = Integer.parseInt(textFieldNOutputs.getText());
				
				if (nInputs <= 0 || nOutputs <= 0) {
					JOptionPane.showMessageDialog(null, "Invalid Inputs");
					return;
				}
				
				DataSet dataset = null;
				try {
					dataset = new DataSet(textFieldFileName.getText(), nInputs, nOutputs);
				} catch (IOException e) {
					return;
				}
				
				learningData = dataset;
				btnTrain.setEnabled(true);
			}
		});
		contentPane.add(btnLoad);
		

		
		JLabel lblNewLabel = new JLabel("Layers");
		lblNewLabel.setBounds(10, 156, 46, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblFileName = new JLabel("Data File");
		lblFileName.setBounds(215, 155, 89, 14);
		contentPane.add(lblFileName);
		
		JLabel lblNeuronsPerLayer = new JLabel("Neurons per Layer");
		lblNeuronsPerLayer.setBounds(10, 181, 140, 14);
		contentPane.add(lblNeuronsPerLayer);
		
		JLabel lblPopulationSize = new JLabel("Population Size");
		lblPopulationSize.setBounds(10, 206, 140, 14);
		contentPane.add(lblPopulationSize);
		
		JLabel lblCrossoverPropability = new JLabel("Crossover Propability");
		lblCrossoverPropability.setBounds(10, 231, 140, 14);
		contentPane.add(lblCrossoverPropability);
		
		JLabel lblMutationPropability = new JLabel("Mutation Propability");
		lblMutationPropability.setBounds(10, 256, 140, 14);
		contentPane.add(lblMutationPropability);
		
		JLabel lblNumberOfInputs = new JLabel("Number of Inputs");
		lblNumberOfInputs.setBounds(10, 84, 112, 14);
		contentPane.add(lblNumberOfInputs);
		
		JLabel lblNumberOfOutputs = new JLabel("Number of Outputs");
		lblNumberOfOutputs.setBounds(10, 109, 112, 14);
		contentPane.add(lblNumberOfOutputs);
		
		textFieldNInputs = new JTextField();
		textFieldNInputs.setBounds(122, 81, 86, 20);
		contentPane.add(textFieldNInputs);
		textFieldNInputs.setColumns(10);
		
		textFieldNOutput = new JTextField();
		textFieldNOutput.setBounds(122, 106, 86, 20);
		contentPane.add(textFieldNOutput);
		textFieldNOutput.setColumns(10);
		
		textFieldLayers = new JTextField();
		textFieldLayers.setText("1");
		textFieldLayers.setBounds(160, 153, 86, 20);
		contentPane.add(textFieldLayers);
		textFieldLayers.setColumns(10);
		
		textFieldNeuronsLayer = new JTextField();
		textFieldNeuronsLayer.setText("1");
		textFieldNeuronsLayer.setBounds(160, 178, 86, 20);
		contentPane.add(textFieldNeuronsLayer);
		textFieldNeuronsLayer.setColumns(10);
		
		textFieldPopSize = new JTextField();
		textFieldPopSize.setText("100");
		textFieldPopSize.setBounds(160, 203, 86, 20);
		contentPane.add(textFieldPopSize);
		textFieldPopSize.setColumns(10);
		
		textFieldCrossover = new JTextField();
		textFieldCrossover.setText(".75");
		textFieldCrossover.setBounds(122, 228, 86, 20);
		contentPane.add(textFieldCrossover);
		textFieldCrossover.setColumns(10);
		
		textFieldMutation = new JTextField();
		textFieldMutation.setText(".2");
		textFieldMutation.setBounds(160, 253, 86, 20);
		contentPane.add(textFieldMutation);
		textFieldMutation.setColumns(10);
		
		textFieldFileName = new JTextField();
		textFieldFileName.setBounds(285, 153, 86, 20);
		contentPane.add(textFieldFileName);
		textFieldFileName.setEditable(false);
		textFieldFileName.setColumns(10);
		
		JLabel lblTournamentSize = new JLabel("Tournament Size");
		lblTournamentSize.setBounds(10, 281, 112, 14);
		contentPane.add(lblTournamentSize);
		
		JLabel lblActivation = new JLabel("Activation Function");
		lblActivation.setBounds(10, 306, 112, 14);
		contentPane.add(lblActivation);
		
		textFieldTournamentSize = new JTextField();
		textFieldTournamentSize.setText("2");
		textFieldTournamentSize.setBounds(160, 278, 86, 20);
		contentPane.add(textFieldTournamentSize);
		textFieldTournamentSize.setColumns(10);
		
		JLabel lblInput = new JLabel("Input");
		lblInput.setBounds(10, 11, 46, 14);
		contentPane.add(lblInput);
		
		textFieldInput = new JTextField();
		textFieldInput.setBounds(45, 8, 140, 20);
		contentPane.add(textFieldInput);
		textFieldInput.setColumns(10);
		
		JButton btnQuery = new JButton("Query");
		btnQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				DataSet learningData = new DataSet(nInputs, nOutputs);
//				addData(learningData);
//				
//				String nnInputTemp = textFieldInput.getText();
//				nnInputTemp.split(" ");
//				double[] input;
//				for(int i = 0; i < nnInputTemp.length(); i++)
//				{
//				    input[i] = Double.parseDouble(nnInputTemp[i]);
//				}
//			
//				result.feedForward(input, learningData);
				System.out.println(nInputs);

			}
		});
		btnQuery.setBounds(10, 36, 89, 23);
		contentPane.add(btnQuery);
	}
	
	private boolean validateInputs() {
		String inputTemp = textFieldNInputs.getText();
		int nInputs = Integer.parseInt(inputTemp);
		
		String outputTemp = textFieldNOutputs.getText();
		int nOutputs = Integer.parseInt(outputTemp);
		
		String layerTemp = textFieldLayers.getText();
		int nLayers = Integer.parseInt(layerTemp);
		
		String nLayerTemp = textFieldNeuronsLayer.getText();
		int nNeurons = Integer.parseInt(nLayerTemp);
		
		String popTemp = textFieldPopSize.getText();
		int popSize = Integer.parseInt(popTemp);
		
		String crossTemp = textFieldCrossover.getText();
		double crossoverPropability = Double.parseDouble(crossTemp);
		
		String mutantTemp = textFieldMutation.getText();
		double mutationPropability = Double.parseDouble(mutantTemp);
		
		String tournamentTemp = textFieldTournamentSize.getText();
		int tournSize = Integer.parseInt(tournamentTemp);
		
		if (
			nInputs <= 0 ||
			nOutputs <= 0 ||
			nLayers <= 0 ||
			nNeurons <= 0 ||
			popSize <= 0 ||
			crossoverPropability < 0 ||
			crossoverPropability > 1 ||
			mutationPropability < 0 ||
			mutationPropability > 1 ||
			tournSize <= 0
			) 
		{
			return false;
		}
		return true;	
	}
	
	class TrainWorker extends SwingWorker<Void, Void> {
		@Override
		protected Void doInBackground() throws Exception {
			btnStop.setEnabled(true);
			textFieldNInputs.setEditable(false);
			textFieldNOutputs.setEditable(false);
			textFieldLayers.setEditable(false);
			textFieldNeuronsLayer.setEditable(false);
			textFieldPopSize.setEditable(false);
			textFieldCrossover.setEditable(false);
			textFieldMutation.setEditable(false);
			textFieldTournamentSize.setEditable(false);
			comboBoxAF.setEditable(false);
			
			if (!validateInputs()) {
				textFieldNInputs.setEditable(true);
				textFieldNOutputs.setEditable(true);
				textFieldLayers.setEditable(true);
				textFieldNeuronsLayer.setEditable(true);
				textFieldPopSize.setEditable(true);
				textFieldCrossover.setEditable(true);
				textFieldMutation.setEditable(true);
				textFieldTournamentSize.setEditable(true);
				comboBoxAF.setEditable(true);
				btnStop.setEnabled(false);
				JOptionPane.showMessageDialog(null, "Invalid Inputs");
				return null;
			}
			
			String inputTemp = textFieldNInputs.getText();
			int nInputs = Integer.parseInt(inputTemp);
			
			String outputTemp = textFieldNOutputs.getText();
			int nOutputs = Integer.parseInt(outputTemp);
			
			String layerTemp = textFieldLayers.getText();
			int nLayers = Integer.parseInt(layerTemp);
			
			String nLayerTemp = textFieldNeuronsLayer.getText();
			int nNeurons = Integer.parseInt(nLayerTemp);
			
			String popTemp = textFieldPopSize.getText();
			int popSize = Integer.parseInt(popTemp);
			
			String crossTemp = textFieldCrossover.getText();
			double crossoverPropability = Double.parseDouble(crossTemp);
			
			String mutantTemp = textFieldMutation.getText();
			double mutationPropability = Double.parseDouble(mutantTemp);
			
			String tournamentTemp = textFieldTournamentSize.getText();
			int tournSize = Integer.parseInt(tournamentTemp);
			
			String afTemp = (String) comboBoxAF.getSelectedItem();
			ActivationFunction af;
			if (afTemp == "sigmoid"){
				af = ActivationFunction.SIGMOID;
			} else if (afTemp == "step") {
				af = ActivationFunction.STEP;
			} else if (afTemp == "tanh") {
				af = ActivationFunction.TANH;
			} else {
				af = ActivationFunction.SIGMOID_STEP;
			}
			
			GeneticAlgorithm moon = new GeneticAlgorithm(nInputs, nOutputs, nLayers, nNeurons, 
					popSize, crossoverPropability, mutationPropability, 
					tournSize, learningData, af);
			
			result = moon.optimize(50000);
			
			textFieldNInputs.setEditable(true);
			textFieldNOutputs.setEditable(true);
			textFieldLayers.setEditable(true);
			textFieldNeuronsLayer.setEditable(true);
			textFieldPopSize.setEditable(true);
			textFieldCrossover.setEditable(true);
			textFieldMutation.setEditable(true);
			textFieldTournamentSize.setEditable(true);
			comboBoxAF.setEditable(true);
			btnStop.setEnabled(false);
			return null;
			
		}
		
	}
}