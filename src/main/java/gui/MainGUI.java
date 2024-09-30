package gui;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

import common.InputName;
import common.SelectDiscipline;
import decathlon.*;
import heptathlon.*;
import excel.ExcelPrinter;                           //INGRID


public class MainGUI {

    private JTextField nameField;
    private JTextField resultField;
    private JComboBox<String> competitionTypeBox;
    private JComboBox<String> disciplineBox;
    private JTextArea outputArea;

    // INGRID
    private String excelName = "Test";
    private int iRow = 0;
    private int iColumn = 0;
    private Object[][] myResultDecathlon = new Object[40][11];
    private Object[][] myResultHeptathlon = new Object[40][8];
    private String myCompetition = new String();
    String[] competitorNrDecathlon = new String[99];
    String[] competitorNrHeptathlon = new String[99];
    private int diciplineNo = 0;
    LocalDate currentDate = LocalDate.now();

    //SLUT INGRID


    public static void main(String[] args) {
        new MainGUI().createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Track and Field Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 900);

        JPanel panel = new JPanel(new GridLayout(6, 1));

        // Input for competitor's name
        nameField = new JTextField(20);
        panel.add(new JLabel("Enter Competitor's Name:"));
        panel.add(nameField);


        String[] competitionType = {
                "Decathlon", "Heptathlon"
        };
        competitionTypeBox = new JComboBox<>(competitionType);
        panel.add(new JLabel("Select competition:"));
        panel.add(competitionTypeBox);

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
        panel.add(new JLabel("Select Discipline:"));
        panel.add(disciplineBox);

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
        panel.add(new JLabel("Enter Result:"));
        panel.add(resultField);

        // Button to calculate and display result
        JButton calculateButton = new JButton("Calculate Score");
        calculateButton.addActionListener(new CalculateButtonListener());
        panel.add(calculateButton);

