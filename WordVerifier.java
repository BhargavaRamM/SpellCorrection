import java.util.*;
import java.lang.*;
import java.io.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

public class WordVerifier {
	public static HashMap<String,String> dataHeaders;
	private ArrayList<String> wrongArrayList, totalWordList;
	private HashSet<String> wrongWordsHashSet;
	private HashSet<String> firstCharacterSet;
	public Hashtable<String,Hashtable<String,Float>> oneCharacterSet,twoCharacterSet,threeCharacterSet;
	Levenshtein levenshtein;
	private long start;
	private long end;
	private long singleCharExecTime, doubleCharExecTime,threeCharExecTime;
	private String timeInString;
	private String replacableWord;
	private Hashtable<String,Float> normalLevWeights, normalTermFreqs;
	ArrayList<List<String>> sheet1 = new ArrayList<List<String>>();
	LinkedHashMap<String,List<List<String>>> xlsdata = new LinkedHashMap<String,List<List<String>>>();
	public ArrayList<List<String>> load(ArrayList<String> wrongWordList, ArrayList<String> totalWordList) {
		wrongArrayList = wrongWordList;
		this.totalWordList = totalWordList;
		ArrayList<List<String>> sheet1Rows = new ArrayList<List<String>>();
		wrongWordsHashSet = new HashSet<String>();
		firstCharacterSet = new HashSet<String>();
		oneCharacterSet = new Hashtable<String,Hashtable<String,Float>>();
		twoCharacterSet = new Hashtable<String,Hashtable<String,Float>>();
		threeCharacterSet = new Hashtable<String,Hashtable<String,Float>>();
		levenshtein = new Levenshtein();
		normalLevWeights = new Hashtable<String,Float>();
		normalTermFreqs = new Hashtable<String,Float>();
		
		if(wrongArrayList != null && wrongArrayList.size() > 0) {
			for(int i = 0; i < wrongArrayList.size(); i++) {
				wrongWordsHashSet.add(wrongArrayList.get(i));
			}
		}
		
		xlsdata.put("sheet1",sheet1);
		if(wrongWordsHashSet != null && wrongWordsHashSet.size() > 0) {
			getWeigthsandTime(1,false);
			getWeigthsandTime(1,true);
			getWeigthsandTime(2,false);
			getWeigthsandTime(2,true);
			getWeigthsandTime(3,false);
			getWeigthsandTime(3,true);
		}
		return sheet1Rows;
		
	}
	
