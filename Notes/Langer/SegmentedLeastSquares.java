package a4solution;

import java.awt.geom.Point2D;
import java.util.ArrayList;


public class SegmentedLeastSquares {

	ArrayList <LineSegment> lineSegments = new ArrayList<LineSegment>();

	Point2D[]  points;

	int      lengthPlusOne=0;  
	double   costSegment;  // this is the constant C in the lecture,  the cost of each segment

	double  a[][];          //   a[i][j] and b[i][j] will be the coefficients of line y = ax + b + e 
	double  b[][];          //   for the segment (xi,..., xj)
	double  e_ij[][];       //   error for the segment (xi, ..., xj)
	double  opt[];			 //   java initializes this to 0.


	public SegmentedLeastSquares(Point2D[] points,  double costOfSegment){
		this.points = points;
		this.costSegment = costOfSegment;

		e_ij = new double[points.length][points.length];
		a    = new double[points.length][points.length];
		b    = new double[points.length][points.length];

		lengthPlusOne = 1 + points.length;
		opt  = new double[ points.length ];
		computeEijAB();
	}

	/*   The formulas for fitting a line to a segment of consecutive
	 *   (x,y) pairs were given in class. 
	 *   Here I give you code that precomputes all e_ij errors in O(N^2) time.
	 *   Note that the obvious brute force computation would have been O(N^3) 
	 *    -- see lecture slides: page 3 middle right slide.
	 */

	public void computeEijAB(){

		//  Our points are indexed from 1 to N.
		//  But for the sum variables we'll want to be able to index from 0 to N
		//  where the 0 value is 0.   So the sum variables with have length that is greater by 1.

		double   denominator;

		double[]  sumX  = new double[ lengthPlusOne ];
		double[]  sumY  = new double[ lengthPlusOne ];
		double[]  sumX2 = new double[ lengthPlusOne ]; 
		double[]  sumY2 = new double[ lengthPlusOne ];
		double[]  sumXY = new double[ lengthPlusOne ];

		for (int i=0; i< points.length; i++){  // Watch out for the 'off by 1' errors
			// The sum_ variables index from 1 to N,  not 0 to N-1,
			// and sum_[0] == 0.

			sumX[i+1]  = sumX[i]  + points[i].getX();
			sumY[i+1]  = sumY[i]  + points[i].getY();
			sumX2[i+1] = sumX2[i] + points[i].getX() * points[i].getX(); 
			sumY2[i+1] = sumY2[i] + points[i].getY() * points[i].getY();
			sumXY[i+1] = sumXY[i] + points[i].getX() * points[i].getY();   
		}

		//  Standard least squares fitting for {(xi, yi), ...  (xj, yj) }.  
		//  The formulas below were given in the lecture for N points.
		//  To do the fits for a segment (xi, ... , xj) where i and j are in 0 to N-1,  
		//  the difference: sum_[j+1] - sum_[i]  and there will be j-i+1 terms we are summing.

		for (int i=0; i< points.length; i++){    
			for (int j = i+1; j < points.length; j++){                                //  i < j
				denominator = (Math.pow(sumX[j+1]-sumX[i],2.0) - (j + 1 - i) * (sumX2[j+1] - sumX2[i]));
				if (denominator == 0){
					System.out.println("No single minimum exists e.g. the minimum is a line running along an infinitely long valley. ");
					a[i][j] = 0.0;
					b[i][j] = 0.0;
					//    In this case,  we don't fit a line segment.  We take y == 0.
				}
				else{
					a[i][j] = ((sumY[j+1] - sumY[i])*(sumX[j+1] - sumX[i]) 
							- (j + 1 - i) * (sumXY[j+1] - sumXY[i]))/ denominator;
					b[i][j] = ((sumX[j+1] - sumX[i])*(sumXY[j+1]- sumXY[i]) 
							-  (sumX2[j+1] - sumX2[i])*(sumY[j+1] - sumY[i]) )/ denominator;

					//    sum_i (yi - (a xi + b))^2 
					//  = sum_i (yi^2 - 2 (a xi + b) yi + (a xi + b)^2)
					//  = sum_i yi^2 - 2a xi yi - 2b yi  + a^2 xi^2 + 2ab xi + b^2

					//  Careful:  e_ij[1][1] refers to e11.

					e_ij[i][j] = (sumY2[j+1]-sumY2[i]) 
							- 2*a[i][j]*         (sumXY[j+1]-sumXY[i])
							- 2*b[i][j]*          (sumY[j+1] -sumY[i])
							+   a[i][j]*a[i][j] * (sumX2[j+1]-sumX2[i])
							+ 2*a[i][j]*b[i][j] * (sumX[j+1]-sumX[i]) 
							+   b[i][j]*b[i][j] * (j+1 - i);
				}
			}
		}
	}  

