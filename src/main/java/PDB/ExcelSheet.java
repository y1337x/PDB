package PDB;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

public class ExcelSheet {

    final private String FILE_NAME;
    private Workbook workbook;
    private Sheet sheet;
    private boolean modern;

    /**
     * Loads an Excel sheet defined by the path + file name into the locale variable workbook, for example C:/tab.xls
     * @param filename
     * @throws IOException
     */
    public ExcelSheet(String filename) throws IOException {
        this.FILE_NAME = filename;

        // detect file extension (.xls or .xlsx)
        String fileType = FilenameUtils.getExtension(this.FILE_NAME);

        // use a file stream to read the byte code from file
        FileInputStream inputStream = new FileInputStream(new File(this.FILE_NAME));

        // .xls and .xlsx needing different classes (drivers)
        if (fileType.equals("xls")) {
            this.workbook = new HSSFWorkbook(inputStream);
            this.modern = false;
        } else {
            this.workbook = new XSSFWorkbook(inputStream);
            this.modern = true;
        }

        // opens the first sheet of an Excel file
        this.sheet = this.workbook.getSheetAt(0);

        // close the file stream to release the file
        inputStream.close();
    }

    /**
     * Creates an empty .xls Excel file with name PDB.xls
     */
    public ExcelSheet() {
        this.FILE_NAME = "./PDB.xls";
        this.workbook = new HSSFWorkbook();
        this.sheet = this.workbook.createSheet();
        // creates an empty sheet with name PDB in the new Excel file
        this.workbook.setSheetName(0, "PDB");
    }

    /**
     * Creates an empty Excel file with name PDB.xls or PDB.xlsx. If the parameter is true, it will be a modern .xlsx sheet.
     * If the param is false, it will be an old .xls sheet.
     * @param xlsx
     */
    public ExcelSheet(boolean xlsx) {
        // if xlsx is false, create an empty sheet in old Excel format .xls
        if(!xlsx) {
            this.FILE_NAME = "./PDB.xls";
            this.workbook = new HSSFWorkbook();
            this.sheet = this.workbook.createSheet();
            // creates an empty sheet with name PDB in the new Excel file
            this.workbook.setSheetName(0, "PDB");
        // if xlsx is true, create an empty sheet in new Excel format .xlsx
        } else {
            this.FILE_NAME = "./PDB.xlsx";
            this.workbook = new XSSFWorkbook();
            this.sheet = this.workbook.createSheet();
            // creates an empty sheet with name PDB in the new Excel file
            this.workbook.setSheetName(0, "PDB");
        }
    }

    /**
     * Saves an open Excel file to the file system.
     * CAUTION! This function overwrites existing files with the same file name. CAUTION!
     * Filename: PDB.xls or PDB.xlsx
     * Path: Execution path of .jar
     * @throws IOException
     */
    private void writeExcelSheetToFile() throws IOException {
        // opens a file output stream
        FileOutputStream outputStream = new FileOutputStream(this.FILE_NAME);
        // write file to file system
        this.workbook.write(outputStream);
        // release file/blocked resources
        outputStream.close();
    }

    /**
     * Prints an open Excel sheet cell by cell to the console
     * Unfortunately, images are not stored in the cells. So there is no data to be displayed here.
     */
    public void printSheetCellByCell() {
        Iterator<Row> rowIterator = this.sheet.iterator();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.iterator();

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                String s = cell.getStringCellValue();
                try {
                    String href = cell.getHyperlink().getAddress();
                    System.out.println(s + ": " + href);
                } catch (Exception e) {
                    System.out.println(s);
                }
            }
            System.out.println("\n");
        }
    }

    public LinkedList<String> getTableHeaders() {
        LinkedList<String> headers = new LinkedList<String>();
        LinkedList<String> temp = new LinkedList<String>();

        Iterator<Row> rowIterator = this.sheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.iterator();

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                temp.add(cell.toString());
            }

            if (temp.size() > headers.size()) {
                headers = temp;
                temp = new LinkedList<String>();
            }
            else return headers;
        }

        return headers;
    }

    public LinkedList<LinkedList<String>> getContentLists() {
        LinkedList<LinkedList<String>> content = new LinkedList<>();
        LinkedList<String> rowList = new LinkedList<>();

        Iterator<Row> rowIterator = this.sheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.iterator();

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                String s = cell.getStringCellValue();
                try {
                    String href = cell.getHyperlink().getAddress();
                    s += ": " + href;
                } catch (Exception e) {

                }
                rowList.add(s);
            }
            content.add(rowList);
            rowList = new LinkedList<>();
        }

        return content;
    }

    public LinkedList<String> getRowStringList(String separator) {
        LinkedList<String> rowList = new LinkedList<String>();

        Iterator<Row> rowIterator = this.sheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.iterator();

            String rowString = "";
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                rowString = rowString + cell.toString() + separator;
            }
            rowList.add(rowString);
        }

        return rowList;
    }

    public void writeRowStringList(LinkedList<String> list, String separator) throws IOException {
        Row row = null;
        Cell cell = null;
        int rowNum;

        for (rowNum = 0; rowNum < list.size(); rowNum++) {
            // create empty row
            row = this.sheet.createRow(rowNum);

            // split row string into cell strings
            String[] cellStrings = list.get(rowNum).split("|");
            // for each row string part
            for (int i = 0; i < cellStrings.length; i++) {
                String s = cellStrings[i];
                if (!s.equals(separator)) {
                    // create a cell
                    cell = row.createCell(i);
                    // fill the cell with the row string part
                    cell.setCellValue(cellStrings[i]);
                }
            }
        }

        this.writeExcelSheetToFile();
    }

}