package q.autocross.results;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ResultsProcessor {
	private Map<String, Double> paxes;

	public static void main(String[] args) throws Exception {
		CommandLine cmd = new DefaultParser().parse(new ResultsOptions(), args);
		ResultsProcessor processor = new ResultsProcessor();
		processor.readPaxes();
		processor.processResults(new File(cmd.getOptionValue("r")),
				processor.readTSV(new File(cmd.getOptionValue("p"))));
	}


	public Map<String, Integer> buildHeaderMap(Row row) {
		Map<String, Integer> header = new HashMap<String, Integer>();
		int i = 0;
		for (Cell cell : row) {
			header.put(cell.getStringCellValue(), i);
			i++;
		}
		return header;
	}

	public Pair<HSSFSheet, Map<String, Integer>> readExcelFile(File file) throws Exception {
		POIFSFileSystem fs = new POIFSFileSystem(file);
		HSSFWorkbook wb = new HSSFWorkbook(fs.getRoot(), true);
		HSSFSheet sheet = wb.getSheetAt(0);
		return Pair.of(sheet, buildHeaderMap(sheet.getRow(0)));
	}


	public CSVParser readTSV(File file) {
		try {
			CSVFormat format = CSVFormat.newFormat('\t').withQuote('"').withFirstRecordAsHeader();
			return CSVParser.parse(file, Charset.defaultCharset(), format);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void processResults(File file, CSVParser regSheet) throws Exception {

		List<CSVRecord> rawResults = readTSV(file).getRecords();
		List<CSVRecord> regs = regSheet.getRecords();
		List<Competitor> competitors = new ArrayList<>();
		Set<String> carClasses = new TreeSet<>();
		for (CSVRecord rawResult : rawResults) {
			String carClass = rawResult.get("Class").trim().toUpperCase();
			String carNumber = rawResult.get("Number").trim();
			carClasses.add(carClass);
			CSVRecord matchingReg = getRow(regs, carNumber, carClass);
			if (matchingReg != null) {
				Competitor c = new Competitor().setBmwClass(carClass).setNumber(carNumber)
						.setPax(matchingReg.get("PAX")).setFirstName(rawResult.get("First Name").trim())
						.setLastName(rawResult.get("Last Name").trim());
				c.setRuns(readRuns(c, rawResult));

				competitors.add(c);
			} else {
				System.out
						.println("NO " + rawResult.get("First Name").trim() + " " + rawResult.get("Last Name").trim());
			}

		}

		for (String carClass : carClasses) {
			List<Competitor> classCompetitors = competitors.stream()
					.filter(c -> c.getBmwClass().contentEquals(carClass)).collect(Collectors.toList());
			Collections.sort(classCompetitors);
			for(int i=0;i<classCompetitors.size();i++) {
				System.out.println(i+" "+classCompetitors.get(i));
			}
		}

		// Output:
		// best morning run # and best afternoon run # , average (post-pax for non-bmws)
		// Place
		// Points: .5, 2,3,4,5,6,7,8,9, etc
		// FTD y/n ... Maybe ?
		// Novice status
	}

	public List<Run> readRuns(Competitor c, CSVRecord results) {
		List<Run> runs = new ArrayList<>();

		for (Run.Session session : Run.Session.values()) {

			int numRuns = countRuns(results, session);
			for (int i = 1; i < numRuns + 1; i++) {
				String rawTime = results.get("Run " + i + session.sessionResultAppend()).trim();
				Double parsedTime = null;
				try {
					parsedTime = Double.parseDouble(rawTime);
				} catch (NumberFormatException e) {
					runs.add(Run.noRun());
				}
				if (parsedTime != null) {
					Run r = new Run().setRawTime(parsedTime).setSession(session);
					r.setPaxTime(c.getPax().isEmpty() ? r.getRawTime()
							: r.getRawTime() * paxes.get(c.getPax().toUpperCase()));
					try {
						String penalties = results.get("Pen " + i + session.sessionResultAppend()).trim();
						if (penalties.length() > 0) {
							r.setPenalties(Integer.parseInt(penalties));
						}
					} catch (NumberFormatException e) {
						r.setFinished(false);
					}
					runs.add(r);
				}
			}
		}
		return runs;
	}

	public int countRuns(CSVRecord results, Run.Session session) {
		int runsThisSession = 1;
		while (true) {
			try {
				results.get("Run " + (runsThisSession + 1) + session.sessionResultAppend());
			} catch (IllegalArgumentException e) {
				return runsThisSession;
			}
			runsThisSession++;
		}
	}

	public Row getRow(Pair<HSSFSheet, Map<String, Integer>> sheet, String carNum, String carClass) {
		for (Row row : sheet.getLeft()) {
			if (hasCell(row, sheet.getRight(), "Class", carClass) && hasCell(row, sheet.getRight(), "Number", carNum)) {
				return row;
			}
		}
		return null;
	}

	public CSVRecord getRow(List<CSVRecord> records, String carNum, String carClass) {
		try {
			for (CSVRecord record : records) {
				if (hasCell(record, "Class", carClass) && hasCell(record, "Number", carNum)) {
					return record;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;

	}

	public String getCell(Row row, Map<String, Integer> headerMap, String key) {
		return row.getCell(headerMap.get(key)).getStringCellValue();
	}

	public boolean hasCell(Row row, Map<String, Integer> headerMap, String key, String target) {
		return target.contentEquals(getCell(row, headerMap, key));
	}

	public boolean hasCell(CSVRecord row, String key, String target) {
		return target.contentEquals(row.get(key));
	}

	public void readPaxes() throws Exception {
		paxes = new HashMap<>();
		CSVParser parser = readTSV(new File("conf" + File.separator + "pax.txt"));
		for (CSVRecord record : parser.getRecords()) {
			paxes.put(record.get("Class").trim().toUpperCase(), Double.parseDouble(record.get("PAX").trim()));
		}
	}

	
// Don't use this yet...  Would if I needed to read excel files
	public void read(File in) throws Exception {
		// Workbook workbook = WorkbookFactory.create(in);\
		POIFSFileSystem fs = new POIFSFileSystem(in);
		HSSFWorkbook wb = new HSSFWorkbook(fs.getRoot(), true);
		HSSFSheet sheet = wb.getSheetAt(0);
		for (Row row : sheet) {
			for (Cell cell : row) {
				// Do something here
			}
		}
	}
}
