import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class MM1QueueSimulator extends JFrame {

    private JTextField arrivalRateField;
    private JTextField serviceRateField;
    private JButton startButton;
    private JTextArea outputArea;

    public MM1QueueSimulator() {
        // Set up the frame
        setTitle("M/M/1 Queue Simulator");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Input panel for rates
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("Arrival Rate (λ):"));
        arrivalRateField = new JTextField();
        inputPanel.add(arrivalRateField);
        inputPanel.add(new JLabel("Service Rate (μ):"));
        serviceRateField = new JTextField();
        inputPanel.add(serviceRateField);

        startButton = new JButton("Start Simulation");
        inputPanel.add(startButton);

        add(inputPanel, BorderLayout.NORTH);

        // Output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Start button action
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulateQueue();
            }
        });
    }

    private void simulateQueue() {
    outputArea.setText("");
    try {
        double lambda = Double.parseDouble(arrivalRateField.getText());
        double mu = Double.parseDouble(serviceRateField.getText());

        if (lambda <= 0 || mu <= 0) {
            JOptionPane.showMessageDialog(this, "Rates must be positive numbers!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int customersServed = 0;
        double totalWaitTime = 0;
        double totalServiceTime = 0;
        double currentTime = 0;
        double nextArrivalTime = getNextExponential(lambda);
        double nextDepartureTime = Double.MAX_VALUE;
        boolean serverBusy = false;
        Queue<Double> queue = new LinkedList<>();

        while (customersServed < 1000) {
            if (nextArrivalTime <= nextDepartureTime) {
                // Process arrival
                currentTime = nextArrivalTime;
                if (!serverBusy) {
                    serverBusy = true;
                    nextDepartureTime = currentTime + getNextExponential(mu);
                } else {
                    queue.add(currentTime);
                }
                nextArrivalTime = currentTime + getNextExponential(lambda);
            } else {
                // Process departure
                currentTime = nextDepartureTime;
                customersServed++;
                double serviceTime = getNextExponential(mu);
                totalServiceTime += serviceTime;
                totalWaitTime += currentTime - (queue.isEmpty() ? currentTime - serviceTime : queue.poll());
                if (!queue.isEmpty()) {
                    nextDepartureTime = currentTime + getNextExponential(mu);
                } else {
                    serverBusy = false;
                    nextDepartureTime = Double.MAX_VALUE;
                }
            }
        }

        // Calculate performance metrics
        double avgWaitTime = totalWaitTime / customersServed;
        double utilization = totalServiceTime / currentTime;

        // Format the results
        DecimalFormat df = new DecimalFormat("#.####");

        outputArea.append("Simulation Results:\n");
        outputArea.append("Total Customers Served: " + customersServed + "\n");
        outputArea.append("Average Wait Time: " + df.format(avgWaitTime) + " units\n");
        outputArea.append("Server Utilization: " + df.format(utilization * 100) + "%\n");

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Please enter valid numerical values for the rates!", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private double getNextExponential(double rate) {
        Random rand = new Random();
        return -Math.log(1.0 - rand.nextDouble()) / rate;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MM1QueueSimulator().setVisible(true);
            }
        });
    }
}
