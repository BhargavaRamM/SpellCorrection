public class WorkerThread implements Runnable {
	private String wrongWord;
	private String dicWord;
	private boolean isKeyboardLayout;
	
	public WorkerThread(String wrongWord, String dicWord, boolean isKeyboardLayout) {
		this.wrongWord = wrongWord;
		this.dicWord = dicWord;
		this.isKeyboardLayout = isKeyboardLayout;
	}
    public void run () {
		processCommand(wrongWord,dicWord,isKeyboardLayout);
	}
	private void processCommand(String st, String lt, boolean isKeyboardLayout) {
		Levenshtein.getLevenshteinDistance(st, lt, isKeyboardLayout);
	}
}