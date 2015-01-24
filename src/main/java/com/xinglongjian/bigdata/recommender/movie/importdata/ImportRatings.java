package com.xinglongjian.bigdata.recommender.movie.importdata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.xinglongjian.bigdata.recommender.movie.model.Rating;

/**
 * import rating.dat data to mysql database
 * @author root
 *
 */
public class ImportRatings {
	public final static String TABLE_NAME = "movie_preferences";
	public final static String USER_ID_COLUMN = "userID";
	public final static String MOVIE_ID_COLUMN = "movieID";
	public final static String RATING = "preference";
	public final static String TIMESTAMP = "timestamp";
	
	private static Rating fillRating(String line) {

		String[] ra = line.split("::");
		Rating rating = new Rating();
		rating.setUser_id(Integer.parseInt(ra[0]));
		rating.setMovie_id(Integer.parseInt(ra[1]));
		rating.setRating(Integer.parseInt(ra[2]));
		rating.setTimestamp(Integer.parseInt(ra[3]));
		return rating;
	}


	public static void insertRatings(List<Rating> ratings) {
		Connection conn = DBUtil.getJDBCConnection();
		PreparedStatement ps = null;
		String sql = "insert into " + TABLE_NAME + " ( " + USER_ID_COLUMN
				+ ", " + MOVIE_ID_COLUMN + ", " + RATING + ", " + TIMESTAMP
				+ ") values (?, ?, ?, ?)";
		try {
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(sql);

			for (Rating rating : ratings) {
				ps.setInt(1, rating.getUser_id());
				ps.setInt(2, rating.getMovie_id());
				ps.setInt(3, rating.getRating());
				ps.setInt(4, rating.getTimestamp());
				ps.addBatch();
			}

			ps.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		try {
			ImportRatings ir=new ImportRatings();
			ClassLoader cl=ir.getClass().getClassLoader();
			URL url=cl.getResource("movies/ml-1m/users.dat");
			String filepath=url.getFile();
			File file=new File(filepath);
			LineNumberReader lineReader = new LineNumberReader(new FileReader(file));
			String line = "";
			List<Rating> ratingList = new ArrayList<Rating>();
			while ((line = lineReader.readLine()) != null) {
				ratingList.add(fillRating(line));
			}
			insertRatings(ratingList);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
