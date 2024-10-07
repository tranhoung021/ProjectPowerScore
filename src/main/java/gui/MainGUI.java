package gui;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.io.File;
import java.util.List;
import common.InputName;
import common.SelectDiscipline;
import decathlon.*;
import excel.ExcelTable;
import heptathlon.*;
import excel.ExcelPrinter;                           //INGRID
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static java.lang.Integer.parseInt;


public class MainGUI {
    private JFrame frame;

    private JTextField nameField;
    private JTextField resultField;
    private JComboBox<String> competitionTypeBox;
    private JComboBox<String> disciplineBox;
    private JTextArea outputArea;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private File excelFile;

    private int iRow = 0;
    private int iColumn = 0;
    private Object[][] myResultDecathlon = new Object[41][12];
    private Object[][] myResultHeptathlon = new Object[41][9];
    private String myCompetition = new String();
    String[] competitorNrDecathlon = new String[99];
    String[] competitorNrHeptathlon = new String[99];
    private int diciplineNo = 0;
    private int myTotalScoreDecathlon=0;
    private int myTotalScoreHeptathlon=0;
    private int addingScore=0;
    LocalDateTime currentDateTime = LocalDateTime.now();


    public static void main(String[] args) {
        // Set the FlatLaf look and feel
        FlatLightLaf.setup();

        // Ensure GUI creation happens on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new MainGUI().createAndShowGUI());
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Track and Field Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1600, 900);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel for existing components
        JPanel leftPanel = new JPanel(new GridLayout(12, 1));
        leftPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Input Data",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        leftPanel.setPreferredSize(new Dimension(450, 900)); // Set preferred size for left panel

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Input for competitor's name
        nameField = new JTextField(20);
        leftPanel.add(new JLabel("Enter Competitor's Name:"), gbc);
        gbc.gridy++;
        leftPanel.add(nameField, gbc);


        String[] competitionType = {"Decathlon", "Heptathlon"};
        competitionTypeBox = new JComboBox<>(competitionType);
        gbc.gridy++;
        leftPanel.add(new JLabel("Select competition:"), gbc);
        gbc.gridy++;
        leftPanel.add(competitionTypeBox, gbc);

        // Dropdown for selecting discipline
        String[] disciplinesDeca = {
                "100m (Measured in seconds)", "400m (Measured in seconds)", "1500m (Measured in seconds)",
                "110m Hurdles (Measured in seconds)", "Long Jump (Measured in centimeters)", "High Jump (Measured in centimeters)",
                "Pole Vault (Measured in centimeters)", "Discus Throw (Measured in meters)",
                "Javelin Throw (Measured in meters)", "Shot Put (Measured in meters)"
        };

        String[] disciplinesHept = {
                "100m Hurdles (Measured in seconds)", "200m (Measured in seconds)",
                "800m (Measured in seconds)", "Long Jump (Measured in centimeters)",
                "High Jump (Measured in centimeters)", "Javelin Throw (Measured in meters)", "Shot Put (Measured in meters)"
        };


        disciplineBox = new JComboBox<>();
        gbc.gridy++;
        leftPanel.add(new JLabel("Select Discipline:"), gbc);
        gbc.gridy++;
        leftPanel.add(disciplineBox, gbc);

        // Populate disciplineBox with decathlon disciplines by default
        for (String discipline : disciplinesDeca) {
            disciplineBox.addItem(discipline);
        }

        competitionTypeBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCompetition = (String) competitionTypeBox.getSelectedItem();


                disciplineBox.removeAllItems();

