/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package imageops;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/**
 *
 * @author David
 */
public class DBReader {
    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    
    private float[][][] grids;
    private int[] timeStamp;
    private int tableCount;
    
    private final String startDate,
            startTime, 
            endDate, 
            endTime, 
            events,
            graphics;
    
    public DBReader(String startDate, String startTime, String endDate, String endTime, String events, String graphics) {
        this.startDate = startDate;
        this.startTime = startTime; 
        this.endDate = endDate; 
        this.endTime = endTime; 
        this.events = events;
        this.graphics = graphics;
    }
    

    public void readDataBase() throws Exception {
        try {
                // this will load the MySQL driver, each DB has its own driver
            // Class.forName("com.mysql.jdbc.Driver");  // I think this is depricated, now autodetected.
            // setup the connection with the DB.
            connect = DriverManager.getConnection("jdbc:mysql://joshatron.ddns.net/solartest?"
                            + "user=davidb&password=manda621");

            // statements allow to issue SQL queries to the database
            statement = connect.createStatement();
            // resultSet gets the result of the SQL query
            resultSet = statement.executeQuery("SELECT * FROM Measurement WHERE MeasurementID=1046");
            writeResultSet(resultSet);

        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }

    }

    private void writeMetaData(ResultSet resultSet) throws SQLException {
        // now get some metadata from the database
        System.out.println("The columns in the table are: ");
        System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
            System.out.println("Column " + i + " " + resultSet.getMetaData().getColumnName(i));
        }
    }

    private void writeResultSet(ResultSet resultSet) throws SQLException {
        // resultSet is initialised before the first data set
        while (resultSet.next()) {
      // it is possible to get the columns via name
            // also possible to get the columns via the column number
            // which starts at 1
            // e.g., resultSet.getSTring(2);
            String user = resultSet.getString("myuser");
            String website = resultSet.getString("webpage");
            String summary = resultSet.getString("summary");
            Date date = resultSet.getDate("datum");
            String comment = resultSet.getString("comments");
            System.out.println("User: " + user);
            System.out.println("Website: " + website);
            System.out.println("Summary: " + summary);
            System.out.println("Date: " + date);
            System.out.println("Comment: " + comment);
        }
    }

    // you need to close all three to make sure
    private void close() {
        close((Closeable) resultSet);
        close((Closeable) statement);
        close((Closeable) connect);
    }

    private void close(Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (Exception e) {
            // don't throw now as it might leave following closables in undefined state
        }
    }
    
    public float[][] getGrid(int index) {
        return grids[index];
    }
    
    //public int get
}
