
package com.poppulo.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;



public class ExcelReader {

    private String path;
    private ThreadLocal<FileInputStream> threadLocalFis = ThreadLocal.withInitial(() -> null);
    private ThreadLocal<XSSFWorkbook> threadLocalWorkbook = ThreadLocal.withInitial(() -> null);
    private ThreadLocal<XSSFSheet> threadLocalSheet = ThreadLocal.withInitial(() -> null);
    private ThreadLocal<XSSFRow> threadLocalRow = ThreadLocal.withInitial(() -> null);
    private ThreadLocal<XSSFCell> threadLocalCell = ThreadLocal.withInitial(() -> null);

    public ExcelReader(String path) {
        this.path = path;
    }

    private void initializeWorkbook() throws IOException {
        threadLocalFis.set(new FileInputStream(path));
        threadLocalWorkbook.set(new XSSFWorkbook(threadLocalFis.get()));
    }

    private XSSFWorkbook getWorkbook() throws IOException {
        if (threadLocalWorkbook.get() == null) {
            initializeWorkbook();
        }
        return threadLocalWorkbook.get();
    }

    private XSSFSheet getSheet(String sheetName) throws IOException {
        if (threadLocalSheet.get() == null || !threadLocalSheet.get().getSheetName().equals(sheetName)) {
            threadLocalSheet.set(getWorkbook().getSheet(sheetName));
        }
        return threadLocalSheet.get();
    }

    public int getRowCount(String sheetName) throws IOException {
        int index = getWorkbook().getSheetIndex(sheetName);
        if (index == -1)
            return 0;
        else
            return getSheet(sheetName).getLastRowNum() + 1;
    }

    public String getCellData(String sheetName, String colName, int rowNum) throws IOException {
        int colNum = -1;
        int index = getWorkbook().getSheetIndex(sheetName);
        if (index == -1)
            return "";

        XSSFSheet sheet = getSheet(sheetName);
        XSSFRow row = sheet.getRow(0);

        for (int i = 0; i < row.getLastCellNum(); i++) {
            if (row.getCell(i).getStringCellValue().trim().equals(colName.trim()))
                colNum = i;
        }
        if (colNum == -1)
            return "";

        row = sheet.getRow(rowNum - 1);
        if (row == null)
            return "";

        XSSFCell cell = row.getCell(colNum);
        if (cell == null)
            return "";

        if (cell.getCellType() == CellType.STRING)
            return cell.getStringCellValue();
        else if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA)
            return String.valueOf(cell.getNumericCellValue());
        else if (cell.getCellType() == CellType.BLANK)
            return "";
        else
            return String.valueOf(cell.getBooleanCellValue());
    }

    public String getCellData(String sheetName, int colNum, int rowNum) throws IOException {
        int index = getWorkbook().getSheetIndex(sheetName);
        if (index == -1)
            return "";

        XSSFSheet sheet = getSheet(sheetName);
        XSSFRow row = sheet.getRow(rowNum - 1);
        if (row == null)
            return "";

        XSSFCell cell = row.getCell(colNum);
        if (cell == null)
            return "";

        if (cell.getCellType() == CellType.STRING)
            return cell.getStringCellValue();
        else if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA)
            return String.valueOf(cell.getNumericCellValue());
        else if (cell.getCellType() == CellType.BLANK)
            return "";
        else
            return String.valueOf(cell.getBooleanCellValue());
    }

    public boolean setCellData(String sheetName, String colName, int rowNum, String data) throws IOException {
        FileOutputStream fileOut = null;
        try {
            threadLocalFis.set(new FileInputStream(path));
            threadLocalWorkbook.set(new XSSFWorkbook(threadLocalFis.get()));

            int index = getWorkbook().getSheetIndex(sheetName);
            int colNum = -1;
            if (index == -1)
                return false;

            XSSFSheet sheet = getSheet(sheetName);
            XSSFRow row = sheet.getRow(0);

            for (int i = 0; i < row.getLastCellNum(); i++) {
                if (row.getCell(i).getStringCellValue().trim().equals(colName))
                    colNum = i;
            }
            if (colNum == -1)
                return false;

            sheet.autoSizeColumn(colNum);
            row = sheet.getRow(rowNum - 1);
            if (row == null)
                row = sheet.createRow(rowNum - 1);

            XSSFCell cell = row.getCell(colNum);
            if (cell == null)
                cell = row.createCell(colNum);

            cell.setCellValue(data);

            fileOut = new FileOutputStream(path);
            getWorkbook().write(fileOut);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fileOut != null) {
                fileOut.close();
            }
        }
        return true;
    }

    public boolean setCellData(String sheetName, String colName, int rowNum, String data, String url) throws IOException {
        FileOutputStream fileOut = null;
        try {
            threadLocalFis.set(new FileInputStream(path));
            threadLocalWorkbook.set(new XSSFWorkbook(threadLocalFis.get()));

            int index = getWorkbook().getSheetIndex(sheetName);
            int colNum = -1;
            if (index == -1)
                return false;

            XSSFSheet sheet = getSheet(sheetName);
            XSSFRow row = sheet.getRow(0);

            for (int i = 0; i < row.getLastCellNum(); i++) {
                if (row.getCell(i).getStringCellValue().trim().equalsIgnoreCase(colName))
                    colNum = i;
            }

            if (colNum == -1)
                return false;

            sheet.autoSizeColumn(colNum);
            row = sheet.getRow(rowNum - 1);
            if (row == null)
                row = sheet.createRow(rowNum - 1);

            XSSFCell cell = row.getCell(colNum);
            if (cell == null)
                cell = row.createCell(colNum);

            cell.setCellValue(data);
            XSSFCreationHelper createHelper = threadLocalWorkbook.get().getCreationHelper();

            CellStyle hlink_style = threadLocalWorkbook.get().createCellStyle();
            XSSFFont hlink_font = threadLocalWorkbook.get().createFont();
            hlink_font.setUnderline(XSSFFont.U_SINGLE);
            hlink_font.setColor(IndexedColors.BLUE.getIndex());
            hlink_style.setFont(hlink_font);

            XSSFHyperlink link = createHelper.createHyperlink(HyperlinkType.FILE);
            link.setAddress(url);
            cell.setHyperlink(link);
            cell.setCellStyle(hlink_style);

            fileOut = new FileOutputStream(path);
            getWorkbook().write(fileOut);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fileOut != null) {
                fileOut.close();
            }
        }
        return true;
    }

    public boolean addSheet(String sheetname) throws IOException {
        FileOutputStream fileOut = null;
        try {
            threadLocalWorkbook.set(new XSSFWorkbook());
            threadLocalWorkbook.get().createSheet(sheetname);
            fileOut = new FileOutputStream(path);
            threadLocalWorkbook.get().write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fileOut != null) {
                fileOut.close();
            }
        }
        return true;
    }

    public boolean removeSheet(String sheetName) throws IOException {
        FileOutputStream fileOut = null;
        try {
            int index = getWorkbook().getSheetIndex(sheetName);
            if (index == -1)
                return false;

            threadLocalWorkbook.set(new XSSFWorkbook(threadLocalFis.get()));
            threadLocalWorkbook.get().removeSheetAt(index);
            fileOut = new FileOutputStream(path);
            threadLocalWorkbook.get().write(fileOut);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fileOut != null) {
                fileOut.close();
            }
        }
        return true;
    }
}
   