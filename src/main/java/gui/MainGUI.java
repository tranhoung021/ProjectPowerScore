package gui;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



import java.awt.*;

import decathlon.*;
import heptathlon.*;


public class MainGUI {

    private JTextField nameField;
    private JTextField resultField;
    private JComboBox<String> competitionTypeBox;
    private JComboBox<String> disciplineBox;
    private JTextArea outputArea;

    public static void main(String[] args) {
        new MainGUI().createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Track and Field Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        JPanel panel = new JPanel(new GridLayout(6, 1));

        // Input for competitor's name
        nameField = new JTextField(20);
        panel.add(new JLabel("Enter Competitor's Name:"));
        panel.add(nameField);

        String [] competitionType = {
                "Decathlon","Heptathlon"
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
                if(competition.equals("Decathlon")) {
                    switch (discipline) {
                        case "100m (Measured in seconds)":
                            Deca100M deca100M = new Deca100M();
                            score = deca100M.calculateResult(result);
                            break;
                        case "400m (Measured in seconds)":
                            Deca400M deca400M = new Deca400M();
                            score = deca400M.calculateResult(result);
                            break;
                        case "1500m (Measured in seconds)":
                            Deca1500M deca1500M = new Deca1500M();
                            score = deca1500M.calculateResult(result);
                            break;
                        case "110m Hurdles (Measured in seconds)":
                            Deca110MHurdles deca110MHurdles = new Deca110MHurdles();
                            score = deca110MHurdles.calculateResult(result);
                            break;
                        case "Long Jump (Measured in centimeters)":
                            DecaLongJump decaLongJump = new DecaLongJump();
                            score = decaLongJump.calculateResult(result);
                            break;
                        case "High Jump (Measured in centimeters)":
                            DecaHighJump decaHighJump = new DecaHighJump();
                            score = decaHighJump.calculateResult(result);
                            break;
                        case "Pole Vault (Measured in centimeters)":
                            DecaPoleVault decaPoleVault = new DecaPoleVault();
                            score = decaPoleVault.calculateResult(result);
                            break;
                        case "Discus Throw (Measured in meters)":
                            DecaDiscusThrow decaDiscusThrow = new DecaDiscusThrow();
                            score = decaDiscusThrow.calculateResult(result);
                            break;
                        case "Javelin Throw (Measured in meters)":
                            DecaJavelinThrow decaJavelinThrow = new DecaJavelinThrow();
                            score = decaJavelinThrow.calculateResult(result);
                            break;
                        case "Shot Put (Measured in meters)":
                            DecaShotPut decaShotPut = new DecaShotPut();
                            score = decaShotPut.calculateResult(result);
                            break;
                    }
                } else if(competition.equals("Heptathlon")) {
                    switch (discipline) {
                        case "100m Hurdles (Measured in seconds)":
                            Hep100MHurdles hep100MHurdles = new Hep100MHurdles();
                            score = hep100MHurdles.calculateResult(result);
                            break;
                        case "200m (Measured in seconds)":
                            Hep200M hep200M = new Hep200M();
                            score = hep200M.calculateResult(result);
                            break;
                        case "800m (Measured in seconds)":
                            Hep800M hep800M = new Hep800M();
                            score = hep800M.calculateResult(result);
                            break;
                        case "Long Jump (Measured in centimeters)":
                            HeptLongJump heptLongJump = new HeptLongJump();
                            score = heptLongJump.calculateResult(result);
                            break;
                        case "High Jump (Measured in centimeters)":
                            HeptHightJump heptHightJump = new HeptHightJump();
                            score = heptHightJump.calculateResult(result);
                            break;
                        case "Javelin Throw (Measured in meters)":
                            HeptJavelinThrow heptJavelinThrow = new HeptJavelinThrow();
                            score = heptJavelinThrow.calculateResult(result);
                            break;
                        case "Shot Put (Measured in meters)":
                            HeptShotPut heptShotPut = new HeptShotPut();
                            score = heptShotPut.calculateResult(result);
                            break;
                    }
                }


                outputArea.append("Competitor: " + name + "\n");
                outputArea.append("Competition: " + competition + "\n");
                outputArea.append("Discipline: " + discipline + "\n");
                outputArea.append("Result: " + result + "\n");
                outputArea.append("Score: " + score + "\n\n");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number for the result.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
