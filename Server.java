import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

public class Server {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        server.createContext("/", new StaticHandler());
        server.createContext("/api/buildings", new BuildingsHandler());
        server.createContext("/api/download", new DownloadHandler());
        
        server.start();
        System.out.println("Server running on http://localhost:8080");
    }
}

class StaticHandler implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/")) path = "/index.html";
        
        File file = new File("." + path);
        if (!file.exists()) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
            return;
        }
        
        String contentType = getContentType(path);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, file.length());
        
        Files.copy(file.toPath(), exchange.getResponseBody());
        exchange.close();
    }
    
    private String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        return "text/plain";
    }
}

class BuildingsHandler implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        String response = "[" +
            "{\"name\":\"Library\",\"x\":150,\"y\":100,\"width\":80,\"height\":60,\"color\":\"#e74c3c\"}," +
            "{\"name\":\"Admin\",\"x\":300,\"y\":150,\"width\":70,\"height\":50,\"color\":\"#f39c12\"}," +
            "{\"name\":\"Lab\",\"x\":200,\"y\":250,\"width\":90,\"height\":70,\"color\":\"#27ae60\"}," +
            "{\"name\":\"Cafeteria\",\"x\":400,\"y\":200,\"width\":100,\"height\":80,\"color\":\"#9b59b6\"}" +
            "]";
        
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length());
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }
}

class DownloadHandler implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        String response = "{\"message\":\"CampusMapViewer.jar download initiated\",\"size\":\"2.5MB\"}";
        
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length());
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }
}