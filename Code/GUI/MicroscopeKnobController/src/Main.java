import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLEditorKit;

import com.fazecast.jSerialComm.SerialPort;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.text.html.HTMLDocument;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTabbedPane;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

public class Main extends JFrame {

	private String userInput = "";
	private SerialPort[] portNames;
	static SerialPort chosenPort;
	private JComboBox<String> portList;
	private PrintWriter serialSend;
	private Scanner serialRead;
	private boolean userToggleSerial;

	private static final long serialVersionUID = 5461360408065689724L;
	private JTabbedPane tabbedPane;
	private JPanel contentPane;
	private JLabel lblNewLabel;
	private JButton btnSendSteps;
	private JButton btnSendGain;
	private JButton btnConnect;
	private JTextField textField_Steps;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton rdbtnX;
	private JRadioButton rdbtnY;
	private JRadioButton rdbtnZ;
	private JList<Object> list;
	private JLabel lblGain;
	private JComboBox<String> gainComboBox;
	private final static JTextArea textArea = new JTextArea();
	DefaultCaret caret = (DefaultCaret) textArea.getCaret();

	private static JScrollPane scroll = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private JLabel lblStatusImage;
	private JLabel lblConnectStatus;

	private List<BufferedImage> images;
	private JPanel panelAbout;
	private JPanel panelCalibrate;
	private JEditorPane editorPaneAbout;
	private JFormattedTextField fieldTable;
	private JButton btnSave;
	private JButton btnLoad;
	private String[] gainPos = { "1", "3", "10", "30" };
	private JTable table;
	private JButton btnSaveDefault;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Main() {
		setResizable(false);
		initComponents();
		createEvents();
	}

	private void initComponents() {
		setDefaultLookAndFeelDecorated(true);
		setTitle("STM Knob Controller");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 339, 371);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		try {
			InputStream iconImageFile = Main.class.getResourceAsStream("/icons/atom.png");
			ImageInputStream iconImageInput = ImageIO.createImageInputStream(iconImageFile);

			InputStream disconnectedImageFile = Main.class.getResourceAsStream("/icons/disconnected_small.png");
			ImageInputStream disconnectedImageInput = ImageIO.createImageInputStream(disconnectedImageFile);

			InputStream connectedImageFile = Main.class.getResourceAsStream("/icons/connected_small.png");
			ImageInputStream connectedImageInput = ImageIO.createImageInputStream(connectedImageFile);

			setIconImage(ImageIO.read(iconImageInput));
			images = new ArrayList<>(2);
			images.add(ImageIO.read(disconnectedImageInput));
			images.add(ImageIO.read(connectedImageInput));
		} catch (IOException e) {
			e.printStackTrace();
		}

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		list = new JList<Object>();

		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		textArea.append("Select the motor driver's serial port, then \n" + "click Connect to enable Control/Calibrate.\n");
		textArea.setEditable(false);
		textArea.setBounds(6, 136, 332, 121);

		JLabel lblStatus = new JLabel("Status:", SwingConstants.CENTER);

		lblStatusImage = new JLabel("");
		lblStatusImage.setIcon(new ImageIcon(images.get(0)));

