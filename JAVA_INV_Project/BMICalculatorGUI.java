import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class BMI {
    private double height, weight;
    private String name;
    private int age;

    public BMI(String name, int age, double weight, double height) {
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.height = height;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public double getBMI() {
        return weight / (height * height);
    }

    public String getStatus() {
        double bmi = getBMI();
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi <= 24.9) {
            return "Normal range";
        } else if (bmi <= 29.9) {
            return "Overweight";
        } else {
            return "Obese " + getObeseClass(bmi);
        }
    }

    private String getObeseClass(double bmi) {
        if (bmi <= 34.9) {
            return "class I";
        } else if (bmi <= 39.9) {
            return "class II";
        } else {
            return "class III";
        }
    }

    public String getTips() {
        double bmi = getBMI();
        if (bmi < 18.5) {
            return "Tip: Try to include more nutritious, high-calorie foods in your diet.";
        } else if (bmi <= 24.9) {
            return "Tip: Keep up the good work maintaining your healthy weight!";
        } else if (bmi <= 29.9) {
            return "Tip: Incorporate more physical activity into your daily routine.";
        } else {
            return "Tip: Consult with a healthcare provider for personalized advice.";
        }
    }
}

public class BMICalculatorGUI extends JFrame {
    private JTextField nameField, ageField, weightField, heightField;
    private JComboBox<String> heightUnitBox;
    private JTextArea resultArea;
    private BufferedImage backgroundImage;

    public BMICalculatorGUI() {
        setTitle("BMI Calculator");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("BMI.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Custom panel to paint background image
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        panel.setLayout(new GridLayout(9, 2, 10, 10));
        setContentPane(panel);

        Font font = new Font("Arial", Font.PLAIN, 20);

        // Name input
        add(createLabel("Enter Your Name: ", font));
        nameField = new JTextField();
        nameField.setFont(font);
        add(nameField);

        // Age input
        add(createLabel("Enter Your Age: ", font));
        ageField = new JTextField();
        ageField.setFont(font);
        add(ageField);

        // Weight input
        add(createLabel("Enter Your Weight (kg): ", font));
        weightField = new JTextField();
        weightField.setFont(font);
        add(weightField);

        // Height input with unit selection
        add(createLabel("Enter Your Height: ", font));
        heightField = new JTextField();
        heightField.setFont(font);
        add(heightField);

        add(createLabel("Select Height Unit: ", font));
        String[] heightUnits = {"Meters", "Feet", "Inches", "Centimeters"};
        heightUnitBox = new JComboBox<>(heightUnits);
        heightUnitBox.setFont(font);
        add(heightUnitBox);

        // Calculate button
        JButton calculateButton = createButton("Calculate BMI", Color.BLUE, font);
        calculateButton.addActionListener(new CalculateButtonListener());
        add(calculateButton);

        // Add empty labels to maintain the grid layout
        add(new JLabel());
        add(new JLabel());

        // Result area
        resultArea = new JTextArea();
        resultArea.setFont(new Font("Arial", Font.PLAIN, 20));
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBackground(Color.WHITE);
        resultArea.setPreferredSize(new Dimension(200, 200));
        add(new JLabel()); // Empty label for layout
        add(new JScrollPane(resultArea));

        setVisible(true);
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(Color.BLACK);
        return label;
    }

    private JButton createButton(String text, Color color, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setBorderPainted(false);
        return button;
    }

    private class CalculateButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                double weight = Double.parseDouble(weightField.getText());
                double height = Double.parseDouble(heightField.getText());
                String heightUnit = (String) heightUnitBox.getSelectedItem();

                switch (heightUnit) {
                    case "Feet":
                        height *= 0.3048;
                        break;
                    case "Inches":
                        height *= 0.0254;
                        break;
                    case "Centimeters":
                        height *= 0.01;
                        break;
                }

                BMI bmi = new BMI(name, age, weight, height);
                double bmiValue = bmi.getBMI();
                String status = bmi.getStatus();
                String tips = bmi.getTips();

                resultArea.setText(String.format("Name: %s\nAge: %d\nWeight: %.2f kg\nHeight: %.2f meters\nBMI: %.2f\nStatus: %s\n%s",
                        name, age, weight, height, bmiValue, status, tips));
            } catch (NumberFormatException ex) {
                resultArea.setText("Please enter valid numeric values.");
            }
        }
    }

    public static void main(String[] args) {
        new BMICalculatorGUI();
    }
}
