import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class MM1QueueSimulatorAWT extends Frame {

    private TextField arrivalRateField;
    private TextField serviceRateField;
    private Button startButton;
    private TextArea outputArea;

    public MM1QueueSimulatorAWT() {
        setTitle("M/M/1 Queue Simulator");
        setSize(400, 250);
        setLayout(new BorderLayout());

        Panel inputPanel = new Panel();
        inputPanel.setLayout(new GridLayout(3, 1));

        Panel arrivalRatePanel = createInputPanel("Arrival Rate (λ):", arrivalRateField = new TextField(5));
        inputPanel.add(arrivalRatePanel);

        Panel serviceRatePanel = createInputPanel("Service Rate (μ):", serviceRateField = new TextField(5));
        inputPanel.add(serviceRatePanel);

        startButton = new Button("Start Simulation");
        inputPanel.add(startButton);

        add(inputPanel, BorderLayout.NORTH);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        add(outputArea, BorderLayout.CENTER);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulateQueue();
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
            }
        });
    }

    private Panel createInputPanel(String labelText, TextField textField) {
        Panel panel = new Panel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(new Label(labelText));
        panel.add(textField);
        return panel;
    }

    private void simulateQueue() {
        outputArea.setText("");  
        try {
            double lambda = Double.parseDouble(arrivalRateField.getText());
            double mu = Double.parseDouble(serviceRateField.getText());

            if (lambda <= 0 || mu <= 0) {
                showErrorMessage("Rates must be positive numbers!");
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
            showErrorMessage("Please enter valid numerical values for the rates!");
        }
    }

    private double getNextExponential(double rate) {
        Random rand = new Random();
        return -Math.log(1.0 - rand.nextDouble()) / rate;
    }

    private void showErrorMessage(String message) {
        Dialog dialog = new Dialog(this, "Error", true);
        dialog.setLayout(new FlowLayout());
        dialog.add(new Label(message));
        Button okButton = new Button("OK");
        dialog.add(okButton);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            }
        });
        dialog.setSize(300, 100);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        MM1QueueSimulatorAWT simulator = new MM1QueueSimulatorAWT();
        simulator.setVisible(true);
    }
}
