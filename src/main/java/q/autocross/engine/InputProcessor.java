package q.autocross.engine;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class InputProcessor {
	private static final String emailSalt = "$2a$08$rEGmSA1ejUIr6mxkspSNLe";
	
	public static String hashEmail(String email) {
		return BCrypt.hashpw(email, emailSalt);
	}
	
	public List<Person> read(InputStream in) throws Exception {
		Workbook workbook = WorkbookFactory.create(in);
		List<Person> people = new ArrayList<>();
		workbook.forEach(sheet -> {
			if (sheet.getSheetName().toLowerCase().contains("axware")
					|| sheet.getSheetName().toLowerCase().contains("eventexport")) {
				people.addAll(processSheet(sheet));
			}

		});
		return people;
	}

	public List<Person> processSheet(Sheet sheet) {
		Iterator<Row> rowIterator = sheet.rowIterator();
		Row row = rowIterator.next();
		Map<Integer,String> colMap = mapColumns(row.cellIterator());
		return buildPersonGrid(rowIterator, colMap);
	}
	
	public Map<Integer, String> mapColumns(Iterator<Cell> cellIterator) {
		DataFormatter dataFormatter = new DataFormatter();
		Map<Integer,String> map = new HashMap<>();
		int i=0;
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			String cellValue = dataFormatter.formatCellValue(cell);
			map.put(i, cellValue);
			i++;
		}
		return map;
	}

	public List<Person> buildPersonGrid(Iterator<Row> rowIterator, Map<Integer, String> colIndex) {
		List<Person> people =  new ArrayList<>();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			people.add(buildPerson(row, colIndex));
		}
		return people;
	}

	public Person buildPerson(Row row, Map<Integer, String> colIndex) {
		DataFormatter dataFormatter = new DataFormatter();
		Person person = new Person();
		person.setClubId(Session.Singleton.INSTANCE.get().getClubId());
		
		Iterator<Cell> cellIterator = row.cellIterator();
		Map<String, Function<String, Person>> setters = new HashMap<>();
		setters.put("First Name", person::setFirstName);
		setters.put("Last Name", person::setLastName);
		setters.put("Number", person::setPrimaryNumber);
		setters.put("Email #1", person::setUnhashedEmail);
		int cellNum = 0;
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			Optional<Function<String, Person>> setter = Optional.ofNullable(setters.get(colIndex.get(cellNum)));
			if(setter.isPresent()) {

				setter.get().apply(dataFormatter.formatCellValue(cell));
			}

			cellNum++;
		}
		return person;
	}
	

}