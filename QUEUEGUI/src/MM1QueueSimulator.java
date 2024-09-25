import javax.swing.*;
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
    setTitle("M/M/1 Queue Simulator");
    setSize(400, 250);
    setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

    JPanel arrivalRatePanel = createInputPanel("Arrival Rate (λ):", arrivalRateField = new JTextField(5));
    inputPanel.add(arrivalRatePanel);
    JPanel serviceRatePanel = createInputPanel("Service Rate (μ):", serviceRateField = new JTextField(10));
    inputPanel.add(serviceRatePanel);

    startButton = new JButton("Start Simulation");
    inputPanel.add(startButton);

    add(inputPanel);

    outputArea = new JTextArea();
    outputArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(outputArea);
    add(scrollPane);

    startButton.addActionListener(e -> simulateQueue());
}

private JPanel createInputPanel(String labelText, JTextField textField) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    panel.add(new JLabel(labelText));
    panel.add(textField);
    return panel;
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
                currentTime = nextArrivalTime;
                if (!serverBusy) {
                    serverBusy = true;
                    double serviceTime = getNextExponential(mu); 
                    totalServiceTime += serviceTime;
                    nextDepartureTime = currentTime + serviceTime;
                } else {
                    queue.add(currentTime);
                }
                nextArrivalTime = currentTime + getNextExponential(lambda);
            } else {
                currentTime = nextDepartureTime;
                customersServed++;

                if (!queue.isEmpty()) {
                    double arrivalTime = queue.poll();
                    double waitTime = currentTime - arrivalTime;
                    totalWaitTime += waitTime;
                    double serviceTime = getNextExponential(mu);
                    totalServiceTime += serviceTime;
                    nextDepartureTime = currentTime + serviceTime;
                } else {
                    serverBusy = false;
                    nextDepartureTime = Double.MAX_VALUE;
                }
            }
        }

        double avgWaitTime = totalWaitTime / customersServed; 
        double utilization = totalServiceTime / currentTime;   

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
        SwingUtilities.invokeLater(() -> new MM1QueueSimulator().setVisible(true));
    }
}