	private void getWeigthsandTime(int i, boolean withKeyboard) {
		Hashtable<String,Hashtable<String,Float>> hashTable = new Hashtable<String,Hashtable<String,Float>>();
		for(Iterator<String> iterator = wrongWordsHashSet.iterator();iterator.hasNext();) {
			String type = i + "character"+(withKeyboard ? "with Keyboard" : "Without Keyboard");
			ArrayList<List<String>> sheet1Rows = new ArrayList<List<String>>();
			List<String> datacols = new ArrayList<String>();
			String wrongWord = (String) iterator.next();
			firstCharacterSet = getCharacterSet(wrongWord,i,totalWordList);
			
			start = System.currentTimeMillis();
			normalLevWeights = levenshtein.getNormalizedLevWeights(oneCharacterSet,wrongWord,withKeyboard);
			end = System.currentTimeMillis();
			singleCharExecTime = singleCharExecTime + (end - start);
			
			start = System.currentTimeMillis();
			normalTermFreqs = levenshtein.getNormalizedTermFrequencies(firstCharacterSet,totalWordList);
			end = System.currentTimeMillis();
			doubleCharExecTime = doubleCharExecTime + (end - start);
			
			start = System.currentTimeMillis();
			Hashtable<String,Float> totalWeights = levenshtein.getTotalWeights(normalTermFreqs,normalLevWeights);
			hashTable.put(wrongWord,totalWeights);
			replacableWord = getReplacableWord(wrongWord,hashTable);
			end = System.currentTimeMillis();
			threeCharExecTime = threeCharExecTime + (end - start);
			
			System.out.println(wrongWord+" : "+replacableWord);
			datacols.add(type);
			datacols.add(" "+wrongWord);
			datacols.add(" "+replacableWord);
			sheet1Rows.add(datacols);
			sheet1.addAll(sheet1Rows);
			
			try {
				WordVerifier.createXLS(xlsdata,"output.xls");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try{
			File xlsFile = new File("output.xls");
			FileInputStream file1 = new FileInputStream(xlsFile);
			POIFSFileSystem filesFileSystem = new POIFSFileSystem(file1);
			HSSFWorkbook wb = new HSSFWorkbook(filesFileSystem);
			HSSFSheet sheet = wb.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.rowIterator();
			
			HSSFCellStyle myStyle = wb.createCellStyle();
			myStyle.setFillPatterns(HSSFCellStyle.FINE_DOTS);
			myStyle.setFillForegroundColor(new HSSFColor.YELLOW().getIndex());
			myStyle.setFillBackgroundColor(new HSSFColor.YELLOW().getIndex());
			
			int propCount = 0;
			int count = 0;
			while(rowIterator.hasNext()) {
				HSSFRow hssfrow = (HSSFRow) rowIterator.next();
				int rowNumber = hssfrow.getRowNumber();
				count = rowNumber;
			}
			
			timeInString = String.format("Execution Time: %d min %d sec", 
											TimeUnit.MILLISECONDS.toMinutes(singleCharExecTime),
											TimeUnit.MILLISECONDS.toSeconds(singleCharExecTime) -
											TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(singleCharExecTime)));
											
			System.out.println("Normalized Levenshtein Weight is: "+timeInString);
			count = count+2;
			HSSFRow row1 = sheet.createRow(count);
			HSSFCell cell1 = row1.createCell(0);
			cell1.setCellValue("Normalized Weight: "+timeInString);
			cell1.setCellStyle(myStyle);
			
			timeInString = String.format("Execution Time : %d min %d Sec ",
											TimeUnit.MILLISECONDS.toMinutes(doubleCharExecTime),
											TimeUnit.MILLISECONDS.toSeconds(doubleCharExecTime)-
											TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(doubleCharExecTime)));
			
			System.out.println("Normalized Term Frequency time : "+timeInString);
			count = count+1;
			HSSFRow row2 = sheet.createRow(count);
			HSSFCell cell2 = row2.createCell(0);
			cell2.setCellValue("Normalized Term Frequency is: "+timeInString);
			cell2.setCellStyle(myStyle);
			
			timeInString = String.format("Execution Time: %d min %d sec",
											TimeUnit.MILLISECONDS.toMinutes(threeCharExecTime),
											TimeUnit.MILLISECONDS.toSeconds(threeCharExecTime)-
											TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(doubleCharExecTime)));
			System.out.println("Total Weight time : "+timeInString);
			count = count+1;
			HSSFRow row3 = sheet.createRow(count);
			HSSFCell cell3 = row3.createCell(0);
			cell3.setCellValue("Total weights time: "+timeInString);
			cell3.setCellStyle(myStyle);
			
			file1.close();
			FileOutputStream out = new FileOutputStream(xlsFile);
			wb.write(out);
			out.close();
			
		}
		catch(IOException ie) {
			ie.printStackTrace();
		}
	}
	
	private String getReplacableWord(wrongWord,Hashtable<String,Hashtable<String,Float>> hashtable) {
		String word = "";
		Hashtable<String,Float> innerHashtable = hashtable.get(wrongWord);
		word = getMaximumTotalWeightWord(innerHashtable);
		return word;   // returns the replacableWord which has maximum weight.
	}
	
	public String getMaximumTotalWeightWord(Hashtable<String,Float> hashtable) {
		Float maxFloat = 0.0f;
		String maxString = "";
		Set<String> strings = hashtable.keySet();
		ArrayList<String> arrayList = new ArrayList<String>(strings);
		for(int i = 0; i < arrayList.size(); i++) {
			if(i == 0) {
				maxFloat = hashtable.get(arrayList.get(i));
				maxString = arrayList.get(i);
			}
			if(hashtable.get(arrayList.get(i)) > maxFloat) {
				maxFloat = hashtable.get(arrayList.get(i));
				maxString = arrayList.get(i);
			}
		}
		return maxString;				//returns a word or string which has maximum weight from total word list.
	}
	
	private HashSet<String> getCharacterSet(String wrongWord,int length, ArrayList<String> totalWordList) {
		HashSet<String> lengthCharSet = new HashSet<String>();
		for(Iterator<String> iterator2 = totalWordList.iterator(); iterator2.hasNext();) {
			String dicWord = (String) iterator2.next();
			if(wrongWord.length() > length){
				if(dicWord.startWith(wrongWord.substring(0,length))){
					lengthCharSet.add(dicWord);
				}
			}
		}
		return lengthCharSet;  //returns a set of words from dictionary.
	}
	
	public static void createXLS(LinkedHashMap<String,List<List<String>>> xlsData, String filePath)throws Exception {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFCellStyle my_style = new HSSFCellStyle();
		
		HSSFCellStyle my_style = wb.createCellStyle();
		my_style.setFillPatterns(HSSFCellStyle.FINE_DOTS);
		my_style.setFillForegroundColor(new HSSFColor.YELLOW().getIndex());
		my_style.setFillBackgroundColor(new HSSFColor.YELLOW().getIndex());
		
		for(Entry<String,List<List<String>>> sheet : xlsData.entrySet()) {
			HSSFSheet xlsSheet = workbook.createSheet(sheet.getKey());
			if(sheet.getValue() == null || sheet.getValue().size() == 0) {
				continue;
			} else {
				int rowNum = 0;
				int colNum = 0;
				
				for(List<String> rows : sheet.getValues()) {
					if(rowNum == 0) {
						HSSFRow row = xlsSheet.createRow(rowNum++);
						List<String> headCols = new ArrayList<String>();
						headCols.add("Type");
						headCols.add("Wrong Word");
						headCols.add("Replaceable Word");
						
						for(String colData : headCols) {
							HSSFCell cell = row.createCell(colNum++);
							cell.setCellValue(new HSSFRichTextString(colData));
							cell.setCellStyle(my_style);
						}						
					} else {
						colNum = 0;
						HSSFRow row = xlsSheet.createRow(rowNum++);
						if(rows == null || rows.size() == 0) {
							continue;
						}
						for(String colData : rows) {
							HSSFCell cell = row.createCell(colNum++);
							cell.setCellValue(new HSSFRichTextString(colData));
						}
					}
				}
			}
		}
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath);
			workbook.write(fos);
		} finally {
			if(fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch(IOException ioe) {
					ioe.printStackTrace();
				}
				
			}
		}
	}
	
}