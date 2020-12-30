package net.crimsonite.rena;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDABuilder;

public class RenaTest {
	
	private static String token;
	
	public static void main(String[] args) {
		List<String> list;
		try {
			list = Files.readAllLines(Paths.get("config.txt"));
			token = list.get(0);
			
			JDABuilder.createLight(token).build();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (LoginException e) {
			e.printStackTrace();
		}
		
	}

}
