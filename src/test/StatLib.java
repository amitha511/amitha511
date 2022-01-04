package test;


public class StatLib {


	// simple average
	public static float avg(float[] x){
		float r;
		float sum=0;
		for(int i=0; i< x.length;i++)
			sum+=x[i];

		r = sum/(float)x.length;
		return r;
	}

	// returns the variance of X and Y
	public static float var(float[] x){
		float r;
		float sum=0;
		for(int i=0; i< x.length;i++)
		{ 
			sum += (x[i])*(x[i]);			
		}
				
		r = (sum/(float)x.length)- (avg(x)*avg(x));
		return r;
	}

	// returns the covariance of X and Y
	public static float cov(float[] x, float[] y){
		float r ;
		int size = y.length;
		if (x.length < y.length)
			size= x.length;
		
		float[] arr = new float[size];
		
		for(int i=0 ; i < size ; i++)
		{
			arr[i] = x[i]*y[i];
		}
		
		
		r= avg(arr) - avg(x)*avg(y);
		return r;
	}


	// returns the Pearson correlation coefficient of X and Y
	public static float pearson(float[] x, float[] y){
		float r;
		float culcov= cov(x,y);
		float culx = (float) Math.sqrt(var(x));
		float culy = (float) Math.sqrt(var(y));
		
		
		r=culcov /(culx*culy);
		return r;
	}
	
	// performs a linear regression and returns the line equation
	public static Line linear_reg(Point[] points){

		int size = points.length;
		float[] x= new float[size];
		float[] y= new float[size];		
		
		for(int i=0; i< size ;i++) {
			x[i]=(float)points[i].x;
		}

		for(int i=0; i< size ;i++)
			y[i]=(float)points[i].y;
		
		float a = (float)(cov(x,y) / var(x));
		float b =(float)((avg(y)) -(avg(x)*a));
		Line l = new Line(a,b);

		return l;
	}

	// returns the deviation between point p and the line equation of the points
	public static float dev(Point p,Point[] points){
		Line l = linear_reg(points);
		float y = l.f(p.x);
		
		return (float)Math.abs(p.y - y);
	}

	// returns the deviation between point p and the line
	public static float dev(Point p,Line l){
		float r;
		float y = l.f(p.x);
		r= Math.abs( p.y - y);
		return r;
	}
	
}
