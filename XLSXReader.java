import java.io.*;
import java.util.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class XLSXReader {
    public static void main(String []args) {
	   WordVerifier verifier = new WordVerifier();  
       XLSXReader xlsxReader = new XLSXReader();	
	   xlsxReader.readExcel(verifier);
	}
	
	private ArrayList<String> wrongWordList, correctWordList;
	private ArrayList<String> totalWordList;
	
	public void readExcel(WordVerifier verifier) {
		wrongWordList = new ArrayList<String>();
		correctWordList = new ArrayList<String>();
		totalWordList = new ArrayList<String>();
		
		try {
			File excel = new File("Input 1.xlsx");
			FileInputStream fileout = new FileInputStream(excel.getAbsolutePath());
			XSSFWorkbook wb = new XSSFWorkBook(fileout);
			XSSFSheet sheet = new wb.getSheetAt(0);
			
			Iterator<Row> iterator = sheet.iterator();
			while(iterator.hasNext()) {
				Row row = iterator.next();
				Cell cell_1 = row.getCell(1);      // Rows and cells should be understood more clearly. what difference does it make if 0 or 1?
				switch(cell_1.getCellType()) {
					case Cell.CELL_TYPE_STRING:
						getTotalWords(cell_1.getStringCellValue());
						break;
					case Cell.CELL_TYPE_NUMERIC:
						break;
					Case Cell.CELL_TYPE_BOOLEAN:
						break;
					default:
				}
			}
			
			File wrongExcel = new File("WRONG WORD LIST.xlsx");
			FileInputStream wrongExcelFileOut = new FileInputStream(wrongExcel.getAbsolutePath());
			XSSFWorkbook wrongExcelwb = new XSSFWorkbook(wrongExcelFileOut);
			XSSFSheet wrongExcelsheet = new wrongExcelwb.getSheetAt(1);

				Iterator<Row> wrongExceliterator = wrongExcelsheet.iterator();
				while(wrongExceliterator.hasNext()) {
					Row row = wrongExceliterator.next();
					Cell cell_0 = row.getCell(0);
					if(cell_0 != null) {
						wrongWordList.add(cell_0.getStringCellValue());   //what is getStringCellValue?? 
					}
				}
				
				correctWordList = getCorrectWordList();
				verifier.load(wrongWordList,correctWordList);
				
				wrongExcelwb.close();
				wrongExcelFileOut.close();
				wb.close();
				fileout.close();
		} catch (FileNotFoundException f) {
			f.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		private void getTotalWords(String str) { 
			if(str != null) {
				str = str.replaceAll("[^A-Za-z'] "," ")
				String words = str.split("\\s");   // what is this split??
				if(words != null && words.length > 0) {
					for (int i = 0; i < words.length; i++) {
						String word = words[i].replaceAll("[^A-Za-z' ]"," "); //replace special characters by space
						totalWordList.add(words[j]); //Add words to the totalWordList to get term frequency.
					}
				}
			}
		}
		
		/* If any word in totalWordList does not match with a word in wrongWordList, then the word is a correct word. Hence we remove it from totalWordList */
		private ArrayList<String> getCorrectWordList() {
			if(wrongWordList.size() > 0 && totalWordList.size() > 0) {
				ArrayList<String> words = new ArrayList<String>(totalWordList);
				for(int i = 0; i < totalWordList.size(); i++) {
					for(int j = 0; j < wrongWordList.size(); j++) {
						if(totalWordList.get(i).equalsIgnoreCase(wrongWordList.get(j))) 
							words.remove(totalWordList.get(i))
					}
				}
				return words;
			} else {
				return null;
			}
			
		}
			
		}
	}
}