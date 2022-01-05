package test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Commands {
	
	// Default IO interface
	public interface DefaultIO{
		public String readText();
		public void write(String text);
		public float readVal();
		public void write(float val);

		// you may add default methods here
	}
	
	// the default IO to be used in all commands
	DefaultIO dio;
	public Commands(DefaultIO dio) {
		this.dio=dio;
	}
	
	// you may add other helper classes here
	public class Anomalies{
		int start;
		int end;
		int length;

		public Anomalies(int start,int end, int length){
			this.start=start;
			this.end=end;
			this.length=length;
		}
	}

	// the shared state of all commands
	private class SharedState{
		float threshold;
		SimpleAnomalyDetector anomalyDetector = new SimpleAnomalyDetector();
		List<AnomalyReport> anomalyReport = new ArrayList<>();
		int anomalyStart;
		int anomalyEnd;
		int anomalylength;
		List<Anomalies> anomalies = new ArrayList<>();
		int n=0;

		// implement here whatever you need
		
	}
	
	private SharedState sharedState=new SharedState();

	
	// Command abstract class
	public abstract class Command{

		protected String description;
		
		public Command(String description) {
			this.description=description;
		}
		
		public abstract void execute();
	}


	// Command class for example:

	public class temp extends Command{

		public temp() {
			super("this is an example of command");
		}

		@Override
		public void execute() {
			dio.write(description);
		}
	}
	public class uploadCommand extends Command{

		PrintWriter trainCSV;
		PrintWriter testCSV;

		public uploadCommand() {
			super("upload a time series csv file\n");
			try {
				trainCSV = new PrintWriter(new FileWriter("anomalyTrain.csv"));
				testCSV = new PrintWriter(new FileWriter("anomalyTest.csv"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void execute() {
			dio.write("Please upload your local train CSV file.\n");
			String line = dio.readText();
			while(!line.equals("done")){
				trainCSV.println(line);
				trainCSV.flush();
				line = dio.readText();
			}
			dio.write("Upload complete.\n");

			dio.write("Please upload your local test CSV file.\n");
			String line1 = dio.readText();
			while(!line1.equals("done")){
				testCSV.println(line1);
				testCSV.flush();
				line1 = dio.readText();
			}
			dio.write("Upload complete.\n");
		}

	}

	public class algorithmSetting extends Command{

		public algorithmSetting() {
			super("algorithm settings\n");
		}

		@Override
		public void execute() {

			dio.write("The current correlation threshold is 0.9\n");
			boolean flag = false;
			dio.write("type a new threshold\n");
			sharedState.threshold = Float.parseFloat(dio.readText());
			if(sharedState.threshold < 0 || sharedState.threshold > 1)
			{
				dio.write("please choose a value between 0 and 1.\n");
				dio.write("type a new threshold\n");
				sharedState.threshold = Float.parseFloat(dio.readText());
			}
		}
	}

	public class detectAnomalies extends Command{

		public detectAnomalies() {
			super("detect anomalies\n");

		}

		@Override
		public void execute() {
			TimeSeries tsTrain = new TimeSeries("anomalyTrain.csv");
			sharedState.anomalyDetector.learnNormal(tsTrain);

			TimeSeries tsTest = new TimeSeries("anomalyTest.csv");
			sharedState.anomalyReport=sharedState.anomalyDetector.detect(tsTest);

			dio.write("anomaly detection complete.\n");
		}
	}

	public class displayResults extends Command{

		public displayResults() {
			super("display results\n");
		}

		@Override
		public void execute() {
			for( AnomalyReport report : sharedState.anomalyReport )
				dio.write(report.timeStep+"\t "+report.description+"\n");
			dio.write("Done.\n");
		}
	}


	public class uploadAnomalies extends Command{

		public uploadAnomalies() {
			super("upload anomalies and analyze results\n");
		}
		@Override
		public void execute() {
			sharedState.anomalies.clear();
			int P=0;
			int N = sharedState.anomalyReport.size();
			for(int i=0;i<sharedState.anomalyReport.size()-1;) {
				boolean flag = false;
				sharedState.anomalyStart = (int) sharedState.anomalyReport.get(i).timeStep;
				sharedState.anomalylength = 1;
				while (sharedState.anomalyReport.get(i).description.equals(sharedState.anomalyReport.get(i + 1).description) && sharedState.anomalyReport.get(i).timeStep == i) {
					sharedState.anomalylength++;
					i++;
					flag = true;
				}
				sharedState.anomalyEnd = sharedState.anomalyStart + sharedState.anomalylength;
				N -= sharedState.anomalylength;
				if (!flag)
					i++;
				sharedState.anomalies.add(new Anomalies(sharedState.anomalyStart,sharedState.anomalyEnd,sharedState.anomalylength));
			}
			P =sharedState.anomalies.size();


			Scanner userAnomaliesCsv=null;
			dio.write("Please upload your local anomalies file.\n");
			dio.write("Upload complete.\n");

			List<Anomalies> useranomalies = new ArrayList<>();

			while(true){

				String line = dio.readText();
				if(line.equals("done"))
					break;
				String[] number = line.split(",");
				int start =Integer.parseInt(number[0]);
				int end =Integer.parseInt(number[1]);
				int l = end - start +1;
				useranomalies.add(new Anomalies(start,end,l));
			}

			float TP=0;
			float FP=0;
			float FN=0;
			float TN=0;
			for(Anomalies a : sharedState.anomalies) {
				for (int i = 0; i < useranomalies.size(); i++) {
					if ((a.start > useranomalies.get(i).end) || (a.end < useranomalies.get(i).start))
						continue;

					TP++;
					break;
				}
			}

			dio.write("True Positive Rate: " + (TP/P)+"\n");
			dio.write("False Positive Rate: " + (FP/N)+"\n");
		}
	}

	public class exit extends Command{

		public exit() {
			super("exit\n");
		}
		@Override
		public void execute() {
			dio.write(description);
		}
	}


/*
	// Command class for example:
	public class ExampleCommand extends Command{

		public ExampleCommand() {
			super("this is an example of command");
		}

		@Override
		public void execute() {
			dio.write(description);
		}
	}

 */

	// implement here all other commands
	
}
