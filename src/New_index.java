import java.awt.event.ItemEvent;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.io.FileOutputStream;  
import java.io.PrintStream; 
import java.io.FileNotFoundException;

public class New_index {
	private static final double EARTH_RADIUS = 6371393; 
	static int num_cluster; //number of clusters
	static clustered_point[][]points; // all data points
	static double []compact; //compactness
	static double []index; //index value for every cluster

	public static void main(String[] args) {
		// read csv file
		String filePath1 = new String("D:\\科研相关\\聚类评价算法\\newHubei_enter\\Hubei_enter\\dbscan1.csv"); //remember to change the path
		String filePath2 = new String("D:\\科研相关\\聚类评价算法\\newHubei_enter\\Hubei_enter\\dbscan2.csv");
		String filePath3 = new String("D:\\科研相关\\聚类评价算法\\newHubei_enter\\Hubei_enter\\dbscan3.csv");
		String filePath4 = new String("D:\\科研相关\\聚类评价算法\\newHubei_enter\\Hubei_enter\\dbscan4.csv");
		String filePath5 = new String("D:\\科研相关\\聚类评价算法\\newHubei_enter\\Hubei_enter\\hubei_512.csv");
		String filePath6 = new String("D:\\科研相关\\聚类评价算法\\newHubei_enter\\Hubei_enter\\hubei_1024.csv");
		String filePath7 = new String("D:\\科研相关\\聚类评价算法\\newHubei_enter\\Hubei_enter\\hubei_2048.csv");
		String filePath8 = new String("D:\\科研相关\\聚类评价算法\\newHubei_enter\\Hubei_enter\\hubei_4096.csv");
		String [] filePath = {filePath1,filePath2,filePath3,filePath4,filePath5,filePath6,filePath7,filePath8};
		for(int i = 0; i < 4; i ++) {
			//calculate new index for each data set
			computeIndex(i,filePath);
		}
		
		
	}

	private static void computeIndex(int fileId, String[] filePath) {
		// compute new index for data set i
		//set all global var to null
		System.out.println("fileID:[" + fileId +"]");
		compact = null;
		points = null;
		index = null;
		
		long startTime = System.currentTimeMillis();
		ReadCSV file_csv = new ReadCSV(filePath[fileId]);
		points = file_csv.read(filePath[fileId]);
		num_cluster = file_csv.num_cluster;
		//calculate Ck for every cluster
		compact = new double[num_cluster];
		for (int i = 0; i < num_cluster; i++) {
			compact[i] = cal_c(i); 
//			System.out.println("compactness[" + i + "]: " + compact[i]);
		}
		
		//calculate distance for the closet point of every two clusters
		double [][]dist = new double[num_cluster][num_cluster];
		dist = calMinDist();
		//calculate index i for every cluster
		index = new double[num_cluster];
		for (int i = 0; i < num_cluster; i++) {
			index[i] = calIndexI(i,dist);
		}
		// calculate index for all clusters
		double index_all = calIndex();
		long endTime = System.currentTimeMillis();
		try {
			File file = new File("D:\\科研相关\\聚类评价算法\\newHubei_enter\\Hubei_enter\\new_index.txt");
			FileOutputStream Out = new FileOutputStream(file,true);	
			String content = "Total time cost for file [" + fileId + "]: " + (endTime - startTime) + "\n";
			Out.write(content.getBytes());
			content = "result of file [" + fileId + "]: " + index_all + "\n";
			Out.write(content.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private static double calIndex() {
		// calculate new_index value as final result
		double result = index[0];
		for(double item : index) {
			if(item <= result) {
				result = item;
			}
		}
		return result;
	}

	private static double calIndexI(int index, double[][] dist) {
		// calculate new_index value for cluster i
		double result = 99999999;
		for(int i = 0; i < num_cluster; i++) {
			if(index == i) {
				continue;
			}
			double temp = (compact[index] * points[index].length + compact[i] * points[i].length) 
					/ (points[index].length + points[i].length);
			temp = temp * dist[index][i];
			if(temp <= result) {
				result = temp;
			}
		}
		return result;
	}

	private static double[][] calMinDist() {
		// calculate min dist for all different clusters
		double [][]dist = new double[num_cluster][num_cluster];
		for(int i = 0; i < num_cluster; i++) {
			for(int j = 0; j < num_cluster; j++) {
				if(j < i) {
					//already calculated
					dist[i][j] = dist[j][i];
				}
				else if(i == j) {
					//no need to compute dist of the two same clusters
					dist[i][j] = -1;
				}
				else {
					//calculate dist ij
					dist[i][j] = calDistIJ(i,j);
//					System.out.println("mindist[" + i + "," + j +  "]: " + dist[i][j]);
				}
			}
		}
		return dist;
	}

	private static double calDistIJ(int index_i, int index_j) {
		// calculate min dist for two different clusters
		double min_dist = 999999;
		for(int i = 0; i <points[index_i].length; i ++) {
			for(int j = 0; j < points[index_j].length; j++) {
				double dist = getDistance(points[index_i][i].point, points[index_j][j].point);
				if(dist <= min_dist) {
					min_dist = dist;
				}
				
			}
		}
		return min_dist;
	}

	private static double cal_c(int index) {
		// calculate compactness parameter for cluster i
		double max_dist = -1;
		for(int i = 0; i <points[index].length; i ++) {
			for(int j = 0; j < points[index].length; j++) {
				if(i > j) continue; //compute half of the matrix to save time
				else {
					double dist = getDistance(points[index][i].point, points[index][j].point);
					if(dist >= max_dist) {
						max_dist = dist;
					}
				}
			}
		}
		
		return 1.0 / max_dist;
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
	public ReadCSV(String file) {
		filePath = file;
	}
	public clustered_point[][] read(String file) {
		try {
			//read file
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
//			System.out.print(num_lines);
			String line = null;
			clustered_point[][] classified_points;
			int count = 0;
			//read first line to get num of clusters
			line = reader.readLine();
			String[] item = line.split(",");
			num_cluster = Integer.parseInt(item[2]);
			int index = num_cluster;
			classified_points = new clustered_point[num_cluster][];
			count ++;
			int flag = 1;
			//read the rest lines to initialize the array
			while((line = reader.readLine()) != null) {
				item = line.split(",");
				if(Integer.parseInt(item[2]) == index) {
					count ++;
				}
				else {
					if(flag == 1) {
						classified_points[index - 1] = new clustered_point[count];
						flag = 0;
					}
					else {
						classified_points[index - 1] = new clustered_point[count+1];
					}
					
					index = Integer.parseInt(item[2]);	
					count = 0;
				}
			}
			classified_points[index - 1] = new clustered_point[count + 1];
			BufferedReader reader3 = new BufferedReader(new FileReader(filePath));
			int i = num_cluster - 1,j = 0;
			//read the data from file
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
			}
			reader.close();
			reader3.close();
			return classified_points;
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
