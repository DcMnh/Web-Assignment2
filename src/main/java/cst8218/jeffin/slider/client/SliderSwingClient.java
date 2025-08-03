package cst8218.jeffin.slider.client;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.net.URI;
import javax.swing.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

/**
 * Swing client that allows updating a slider's size and position via the REST API.
 */
public class SliderSwingClient {

    private static final String API_BASE = "http://localhost:8080/A2/resources/slider";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    
    // Validation constants (matching Slider entity constraints)
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 200;
    private static final int MIN_X = 0;
    private static final int MAX_X = 800;
    private static final int MIN_Y = 0;
    private static final int MAX_Y = 600;
    private static final int MIN_MAX_TRAVEL = 1;
    private static final int MAX_MAX_TRAVEL = 100;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SliderSwingClient::createAndShow);
    }
    
    /**
     * Sets status message with appropriate color coding
     */
    private static void setStatusMessage(JTextField statusField, String message, boolean isError) {
        SwingUtilities.invokeLater(() -> {
            statusField.setText(message);
            statusField.setForeground(isError ? Color.RED : Color.BLACK);
        });
    }
    
    /**
     * Validates if a string represents a valid integer within the specified range
     */
    private static boolean validateIntegerRange(String value, int min, int max, String fieldName, JTextField statusField) {
        try {
            int intValue = Integer.parseInt(value.trim());
            if (intValue < min || intValue > max) {
                setStatusMessage(statusField, "Validation Error: " + fieldName + " must be between " + min + " and " + max, true);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            setStatusMessage(statusField, "Validation Error: " + fieldName + " must be a valid integer", true);
            return false;
        }
    }
    
    /**
     * Validates if a string represents a valid long (for ID)
     */
    private static boolean validateLong(String value, String fieldName, JTextField statusField) {
        try {
            long longValue = Long.parseLong(value.trim());
            if (longValue <= 0) {
                setStatusMessage(statusField, "Validation Error: " + fieldName + " must be a positive number", true);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            setStatusMessage(statusField, "Validation Error: " + fieldName + " must be a valid number", true);
            return false;
        }
    }
    
    /**
     * Validates movement direction (should be 1 or -1)
     */
    private static boolean validateMovementDirection(String value, JTextField statusField) {
        try {
            int intValue = Integer.parseInt(value.trim());
            if (intValue != 1 && intValue != -1) {
                setStatusMessage(statusField, "Validation Error: Movement Direction should be 1 (right) or -1 (left)", true);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            setStatusMessage(statusField, "Validation Error: Movement Direction must be a valid integer (1 or -1)", true);
            return false;
        }
    }
    
    /**
     * Validates direction change count (should be non-negative)
     */
    private static boolean validateDirectionChangeCount(String value, JTextField statusField) {
        try {
            int intValue = Integer.parseInt(value.trim());
            if (intValue < 0) {
                setStatusMessage(statusField, "Validation Error: Direction Change Count must be non-negative (≥ 0)", true);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            setStatusMessage(statusField, "Validation Error: Direction Change Count must be a valid integer", true);
            return false;
        }
    }

    private static void createAndShow() {
        JFrame frame = new JFrame("Slider Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(0, 2, 5, 5)); // 2 columns, auto rows, 5px gaps

        // Prompt for login once
        String username = JOptionPane.showInputDialog("Enter username:");
        String password = JOptionPane.showInputDialog("Enter password:");
        String authHeader = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

        JTextField idField = new JTextField("1", 5);
        JTextField sizeField = new JTextField("50", 5);
        JTextField xField = new JTextField("100", 5);
        JTextField yField = new JTextField("100", 5);
        JTextField maxTravelField = new JTextField("100", 5);
        JTextField currentTravelField = new JTextField("0", 5);
        JTextField mvtDirectionField = new JTextField("1", 5);
        JTextField dirChangeCountField = new JTextField("0", 5);
        
        // Set tooltips for better user experience with validation ranges
        idField.setToolTipText("Slider ID (Long) - Must be a positive number");
        sizeField.setToolTipText("Size in pixels (1-200) - Integer range: " + MIN_SIZE + "-" + MAX_SIZE);
        xField.setToolTipText("X position (0-800) - Integer range: " + MIN_X + "-" + MAX_X);
        yField.setToolTipText("Y position (0-600) - Integer range: " + MIN_Y + "-" + MAX_Y);
        maxTravelField.setToolTipText("Maximum travel distance (1-100) - Integer range: " + MIN_MAX_TRAVEL + "-" + MAX_MAX_TRAVEL);
        currentTravelField.setToolTipText("Current travel distance - Must be a valid integer");
        mvtDirectionField.setToolTipText("Movement direction - Must be 1 (right) or -1 (left)");
        dirChangeCountField.setToolTipText("Direction change count - Must be a non-negative integer (≥ 0)");
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
            // Clear any previous status messages
            setStatusMessage(statusField, "Validating input...", false);
            
            // Get trimmed values from fields
            String idText = idField.getText().trim();
            String sizeText = sizeField.getText().trim();
            String xText = xField.getText().trim();
            String yText = yField.getText().trim();
            String maxTravelText = maxTravelField.getText().trim();
            String currentTravelText = currentTravelField.getText().trim();
            String mvtDirectionText = mvtDirectionField.getText().trim();
            String dirChangeCountText = dirChangeCountField.getText().trim();
            
            // Validate all inputs
            if (!validateLong(idText, "ID", statusField)) return;
            if (!validateIntegerRange(sizeText, MIN_SIZE, MAX_SIZE, "Size", statusField)) return;
            if (!validateIntegerRange(xText, MIN_X, MAX_X, "X position", statusField)) return;
            if (!validateIntegerRange(yText, MIN_Y, MAX_Y, "Y position", statusField)) return;
            if (!validateIntegerRange(maxTravelText, MIN_MAX_TRAVEL, MAX_MAX_TRAVEL, "Max Travel", statusField)) return;
            
            // Validate current travel (allow any integer, but check if it's valid)
            if (!validateIntegerRange(currentTravelText, Integer.MIN_VALUE, Integer.MAX_VALUE, "Current Travel", statusField)) return;
            
            // Validate movement direction (should be 1 or -1)
            if (!validateMovementDirection(mvtDirectionText, statusField)) return;
            
            // Validate direction change count (should be non-negative)
            if (!validateDirectionChangeCount(dirChangeCountText, statusField)) return;
            
            try {
                // Parse values (we know they're valid from validation)
                long id = Long.parseLong(idText);
                int size = Integer.parseInt(sizeText);
                int x = Integer.parseInt(xText);
                int y = Integer.parseInt(yText);
                int maxTravel = Integer.parseInt(maxTravelText);
                int currentTravel = Integer.parseInt(currentTravelText);
                int mvtDirection = Integer.parseInt(mvtDirectionText);
                int dirChangeCount = Integer.parseInt(dirChangeCountText);

                setStatusMessage(statusField, "Sending update request...", false);
                
                String json = String.format(
                    "{\"id\":%d,\"size\":%d,\"x\":%d,\"y\":%d,\"currentTravel\":%d,\"maxTravel\":%d,\"mvtDirection\":%d,\"dirChangeCount\":%d}",
                    id, size, x, y, currentTravel, maxTravel, mvtDirection, dirChangeCount);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/" + id))
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .header("Authorization", authHeader)
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenAccept(resp -> {
                            String responseMessage = "Status: " + resp.statusCode() + " - ";
                            boolean isError = false;
                            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                                responseMessage += "Success! Slider updated.";
                            } else {
                                responseMessage += "Error: " + (resp.body() != null ? resp.body() : "Unknown error");
                                isError = true;
                            }
                            final String finalMessage = responseMessage;
                            final boolean finalIsError = isError;
                            SwingUtilities.invokeLater(() -> {
                                setStatusMessage(statusField, finalMessage, finalIsError);
                            });
                        })
                        .exceptionally(ex -> {
                            SwingUtilities.invokeLater(() ->
                                    setStatusMessage(statusField, "Request failed: " + ex.getMessage(), true));
                            return null;
                        });
            } catch (Exception ex) {
                setStatusMessage(statusField, "Unexpected error: " + ex.getMessage(), true);
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
        frame.add(new JLabel("Max Travel:"));
        frame.add(maxTravelField);
        frame.add(new JLabel("Current Travel:"));
        frame.add(currentTravelField);
        frame.add(new JLabel("Movement Direction:"));
        frame.add(mvtDirectionField);
        frame.add(new JLabel("Direction Change Count:"));
        frame.add(dirChangeCountField);
        frame.add(new JLabel("")); // Empty cell for alignment
        frame.add(testConnection);
        frame.add(new JLabel("")); // Empty cell for alignment
        frame.add(update);
        frame.add(new JLabel("Status:"));
        frame.add(statusField);
        frame.pack();
        frame.setVisible(true);
    }
}
