package com.merit.dashboard.model;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;

/**
 *
 * @author satya
 */
public class GroupRoleXlsParser {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List getGroupRoleAssoicateMap(String fileName) {

		List headerCellDataList = new ArrayList();
		int headerCellDataLength = 0;
		List cellDataList = new ArrayList();
                BigDecimal bd;//modified
		try {
			FileInputStream fileInputStream = new FileInputStream(fileName);

			POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);

			HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
			HSSFSheet hssfSheet = workBook.getSheetAt(0);

			Iterator headerRowIterator = hssfSheet.getRow(0).cellIterator();
			// headerRowIterator.
                        int totalRow;//
                        Iterator rowIterator;//
                        List cellTempList;//
                        HSSFCell hssfCell;//
			while (headerRowIterator.hasNext()) {
				hssfCell = (HSSFCell) headerRowIterator.next();
				headerCellDataList.add(hssfCell.getStringCellValue().toString());
			}
			headerCellDataLength = headerCellDataList.size();
			cellDataList.add(headerCellDataList);

			totalRow = 0;
			rowIterator = hssfSheet.rowIterator();

			while (rowIterator.hasNext()) {
				if (totalRow != 0) {
					HSSFRow hssfRow = (HSSFRow) rowIterator.next();
					cellTempList = new ArrayList();
					for (int i = 0; i < headerCellDataLength + 1; i++) {
						hssfCell = hssfRow.getCell(i,HSSFRow.CREATE_NULL_AS_BLANK);
						if (hssfCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                                    bd = new BigDecimal(hssfCell.getNumericCellValue());//modified
							cellTempList.add(bd);//modified
						} else
							cellTempList.add(hssfCell.getStringCellValue().toString());
					}
					cellDataList.add(cellTempList);
				} else {
					rowIterator.next();
				}
				totalRow++;
                                bd = null;
			}
                        headerCellDataList = null;//modified
			System.out.println(cellDataList);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
                    headerCellDataList = null;//$
		}
		return cellDataList;
	}

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		GroupRoleXlsParser parser = new GroupRoleXlsParser();
		parser.getGroupRoleAssoicateMap("/home/jaykumar/Desktop/Availability_TestCase.xls");
	}

}