package base;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseConnection {
    private static final String SQLUrl = "jdbc:mysql://localhost/";

    public static Connection establishConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(SQLUrl, username, password);
    }

    public static void loadDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getDatabaseConnection(Connection connection, String database,
                                                   String username, String password) throws SQLException {
        String dbName = useOrCreateAndUseDB(connection, database);
        return DriverManager.getConnection(SQLUrl + dbName, username, password);
    }

    /**
     * Checks whether a database with a particular name exists, creates it if not.
     *
     * @param databaseName name of a database that should exist or should be created
     * @return database name after creating or validating the existence
     * @throws SQLException
     */
    private static String useOrCreateAndUseDB(Connection connection, String databaseName) throws SQLException {
        Statement st = connection.createStatement();
        ResultSet catalogs = connection.getMetaData().getCatalogs();
        ArrayList<String> databasesList = new ArrayList<>();
        while (catalogs.next()) {
            databasesList.add(catalogs.getString("TABLE_CAT"));
        }
        if (!databasesList.contains(databaseName.toLowerCase())) {
            st.executeUpdate("CREATE DATABASE " + databaseName);
        }
        st.close();
        return databaseName;
    }
}