		lblConnectStatus = new JLabel("Disconnected");
		lblConnectStatus.setHorizontalAlignment(SwingConstants.CENTER);

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(list)
					.addGap(0))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(14)
					.addComponent(scroll, GroupLayout.PREFERRED_SIZE, 297, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(18, Short.MAX_VALUE))
				.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
					.addGap(58)
					.addComponent(lblStatus)
					.addGap(18)
					.addComponent(lblConnectStatus)
					.addGap(18)
					.addComponent(lblStatusImage, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(75, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(41)
							.addComponent(list, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scroll, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(6)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblConnectStatus)
								.addComponent(lblStatus)))
						.addComponent(lblStatusImage, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)))
		);

		JPanel panelConnect = new JPanel();
		tabbedPane.addTab("Connect", null, panelConnect, null);
		panelConnect.setLayout(null);

		portList = new JComboBox<String>();
		portList.setBounds(36, 18, 229, 27);
		panelConnect.add(portList);

		populateSerialList();

		btnConnect = new JButton("Connect");
		btnConnect.setBounds(92, 50, 117, 29);
		panelConnect.add(btnConnect);

		JPanel panelControl = new JPanel();
		tabbedPane.addTab("Control", null, panelControl, null);
		tabbedPane.setEnabledAt(1, false);
		panelControl.setLayout(null);

		rdbtnX = new JRadioButton("X");
		rdbtnX.setBounds(65, 6, 40, 23);
		panelControl.add(rdbtnX);
		rdbtnX.setSelected(true);
		buttonGroup.add(rdbtnX);

		rdbtnY = new JRadioButton("Y");
		rdbtnY.setBounds(135, 6, 40, 23);
		panelControl.add(rdbtnY);
		buttonGroup.add(rdbtnY);

		rdbtnZ = new JRadioButton("Z");
		rdbtnZ.setBounds(205, 6, 40, 23);
		panelControl.add(rdbtnZ);
		buttonGroup.add(rdbtnZ);

		lblNewLabel = new JLabel("Voltage Steps");
		lblNewLabel.setBounds(193, 34, 86, 16);
		panelControl.add(lblNewLabel);

		textField_Steps = new JTextField();
		textField_Steps.setBounds(206, 52, 60, 26);
		panelControl.add(textField_Steps);
		textField_Steps.setColumns(10);

		btnSendSteps = new JButton("Send");
		btnSendSteps.setBounds(199, 77, 75, 29);
		panelControl.add(btnSendSteps);

		lblGain = new JLabel("Gain");
		lblGain.setBounds(39, 34, 34, 16);
		panelControl.add(lblGain);

		btnSendGain = new JButton("Send");
		btnSendGain.setBounds(18, 77, 75, 29);
		panelControl.add(btnSendGain);


		DefaultComboBoxModel<String> gainPosModel = new DefaultComboBoxModel<String>(gainPos);
		gainComboBox = new JComboBox<String>(gainPosModel);

		gainComboBox.setBounds(19, 52, 74, 27);
		panelControl.add(gainComboBox);

		panelCalibrate = new JPanel();
		tabbedPane.addTab("Calibrate", null, panelCalibrate, null);
		tabbedPane.setEnabledAt(2, false);
		panelCalibrate.setLayout(null);

		Object[][] gainData = { { 1, 85, 85, 90 },
								{ 3, 55, 55, 60 },
								{ 10, 25, 25, 25 },
								{ 30, 0, 0, 0 } };

		String[] columnNames = { "Gain:", "X degrees", "Y degrees", "Z degrees" };

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(null);
		scrollPane.setBounds(13, 6, 276, 84);
		scrollPane.setBorder(null);
		panelCalibrate.add(scrollPane);

		fieldTable = new JFormattedTextField(IntegerEditor.createFormatter("###"));

		table = createTable(gainData, columnNames, fieldTable);
		setTableAlignment(table);

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowSelectionAllowed(false);
		table.setGridColor(Color.black);
		table.setShowGrid(true);
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(true);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		
		table.setFillsViewportHeight(true);
		
		scrollPane.setViewportView(table);

		btnSave = new JButton("Save");
		btnSave.setBounds(94, 92, 65, 29);
		panelCalibrate.add(btnSave);

		btnLoad = new JButton("Load");
		btnLoad.setBounds(16, 92, 65, 29);
		panelCalibrate.add(btnLoad);
		
		btnSaveDefault = new JButton("Save Default");
		btnSaveDefault.setBounds(172, 92, 117, 29);
		panelCalibrate.add(btnSaveDefault);

		panelAbout = new JPanel();
		tabbedPane.addTab("About", null, panelAbout, null);
		panelAbout.setLayout(null);

		String aboutText = ("<html><center>STM Knob Controller 2.0<br>Made by Dale Mitchell<br>mitchell.1367@osu.edu<br><a href=\"https://git.io/Dales-STM-Knob-Controller\">https://git.io/Dales-STM-Knob-Controller</a></center></html>");
		editorPaneAbout = new JEditorPane(new HTMLEditorKit().getContentType(), aboutText);
		editorPaneAbout.setText(aboutText);

		Font font = UIManager.getFont("Label.font");
		String bodyRule = "body { font-family: " + font.getFamily() + "; " + "font-size: " + font.getSize() + "pt; }";
		((HTMLDocument) editorPaneAbout.getDocument()).getStyleSheet().addRule(bodyRule);

		editorPaneAbout.setBounds(6, 23, 290, 72);
		editorPaneAbout.setEditable(false); // as before
		editorPaneAbout.setFocusable(true);
		editorPaneAbout.setBackground(null); // this is the same as a JLabel
		editorPaneAbout.setBorder(null); // remove the border
		editorPaneAbout.setOpaque(false);
		panelAbout.add(editorPaneAbout);

		contentPane.setLayout(gl_contentPane);

	}

	public JTable createTable(Object[][] data, String[] cols, final JFormattedTextField field) {

		TableCellEditor editor = new IntegerEditor(0, 180);
		field.setHorizontalAlignment(SwingConstants.CENTER);
		JTable table = new JTable(data, cols) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 9174557519288847907L;

			@Override
			public TableCellEditor getCellEditor(int row, int column) {
				int modelColumn = convertColumnIndexToModel(column);
				if (modelColumn > 0) {
					return editor;
				} else {
					return null;
				}
			}
		};
		return table;
	}

	public void setTableAlignment(JTable table) {
		// table header alignment
		JTableHeader header = table.getTableHeader();
		DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
		header.setDefaultRenderer(renderer);
		renderer.setHorizontalAlignment(JLabel.CENTER);

		// table content alignment
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		int rowNumber = table.getColumnCount();
		for (int i = 0; i < rowNumber; i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}
	}

	private void loadCalibrationData() {
		textArea.append("Loading servo calibration data.\n");
		serialSend.println("L"); // request calibration data
		
		String tableDataString = serialRead.nextLine();
		System.out.print("Loading: ");
		System.out.println(tableDataString);
		//String tableDataString = "[[1, 85, 85, 90], [3, 55, 55, 60], [10, 25, 25, 25], [30, 0, 0, 0]]";
		int nRow = table.getRowCount(), nCol = table.getColumnCount();
		int[][] tableData = new int[nRow][nCol];
		tableData = stringToDeep(tableDataString); 
	    for (int i = 0 ; i < nRow ; i++){
	        for (int j = 0 ; j < nCol ; j++){
	            table.setValueAt(tableData[i][j], i, j);
	        }
	    }
	
	}
	
	private void saveCalibrationData(JTable table) {
		textArea.append("Saving servo calibration data.\n");
	    int nRow = table.getRowCount(), nCol = table.getColumnCount();
	    int[][] tableData = new int[nRow][nCol];
	    for (int i = 0 ; i < nRow ; i++){
	        for (int j = 0 ; j < nCol ; j++){
	            tableData[i][j] = (int) table.getValueAt(i,j);
	        }
	    }
	    String str = Arrays.deepToString(tableData);
	    System.out.print("Saving:   ");
	    System.out.println(str);
		serialSend.println("S"); // instruct microcontroller to receive calibration data
		serialSend.println(str);
//		this.btnLoad.setEnabled(false);
//		this.btnSave.setEnabled(false);
		
	}
	
	private void saveDefaultCalibrationData() {
		textArea.append("Saving default servo calibration data.\n");
		
	    String defaultData = "[[1, 85, 85, 90], [3, 55, 55, 60], [10, 25, 25, 25], [30, 0, 0, 0]]";
	    System.out.print("Saving default:   ");
	    System.out.println(defaultData);
		serialSend.println("S"); // instruct microcontroller to receive calibration data
		serialSend.println(defaultData);
//		this.btnLoad.setEnabled(false);
//		this.btnSave.setEnabled(false);
		
	}
	
	private static int[][] stringToDeep(String str) {
	    int row = 0;
	    int col = 0;
	    for (int i = 0; i < str.length(); i++) {
	        if (str.charAt(i) == '[') {
	            row++;
	        }
	    }
	    row--;
	    for (int i = 0;; i++) {
	        if (str.charAt(i) == ',') {
	            col++;
	        }
	        if (str.charAt(i) == ']') {
	            break;
	        }
	    }
	    col++;

	    String[][] outString = new String[row][col];

	    str = str.replaceAll("\\[", "").replaceAll("\\]", "");

	    String[] s1 = str.split(", ");

	    for (int i = 0, j = -1; i < s1.length; i++) {
	        if (i % col == 0) {
	            j++;
	        }
	        outString[j][i % col] = s1[i];
	        
	    }
	    
	    int[][] out = new int[4][4];
	    for (int i = 0 ; i < 4 ; i++){
	        for (int j = 0 ; j < 4 ; j++){
	            out[i][j] = Integer.parseInt(outString[i][j]);
	        }
	    }
	    
	    return out;
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException ex) {
		}
		return false;
	}

	private void populateSerialList() {
		portNames = SerialPort.getCommPorts();
		portList.removeAllItems();
		for (int i = 0; i < portNames.length; i++)
			portList.addItem(portNames[i].getSystemPortName());
	}

	private void disconnectSerial() {
		chosenPort.closePort();
		tabbedPane.setEnabledAt(1, false);
		tabbedPane.setEnabledAt(2, false);
		portList.setEnabled(true);
		btnConnect.setText("Connect");
		lblStatusImage.setIcon(new ImageIcon(images.get(0)));
		tabbedPane.setSelectedIndex(0);
		lblConnectStatus.setText("Disconnected");
		textArea.append("Port closed.\n");
		textArea.append("Select the motor driver's serial port, then \n" + "click Connect to enable Control/Calibrate.\n");
	}

	private void createEvents() {

		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (btnConnect.getText().equals("Connect")) {
					/*
					 * Attempt to connect to the serial port.
					 */
					chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
					chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
					chosenPort.setBaudRate(115200);
					if (chosenPort.openPort()) {
						userToggleSerial = false;
						btnConnect.setText("Disconnect");
						portList.setEnabled(false);
						lblStatusImage.setIcon(new ImageIcon(images.get(1)));
						lblConnectStatus.setText("Connected");
						tabbedPane.setEnabledAt(1, true);
						tabbedPane.setEnabledAt(2, true);

						textArea.append("Port opened successfully.\n");

						/*
						 * Create a new thread for sending data to the Arduino.
						 */
						Thread serialThread = new Thread() {
							@Override
							public void run() {
								while (chosenPort.isOpen() && userToggleSerial == false) {
									serialSend = new PrintWriter(chosenPort.getOutputStream());
									serialRead = new Scanner(chosenPort.getInputStream());
									try {
										Thread.sleep(10);
									} catch (InterruptedException e1) {
										e1.printStackTrace();
									}
									serialSend.flush();
								}
								if (!userToggleSerial) {
									textArea.append("USB error: cable no longer attached.\n");
									serialRead.close();
								}
								disconnectSerial();
							}
						};
						serialThread.start();
					} else {
						textArea.append("Error: Port busy.\n");
					}

				} else {
					/*
					 * Disconnect from the serial port.
					 */
					userToggleSerial = true;
					chosenPort.closePort();

				}
			}
		});

		portList.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent e) {
				populateSerialList();
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}
		});

		btnSendSteps.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String stepsInput = textField_Steps.getText();
				btnSendSteps.setEnabled(false);
				if (isInteger(stepsInput)) {
					if (rdbtnX.isSelected()) {
						userInput = "X_V_" + stepsInput;
					} else if (rdbtnY.isSelected()) {
						userInput = "Y_V_" + stepsInput;
					} else if (rdbtnZ.isSelected()) {
						userInput = "Z_V_" + stepsInput;
					} else {
						JOptionPane.showMessageDialog(null, "Axis not selected");
					}
					serialSend.println(userInput);
					textArea.append("Sent: " + userInput + "\n");
				} else {
					JOptionPane.showMessageDialog(null, "Error: Steps input is not an integer");
					textArea.append("Error: input is not an integer\n");
				}
				btnSendSteps.setEnabled(true);

			}
		});

		btnSendGain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String gainInputUser = gainPos[gainComboBox.getSelectedIndex()];
				btnSendGain.setEnabled(false);
				if (rdbtnX.isSelected()) {
					userInput = "X_G_" + gainInputUser;
				} else if (rdbtnY.isSelected()) {
					userInput = "Y_G_" + gainInputUser;
				} else if (rdbtnZ.isSelected()) {
					userInput = "Z_G_" + gainInputUser;
				} else {
					JOptionPane.showMessageDialog(null, "Axis not selected");
				}
				serialSend.println(userInput);
				btnSendGain.setEnabled(true);
				textArea.append("Sent: " + userInput + "\n");

			}
		});

		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadCalibrationData();

			}
		});

		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveCalibrationData(table);
			}
		});
		
		btnSaveDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveDefaultCalibrationData();
			}
		});

		editorPaneAbout.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().browse(e.getURL().toURI());
						} catch (IOException | URISyntaxException e1) {
							JOptionPane.showMessageDialog(null,
									"Cannot open link, try typing it into a web browser manually.");
							e1.printStackTrace();
						}
					}
				}
			}
		});

	}
}
