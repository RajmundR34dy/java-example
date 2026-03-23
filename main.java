import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class Main {

    // Static inner class
    static class Calculator {
        private int base;

        // Constructor requires something (a base number)
        public Calculator(int base) {
            this.base = base;
        }

        // Method does something (adds base to input and returns result)
        public int addToBase(int value) {
            return base + value;
        }

        // Another example method (multiplies base with input)
        public int multiplyWithBase(int value) {
            return base * value;
        }
    }

    public static void main(String[] args) throws IOException {
        // If "--server" argument is passed, start the HTTP API server
        if (args.length > 0 && "--server".equals(args[0])) {
            startServer();
        } else {
            // Original calculator console mode
            runConsoleCalculator();
        }
    }

    // Original console-based calculator functionality
    private static void runConsoleCalculator() {
        Scanner scanner = new Scanner(System.in);

        // Ask for base number
        System.out.print("Enter a base number: ");
        int base = scanner.nextInt();

        // Create instance of static inner class
        Calculator calc = new Calculator(base);

        // Ask for another number
        System.out.print("Enter another number: ");
        int num = scanner.nextInt();

        // Use inner class methods
        int sum = calc.addToBase(num);
        int product = calc.multiplyWithBase(num);

        System.out.println("Result of base + number = " + sum);
        System.out.println("Result of base * number = " + product);

        scanner.close();
    }

    // HTTP API server mode
    private static void startServer() throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // GET /api/hello - returns a simple JSON greeting
        server.createContext("/api/hello", new HelloHandler());

        // GET /api/calculate?base=X&value=Y - uses Calculator to compute results
        server.createContext("/api/calculate", new CalculateHandler());

        server.setExecutor(null); // use default executor
        server.start();

        System.out.println("Server started on port " + port);
        System.out.println("Endpoints:");
        System.out.println("  GET http://localhost:" + port + "/api/hello");
        System.out.println("  GET http://localhost:" + port + "/api/calculate?base=10&value=5");
    }

    // Handler for /api/hello
    static class HelloHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = "{\"message\": \"Hello, World!\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    // Handler for /api/calculate - exposes Calculator functionality via API
    static class CalculateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                try {
                    int base = 0;
                    int value = 0;

                    if (query != null) {
                        for (String param : query.split("&")) {
                            String[] pair = param.split("=");
                            if (pair.length == 2) {
                                if ("base".equals(pair[0])) {
                                    base = Integer.parseInt(pair[1]);
                                } else if ("value".equals(pair[0])) {
                                    value = Integer.parseInt(pair[1]);
                                }
                            }
                        }
                    }

                    Calculator calc = new Calculator(base);
                    int sum = calc.addToBase(value);
                    int product = calc.multiplyWithBase(value);

                    String response = "{\"base\": " + base
                            + ", \"value\": " + value
                            + ", \"sum\": " + sum
                            + ", \"product\": " + product + "}";

                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } catch (NumberFormatException e) {
                    String error = "{\"error\": \"Invalid parameters. Use ?base=X&value=Y with integers.\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(400, error.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(error.getBytes());
                    }
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }
}
