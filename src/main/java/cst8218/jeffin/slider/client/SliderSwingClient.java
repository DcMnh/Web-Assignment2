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

    private static final String API_BASE = "http://localhost:8080/Web-Assignment2/resources/slider";
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
                            String msg = "Status: " + resp.statusCode() + "\n";
                            if (resp.body() != null && !resp.body().isEmpty()) {
                                msg += "Response: " + resp.body();
                            }
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(frame, msg,
                                        resp.statusCode() >= 200 && resp.statusCode() < 300 ? "Success" : "Error",
                                        resp.statusCode() >= 200 && resp.statusCode() < 300 ? JOptionPane.INFORMATION_MESSAGE
                                                : JOptionPane.ERROR_MESSAGE);
                            });
                        })
                        .exceptionally(ex -> {
                            SwingUtilities.invokeLater(() ->
                                    JOptionPane.showMessageDialog(frame, "Request failed: " + ex.getMessage(),
                                            "Error", JOptionPane.ERROR_MESSAGE));
                            return null;
                        });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
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
        frame.add(update);
        frame.pack();
        frame.setVisible(true);
    }
}

