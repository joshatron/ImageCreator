/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package imageops;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBReader
{

    public static void main(String[] args)
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String url = "jdbc:mysql://joshatron.ddns.net:3306/solartest";
        String user = "davidb";
        String password = "manda621";
        try
        {
            System.out.println("0");
            con = DriverManager.getConnection(url, user, password);
            System.out.println("1");
            st = con.createStatement();
            rs = st.executeQuery("SELECT XLoc, YLoc, Value FROM Mean WHERE MID > 240");

            if(rs.next())
            {
                System.out.println(rs.getString(1));
            }
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
        }
        finally
        {
            try
            {
                if(rs != null)
                {
                    rs.close();
                }
                if(st != null)
                {
                    st.close();
                }
                if(con != null)
                {
                    con.close();
                }
            }
            catch(SQLException ex)
            {
                System.out.println(ex.getMessage());
            }
        }
    }
}