                if (selectedCompetition.equals("Decathlon")) {
                    for (String discipline : disciplinesDeca) {
                        disciplineBox.addItem(discipline);
                    }
                } else if (selectedCompetition.equals("Heptathlon")) {
                    for (String discipline : disciplinesHept) {
                        disciplineBox.addItem(discipline);
                    }
                }
            }
        });

        // Input for result
        resultField = new JTextField(10);
        gbc.gridy++;
        leftPanel.add(new JLabel("Enter Result:"), gbc);
        gbc.gridy++;
        leftPanel.add(resultField, gbc);

        // Button to calculate and display result
        JButton calculateButton = new JButton("Calculate Score & Save");
        calculateButton.addActionListener(new CalculateButtonListener());
        gbc.gridy++;
        leftPanel.add(calculateButton, gbc);
        // Output area
        outputArea = new JTextArea(5, 40);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        gbc.gridy++;
        leftPanel.add(scrollPane, gbc);


        /*
        // Button to save results
        JButton saveButton = new JButton("Save Score");
        saveButton.addActionListener(new SaveButtonListener());
        gbc.gridy++;
        leftPanel.add(saveButton, gbc);
        */

        // Panel for ExcelReader components
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Excel Data",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        rightPanel.setPreferredSize(new Dimension(750, 900)); // Set preferred size for right panel

        // Panel for buttons and search field
        JPanel controlPanel = new JPanel(new GridBagLayout());
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2); // Add some padding
        rightPanel.add(controlPanel, BorderLayout.NORTH);

        // File chooser button
        JButton openButton = new JButton("Open Excel File");
        openButton.addActionListener(new OpenButtonListener());
        openButton.setPreferredSize(new Dimension(150, 30)); // Set preferred size
        gbc.gridx = 0;
        gbc.gridy = 0;
        controlPanel.add(openButton, gbc); // Add to control panel

        // Buttons to switch between sheets
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton decathlonButton = new JButton("Decathlon");
        decathlonButton.setPreferredSize(new Dimension(100, 30)); // Set preferred size
        JButton heptathlonButton = new JButton("Heptathlon");
        heptathlonButton.setPreferredSize(new Dimension(100, 30)); // Set preferred size
        buttonPanel.add(decathlonButton);
        buttonPanel.add(heptathlonButton);
        gbc.gridx = 0;
        gbc.gridy = 1;
        controlPanel.add(buttonPanel, gbc);

        // Search field with label and placeholder text
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30)); // Set preferred size
        searchField.setText("Enter search term...");
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Enter search term...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Enter search term...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        searchPanel.add(searchField);
        gbc.gridx = 0;
        gbc.gridy = 2;
        controlPanel.add(searchPanel, gbc);


        // Table setup
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        JScrollPane tableScrollPane = new JScrollPane(table);
        rightPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Add panels to main panel
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);

        // Action listeners for sheet buttons
        decathlonButton.addActionListener(e -> loadSheetData("Decathlon"));
        heptathlonButton.addActionListener(e -> loadSheetData("Heptathlon"));

        // Action listener for search field
        searchField.addActionListener(new SearchListener());
    }



    private class CalculateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Competitor's name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String competition = (String) competitionTypeBox.getSelectedItem();
            String discipline = (String) disciplineBox.getSelectedItem();
            String resultText = resultField.getText();

            try {
                double result = Double.parseDouble(resultText);

                int score = 0;
                if (competition.equals("Decathlon")) {
                    switch (discipline) {
                        case "100m (Measured in seconds)":
                            Deca100M deca100M = new Deca100M();
                            score = deca100M.calculateResult(result);
                            diciplineNo = 1;
                            break;
                        case "400m (Measured in seconds)":
                            Deca400M deca400M = new Deca400M();
                            score = deca400M.calculateResult(result);
                            diciplineNo = 2;
                            break;
                        case "1500m (Measured in seconds)":
                            Deca1500M deca1500M = new Deca1500M();
                            score = deca1500M.calculateResult(result);
                            diciplineNo = 3;
                            break;
                        case "110m Hurdles (Measured in seconds)":
                            Deca110MHurdles deca110MHurdles = new Deca110MHurdles();
                            score = deca110MHurdles.calculateResult(result);
                            diciplineNo = 4;
                            break;
                        case "Long Jump (Measured in centimeters)":
                            DecaLongJump decaLongJump = new DecaLongJump();
                            score = decaLongJump.calculateResult(result);
                            diciplineNo = 5;
                            break;
                        case "High Jump (Measured in centimeters)":
                            DecaHighJump decaHighJump = new DecaHighJump();
                            score = decaHighJump.calculateResult(result);
                            diciplineNo = 6;
                            break;
                        case "Pole Vault (Measured in centimeters)":
                            DecaPoleVault decaPoleVault = new DecaPoleVault();
                            score = decaPoleVault.calculateResult(result);
                            diciplineNo = 7;
                            break;
                        case "Discus Throw (Measured in meters)":
                            DecaDiscusThrow decaDiscusThrow = new DecaDiscusThrow();
                            score = decaDiscusThrow.calculateResult(result);
                            diciplineNo = 8;
                            break;
                        case "Javelin Throw (Measured in meters)":
                            DecaJavelinThrow decaJavelinThrow = new DecaJavelinThrow();
                            score = decaJavelinThrow.calculateResult(result);
                            diciplineNo = 9;
                            break;
                        case "Shot Put (Measured in meters)":
                            DecaShotPut decaShotPut = new DecaShotPut();
                            score = decaShotPut.calculateResult(result);
                            diciplineNo = 10;
                            break;
                    }
                } else if (competition.equals("Heptathlon")) {
                    switch (discipline) {
                        case "100m Hurdles (Measured in seconds)":
                            Hep100MHurdles hep100MHurdles = new Hep100MHurdles();
                            score = hep100MHurdles.calculateResult(result);
                            diciplineNo = 1;
                            break;
                        case "200m (Measured in seconds)":
                            Hep200M hep200M = new Hep200M();
                            score = hep200M.calculateResult(result);
                            diciplineNo = 2;
                            break;
                        case "800m (Measured in seconds)":
                            Hep800M hep800M = new Hep800M();
                            score = hep800M.calculateResult(result);
                            diciplineNo = 3;
                            break;
                        case "Long Jump (Measured in centimeters)":
                            HeptLongJump heptLongJump = new HeptLongJump();
                            score = heptLongJump.calculateResult(result);
                            diciplineNo = 4;
                            break;
                        case "High Jump (Measured in centimeters)":
                            HeptHightJump heptHightJump = new HeptHightJump();
                            score = heptHightJump.calculateResult(result);
                            diciplineNo = 5;
                            break;
                        case "Javelin Throw (Measured in meters)":
                            HeptJavelinThrow heptJavelinThrow = new HeptJavelinThrow();
                            score = heptJavelinThrow.calculateResult(result);
                            diciplineNo = 6;
                            break;
                        case "Shot Put (Measured in meters)":
                            HeptShotPut heptShotPut = new HeptShotPut();
                            score = heptShotPut.calculateResult(result);
                            diciplineNo = 7;
                            break;
                    }
                }


                outputArea.append("Competitor: " + name + "\n");
                outputArea.append("Competition: " + competition + "\n");
                outputArea.append("Discipline: " + discipline + "\n");
                outputArea.append("Result: " + result + "\n");
                outputArea.append("Score: " + score + "\n\n");


                //One table for Decathlon and one for Heptathlon
                if (competition.equals("Decathlon")) {
                    //Write headings
                    myResultDecathlon[0][0] = "Participant";
                    myResultDecathlon[0][1] = "100m";
                    myResultDecathlon[0][2] = "400m";
                    myResultDecathlon[0][3] = "1500m";
                    myResultDecathlon[0][4] = "110m Hurdles";
                    myResultDecathlon[0][5] = "Long Jump";
                    myResultDecathlon[0][6] = "High Jump";
                    myResultDecathlon[0][7] = "Pole Vault";
                    myResultDecathlon[0][8] = "Discus Throw";
                    myResultDecathlon[0][9] = "Javelin Throw";
                    myResultDecathlon[0][10] = "Shot put";
                    myResultDecathlon[0][11] = "Total Score";

                    competitorNrDecathlon[0] = "Decathlon";

                    // Check in the array competitorNrDecathlon if the competitor already exists
                    int i = 0;

                    while (i < competitorNrDecathlon.length) {
                        if (Objects.equals(competitorNrDecathlon[i], name)) {         //The competitor is found in the array
                            break;
                        }
                        if (competitorNrDecathlon[i] == null) {         //If it is a new competitor, they are added to the array
                            competitorNrDecathlon[i] = name;
                            break;
                        }
                        i = i + 1;
                    }


                    //the result is added to the competitor row in the result table
                    if (i<myResultDecathlon.length){
                    myResultDecathlon[i][0] = name;
                    iColumn = diciplineNo;
                    myResultDecathlon[i][iColumn] = score;
                    myCompetition = competition;

                    //The total score for the competitor is calculated
                    myTotalScoreDecathlon = 0;
                    for (int iTot = 1; iTot < (11); iTot++) {
                        //System.out.println(iTot);                                    //DEBUG
                        System.out.println("result" + myResultDecathlon[i][iTot]);
                        if (myResultDecathlon[i][iTot] != null) {
                            addingScore = (int) myResultDecathlon[i][iTot];
                            myTotalScoreDecathlon = myTotalScoreDecathlon + addingScore;
                            System.out.println("total" + myTotalScoreDecathlon);
                        }
                    }

                    myResultDecathlon[i][11] = myTotalScoreDecathlon;
                    } else {
                        JOptionPane.showMessageDialog(frame, "Maximum limit of competitors is 40, this is number " + i);
                    }

                } else if (competition.equals("Heptathlon")) {                                                                                 //HEPTA

                    myResultHeptathlon[0][0] = "Participant";
                    myResultHeptathlon[0][1] = "100m Hurdles";
                    myResultHeptathlon[0][2] = "200m";
                    myResultHeptathlon[0][3] = "800m";
                    myResultHeptathlon[0][4] = "Long Jump";
                    myResultHeptathlon[0][5] = "High Jump";
                    myResultHeptathlon[0][6] = "Javelin Throw";
                    myResultHeptathlon[0][7] = "Shot put";
                    myResultHeptathlon[0][8] = "Total Score";

                    competitorNrHeptathlon[0] = "Heptathlon";

                    // Check in the array competitorNr if the competitor already exists
                    int i = 0;


                    while (i < competitorNrHeptathlon.length) {
                        if (Objects.equals(competitorNrHeptathlon[i], name)) {         //The competitor is found in the array
                            break;
                        }
                        if (competitorNrHeptathlon[i] == null) {         //If it is a new competitor, they are added to the array
                            competitorNrHeptathlon[i] = name;
                            break;
                        }
                        i = i + 1;
                    }



                    //the result is added to the competitor row in the result table
                    if (i<myResultHeptathlon.length){
                        myResultHeptathlon[i][0] = name;
                        iColumn = diciplineNo;
                        myResultHeptathlon[i][iColumn] = score;
                        myCompetition = competition;
                        myTotalScoreHeptathlon = 0;
                        for (int iTot = 1; iTot < (8); iTot++) {
                            //System.out.println(iTot);                                    //DEBUG
                            //System.out.println("result" + myResultHeptathlon[i][iTot]);
                            if (myResultHeptathlon[i][iTot] != null) {
                                addingScore = (int) myResultHeptathlon[i][iTot];
                                myTotalScoreHeptathlon = myTotalScoreHeptathlon + addingScore;
                                System.out.println("total" + myTotalScoreHeptathlon);
                            }
                        }

                        myResultHeptathlon[i][8] = myTotalScoreHeptathlon;
                    } else {
                        JOptionPane.showMessageDialog(frame, "Maximum limit of competitors is 40, this is number " + i);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number for the result.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidResultException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Invalid Result", JOptionPane.ERROR_MESSAGE);
            }


            ExcelPrinter excelPrinter = null;
            try {
                excelPrinter = new ExcelPrinter("PowerScore");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            String sheetName = null;
            if (competitorNrDecathlon[0] != null) {
                try {
                    excelPrinter.add(myResultDecathlon, "Decathlon");
                    sheetName = "Decathlon";
                    File excelFile = getExcelFile(); // Call the getExcelFile method to get the file
                    excelPrinter.write(excelFile);
                    System.out.println("Har skrivit Decathlontabellen");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            if (competitorNrHeptathlon[0] != null) {
                try {
                    excelPrinter.add(myResultHeptathlon, "Heptathlon");
                    sheetName = "Heptathlon";
                    File excelFile = getExcelFile(); // Call the getExcelFile method to get the file
                    excelPrinter.write(excelFile);
                    System.out.println("Har skrivit Heptathlontabben");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            if (sheetName != null) {
                loadSheetData(sheetName); // Load data into the GUI
            }


        }
        }


    private File getExcelFile() {
        if (excelFile == null || !excelFile.exists()) {
            String directoryPath = "C:/Eclipse/resultat_";
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");
            String fileName = "PowerScore" + currentDateTime.format(myFormatObj) + ".xlsx";
            excelFile = new File(directoryPath + fileName);
        }
        return excelFile;
    }

    //  Writes the excel sheets
    /*private class SaveButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            ExcelPrinter excelPrinter = null;

            try {
                //create unique file name
                LocalDateTime currentDateTime = LocalDateTime.now();
                DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");
                excelPrinter = new ExcelPrinter("PowerScore"+currentDateTime.format(myFormatObj));    //Excel sheet is created

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("Excelark skapad");


            if (competitorNrDecathlon[0] !=null) {
                try {

                    excelPrinter.add(myResultDecathlon, "Decathlon");        //Add all competitor results to excel
                    excelPrinter.write();
                    System.out.println("Har skrivit Decathlontabellen");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
                if (competitorNrHeptathlon[0] !=null) {
                try {
                    //excelPrinter = new ExcelPrinter("PowerScore");
                    excelPrinter.add(myResultHeptathlon, "Heptathlon");        //Add all competitor results to excel
                    excelPrinter.write();
                    System.out.println("Har skrivit Heptathlontabben");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        }
    }*/

    private void loadSheetData(String sheetName) {
        File excelFile = getExcelFile();
        if (excelFile.exists()) {
            try {
                XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
                XSSFSheet sheet = workbook.getSheet(sheetName);
                if (sheet != null) {
                    try {
                        ExcelTable reader = new ExcelTable();
                        List<String[]> data = reader.readExcelFile(excelFile, sheetName);

                        // Load column names
                        if (!data.isEmpty()) {
                            String[] columnNames = data.get(0);
                            for (String columnName : columnNames) {
                                boolean columnExists = false;
                                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                                    if (tableModel.getColumnName(i).equals(columnName)) {
                                        columnExists = true;
                                        break;
                                    }
                                }
                                if (!columnExists) {
                                    tableModel.addColumn(columnName);
                                }
                            }
                            data.remove(0);
                        }

                        // Load data rows
                        for (String[] rowData : data) {
                            tableModel.addRow(rowData);
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Error reading Excel file: " + ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please open an Excel file first.", "File Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException | InvalidFormatException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private class OpenButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                excelFile = fileChooser.getSelectedFile();
                loadSheetData("Decathlon"); // Load Decathlon sheet by default
            }
        }
    }

    private class SearchListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String text = searchField.getText();
            if (text.trim().length() == 0) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        }
    }
    
}



