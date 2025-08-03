package cst8218.jeffin.slider.client;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.net.URI;
import javax.swing.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Simple Swing client that allows updating a slider's size and position via the
 * REST API.
 */
public class SliderSwingClient {

    private static final String API_BASE = "http://localhost:8080/A2/resources/slider";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SliderSwingClient::createAndShow);
    }

    private static void createAndShow() {
        JFrame frame = new JFrame("Slider Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JTextField idField = new JTextField(5);
        JTextField sizeField = new JTextField(5);
        JTextField xField = new JTextField(5);
        JTextField yField = new JTextField(5);
        JButton update = new JButton("Update Slider");
        JButton testConnection = new JButton("Test API");
        JTextField statusField = new JTextField(30);
        statusField.setEditable(false);
        statusField.setText("Ready to update slider...");
        
        // Make frame effectively final for lambda expressions
        final JFrame finalFrame = frame;

        // Test connection button
        testConnection.addActionListener((ActionEvent e) -> {
            statusField.setText("Testing API connection...");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(resp -> {
                        String responseMessage = "API Test - Status: " + resp.statusCode();
                        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                            responseMessage += " - API is accessible!";
                        } else {
                            responseMessage += " - API Error: " + (resp.body() != null ? resp.body().substring(0, Math.min(100, resp.body().length())) + "..." : "Unknown error");
                        }
                        final String finalMessage = responseMessage;
                        SwingUtilities.invokeLater(() -> {
                            statusField.setText(finalMessage);
                        });
                    })
                    .exceptionally(ex -> {
                        SwingUtilities.invokeLater(() ->
                                statusField.setText("API Test Failed: " + ex.getMessage()));
                        return null;
                    });
        });

        update.addActionListener((ActionEvent e) -> {
            try {
                long id = Long.parseLong(idField.getText());
                int size = Integer.parseInt(sizeField.getText());
                int x = Integer.parseInt(xField.getText());
                int y = Integer.parseInt(yField.getText());

                // Create a more complete JSON object with all required fields
                String json = String.format(
                    "{\"id\":%d,\"size\":%d,\"x\":%d,\"y\":%d,\"currentTravel\":0,\"maxTravel\":100,\"mvtDirection\":1,\"dirChangeCount\":0}",
                    id, size, x, y);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/" + id))
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenAccept(resp -> {
                            String responseMessage = "Status: " + resp.statusCode() + " - ";
                            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                                responseMessage += "Success! Slider updated.";
                            } else {
                                responseMessage += "Error: " + (resp.body() != null ? resp.body() : "Unknown error");
                            }
                            final String finalMessage = responseMessage;
                            SwingUtilities.invokeLater(() -> {
                                statusField.setText(finalMessage);
                            });
                        })
                        .exceptionally(ex -> {
                            SwingUtilities.invokeLater(() ->
                                    statusField.setText("Request failed: " + ex.getMessage()));
                            return null;
                        });
            } catch (Exception ex) {
                statusField.setText("Invalid input: " + ex.getMessage());
            }
        });

        frame.add(new JLabel("ID:"));
        frame.add(idField);
        frame.add(new JLabel("Size:"));
        frame.add(sizeField);
        frame.add(new JLabel("X:"));
        frame.add(xField);
        frame.add(new JLabel("Y:"));
        frame.add(yField);
        frame.add(testConnection);
        frame.add(update);
        frame.add(new JLabel("Status:"));
        frame.add(statusField);
        frame.pack();
        frame.setVisible(true);
    }
}

