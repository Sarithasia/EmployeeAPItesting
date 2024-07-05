
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
    private  ThreadLocal<FileInputStream> threadLocalFis = new ThreadLocal<>();
    private  ThreadLocal<XSSFWorkbook> threadLocalWorkbook = new ThreadLocal<>();
    private  ThreadLocal<XSSFSheet> threadLocalSheet = new ThreadLocal<>();
    private  ThreadLocal<XSSFRow> threadLocalRow = new ThreadLocal<>();
    private  ThreadLocal<XSSFCell> threadLocalCell = new ThreadLocal<>();

    public ExcelReader(String path) {
        this.path = path;
        threadLocalFis.set(null); // Initializing thread local variables
        threadLocalWorkbook.set(null);
        threadLocalSheet.set(null);
        threadLocalRow.set(null);
        threadLocalCell.set(null);
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

        threadLocalSheet.set(getWorkbook().getSheetAt(index));
        threadLocalRow.set(threadLocalSheet.get().getRow(0));

        for (int i = 0; i < threadLocalRow.get().getLastCellNum(); i++) {
            if (threadLocalRow.get().getCell(i).getStringCellValue().trim().equals(colName.trim()))
                colNum = i;
        }
        if (colNum == -1)
            return "";

        threadLocalRow.set(threadLocalSheet.get().getRow(rowNum - 1));
        if (threadLocalRow.get() == null)
            return "";

        threadLocalCell.set(threadLocalRow.get().getCell(colNum));
        if (threadLocalCell.get() == null)
            return "";

        if (threadLocalCell.get().getCellType() == CellType.STRING)
            return threadLocalCell.get().getStringCellValue();
        else if (threadLocalCell.get().getCellType() == CellType.NUMERIC || threadLocalCell.get().getCellType() == CellType.FORMULA)
            return String.valueOf(threadLocalCell.get().getNumericCellValue());
        else if (threadLocalCell.get().getCellType() == CellType.BLANK)
            return "";
        else
            return String.valueOf(threadLocalCell.get().getBooleanCellValue());
    }

    public String getCellData(String sheetName, int colNum, int rowNum) throws IOException {
        int index = getWorkbook().getSheetIndex(sheetName);
        if (index == -1)
            return "";

        threadLocalSheet.set(getWorkbook().getSheetAt(index));
        threadLocalRow.set(threadLocalSheet.get().getRow(rowNum - 1));
        if (threadLocalRow.get() == null)
            return "";

        threadLocalCell.set(threadLocalRow.get().getCell(colNum));
        if (threadLocalCell.get() == null)
            return "";

        if (threadLocalCell.get().getCellType() == CellType.STRING)
            return threadLocalCell.get().getStringCellValue();
        else if (threadLocalCell.get().getCellType() == CellType.NUMERIC || threadLocalCell.get().getCellType() == CellType.FORMULA)
            return String.valueOf(threadLocalCell.get().getNumericCellValue());
        else if (threadLocalCell.get().getCellType() == CellType.BLANK)
            return "";
        else
            return String.valueOf(threadLocalCell.get().getBooleanCellValue());
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

            threadLocalSheet.set(getWorkbook().getSheetAt(index));
            threadLocalRow.set(threadLocalSheet.get().getRow(0));

            for (int i = 0; i < threadLocalRow.get().getLastCellNum(); i++) {
                if (threadLocalRow.get().getCell(i).getStringCellValue().trim().equals(colName))
                    colNum = i;
            }
            if (colNum == -1)
                return false;

            threadLocalSheet.get().autoSizeColumn(colNum);
            threadLocalRow.set(threadLocalSheet.get().getRow(rowNum - 1));
            if (threadLocalRow.get() == null)
                threadLocalRow.set(threadLocalSheet.get().createRow(rowNum - 1));

            threadLocalCell.set(threadLocalRow.get().getCell(colNum));
            if (threadLocalCell.get() == null)
                threadLocalCell.set(threadLocalRow.get().createCell(colNum));

            threadLocalCell.get().setCellValue(data);

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

            threadLocalSheet.set(getWorkbook().getSheetAt(index));
            threadLocalRow.set(threadLocalSheet.get().getRow(0));

            for (int i = 0; i < threadLocalRow.get().getLastCellNum(); i++) {
                if (threadLocalRow.get().getCell(i).getStringCellValue().trim().equalsIgnoreCase(colName))
                    colNum = i;
            }

            if (colNum == -1)
                return false;

            threadLocalSheet.get().autoSizeColumn(colNum);
            threadLocalRow.set(threadLocalSheet.get().getRow(rowNum - 1));
            if (threadLocalRow.get() == null)
                threadLocalRow.set(threadLocalSheet.get().createRow(rowNum - 1));

            threadLocalCell.set(threadLocalRow.get().getCell(colNum));
            if (threadLocalCell.get() == null)
                threadLocalCell.set(threadLocalRow.get().createCell(colNum));

            threadLocalCell.get().setCellValue(data);
            XSSFCreationHelper createHelper = threadLocalWorkbook.get().getCreationHelper();

            // cell style for hyperlinks

            CellStyle hlink_style = threadLocalWorkbook.get().createCellStyle();
            XSSFFont hlink_font = threadLocalWorkbook.get().createFont();
            hlink_font.setUnderline(XSSFFont.U_SINGLE);
            hlink_font.setColor(IndexedColors.BLUE.getIndex());
            hlink_style.setFont(hlink_font);

           // XSSFHyperlink link = createHelper.createHyperlink(XSSFHyperlink.LINK_FILE);
            XSSFHyperlink link = createHelper.createHyperlink(HyperlinkType.FILE);

            link.setAddress(url);
            threadLocalCell.get().setHyperlink(link);
            threadLocalCell.get().setCellStyle(hlink_style);
            
            

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
    }}
   