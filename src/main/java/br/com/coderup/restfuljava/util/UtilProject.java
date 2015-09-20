package br.com.coderup.restfuljava.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class UtilProject {

	public static String getMainPath() {
		return "/api/v" + UtilProject.getVersionApp() + "/";
	}
	
	public static int getVersionApp() {
		Properties prop = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();  
		try (InputStream stream = loader.getResourceAsStream("application.properties")){         
			prop.load(stream);
			return Integer.parseInt(String.valueOf(prop.get("app.version")));
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
}
