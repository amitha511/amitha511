package test;
import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static test.StatLib.*;

public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {
	List<CorrelatedFeatures> cor = new ArrayList<CorrelatedFeatures>();

	@Override
	public void learnNormal(TimeSeries ts) {
		float m = 0;
		int c = -1, i, j;
		float p = 0;
		float[] fi = new float[ts.li.size()];
		float[] fj = new float[ts.li.size()];
		for (i = 0; i < ts.numcol; i++) {
			m = 0;
			c = -1;
			for (j = i + 1; j < ts.numcol; j++) {
				fi = col(ts, i);
				fj = col(ts, j);
				p = Math.abs(pearson(fi, fj));
				if (p > m && p >= 0.9) {
					m = p;
					c = j;
				}

			}
			if (c != -1) {
				j = c;
				fj = col(ts, j);
				Point[] point = doPoint(fi, fj);// e.g : corolasya a,c --> point(a,b)
				Line reg = linear_reg(point); // l = regresya line

				float threshold = 0;
				for (int k = 0; k < point.length; k++) {
					float dev = dev(point[k], reg);

					if (threshold < dev) {
						threshold = dev(point[k], reg); //max
					}
				}

				cor.add(new CorrelatedFeatures(ts.li.get(0)[i], ts.li.get(0)[j], m, reg, (float) (threshold * 1.1)));
			}
		}
	}

	public Point[] doPoint(float[] x, float[] y) {

		if (x.length == y.length) {
			Point[] p = new Point[x.length];
			for (int i = 0; i < x.length; i++)
				p[i] = new Point(x[i], y[i]);
			return p;

		} else
			return null;
	}

	public float[] col(TimeSeries ts, int colm) {
		int j = 0;
		float[] vec = new float[ts.li.size() - 1];
		String temp; //overflow
		for (int i = 1; i < ts.li.size(); i++) {
			temp = ts.li.get(i)[colm];
			Float.parseFloat(temp);
			vec[j] = Float.parseFloat(temp);
			j++;
		}

		return vec;
	}


	@Override
	public List<AnomalyReport> detect(TimeSeries ts) {
		List<AnomalyReport> reports = new ArrayList<AnomalyReport>();


		int i=0,r=0,j=0;//r=num of row
		Point point;
		int feature1=-1;
		int feature2=-1;
		r=1;
		float[] row= new float[ts.numcol];


		for(i =0; i < this.cor.size(); i++) {

			for (j = 0; j < ts.numcol; j++) {  //feature1

				if (ts.li.get(0)[j].equals(this.cor.get(i).feature1) == true) {
					feature1 = j;
					break;
				}
			}

			for (j = 0; j < ts.numcol; j++) {
				if (ts.li.get(0)[j].equals(this.cor.get(i).feature2) == true) {
					feature2 = j;
					break;
				}
			}

			if (feature1 == -1 || feature2 == -1) {
				continue;
			}

			for (r=1; r < ts.li.size(); r++) {

				int k = 0;
				for (String s : ts.li.get(r)) {
					row[k] = Float.parseFloat(s);
					k++;
				}

				point = new Point(row[feature1], row[feature2]);
				if (dev(point, this.cor.get(i).lin_reg) > this.cor.get(i).threshold) {
					AnomalyReport report = new AnomalyReport(ts.li.get(0)[feature1] + "-" + ts.li.get(0)[feature2], (long) r);
					reports.add(report);

				}

			}
		}


		return reports;
	}



	public List<CorrelatedFeatures> getNormalModel () {
		return this.cor;
	}


}
