package tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class ProjectsTable extends AbstractTable {
    private static Connection connection;
    private static ProjectsTable instance;
    final private String tableName = "Projects";
    final private String nameColumnName = "Name";
    final private String shortNameColumnName = "ShortName";
    final private String descriptionColumnName = "Description";

    private static Set<String> projectsShortNamesSet = new HashSet<>();

    private ProjectsTable() throws SQLException {
        if (!tableExists(connection)) {
            Statement st = connection.createStatement();
            String sqlTable = String.format("CREATE TABLE %s " +
                            "(id int not null auto_increment, " +
                            "%s varchar(255) NOT NULL UNIQUE, " +
                            "%s varchar(20) NOT NULL UNIQUE, " +
                            "%s text, " +
                            "primary key (id));",
                    tableName, nameColumnName, shortNameColumnName, descriptionColumnName);
            st.executeUpdate(sqlTable);
            st.close();
        } else {
            ResultSet rs = getTable(connection);
            while (rs.next()) {
                projectsShortNamesSet.add(rs.getString(shortNameColumnName));
            }
            rs.close();
        }
    }

    public static ProjectsTable getInstance(Connection conn) throws SQLException {
        connection = conn;
        if (instance == null) {
            instance = new ProjectsTable();
        }
        return instance;
    }

    public String getTableName() {
        return tableName;
    }

    public void addNewProject(String name, String shortName, String description) throws SQLException {
        String sql = String.format("INSERT INTO %s (%s, %s, %s) values (\"%s\", \"%s\", \"%s\");",
                tableName, nameColumnName, shortNameColumnName, descriptionColumnName,
                name, shortName, description);
        Statement st = connection.createStatement();
        st.executeUpdate(sql);
        st.close();
        projectsShortNamesSet.add(shortName);
    }

    @Override
    public void clearTable(Connection connection) throws SQLException {
        super.clearTable(connection);
        projectsShortNamesSet.clear();
    }

    public Set<String> getProjectsShortNamesSet() {
        return projectsShortNamesSet;
    }

    public String getShortNameColumnName() {
        return shortNameColumnName;
    }

    public void deleteProject(String projectId) throws SQLException {
        Statement st = connection.createStatement();
        String deletePr = String.format("DELETE FROM %s WHERE id=%s;", tableName, projectId);
        st.executeUpdate(deletePr);
        st.close();
    }

    public void updateProject(String taskId, String name, String shortName, String description) throws SQLException {
        Statement st = connection.createStatement();
        String upd = String.format("UPDATE %s SET %s=\"%s\", %s=\"%s\", %s=\"%s\" WHERE id=%s;",
                tableName, nameColumnName, name, shortNameColumnName, shortName,
                descriptionColumnName, description, taskId);
        st.executeUpdate(upd);
        st.close();
    }
}
