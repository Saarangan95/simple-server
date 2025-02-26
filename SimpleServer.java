import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class SimpleServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 1. Basic Hello World
        server.createContext("/hello", exchange -> sendResponse(exchange, "<h1>Hello from Java Server</h1>"));

        // 2. API returning JSON response
        server.createContext("/json", exchange -> sendResponse(exchange, "{\"message\": \"Hello from JSON API\"}", "application/json"));

        // 3. API returning plain text
        server.createContext("/text", exchange -> sendResponse(exchange, "This is a plain text response", "text/plain"));

        // 4. API returning current timestamp
        server.createContext("/timestamp", exchange -> {
            String timestamp = "{\"timestamp\": \"" + System.currentTimeMillis() + "\"}";
            sendResponse(exchange, timestamp, "application/json");
        });

        // 5. API with query parameters (e.g., /greet?name=John)
        server.createContext("/greet", exchange -> {
            Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
            String name = params.getOrDefault("name", "Guest");
            sendResponse(exchange, "{\"greeting\": \"Hello, " + name + "!\"}", "application/json");
        });

        // 6. API returning random number
        server.createContext("/random", exchange -> {
            int randomNumber = (int) (Math.random() * 100);
            sendResponse(exchange, "{\"random\": " + randomNumber + "}", "application/json");
        });

        // 7. API returning 404 Not Found response
        server.createContext("/notfound", exchange -> sendErrorResponse(exchange, 404, "This API endpoint does not exist"));

        // 8. API returning 500 Internal Server Error response
        server.createContext("/error", exchange -> sendErrorResponse(exchange, 500, "Internal Server Error"));

        // 9. API with delay simulation (e.g., 3 seconds)
        server.createContext("/slow", exchange -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendResponse(exchange, "{\"message\": \"Delayed response after 3 seconds\"}", "application/json");
        });

        // 10. API that echoes back query parameters (e.g., /echo?data=test)
        server.createContext("/echo", exchange -> {
            Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
            sendResponse(exchange, "{\"echo\": \"" + params.toString() + "\"}", "application/json");
        });

        // 11. API returning HTML content
        server.createContext("/html", exchange -> {
            String htmlContent = "<html><body><h1>Welcome to My Java Server</h1></body></html>";
            sendResponse(exchange, htmlContent, "text/html");
        });

        // 12. API that always returns status 401 Unauthorized
        server.createContext("/unauthorized", exchange -> sendErrorResponse(exchange, 401, "Unauthorized Access"));

        // 13. API returning list of users in JSON format
        server.createContext("/users", exchange -> {
            String usersJson = "[{\"id\": 1, \"name\": \"Alice\"}, {\"id\": 2, \"name\": \"Bob\"}]";
            sendResponse(exchange, usersJson, "application/json");
        });

        // 14. API that simulates a server maintenance message
        server.createContext("/maintenance", exchange -> sendErrorResponse(exchange, 503, "Server Under Maintenance"));

        // 15. API returning system environment variables in JSON format
        server.createContext("/env", exchange -> {
            Map<String, String> env = System.getenv();
            StringBuilder json = new StringBuilder("{");
            for (Map.Entry<String, String> entry : env.entrySet()) {
                json.append("\"").append(entry.getKey()).append("\": \"").append(entry.getValue()).append("\", ");
            }
            json.append("}");
            sendResponse(exchange, json.toString(), "application/json");
        });

        server.setExecutor(null);
        server.start();
        System.out.println("Server is running on http://localhost:8080");
    }

    // Method to send a normal response
    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        sendResponse(exchange, response, "text/html");
    }

    // Method to send a response with a specific content type
    private static void sendResponse(HttpExchange exchange, String response, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    // Method to send an error response
    private static void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        String response = "{\"error\": \"" + message + "\", \"status\": " + statusCode + "}";
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    // Method to convert query parameters into a Map
    private static Map<String, String> queryToMap(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null) return map;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2) {
                map.put(pair[0], pair[1]);
            }
        }
        return map;
    }
}