        // Output area
        outputArea = new JTextArea(5, 40);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane);

        //INGRID
        // Button to save results result
        JButton saveButton = new JButton("Save Score");
        saveButton.addActionListener(new SaveButtonListener());
        panel.add(saveButton);
        //frame.add(panel);
        //frame.setVisible(true);
        //SLUT INGRID


        frame.add(panel);
        frame.setVisible(true);
    }

    private class CalculateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
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
                            diciplineNo = 1;                                        //INGRID till tabellen
                            break;
                        case "400m (Measured in seconds)":
                            Deca400M deca400M = new Deca400M();
                            score = deca400M.calculateResult(result);
                            diciplineNo = 2;                                        //INGRID till tabellen
                            break;
                        case "1500m (Measured in seconds)":
                            Deca1500M deca1500M = new Deca1500M();
                            score = deca1500M.calculateResult(result);
                            diciplineNo = 3;                                        //INGRID till tabellen
                            break;
                        case "110m Hurdles (Measured in seconds)":
                            Deca110MHurdles deca110MHurdles = new Deca110MHurdles();
                            score = deca110MHurdles.calculateResult(result);
                            diciplineNo = 4;                                        //INGRID till tabellen
                            break;
                        case "Long Jump (Measured in centimeters)":
                            DecaLongJump decaLongJump = new DecaLongJump();
                            score = decaLongJump.calculateResult(result);
                            diciplineNo = 5;                                        //INGRID till tabellen
                            break;
                        case "High Jump (Measured in centimeters)":
                            DecaHighJump decaHighJump = new DecaHighJump();
                            score = decaHighJump.calculateResult(result);
                            diciplineNo = 6;                                        //INGRID till tabellen
                            break;
                        case "Pole Vault (Measured in centimeters)":
                            DecaPoleVault decaPoleVault = new DecaPoleVault();
                            score = decaPoleVault.calculateResult(result);
                            diciplineNo = 7;                                        //INGRID till tabellen
                            break;
                        case "Discus Throw (Measured in meters)":
                            DecaDiscusThrow decaDiscusThrow = new DecaDiscusThrow();
                            score = decaDiscusThrow.calculateResult(result);
                            diciplineNo = 8;                                        //INGRID till tabellen
                            break;
                        case "Javelin Throw (Measured in meters)":
                            DecaJavelinThrow decaJavelinThrow = new DecaJavelinThrow();
                            score = decaJavelinThrow.calculateResult(result);
                            diciplineNo = 9;                                        //INGRID till tabellen
                            break;
                        case "Shot Put (Measured in meters)":
                            DecaShotPut decaShotPut = new DecaShotPut();
                            score = decaShotPut.calculateResult(result);
                            diciplineNo = 10;                                        //INGRID till tabellen
                            break;
                    }
                } else if (competition.equals("Heptathlon")) {
                    switch (discipline) {
                        case "100m Hurdles (Measured in seconds)":
                            Hep100MHurdles hep100MHurdles = new Hep100MHurdles();
                            score = hep100MHurdles.calculateResult(result);
                            diciplineNo = 1;                                        //INGRID till tabellen
                            break;
                        case "200m (Measured in seconds)":
                            Hep200M hep200M = new Hep200M();
                            score = hep200M.calculateResult(result);
                            diciplineNo = 2;                                        //INGRID till tabellen
                            break;
                        case "800m (Measured in seconds)":
                            Hep800M hep800M = new Hep800M();
                            score = hep800M.calculateResult(result);
                            diciplineNo = 3;                                        //INGRID till tabellen
                            break;
                        case "Long Jump (Measured in centimeters)":
                            HeptLongJump heptLongJump = new HeptLongJump();
                            score = heptLongJump.calculateResult(result);
                            diciplineNo = 4;                                        //INGRID till tabellen
                            break;
                        case "High Jump (Measured in centimeters)":
                            HeptHightJump heptHightJump = new HeptHightJump();
                            score = heptHightJump.calculateResult(result);
                            diciplineNo = 5;                                        //INGRID till tabellen
                            break;
                        case "Javelin Throw (Measured in meters)":
                            HeptJavelinThrow heptJavelinThrow = new HeptJavelinThrow();
                            score = heptJavelinThrow.calculateResult(result);
                            diciplineNo = 6;                                        //INGRID till tabellen
                            break;
                        case "Shot Put (Measured in meters)":
                            HeptShotPut heptShotPut = new HeptShotPut();
                            score = heptShotPut.calculateResult(result);
                            diciplineNo = 7;                                        //INGRID till tabellen
                            break;
                    }
                }


                outputArea.append("Competitor: " + name + "\n");
                outputArea.append("Competition: " + competition + "\n");
                outputArea.append("Discipline: " + discipline + "\n");
                outputArea.append("Result: " + result + "\n");
                outputArea.append("Score: " + score + "\n\n");


                //INGRID
                //One table for Decathlon and one for Heptathlon
                if (competition.equals("Decathlon")) {                                                   //DECA
                    //Write headings
                    myResultDecathlon[0][0] = "Scoretabell";
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
                    myResultDecathlon[i][0] = name;
                    iColumn = diciplineNo;
                    myResultDecathlon[i][iColumn] = score;
                    myCompetition = competition;

                } else if (competition.equals("Heptathlon")) {                                                                                 //HEPTA

                    myResultHeptathlon[0][0] = "Scoretabell";
                    myResultHeptathlon[0][1] = "100m Hurdles";
                    myResultHeptathlon[0][2] = "200m";
                    myResultHeptathlon[0][3] = "800m";
                    myResultHeptathlon[0][4] = "Long Jump";
                    myResultHeptathlon[0][5] = "High Jump";
                    myResultHeptathlon[0][6] = "Javelin Throw";
                    myResultHeptathlon[0][7] = "Shot put";
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
                    myResultHeptathlon[i][0] = name;
                    iColumn = diciplineNo;
                    myResultHeptathlon[i][iColumn] = score;
                    myCompetition = competition;
                }
                //SLUT INGRID

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number for the result.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidResultException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Invalid Result", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    //  Writes the excel sheets
    private class SaveButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            ExcelPrinter excelPrinter = null;

            try {
                excelPrinter = new ExcelPrinter("PowerScore");    //Excel sheet is created

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
            //SLUT INGRID
        }
    }
}



