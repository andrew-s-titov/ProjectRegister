package tables;

import java.sql.*;

public abstract class AbstractTable {
    abstract String getTableName();

    public String printTableHeaderHTML(Connection connection) throws SQLException {
        StringBuilder str = new StringBuilder("<tr>");
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet rsTable = meta.getColumns(null, null, this.getTableName(), null);

        while (rsTable.next()) {
            str.append("<th align=\"left\">");
            str.append(rsTable.getString("COLUMN_NAME"));
            str.append("</th>");
        }
        str.append("<th></th>");
        str.append("</tr>");
        return str.toString();
    }

    public String printTableHTML(Connection connection, String editServletPath, String editButtonName) throws SQLException {
        String tableName = this.getTableName();
        StringBuilder str = new StringBuilder("<html><head><title>" + tableName +
                "</title></head><body><table style=\"width:50%\"><caption style=\"font-size:2em; height:60px\">"
                + tableName + "</caption>");

        str.append(this.printTableHeaderHTML(connection));

        ResultSet rs = getTable(connection);
        ResultSetMetaData meta = rs.getMetaData();
        int columnsCount = meta.getColumnCount();

        while (rs.next()) {
            String rowId = rs.getString(1);
            str.append("<tr>");
            for (int i = 1; i <= columnsCount; i++) {
                String value = rs.getString(i);
                if (rs.wasNull() || value.equalsIgnoreCase("null")) {
                    value = "";
                }
                str.append("<td>");
                str.append(value);
                str.append("</td>");
            }

            str.append(createPostButton(editServletPath, editButtonName, rowId));
            str.append("</tr>");
        }
        str.append("</table><p></p></body></html>");
        return str.toString();
    }

    public void clearTable(Connection connection) throws SQLException {
        Statement st = connection.createStatement();
        String query = "DELETE FROM " + this.getTableName();
        st.executeUpdate(query);
        query = String.format("ALTER TABLE %s AUTO_INCREMENT = 1", this.getTableName());
        st.executeUpdate(query);
        st.close();
    }

    protected boolean tableExists(Connection connection) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet rs = meta.getTables(null, null, this.getTableName(), new String[]{"TABLE"});
        boolean exists = rs.next();
        rs.close();
        return exists;
    }

    public ResultSet getTable(Connection connection) throws SQLException {
        Statement st = connection.createStatement();
        String query = "SELECT * FROM " + this.getTableName() + ";";
        return st.executeQuery(query);
    }

    private String createPostButton(String servletPath, String buttonName, String buttonValue) {
        StringBuilder str = new StringBuilder();
        str.append("<td><form action=\"");
        str.append(servletPath);
        str.append("\" method=\"POST\"><button name=\"");
        str.append(buttonName);
        str.append("\" value=\"");
        str.append(buttonValue);
        str.append("\">");
        str.append(buttonName);
        str.append("</button></form></td>");
        return str.toString();
    }
}
