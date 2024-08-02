
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class Node {
    int id;
    String bloodGroup;
    boolean isSeeker;
    List<Edge> edges;

    public Node(int id, String bloodGroup, boolean isSeeker) {
        this.id = id;
        this.bloodGroup = bloodGroup;
        this.isSeeker = isSeeker;
        this.edges = new ArrayList<>();
    }
}

class Edge {
    int target;
    int weight;

    public Edge(int target, int weight) {
        this.target = target;
        this.weight = weight;
    }
}

class EdgeComparator implements java.util.Comparator<Edge> {
    public int compare(Edge a, Edge b) {
        return Integer.compare(a.weight, b.weight);
    }
}

public class BloodBankGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel, homePanel, mapPanel, helpPanel, signUpPanel, graphPanel;
    private JTextArea resultArea;

    private JTextField idField, bloodGroupField, isSeekerField;
    private JTextField node1Field, node2Field, distanceField;
    private JTextField newUserField, newAgeField, newBloodGroupField, newPhoneField;

    private List<Node> nodes;
    private Map<Integer, Integer> idToIndex;

    private Image backgroundImage;

    public BloodBankGUI() {
        setTitle("Danesh Blood Bank");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBackground(g);
            }
        };

        loadBackgroundImage("Blood_08.jpg");

        createHomePanel();
        createMapPanel();
        createHelpPanel();
        createSignUpPanel();
        createGraphPanel();

        mainPanel.add(homePanel, "Home");
        mainPanel.add(mapPanel, "Map");
        mainPanel.add(helpPanel, "Help");
        mainPanel.add(signUpPanel, "SignUp");
        mainPanel.add(graphPanel, "Graph");

        add(mainPanel);
        cardLayout.show(mainPanel, "Home");
        setVisible(true);
    }

    private void loadBackgroundImage(String path) {
        try {
            backgroundImage = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawBackground(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void createHomePanel() {
        homePanel = new JPanel(new GridLayout(4, 1)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBackground(g);
            }
        };
        homePanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("------Welcome Danesh Blood Bank------", JLabel.CENTER);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        homePanel.add(welcomeLabel);

        JButton mapButton = createButton("A. Blood Donor Map", Color.ORANGE);
        JButton helpButton = createButton("B. Help Desk", Color.CYAN);
        JButton signUpButton = createButton("C. Sign Up", Color.GREEN);
        JButton endButton = createButton("D. End", Color.RED);

        mapButton.addActionListener(e -> cardLayout.show(mainPanel, "Map"));
        helpButton.addActionListener(e -> cardLayout.show(mainPanel, "Help"));
        signUpButton.addActionListener(e -> cardLayout.show(mainPanel, "SignUp"));
        endButton.addActionListener(e -> System.exit(0));

        homePanel.add(mapButton);
        homePanel.add(helpButton);
        homePanel.add(signUpButton);
        homePanel.add(endButton);
    }

    private void createMapPanel() {
        mapPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBackground(g);
            }
        };
        mapPanel.setOpaque(false);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.setOpaque(false);

        Font font = new Font("Copperplate Gothic Bold", Font.PLAIN, 20);

        idField = new JTextField();
        idField.setFont(font);
        bloodGroupField = new JTextField();
        bloodGroupField.setFont(font);
        isSeekerField = new JTextField();
        isSeekerField.setFont(font);

        inputPanel.add(createLabel("ID:", font));
        inputPanel.add(idField);
        inputPanel.add(createLabel("Blood Group:", font));
        inputPanel.add(bloodGroupField);
        inputPanel.add(createLabel("Is Seeker (true/false):", font));
        inputPanel.add(isSeekerField);

        node1Field = new JTextField();
        node1Field.setFont(font);
        node2Field = new JTextField();
        node2Field.setFont(font);
        distanceField = new JTextField();
        distanceField.setFont(font);

        inputPanel.add(createLabel("Add Edges (node1, node2, distance):", font));
        inputPanel.add(new JLabel(""));
        inputPanel.add(createLabel("Node 1:", font));
        inputPanel.add(node1Field);
        inputPanel.add(createLabel("Node 2:", font));
        inputPanel.add(node2Field);
        inputPanel.add(createLabel("Distance:", font));
        inputPanel.add(distanceField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton addNodeButton = createButton("Add Node", Color.ORANGE);
        JButton addEdgeButton = createButton("Add Edge", Color.CYAN);
        JButton calculateButton = createButton("Calculate", Color.GREEN);
        JButton showGraphButton = createButton("Show Graph", Color.MAGENTA);

        addNodeButton.addActionListener(e -> addNode());
        addEdgeButton.addActionListener(e -> addEdge());
        calculateButton.addActionListener(e -> calculate());
        showGraphButton.addActionListener(e -> cardLayout.show(mainPanel, "Graph"));

        buttonPanel.add(addNodeButton);
        buttonPanel.add(addEdgeButton);
        buttonPanel.add(calculateButton);
        buttonPanel.add(showGraphButton);

        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Copperplate Gothic Bold", Font.PLAIN, 18));

        mapPanel.add(inputPanel, BorderLayout.NORTH);
        mapPanel.add(buttonPanel, BorderLayout.CENTER);
        mapPanel.add(new JScrollPane(resultArea), BorderLayout.SOUTH);
    }

    private void createHelpPanel() {
        helpPanel = new JPanel(new GridLayout(0, 1)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBackground(g);
            }
        };
        helpPanel.setOpaque(false);

        JButton emergencyButton = createButton("1. Emergency Phone Numbers", Color.ORANGE);
        JButton aboutButton = createButton("2. About Blood Groups", Color.CYAN);
        JButton othersButton = createButton("3. Others", Color.GREEN);

        emergencyButton.addActionListener(e -> showEmergencyNumbers());
        aboutButton.addActionListener(e -> showBloodGroupInfo());
        othersButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Other information will be added soon."));

        helpPanel.add(emergencyButton);
        helpPanel.add(aboutButton);
        helpPanel.add(othersButton);
    }

    private void createSignUpPanel() {
        signUpPanel = new JPanel(new GridLayout(0, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBackground(g);
            }
        };
        signUpPanel.setOpaque(false);

        Font font = new Font("Copperplate Gothic Bold", Font.PLAIN, 20);

        newUserField = new JTextField();
        newUserField.setFont(font);
        newAgeField = new JTextField();
        newAgeField.setFont(font);
        newBloodGroupField = new JTextField();
        newBloodGroupField.setFont(font);
        newPhoneField = new JTextField();
        newPhoneField.setFont(font);

        JButton signUpButton = createButton("Sign Up", Color.ORANGE);

        signUpButton.addActionListener(e -> {
            String name = newUserField.getText();
            String age = newAgeField.getText();
            String bloodGroup = newBloodGroupField.getText();
            String phone = newPhoneField.getText();

            JOptionPane.showMessageDialog(this, "User Name: " + name + "\nAge: " + age + "\nBlood Group: " + bloodGroup + "\nPhone Number: " + phone + "\nRegistration Successful!");
        });

        signUpPanel.add(createLabel("Enter Your Name:", font));
        signUpPanel.add(newUserField);
        signUpPanel.add(createLabel("Enter Your Age:", font));
        signUpPanel.add(newAgeField);
        signUpPanel.add(createLabel("Enter Your Blood Group:", font));
        signUpPanel.add(newBloodGroupField);
        signUpPanel.add(createLabel("Enter Your Phone Number:", font));
        signUpPanel.add(newPhoneField);
        signUpPanel.add(new JLabel(""));
        signUpPanel.add(signUpButton);
    }

    private void createGraphPanel() {
        graphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBackground(g);
                drawGraph(g);
            }
        };
        graphPanel.setOpaque(false);
        graphPanel.setBackground(Color.BLACK);
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(Color.BLACK);
        return label;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setBorderPainted(false);
        return button;
    }

    private void addNode() {
        try {
            int id = Integer.parseInt(idField.getText());
            String bloodGroup = bloodGroupField.getText();
            boolean isSeeker = Boolean.parseBoolean(isSeekerField.getText());

            if (nodes == null) {
                nodes = new ArrayList<>();
                idToIndex = new HashMap<>();
            }

            Node node = new Node(id, bloodGroup, isSeeker);
            nodes.add(node);
            idToIndex.put(id, nodes.size() - 1);

            idField.setText("");
            bloodGroupField.setText("");
            isSeekerField.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input for node.");
        }
    }

    private void addEdge() {
        try {
            int node1 = Integer.parseInt(node1Field.getText());
            int node2 = Integer.parseInt(node2Field.getText());
            int distance = Integer.parseInt(distanceField.getText());

            int idx1 = idToIndex.get(node1);
            int idx2 = idToIndex.get(node2);

            nodes.get(idx1).edges.add(new Edge(idx2, distance));
            nodes.get(idx2).edges.add(new Edge(idx1, distance));

            node1Field.setText("");
            node2Field.setText("");
            distanceField.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input for edge.");
        }
    }

    private void calculate() {
        if (nodes == null || nodes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No nodes to calculate.");
            return;
        }

        int source = -1;
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).isSeeker) {
                source = i;
                break;
            }
        }

        if (source == -1) {
            JOptionPane.showMessageDialog(this, "No blood seeker found.");
            return;
        }

        int n = nodes.size();
        int[] distances = new int[n];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[source] = 0;

        PriorityQueue<Edge> pq = new PriorityQueue<>(new EdgeComparator());
        pq.add(new Edge(source, 0));

        while (!pq.isEmpty()) {
            Edge current = pq.poll();
            int u = current.target;
            int dist_u = current.weight;

            if (dist_u > distances[u]) continue;

            for (Edge edge : nodes.get(u).edges) {
                int v = edge.target;
                int weight = edge.weight;
                if (distances[u] + weight < distances[v]) {
                    distances[v] = distances[u] + weight;
                    pq.add(new Edge(v, distances[v]));
                }
            }
        }

        String seekersBloodGroup = nodes.get(source).bloodGroup;
        resultArea.setText("Blood donors with minimal distance from the blood seeker of matched blood group (" + seekersBloodGroup + "):\n");

        for (int i = 0; i < n; i++) {
            if (!nodes.get(i).isSeeker && nodes.get(i).bloodGroup.equals(seekersBloodGroup)) {
                resultArea.append("Donor ID: " + nodes.get(i).id + ", Distance: " + distances[i] + "\n");
            }
        }

        graphPanel.repaint();
    }

    private void showEmergencyNumbers() {
        String message = "Dhaka      Division : 01711111111111\n"
                + "Khulna     Division : 01711111111111\n"
                + "Rangpur    Division : 01711111111111\n"
                + "Chattogram Division : 01711111111111\n"
                + "Mymensingh Division : 01711111111111\n"
                + "Sylhet     Division : 01711111111111\n"
                + "Rajshahi   Division : 01711111111111\n"
                + "Barishal   Division : 01711111111111\n";
        JOptionPane.showMessageDialog(this, message);
    }

    private void showBloodGroupInfo() {
        String message = "|Blood Group\t|Gives to these groups\t|Receives from these groups|\n"
                + "--------------------------------------------------------------------------\n"
                + "O(-v)\t\t\t\tALL\t\t\t\tO(-v) only\n"
                + "O(+v)\t\t\t\tAB+,A+,B+,O+\t\t\tO- and O+\n"
                + "A(-v)\t\t\t\tAB-,AB+,A+,A-\t\t\tO- and A-\n"
                + "A(+v)\t\t\t\tAB+,A+\t\t\tO-,O+,A-,A+\n"
                + "B(-v)\t\t\t\tB+,AB+\t\t\tO-,B-\n"
                + "B(+v)\t\t\t\tB+,AB+\t\t\tO-,A-,B-,AB-\n"
                + "AB(-v)\t\t\t\tAB-,AB+\t\t\tO-,A-,B-,AB-\n"
                + "AB(+v)\t\t\t\tAB+ Only\t\t\tALL\n";
        JOptionPane.showMessageDialog(this, message);
    }

    private void drawGraph(Graphics g) {
        if (nodes == null || nodes.isEmpty()) return;

        Map<Integer, Point> nodeLocations = new HashMap<>();
        Random rand = new Random();

        for (Node node : nodes) {
            int x = rand.nextInt(graphPanel.getWidth() - 50) + 25;
            int y = rand.nextInt(graphPanel.getHeight() - 50) + 25;
            nodeLocations.put(node.id, new Point(x, y));
        }

        g.setColor(Color.BLUE);
        for (Node node : nodes) {
            Point location = nodeLocations.get(node.id);
            g.fillOval(location.x - 5, location.y - 5, 10, 10);
            g.drawString(String.valueOf(node.id), location.x + 5, location.y - 5);
        }

        g.setColor(Color.YELLOW);
        for (Node node : nodes) {
            Point fromLocation = nodeLocations.get(node.id);
            for (Edge edge : node.edges) {
                Point toLocation = nodeLocations.get(nodes.get(edge.target).id);
                g.drawLine(fromLocation.x, fromLocation.y, toLocation.x, toLocation.y);
                int midX = (fromLocation.x + toLocation.x) / 2;
                int midY = (fromLocation.y + toLocation.y) / 2;
                g.drawString(String.valueOf(edge.weight), midX, midY);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BloodBankGUI::new);
    }
}
