import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class AirplaneManagementApp extends JFrame {
    private JComboBox<String> airportComboBox1;
    private JComboBox<String> airportComboBox2;
    private JTextField distanceField;
    private JTextArea outputArea;
    private Map<String, Integer> airportMap;
    private int[][] distanceMatrix;
    private int airportCount;
    private JLabel clockLabel;
    private Map<String, Point> airportLocations;
    private JPanel mapPanel;
    private BufferedImage[] backgroundImages;
    private int currentImageIndex = 0;

    public AirplaneManagementApp() {
        setTitle("Airplane Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        

        // Load background images
        loadBackgroundImages();

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBackgroundImage(g);
            }
        };

        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false);
        inputPanel.setLayout(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Font font = new Font("Copperplate Gothic Bold", Font.PLAIN, 20);
        Font font1 = new Font("Cooper Black", Font.PLAIN, 20);

        airportComboBox1 = new JComboBox<>();
        airportComboBox1.setFont(font);
        airportComboBox2 = new JComboBox<>();
        airportComboBox2.setFont(font);
        distanceField = new JTextField();
        distanceField.setFont(font);

        inputPanel.add(createLabel("From Airport:", font));
        inputPanel.add(airportComboBox1);
        inputPanel.add(createLabel("To Airport:", font));
        inputPanel.add(airportComboBox2);
        inputPanel.add(createLabel("Distance(KM):", font));
        inputPanel.add(distanceField);

        JButton addButton = createButton("Add Route", font, Color.BLUE, Color.BLACK, "icons8-route-16.png");
        JButton calculateButton = createButton("Calculate Shortest Paths", font, Color.GREEN, Color.BLACK, "icons8-calculate-20.png");

        inputPanel.add(addButton);
        inputPanel.add(calculateButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Output area
        outputArea = new JTextArea();
        outputArea.setFont(font);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        mainPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Clock display
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        clockLabel = new JLabel("World Clock:");
        clockLabel.setFont(font);
        clockLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomPanel.add(clockLabel, BorderLayout.CENTER);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Map panel
        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMap(g);
            }
        };
        mapPanel.setPreferredSize(new Dimension(800, 300));
        mapPanel.setBackground(Color.WHITE);

        add(mainPanel, BorderLayout.CENTER);
        add(mapPanel, BorderLayout.SOUTH);

        airportMap = new HashMap<>();
        airportCount = 0;
        distanceMatrix = new int[100][100];
        airportLocations = new HashMap<>();

        // Initialize the distance matrix with large values
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                } else {
                    distanceMatrix[i][j] = Integer.MAX_VALUE / 2;
                }
            }
        }

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRoute();
                mapPanel.repaint();
            }
        });

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateShortestPaths();
                mapPanel.repaint();
            }
        });

        updateClock();
        startSlideshow();
        populateAirportComboBox();
    }

    private JLabel createLabel(String text, Font font1) {
        JLabel label = new JLabel(text);
        label.setFont(font1);
        label.setForeground(Color.BLACK);
        return label;
    }

    private JButton createButton(String text, Font font1, Color bgColor, Color fgColor, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(font1);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setOpaque(true);
        button.setBorderPainted(false);
        try {
            BufferedImage iconImage = ImageIO.read(new File(iconPath));
            ImageIcon icon = new ImageIcon(iconImage);
            button.setIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return button;
    }

    private void addRoute() {
        String fromAirport = (String) airportComboBox1.getSelectedItem();
        String toAirport = (String) airportComboBox2.getSelectedItem();
        int distance;
        try {
            distance = Integer.parseInt(distanceField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid distance. Please enter a number.");
            return;
        }

        if (fromAirport == null || toAirport == null || fromAirport.equals(toAirport)) {
            JOptionPane.showMessageDialog(this, "Invalid route selection. Please select different airports.");
            return;
        }

        int fromIndex = getAirportIndex(fromAirport);
        int toIndex = getAirportIndex(toAirport);

        distanceMatrix[fromIndex][toIndex] = distance;
        distanceMatrix[toIndex][fromIndex] = distance;

        outputArea.append("Route added: " + fromAirport + " to " + toAirport + " with distance " + distance + "\n");
        updateAirportLocations(fromAirport, toAirport);
    }

    private int getAirportIndex(String airport) {
        if (!airportMap.containsKey(airport)) {
            airportMap.put(airport, airportCount++);
        }
        return airportMap.get(airport);
    }

    private void calculateShortestPaths() {
        int n = airportCount;
        int[][] dist = new int[n][n];

        // Copy the distance matrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = distanceMatrix[i][j];
            }
        }

        // Floyd-Warshall algorithm
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][j] > dist[i][k] + dist[k][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }

        outputArea.append("\nShortest distances between all pairs of airports:\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                outputArea.append("From " + getAirportName(i) + " to " + getAirportName(j) + ": " + (dist[i][j] == Integer.MAX_VALUE / 2 ? "INF" : dist[i][j]) + "\n");
            }
        }
    }

    private String getAirportName(int index) {
        for (Map.Entry<String, Integer> entry : airportMap.entrySet()) {
            if (entry.getValue() == index) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void updateAirportLocations(String from, String to) {
        if (!airportLocations.containsKey(from)) {
            airportLocations.put(from, new Point((int) (Math.random() * mapPanel.getWidth()), (int) (Math.random() * mapPanel.getHeight())));
        }
        if (!airportLocations.containsKey(to)) {
            airportLocations.put(to, new Point((int) (Math.random() * mapPanel.getWidth()), (int) (Math.random() * mapPanel.getHeight())));
        }
    }

    private void drawMap(Graphics g) {
        g.setColor(Color.RED);
        for (Map.Entry<String, Point> entry : airportLocations.entrySet()) {
            String airport = entry.getKey();
            Point location = entry.getValue();
            g.fillOval(location.x, location.y, 10, 10);
            g.drawString(airport, location.x + 10, location.y + 10);
        }

        g.setColor(Color.BLUE);
        for (Map.Entry<String, Integer> entry1 : airportMap.entrySet()) {
            String from = entry1.getKey();
            int fromIndex = entry1.getValue();
            for (Map.Entry<String, Integer> entry2 : airportMap.entrySet()) {
                String to = entry2.getKey();
                int toIndex = entry2.getValue();
                if (distanceMatrix[fromIndex][toIndex] != Integer.MAX_VALUE / 2) {
                    Point fromLocation = airportLocations.get(from);
                    Point toLocation = airportLocations.get(to);
                    g.drawLine(fromLocation.x, fromLocation.y, toLocation.x, toLocation.y);
                }
            }
        }
    }

    private void updateClock() {
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss EEEE, MMMM dd, yyyy");
                clockLabel.setText("World Clock: " + sdf.format(new Date()));
            }
        });
        timer.start();
    }

    private void loadBackgroundImages() {
        try {
            backgroundImages = new BufferedImage[]{
                ImageIO.read(new File("Plane2.jpg")),
                ImageIO.read(new File("Plane2.jpg")),
                ImageIO.read(new File("Plane2.jpg"))
            };
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startSlideshow() {
        Timer slideshowTimer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentImageIndex = (currentImageIndex + 1) % backgroundImages.length;
                repaint();
            }
        });
        slideshowTimer.start();
    }

    private void drawBackgroundImage(Graphics g) {
        if (backgroundImages != null && backgroundImages.length > 0) {
            g.drawImage(backgroundImages[currentImageIndex], 0, 0, getWidth(), getHeight(), null);
        }
    }

    private void populateAirportComboBox() {
        // Assuming a list of airport names
        String[] airports = {"Dhaka", "Sylhet", "Chattogram", "Saidpur", "Delhi", "Dubai", "Paris", "Barishal", "London", "NewYork"};

        for (String airport : airports) {
            airportComboBox1.addItem(airport);
            airportComboBox2.addItem(airport);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AirplaneManagementApp().setVisible(true);
            }
        });
    }
}