	//   Iterative computation of the optimal costs in fitting the data for problem with
	//   points in 0, ..., j,   for j in 0 to N-1.

	public void computeOptIterative( ){

		//   ADD YOUR CODE BELOW HERE

		double optJ;
		double tmpE = 0;

		for (int j=0; j< points.length; j++){
			optJ = Double.MAX_VALUE;
			for (int i=0; i <= j; i++){
				if (i == 0)
					tmpE = costSegment + e_ij[i][j];
				else
					tmpE = costSegment + e_ij[i][j] + opt[i-1];
				if (tmpE < optJ){
					optJ = tmpE;
				}
			}
			opt[j] = optJ;
		}

		//   ADD YOUR CODE ABOVE HERE
	}

	//  This method computes the minimal cost of a least squares fit for the first j samples, 
	//  using recursion with memoization.
	//  Memoization avoids avoid the combinatorial explosion that occurs with naive recursion,
	//  Note that this method just computes the opt values.  It doesn't do the segmentation.

	public double computeOptRecursive(int j){

		//   ADD YOUR CODE BELOW HERE

		double optJ;
		double tmpE = 0;

		optJ = Double.MAX_VALUE;

		//  Solve opt[j] = min_{ 0 <= i < j } { costSegment + e_ij[i][j] + opt[i-1] }
		//
		//  We need check that we don't recompute opt[ k ] for any k more than once,
		//  and also we respect array bounds.

		for (int i=0; i <= j; i++){
			if (i == 0)              //   this is the case where the segment is (0, ..., j)
				tmpE = costSegment + e_ij[i][j];
			else{      //   otherwise,  not just one segment
				//   so we need to segment {0, ..., i-1}
				if (opt[i-1] == 0)   
					//  Once we've computed opt[k], it will be positive.  So, if opt[k] is 0 then we
					//  we haven't computed it yet.  
					opt[i-1] = computeOptRecursive(i-1);
				tmpE = costSegment + e_ij[i][j] + opt[i-1];
			}
			if (tmpE < optJ){    //  keeping track of the min over all i,  i.e.  is this the best i ?  
				optJ = tmpE;
			}     
		}
		return opt[j] = optJ;

		//   ADD YOUR CODE ABOVE HERE
	}

	//   Print out segmentation

	public void computeSegmentation(int j){

		//   ADD YOUR CODE BELOW HERE

		double  tmpCost = 0;
		int  	which_i = 0;
		boolean found = false;

		if (j < 0)
			return;

		// take the minimum over i <= j

		for (int i=0; (i <= j) && !found; i++){
			if (i == 0){
				tmpCost = e_ij[i][j] + costSegment;    
			}
			else  
				tmpCost = e_ij[i][j] + costSegment + opt[i-1];
			if (tmpCost == opt[j]){
				found = true;
				which_i = i;

				//  Here I just print the interval (i,j) and the line y=ax+b  and the error in fitting that line.
				//  But you could put in a data structure  e.g.  an inner class Segment and an ArrayList<Segment>

				LineSegment segment = new LineSegment(i, j, a[i][j], b[i][j], e_ij[i][j]);

				/*
				 * segment.i = i;
				 * 				segment.j = j;
				segment.a = a[i][j];
				segment.b = b[i][j];
				segment.error = e_ij[i][j];

				 */
				
				

				lineSegments.add(segment);

			}
		}
		computeSegmentation(which_i-1);

		//  ADD YOUR CODE ABOVE HERE
	}

	public ArrayList<LineSegment> solveIterative(){

		System.out.println("Solving iteratively...");
		computeOptIterative();
		computeSegmentation( points.length - 1);  //  indices of points is 0, ...,  N-1
		return(lineSegments);
	}

	public ArrayList<LineSegment> solveRecursive(){

		System.out.println("\nSolving recursively...");
		computeOptRecursive( points.length - 1);
		computeSegmentation( points.length - 1);  //  indices of points is 0, ...,  N-1
		return(lineSegments);
	}
}

class LineSegment{
	int i,j;  
	double a,b,error;  
	
	public LineSegment(int i, int j, double a, double b, double error){
		this.i = i;
		this.j = j;
		this.a = a;
		this.b = b;
		this.error = error;
	}

	public String toString(){
		return	 " (" + new Integer(i) + "," + new Integer(j) + ") " 
				+ "   line is  y = " + String.format("%.2f",a) + " x + "
				+ String.format("%.2f ", b) + ",  error is " + String.format("%.2f", error);
	}


}
