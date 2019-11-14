package PDB;

public class Main {

    static PDB pdb;

    // main function, program entry point
    public static void main(String[] args) {
        pdb = new PDB("C:\\Users\\y1337\\Desktop\\Caitlin\\pdb.db");

        //pdb.addEntriesFromExcel("C:\\Users\\y1337\\Desktop\\Caitlin\\alt.xlsx");
        //pdb.addEntriesFromExcel("C:\\Users\\y1337\\Desktop\\Caitlin\\neu.xlsx");
        pdb.addEntriesFromExcel("C:\\Users\\y1337\\Desktop\\Caitlin\\danyel.xlsx");

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                pdb.closeDBConnection();
            }
        }));

    }

    /*
    private static void testExcel() {
        try {
            //ExcelSheet sheet = new ExcelSheet("C:\\Users\\y1337\\Desktop\\Caitlin\\excel2019-11-11-10-17-49.xlsx");
            ExcelSheet sheet = new ExcelSheet("C:\\Users\\y1337\\Desktop\\Caitlin\\excel2019-danyel.xlsx");
            System.out.println(sheet.getTableHeaders());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testSQLite() {
        try {
            db = new SQLiteDB("C:\\Users\\y1337\\Desktop\\Caitlin\\pdb.db");
            //db.testCT();
            //db.testInsert();
            //db.testAddColumns();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    */
}
