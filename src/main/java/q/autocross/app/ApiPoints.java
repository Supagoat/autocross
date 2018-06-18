package q.autocross.app;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import q.autocross.engine.Data;
import spark.Request;
import spark.Response;
import spark.Session;

public class ApiPoints {
	public String upload(Request req, Response resp) {
		Session session = req.session();

		req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
		try {
			Optional<String> fileName = getFileName(req.raw().getPart("uploadedfile"));
			 new Data().read(new ByteArrayInputStream(readUpload(req)));
			return returnStatic("/ui/people/peopleDedupe.html");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Optional<String> getFileName(Part part) {
		return Arrays.stream(part.getHeader("content-disposition").split(";"))
				.filter(cd -> cd.trim().startsWith("filename"))
				.map(cd -> cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "")).findFirst();
	}

	private byte[] readUpload(Request req) {
		try {
			byte[] data = new byte[req.contentLength()];
			int count = 0;
			try (InputStream input = req.raw().getPart("uploadedfile").getInputStream()) {
				int read = 0;
				do {
					read = input.read(data, count, data.length - count);
					count += read;
				} while (read > 0);
			}
			return data;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String returnStatic(String path) {
		//System.out.println("PATH: "+System.getProperty("user.dir"));
		//return new File("").getPath();
		//return Arrays.stream(new File("").list()).collect(Collectors.joining(", "));
		String S = File.separator;
		try (BufferedReader in = new BufferedReader(new FileReader(new File("src"+S+"main"+S+"resources"+S+"public" + path)))){
			StringBuilder sb = new StringBuilder();
			while (in.ready()) {
				sb.append(in.readLine());
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "Error generating response";
		}
	}
}