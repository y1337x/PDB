package PDB;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class SQLiteDB {

    private Connection conn;
    private final String URL;

    public SQLiteDB(String filename) throws SQLException {
        this.URL = "jdbc:sqlite:" + filename;
        try {
            this.conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw e;
        }
    }

    public void closeConnection() throws SQLException {
        try {
            if (this.conn != null) {
                this.conn.close();
            }
        } catch (SQLException ex) {
            throw ex;
        }
    }

    public void addColumns(String tableName, List<String> colNames) throws SQLException {
        SQLException ex = null;
        for (String s:colNames){
            try {
                this.conn.createStatement().execute("ALTER TABLE " + tableName + " ADD COLUMN '" + s + "' TEXT default null");
            } catch (SQLException e) {
                ex = e;
            }
        }
        if (ex != null) throw ex;
    }

    public void addTagsColumn(String tableName) throws SQLException {
        this.conn.createStatement().execute("ALTER TABLE " + tableName + " ADD COLUMN 'Tags' TEXT default null");
    }

    public void addCommentsColumn(String tableName) throws SQLException {
        this.conn.createStatement().execute("ALTER TABLE " + tableName + " ADD COLUMN 'Comments' TEXT default null");
    }

    public void testAddColumns() throws SQLException {
        List<String> list = new LinkedList<>();
        list.add("first");list.add("second");list.add("third");list.add("newCol");
        this.addColumns("patents", list);
    }

    /*
    public void displayColumns(String tableName) {
        String sql = "SELECT * FROM sqlite_master";

        try {
            Statement stmt  = this.conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while (rs.next()) {
                int cc = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= cc; i++) {
                    System.out.println(rs.getMetaData().getColumnName(i));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    */

    public void insert(String tableName, List<String> colNames, List<String> values) throws SQLException {
        String sql = "INSERT INTO " + tableName + "(";

        for (int i = 0; i < colNames.size(); i++) {
            sql += "'" + colNames.get(i) + "'";
            if (i != colNames.size() - 1) sql += ",";
        }

        sql += ") VALUES(";

        for (int i = 0; i < values.size(); i++) {
            sql += "?";
            if (i != values.size() - 1) sql += ",";
        }

        sql += ")";

        PreparedStatement pstmt = this.conn.prepareStatement(sql);
        for (int i = 1; i <= values.size(); i++) {
            //pstmt.setString(i, "'" + values.get(i-1) + "'");
            pstmt.setString(i, values.get(i-1));
        }
        pstmt.executeUpdate();
    }

    public void testInsert() throws SQLException {
        List<String> list = new LinkedList<>();
        list.add("first");list.add("second");list.add("third");list.add("newCol");list.add("testitest");
        List<String> list2 = new LinkedList<>();
        list2.add("first");list2.add("second");list2.add("third");list2.add("newVal");list2.add("hoho");
        this.insert("patents", list, list2);
    }

    public void createTable(String tableName, List<String> cols) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (id integer PRIMARY KEY";

        for (String s: cols) {
            sql += ", '" + s + "' text";
        }

        sql += ");";

        Statement stmt = conn.createStatement();
        stmt.execute(sql);
    }

    public void testCT() throws SQLException {
        List<String> list = new LinkedList<>();
        list.add("first");list.add("second");list.add("third");
        this.createTable("patents", list);
    }

    // does not work
    // SQLException: ResultSet is TYPE_FORWARD_ONLY
    public boolean tableExists(String tableName){
        try{
            DatabaseMetaData md = this.conn.getMetaData();
            ResultSet rs = md.getTables(null, null, tableName, null);
            rs.last();
            return rs.getRow() > 0;
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return false;
    }
}
