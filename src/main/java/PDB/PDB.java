package PDB;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;

public class PDB {

    private ExcelSheet sheet;
    private SQLiteDB db;

    public PDB(String dbFileName) {
        try {
            this.db = new SQLiteDB(dbFileName);
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
    }

    public void closeDBConnection() {
        try {
            this.db.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int addEntriesFromExcel(String excelFileName) {
        // open file
        try {
            this.sheet = sheet = new ExcelSheet(excelFileName);
            System.out.println("Excel file opened");
        } catch (IOException ex) {
            return 1;
        }

        // delete stuff above table column headers
        LinkedList<LinkedList<String>> content = this.sheet.getContentLists();
        /* Index out of bound with 3 rows
        for (int i = 0; i < content.size(); i++) {
            if (content.get(i+1).size() > content.get(i).size()) {
                content.remove(i);
            } else {
                break;
            }
        }
        */
        while (content.size() > 2) {
            if (content.get(1).size() > content.get(0).size()) {
                content.remove(0);
                System.out.println("Deleted first row");
            } else {
                break;
            }
        }

        // add columns or create table if not existent
        try {
            System.out.println("Create table");
            this.db.createTable("pdb", content.get(0));
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        try {
            System.out.println("Add tags col");
            this.db.addTagsColumn("pdb");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        try {
            System.out.println("Add comments col");
            this.db.addCommentsColumn("pdb");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        try {
            System.out.println("Add cols");
            this.db.addColumns("pdb", content.get(0));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        LinkedList<String> header = content.get(0);
        content.remove(0);

        // insert content
        for (LinkedList<String> values:content) {
            try {
                System.out.println("Insert");
                this.db.insert("pdb", header, values);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return 0;
    }

}
