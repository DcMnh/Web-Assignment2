package cst8218.jeffin.slider.client;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.net.URI;
import javax.swing.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Simple Swing client that allows updating a slider's size via the REST API.
 */
public class SliderSwingClient {
    private static final String API_BASE = "http://localhost:8080/Web-Assignment2/resources/slider";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SliderSwingClient::createAndShow);
    }

    private static void createAndShow() {
        JFrame frame = new JFrame("Slider Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JTextField idField = new JTextField(5);
        JTextField sizeField = new JTextField(5);
        JButton update = new JButton("Update Size");

        update.addActionListener((ActionEvent e) -> {
            try {
                long id = Long.parseLong(idField.getText());
                int size = Integer.parseInt(sizeField.getText());
                String json = String.format("{\"id\":%d,\"size\":%d}", id, size);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/" + id))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();
                HttpClient.newHttpClient()
                        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenAccept(resp -> System.out.println("Response: " + resp.body()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        frame.add(new JLabel("ID:"));
        frame.add(idField);
        frame.add(new JLabel("Size:"));
        frame.add(sizeField);
        frame.add(update);
        frame.pack();
        frame.setVisible(true);
    }
}
