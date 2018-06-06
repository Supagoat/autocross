package q.autocross.engine;

import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class Data {

	public void read(InputStream in) throws Exception {
		Workbook workbook = WorkbookFactory.create(in);

		workbook.forEach(sheet -> {
			if (sheet.getSheetName().toLowerCase().contains("axware")
					|| sheet.getSheetName().toLowerCase().contains("eventexport")) {
				processSheet(sheet);
			}

		});

	}

	public void processSheet(Sheet sheet) {
			Iterator<Row> rowIterator = sheet.rowIterator();
		DataFormatter dataFormatter = new DataFormatter();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();

			// Now let's iterate over the columns of the current row
			Iterator<Cell> cellIterator = row.cellIterator();

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				String cellValue = dataFormatter.formatCellValue(cell);
				System.out.print(cellValue + "\t");
			}
			System.out.println();
		}
	}
}