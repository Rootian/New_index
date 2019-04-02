import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;


public class New_index {
	private static final double EARTH_RADIUS = 6371393; 
	static int num_cluster; //number of clusters
	static int num_lines; //number of data points
	static ArrayList<clustered_point> points;
	
	static double []index;

	public static void main(String[] args) {
		// read csv file
		String filePath = new String("/Users/aaronkb/Desktop/512x512.csv");
		ReadCSV file_csv = new ReadCSV(filePath);
		points = file_csv.read(filePath);
		num_cluster = file_csv.num_cluster;
		num_lines = file_csv.num_lines;
		//stored points of index
		clustered_point[][] classified_points = new clustered_point[num_cluster][];
		//calculate Ck for every cluster
		double []compact = new double[num_cluster];
		for (int i = 0; i < num_cluster; i++) {
			compact[i] = cal_c(i);
		}
		//calculate distance for the closet point of every two clusters
		double [][]dist = new double[num_cluster][num_cluster];
		dist = calMinDist();
		//calculate index i for every cluster
		index = new double[num_cluster];
		for (int i = 0; i < num_cluster; i++) {
			index[i] = calIndexI(i);
		}
		// calculate index for all clusters
		double index_all = calIndexI();
		System.out.print(index_all);
		
	}

	private static double calIndexI() {
		// TODO Auto-generated method stub
		return 0;
	}

	private static double calIndexI(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	private static double[][] calMinDist() {
		// TODO Auto-generated method stub
		double [][]dist = new double[num_cluster][num_cluster];
		for(int i = 0; i < num_cluster; i++) {
			for(int j = 0; j < num_cluster; j++) {
				if(j < i) {
					//already calculated
					dist[i][j] = dist[j][i];
				}
				else {
					//calculate dist ij
					dist[i][j] = calDistIJ(i+1,j+1);
				}
			}
		}
		return dist;
	}

	private static double calDistIJ(int index_i, int index_j) {
		// TODO Auto-generated method stub
		return 0;
	}

	private static double cal_c(int index) {
		// calculate compactness parameter for cluster i
		double c;
		int count = 0;
		for(clustered_point cpoint : points) {
			if(cpoint.index == index) {
				count ++;
				 
			}	        
		}
		return 0;
	}
	
	public static double getDistance(Point2D pointA, Point2D pointB) {
	    double radiansAX = Math.toRadians(pointA.getX()); 
	    double radiansAY = Math.toRadians(pointA.getY()); 
	    double radiansBX = Math.toRadians(pointB.getX()); 
	    double radiansBY = Math.toRadians(pointB.getY()); 

	    double cos = Math.cos(radiansAY) * Math.cos(radiansBY) * Math.cos(radiansAX - radiansBX)
	            + Math.sin(radiansAY) * Math.sin(radiansBY);
	    double acos = Math.acos(cos);
	    return EARTH_RADIUS * acos; 
	}

}


class ReadCSV{
	String filePath;
	int num_cluster;
	int num_lines;
	public ReadCSV(String file) {
		filePath = file;
	}
	public ArrayList<clustered_point> read(String file) {
		try {
			//read file
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			LineNumberReader reader2 = new LineNumberReader(new FileReader(filePath));
			reader2.skip(Long.MAX_VALUE);
			num_lines = reader2.getLineNumber();
//			System.out.print(num_lines);
			ArrayList<clustered_point> points = new ArrayList<clustered_point>();
			String line = null;
			clustered_point[][] classified_points;
			int flag = 1;
			int count = 0;
			//read first line to get num of clusters
			line = reader.readLine();
			String[] item = line.split(",");
			num_cluster = Integer.parseInt(item[2]);
			int index = num_cluster;
			classified_points = new clustered_point[num_cluster][];
			count ++;
			
			Point2D pointtemp = new Point2D.Double(Double.parseDouble(item[0]),Double.parseDouble(item[1]));
			clustered_point ptemp = new clustered_point(pointtemp, Integer.parseInt(item[2]));
			points.add(ptemp);
			//read the rest lines to initialize the array
			while((line = reader.readLine()) != null) {
				item = line.split(",");
				if(Integer.parseInt(item[2]) == index) {
					count ++;
				}
				else {
					classified_points[index - 1] = new clustered_point[count];
					index = Integer.parseInt(item[2]);	
					count = 0;
				}
				Point2D point = new Point2D.Double(Double.parseDouble(item[0]),Double.parseDouble(item[1]));
				clustered_point p = new clustered_point(point, Integer.parseInt(item[2]));
				
				points.add(p);
			}
			classified_points[index - 1] = new clustered_point[count];
			BufferedReader reader3 = new BufferedReader(new FileReader(filePath));
			int i = num_cluster - 1,j = 0;
			while((line = reader3.readLine()) != null) {
				item = line.split(",");
				Point2D point = new Point2D.Double(Double.parseDouble(item[0]),Double.parseDouble(item[1]));
				
				if(j == classified_points[i].length) {
					//read the next cluster
					i --;
					j = 0;
				}
				classified_points[i][j] = new clustered_point(point, Integer.parseInt(item[2]));
				j++;
				if(i ==0) {
					int x = 1;
					
				}
				
			}
			System.out.printf("size:%d\n",points.size());
			reader.close();
			reader2.close();
			reader3.close();
			return points;
		}

		catch(IOException e){
			System.out.println("Read file error!");
		}
		return null;
	}
	
}
class clustered_point{
	Point2D point;
	int index;
	public clustered_point(Point2D p, int i) {
		point = p;
		index = i;
	}
}
