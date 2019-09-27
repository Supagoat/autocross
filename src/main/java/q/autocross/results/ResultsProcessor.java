package q.autocross.results;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
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
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ResultsProcessor {
	private Map<String, Double> paxes;
	Map<Run.Session, Integer> runCounts;
	HSSFColor headerColor;

	public ResultsProcessor() {
		runCounts = new HashMap<>();

	}

	public static void main(String[] args) throws Exception {
		CommandLine cmd = new DefaultParser().parse(new ResultsOptions(), args);
		ResultsProcessor processor = new ResultsProcessor();
		processor.getRunCounts().put(Run.Session.Morning, Integer.parseInt(cmd.getOptionValue("m")));
		processor.getRunCounts().put(Run.Session.Afternoon, Integer.parseInt(cmd.getOptionValue("a")));
		processor.readPaxes();
		processor.processResults(new File(cmd.getOptionValue("r")),
				processor.readTSV(new File(cmd.getOptionValue("p"))), cmd.getOptionValue("o"));
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

	public void processResults(File file, CSVParser regSheet, String outputDir) throws Exception {

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
						.setLastName(rawResult.get("Last Name").trim()).setNovice(matchingReg.get("Rookie"))
						.setCarModel(matchingReg.get("Car Model"));
				c.setMorningRuns(readRuns(c, Run.Session.Morning, rawResult));
				c.setAfternoonRuns(readRuns(c, Run.Session.Afternoon, rawResult));
				competitors.add(c);
			} else {
				System.out.println("No matching reg found in " + carClass + " for " + rawResult.get("First Name").trim()
						+ " " + rawResult.get("Last Name").trim());
			}

		}
		outputResults(carClasses, competitors, outputDir);
		for (String carClass : carClasses) {
			List<Competitor> classCompetitors = competitors.stream()
					.filter(c -> c.getBmwClass().contentEquals(carClass)).collect(Collectors.toList());
			Collections.sort(classCompetitors);
		}
	}

	private static final String[] PRE_RUN_COLS = new String[] { "CLASS", "PAX", "CAR", "PLACE", "FIRST", "LAST",
			"MODEL", "NOVICE" };
	private static final String RUN_COL = "RUN ";
	private static final String[] POST_RUN_COLS = new String[] { "AVG", "PAX Avg", "POINTS" };
	private static final String[] YTD_COLS = new String[] { "CAR", "CLASS", "FIRST", "LAST", "PAX", "PLACE", "NOVICE",
			"MODEL", "POINTS" };

	public void outputResults(Set<String> carClasses, List<Competitor> allCompetitors, String outputDir)
			throws Exception {
		XSSFWorkbook eventResultsWB = new XSSFWorkbook();
		XSSFWorkbook ytdResultsWB = new XSSFWorkbook();
		CellStyle cellcolorstyle = eventResultsWB.createCellStyle();
		byte[] headerRgb = new byte[] { (byte) 112, (byte) 134, (byte) 156 };
		XSSFCellStyle xssfHeaderStyle = null;
		if (cellcolorstyle instanceof XSSFCellStyle) {
			XSSFCellStyle xssfcellcolorstyle = (XSSFCellStyle) cellcolorstyle;
			xssfcellcolorstyle.setFillForegroundColor(new XSSFColor(headerRgb, null));
		} else if (cellcolorstyle instanceof HSSFCellStyle) {
			throw new IllegalStateException("How do we have an HSSF cell here?");
		}

		Sheet eventResultsSheet = eventResultsWB.createSheet("Event Results");
		Sheet ytdResultsSheet = ytdResultsWB.createSheet("YTD Results");
		int eventResultRowNum = 0;
		int ytdRowNum = 0;
		Row ytdRow = ytdResultsSheet.createRow(ytdRowNum++);
		outputHeaders(ytdRow, YTD_COLS, new String[] {}, false, xssfHeaderStyle);
		for (String carClass : carClasses) {

			Row eventResultRow = eventResultsSheet.createRow(eventResultRowNum++);

			outputHeaders(eventResultRow, PRE_RUN_COLS, POST_RUN_COLS, true, xssfHeaderStyle);
			
			List<Competitor> classCompetitors = allCompetitors.stream()
					.filter(c -> c.getBmwClass().contentEquals(carClass)).collect(Collectors.toList());
			Collections.sort(classCompetitors);
			int pos = 1;
			for (Competitor competitor : classCompetitors) {
				ytdRow = ytdResultsSheet.createRow(ytdRowNum++);
				eventResultRow = eventResultsSheet.createRow(eventResultRowNum++);
				int col = 0;

				Cell ytdResultCell = ytdRow.createCell(col);
				Cell eventResultCell = eventResultRow.createCell(col++);
				ytdResultCell.setCellValue(competitor.getNumber());
				eventResultCell.setCellValue(competitor.getBmwClass());

				ytdResultCell = ytdRow.createCell(col);
				eventResultCell = eventResultRow.createCell(col++);
				ytdResultCell.setCellValue(competitor.getBmwClass());
				eventResultCell.setCellValue(competitor.getPax());

				ytdResultCell = ytdRow.createCell(col);
				eventResultCell = eventResultRow.createCell(col++);
				ytdResultCell.setCellValue(competitor.getFirstName());
				eventResultCell.setCellValue(competitor.getNumber());

				ytdResultCell = ytdRow.createCell(col);
				eventResultCell = eventResultRow.createCell(col++);
				ytdResultCell.setCellValue(competitor.getLastName());
				eventResultCell.setCellValue(pos);

				ytdResultCell = ytdRow.createCell(col);
				eventResultCell = eventResultRow.createCell(col++);
				ytdResultCell.setCellValue(competitor.getPax());
				eventResultCell.setCellValue(competitor.getFirstName());

				ytdResultCell = ytdRow.createCell(col);
				eventResultCell = eventResultRow.createCell(col++);
				ytdResultCell.setCellValue(pos);
				eventResultCell.setCellValue(competitor.getLastName());

				ytdResultCell = ytdRow.createCell(col);
				eventResultCell = eventResultRow.createCell(col++);
				ytdResultCell.setCellValue(competitor.getNovice());
				eventResultCell.setCellValue(competitor.getCarModel());

				ytdResultCell = ytdRow.createCell(col);
				eventResultCell = eventResultRow.createCell(col++);
				ytdResultCell.setCellValue(""); // Model has a column but my example shows it always empty
				eventResultCell.setCellValue(competitor.getNovice());

				ytdResultCell = ytdRow.createCell(col);
				ytdResultCell.setCellValue(pos == 1 ? 0.5 : pos);

				col = setRunCells(competitor.getMorningRuns(), eventResultRow, col);
				col = setRunCells(competitor.getAfternoonRuns(), eventResultRow, col);

				eventResultCell = eventResultRow.createCell(col++);
				eventResultCell.setCellValue(competitor.getAvg(true));

				eventResultCell = eventResultRow.createCell(col++);
				eventResultCell.setCellValue(competitor.getAvg(false));

				eventResultCell = eventResultRow.createCell(col++);
				eventResultCell.setCellValue(pos == 1 ? 0.5 : pos);

				pos++;
			}
		}

		try (OutputStream fileOut = new FileOutputStream(outputDir + File.separator + "finalResults.xlsx")) {
			eventResultsWB.write(fileOut);
		}
		try (OutputStream fileOut = new FileOutputStream(outputDir + File.separator + "ytdInputResults.xlsx")) {
			ytdResultsWB.write(fileOut);
		}
		eventResultsWB.close();
		ytdResultsWB.close();
	}

	public int setRunCells(List<Run> runs, Row row, int col) {
		for (Run run : runs) {
			Cell cell = row.createCell(col++);
			if (run.getRawTime() < 0) {
				cell.setCellValue("-");
			} else {
				cell.setCellValue(
						run.isFinished() ? String.valueOf(Run.getTimeFormat().format(run.getConedRawTime())) : "dnf");
			}
		}
		return col;
	}

	public void outputHeaders(Row row, String[] preHeaders, String[] postHeaders, boolean runHeaders, XSSFCellStyle xssfHeaderStyle) {
		int colCountBefore = 0;
		int colCountAfter = preHeaders.length;
		for (int c = colCountBefore; c < colCountAfter; c++) {
			Cell cell = row.createCell(c);
			cell.setCellStyle(xssfHeaderStyle);
			cell.setCellValue(preHeaders[c - colCountBefore]);
		}

		if (runHeaders) {
			colCountBefore = colCountAfter;
			colCountAfter = colCountBefore + getRunCounts().get(Run.Session.Morning);
			for (int c = colCountBefore; c < colCountAfter; c++) {
				Cell cell = row.createCell(c);
				cell.setCellStyle(xssfHeaderStyle);
				cell.setCellValue("RUN " + (c - colCountBefore + 1));
			}

			colCountBefore = colCountAfter;
			colCountAfter = colCountBefore + getRunCounts().get(Run.Session.Afternoon);
			for (int c = colCountBefore; c < colCountAfter; c++) {
				Cell cell = row.createCell(c);
				cell.setCellStyle(xssfHeaderStyle);
				cell.setCellValue("RUN " + (c - colCountBefore + 1));
			}
		}
		colCountBefore = colCountAfter;
		colCountAfter = colCountBefore + postHeaders.length;
		for (int c = colCountBefore; c < colCountAfter; c++) {
			Cell cell = row.createCell(c);
			cell.setCellStyle(xssfHeaderStyle);
			cell.setCellValue(postHeaders[c - colCountBefore]);
		}
	}

	public List<Run> readRuns(Competitor c, Run.Session session, CSVRecord results) {
		List<Run> runs = new ArrayList<>();

		for (int i = 1; i < getRunCounts().get(session) + 1; i++) {
			String rawTime = results.get("Run " + i + session.sessionResultAppend()).trim();
			Double parsedTime = null;
			try {
				parsedTime = Double.parseDouble(rawTime);
			} catch (NumberFormatException e) {
				runs.add(Run.noRun());
			}
			if (parsedTime != null) {
				Run r = new Run().setRawTime(parsedTime).setSession(session);
				try {
					r.setPaxTime(c.getPax().isEmpty() ? r.getRawTime()
							: r.getRawTime() * paxes.get(c.getPax().toUpperCase()));
				} catch (NullPointerException e) {
					System.out.println("Failed to find pax on : " + c);
					throw e;
				}
				try {
					String penalties = results.get("Pen " + i + session.sessionResultAppend()).trim();
					if (penalties.length() > 0) {
						r.setPenalties(Integer.parseInt(penalties));
						r.setPaxPenalties(c.getPax().isEmpty() ? r.getPenalties()
								: r.getPenalties() * paxes.get(c.getPax().toUpperCase()));
					}
				} catch (NumberFormatException e) {
					r.setFinished(false);
				}
				runs.add(r);
			}
		}
		return runs;
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

	public Map<String, Double> getPaxes() {
		return paxes;
	}

	public ResultsProcessor setPaxes(Map<String, Double> paxes) {
		this.paxes = paxes;
		return this;
	}

	public Map<Run.Session, Integer> getRunCounts() {
		return runCounts;
	}

	public ResultsProcessor setRunCounts(Map<Run.Session, Integer> runCounts) {
		this.runCounts = runCounts;
		return this;
	}

	// Don't use this yet... Would if I needed to read excel files
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
