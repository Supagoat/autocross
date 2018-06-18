package q.autocross;

import static spark.Spark.*;

import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCrypt;

import q.autocross.app.ApiPoints;
import spark.Spark;

public class Main {

	public static void main(String[] args) {
		Spark.staticFiles.location("/public");
		
		path("/api", () -> {
			get("/hello", (req, res) -> "Hello World");
			post("/upload", (req, res) -> new ApiPoints().upload(req, res)); 
		});
		
		get("/", (req,res) -> new ApiPoints().returnStatic("/ui/index.html"));

	}
}