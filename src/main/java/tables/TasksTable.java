package tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TasksTable extends AbstractTable {
    private static TasksTable instance;
    private static Connection connection;
    final private String tableName = "Tasks";
    final private String projectColumnName = "ProjectShortName";
    final private String nameColumnName = "TaskName";
    final private String hoursColumnName = "Hours";
    final private String startDateColumnName = "StartDate";
    final private String finishDateColumnName = "FinishDate";
    final private String statusColumnName = "Status";
    final private String[] statuses = {"not started", "in progress", "finished", "postponed"};

    private TasksTable() throws SQLException {
        if (!tableExists(connection)) {
            Statement st = connection.createStatement();
            String sqlTable = String.format("CREATE TABLE %s " +
                            "(id int not null auto_increment, " +
                            "%s varchar(20) not null, " +
                            "%s varchar(100) not null, " +
                            "%s int, " +
                            "%s date not null, " +
                            "%s date not null, " +
                            "%s varchar(20) not null, " +
                            "primary key (id));",
                    tableName, projectColumnName, nameColumnName, hoursColumnName,
                    startDateColumnName, finishDateColumnName, statusColumnName);
            st.executeUpdate(sqlTable);
            st.close();
        }
    }

    public static TasksTable getInstance(Connection conn) throws SQLException {
        connection = conn;
        if (instance == null) {
            instance = new TasksTable();
        }
        return instance;
    }

    public String getTableName() {
        return tableName;
    }

    public void addTask(String projectShortName, String name, String hours, String startDate, String finishDate,
                        String status) throws SQLException {
        Statement st = connection.createStatement();
        String query = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) values " +
                        "(\"%s\", \"%s\", %s, \"%s\", \"%s\", \"%s\");",
                tableName, projectColumnName, nameColumnName, hoursColumnName, startDateColumnName,
                finishDateColumnName, statusColumnName, projectShortName, name, hours,
                startDate, finishDate, status);
        st.executeUpdate(query);
        st.close();
    }

    public void updateTask(String taskId, String name, String hours, String startDate, String finishDate,
                           String status) throws SQLException {
        Statement st = connection.createStatement();
        String query = String.format("UPDATE %s SET %s=\"%s\", %s=%s, %s=\"%s\",  %s=\"%s\", %s=\"%s\" WHERE id=%s;",
                tableName, nameColumnName, name, hoursColumnName, hours, startDateColumnName, startDate,
                finishDateColumnName, finishDate, statusColumnName, status, taskId);
        st.executeUpdate(query);
        st.close();
    }

    public void editProjectShortName(String shortName, String shortNameOld) throws SQLException {
        Statement st = connection.createStatement();
        String query = String.format("UPDATE %s SET %s=\"%s\" WHERE %s=\"%s\";",
                tableName, projectColumnName, shortName, projectColumnName, shortNameOld);
        st.executeUpdate(query);
        st.close();
    }

    public void deleteTask(String taskId) throws SQLException {
        Statement st = connection.createStatement();
        String sql = String.format("DELETE FROM %s WHERE id=%s;", tableName, taskId);
        st.executeUpdate(sql);
        st.close();
    }

    public String[] getStatuses() {
        return statuses;
    }

    public String getProjectColumnName() {
        return projectColumnName;
    }

    @Override
    public ResultSet getTable(Connection connection) throws SQLException {
        Statement st = connection.createStatement();
        String query = "SELECT * FROM " + this.getTableName() + " ORDER BY " + projectColumnName + ";";
        return st.executeQuery(query);
    }
}
