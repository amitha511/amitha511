package test;

import java.util.*;

import test.Commands.Command;
import test.Commands.DefaultIO;
import test.Commands.algorithmSetting;
import test.Commands.detectAnomalies;
import test.Commands.uploadCommand;
import test.Commands.displayResults;
import test.Commands.uploadAnomalies;

public class CLI {

	ArrayList<Command> commands;
	DefaultIO dio;
	Commands c;
	
	public CLI(DefaultIO dio) {
		this.dio=dio;
		c=new Commands(dio);
		// example: commands.add(c.new ExampleCommand());
		// implement
		if(commands == null) {
			commands = new ArrayList<>();
			commands.add( c.new temp());
			commands.add( c.new uploadCommand());
			commands.add(c.new algorithmSetting());
			commands.add( c.new detectAnomalies());
			commands.add( c.new displayResults());
			commands.add( c.new uploadAnomalies());
		}

	}
	
	public void start() {
		int i;
		String option;

		while(true) {
			dio.write("Welcome to the Anomaly Detection Server.\n");
			dio.write("Please choose an option:\n");
			for (i = 1; i < commands.size() ; i++) {
				dio.write(i + ". " + commands.get(i).description);
			}
			dio.write("6. exit\n");
			option = dio.readText();
/*			if(option.equals(""))
				option = dio.readText();*/
			int val = Integer.parseInt(option);
			if (val ==6) {
				break;
			}
			commands.get(val).execute();
		}
	}
}
