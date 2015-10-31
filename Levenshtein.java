import java.util.*;
import java.lang.*;
public class Levenshtein  {
	public static float levWeight;
	private static float defaultFreq = 0.00000000001f;
	public static int KeyBoardLayoutDistance = 0;
	
	//oneCharacterset is the set of all words in dictionary with edit distance of one for a wrong word. 
	//We iterate through all such set of words and create a table with levenshtein distance for each of such word and we return the table;
	//Hashset is used for maintaining such set of words.
	// We use Normalization to normalize the weights of distances.
	
	public Hashtable<String,Float> getTotalWeights(Hashtable<String,Float> normalTermFrequencyTable, Hashtable<String,Float> normalLevWeightsTable) {
		Hashtable<String,Float> totalWeightsTable = new Hashtable<String,Float>();
		for (Iterator<String> iterator = normalTermFrequencyTable.keySet().iterator(); iterator.hasNext();) {
			String string = iterator.next();
			if(string != null) {
				if(normalTermFrequencyTable.get(string) != null && normalLevWeightsTable.get(string) != null) {
					float totalWeight = normalTermFrequencyTable.get(string)/normalLevWeightsTable.get(string);
					if(!Float.isInfinite(totalWeight) && !Float.isNaN(totalWeight) {
						totalWeightsTable.put(string,totalWeight);
					}
				}
			}
		}
		return totalWeightsTable;	
	}
	
	public Hashtable<String,Float> getTermFrequencies(Hashset<String> oneCharacterset, ArrayList<String> dataArray) {
		Hashtable<String, Float> termFrequencyTable = new Hashtable<String, Float>();
		for (Iterator<String> iterator = oneCharacterset.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			float conunt = 0;
			for (int i = 0; i < dataArray.size(); i++) {
				String misSpeltWord = dataArray.get(i);
				if(string.equalsIgnoreCase(misSpeltWord)){
					count = count + 1;
				} /* else {
					count = defaultFreq;
				}*/
			}
			if(count > 0) {
				termFrequencyTable.put(string, (float) count);
			} else {
				termFrequencyTable.put(string,defaultFreq);
			}
		}
		return termFrequencyTable;
	}
	
	public Hashtable<String,Float> getNormalizedTermFrequencies(Hashset<String> oneCharacterset, ArrayList<String> dataArray) {
		Normaization normalization = new Normaization();
		HashTable<String,Float> normalTermFrequencyTable = new Hashtable<String,Float>();
		Hashtable<String,Float> termFrequencyTable = getTermFrequencies(oneCharacterset,dataArray);
		for(Iterator<String> iterator = termFrequencyTable.keySet();iterator.hasNext();) {
			String string = iterator.next();
			float normalTermFrequency = normalization.normalize(termFrequencyTable.get(string), termFrequencyTable);
			normalTermFrequencyTable.put(string,normalTermFrequency);
		}
		return normalTermFrequencyTable;
	}
	
	public Hashtable<String,Float> getNormalizedLevenshteinWeights(Hashset<String> oneCharacterset, String wrongWord, boolean isKeyboard) {
		Normaization normalization = new Normaization();
		Hashtable<String,Float> normalLevWeightsTable = new Hashtable<String,Float>();
		Hashtable<String,Float> levWeightsTable = getLevenshteinWeights(oneCharacterset,wrongWord,isKeyboard);
		for(Iterator<String> iterator = levWeightsTable.keySet(); iterator.hasNext();) {
			String string = iterator.next();
			float normalFreq = normalization.normalize(levWeightsTable.get(string),levWeightsTable);
			normalLevWeightsTable.put(string,normalFreq);
		}
		return normalLevWeightsTable;
	}
	
	public Hashtable<String,Float> getLevenshteinWeights(Hashset<String> oneCharacterset, String wrongWord, boolean isKeyboard) {
		ExecutorService executor = Executor.newFixedThreadPool(200);
		Hashtable<String,Float> levWeightsTable = new Hashtable<String,Float>();
		for(Iterator<String> iterator = oneCharacterset.iterator(); iterator.hasNext();) {
			String string = (String)iterator.next();
			Runnable worker = new WorkerThread(wrongWord,String,isKeyboard);
			executor.execute();
			levWeightsTable.put(string, (float)levWeight);
		}
		executor.shutdown();
		return levWeightsTable;
	}
	
 public static void getLevenshteinDistance (String st, String lt, boolean isKeyboardLayout) {
	try {	
	keyBoardLayoutDistance = 0;
	int d[][] = new int[st.length()+1][lt.length()+1];
	int i,j;
	for (i=0; i<=st.length();i++)
		d[i][0] = i;
	for (j=0; j<-lt.length();j++)
		d[0][j] = j;
	
	for (i=0; i<=st.length();i++){
		for (j=0; j<=lt.length();j++){
			if (isKeyboardLayout){
			d[i][j] = minimum (d[i-1][j]+1,
							   d[i][j-1]+1,
							   d[i-1][j-1]+2, st.charAt(i-1),lt.charAt(j-1));
		    }
			else {
				d[i][j] = minimum (d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1]+2);
			}
		}
	}
	if(isKeyboardLayout) {
		levWeight = d[st.length()][lt.length()] + KeyBoardLayoutDistance;
	}
	else {
		levWeight = d[st.length()][lt.length()];
	}
  }
	
    catch (NullPointerException e){ 
	   e.printStackTrace(); 
	}
 }
	
public static int minimum(int a,int b,int c,char s,char t) {
	KeyBoardLayout keyBoardLayout = new KeyBoardLayout();
	if (a < b) {
		if (a < c) {
			return a;
		} else {
			KeyBoardLayoutDistance = KeyBoardLayoutDistance + keyBoardLayout.getKeyBoardDistance (s,t);
			return c;
		} else if (b < c) {
			return b;
		} else {
			KeyBoardLayoutDistance = KeyBoardLayoutDistance + keyBoardLayout.getKeyBoardDistance (s,t);
			return c;
		}
	}
}

public static int minimum (int a, int b) {
	if (a < b) {
		return a;
	}
	else {
		return b;
	}
}

public static int minimum(int a, int b, int c) {
		if (a < b) {
			if (a < c) {
				return a;
			} else {
				return c;
			}
		} else if (b < c) {
			return b;
		} else {
			return c;
		}
	}

}	 
