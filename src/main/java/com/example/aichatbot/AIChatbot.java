package com.example.aichatbot;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

public class AIChatbot extends Application {

    private VBox chatBox;
    private TextField inputField;
    private Button sendButton;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Artificial Intelligent Chatbot");

        // Create UI elements
        chatBox = new VBox(10);
        chatBox.setPadding(new Insets(10));
        chatBox.setStyle("-fx-background-color: #F5F5F5;");

        ScrollPane scrollPane = new ScrollPane(chatBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(1000);

        inputField = new TextField();
        inputField.setPromptText("Type a message...");
        inputField.setFont(Font.font("Arial", 14));
        inputField.setPrefHeight(40);

        sendButton = new Button("Send");
        sendButton.setFont(Font.font("Arial", 14));
        sendButton.setPrefHeight(40);
        sendButton.setOnAction(e -> sendMessage());

        HBox inputBox = new HBox(10, inputField, sendButton);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new Insets(10));
        inputBox.setStyle("-fx-background-color: #FFFFFF;");

        VBox root = new VBox(scrollPane, inputBox);
        root.setStyle("-fx-background-color: #E0E0E0;");

        // Create scene and show stage
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void sendMessage() {
        String userInput = inputField.getText();
        if (userInput.isEmpty()) {
            return;
        }

        addMessage("You: " + userInput, Pos.TOP_RIGHT, "#DCF8C6");

        // Clear the input field
        inputField.clear();

        // Call the chatbot API
        // Call the chatbot API asynchronously
        getChatbotResponse(userInput).thenAccept(response ->
                Platform.runLater(() -> addMessage("AI Bot: " + response, Pos.TOP_LEFT, "#FFFFFF"))
        );
    }

    private CompletableFuture<String> getChatbotResponse(String userInput) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("inputs", userInput);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api-inference.huggingface.co/models/facebook/blenderbot-400M-distill"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + "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")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                // Parse the response array
                String responseBody = response.body();
                JsonArray jsonArray = JsonParser.parseString(responseBody).getAsJsonArray();
                JsonObject firstObject = jsonArray.get(0).getAsJsonObject();
                return firstObject.get("generated_text").getAsString();

            } catch (Exception e) {
                e.printStackTrace();
                return "Sorry, I couldn't process your request.";
            }
        });
    }

    private void addMessage(String message, Pos alignment, String bgColor) {
        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("Arial", 14));
        messageLabel.setPadding(new Insets(10));
        messageLabel.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 10;");
        HBox messageBox = new HBox(messageLabel);
        messageBox.setAlignment(alignment);
        messageBox.setPadding(new Insets(5));
        chatBox.getChildren().add(messageBox);
    }

    public static void main(String[] args) {
        launch(args);
    }
}



