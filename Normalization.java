import java.util.*;
import java.lang.*;

public class Normalization {
	ArrayList<Float> allFloats;
	
	public Float normalize(float currentLD, Hashtable<String,Float> hashTable) {
		allFloats = getFloats(hashTable);
		float normalFreq = 0.0f;
		if(allFloats.size() > 0) {
			float minLD = minimum(allFloats);
			float maxLD = maximum(allFloats);
			normalFreq = ( (currentLD - minLD) / (maxLD - minLD) );
		}
		return normalFreq;	
	}
	public ArrayList<Float> getFloats(Hashtable<String,Float> hashTable) {
		ArrayList<Float> floats = new ArrayList<Float>();
		for(Iterator<String> iterator = hashTable.keySet().iterator(); iterator.hasNext();) {
			String type = (String) iterator.next();
			floats.add(hashTable.get(type));
		}
		return floats;
	}
	public float minimum(ArrayList<Float> arrayFloats) {
		if(arrayFloats == null) {
			throw IllegalArgumentException("arrayFloats cannot be null");
		}else if(arrayFloats.size() == 0) {
			throw IllegalArgumentException("arrayFloats cannot be empty");
		} 
		float min = arrayFloats.get(0);
		for(int i = 1; i < arrayFloats.size(); i++) {
			if(Float.isNaN(arrayFloats.get(i))) {
				return Float.NaN;
			}
			if(arrayFloats.get(i) < min) {
				min = arrayFloats.get(i);
			}
		}
		return min;		
	}
	
	public float maximum(ArrayList<Float> arrayFloats) {
		if(arrayFloats == null) {
			throw IllegalArgumentException("arrayFloats cannot be null");
		} else if(arrayFloats.size() == 0) {
			throw IllegalArgumentException("arrayFloats cannot be empty");
		}
		float max = arrayFloats.get(0);
		for(int i = 1; i < arrayFloats.size(); i++) {
			if(Float.isNaN(arrayFloats.get(i))) {
				return Float.NaN;
			}
			if(arrayFloats.get(i) > max) {
				max = arrayFloats.get(i);
			}
		}
		return max;
	}
	
}
