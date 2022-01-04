package test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TimeSeries {
	List<String[]> li= new ArrayList<>();
	int numcol;

	public TimeSeries(String csvFileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(csvFileName));
			String string = reader.readLine();
			String titel[] = string.split(",");
			this.numcol = titel.length;
			li.add(titel);

			boolean flag = true;
			while((string = reader.readLine())!= null){
				String row[] = string.split(",");
				this.li.add(row);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
