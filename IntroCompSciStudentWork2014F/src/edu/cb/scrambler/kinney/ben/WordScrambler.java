package edu.cb.scrambler.kinney.ben;

import java.util.List;

import edu.jenks.dist.cb.scrambler.Scrambler;

public class WordScrambler implements Scrambler {
	
	private static final char TOKEN='A';

	public String wordScrambler(String word) {
		StringBuilder sb = new StringBuilder(word.length());
		for(int index = 0; index < word.length() - 1;index++) {
			char curChar = word.charAt(index);
			char nextChar = word.charAt(index +1);
			if (curChar == TOKEN && nextChar != TOKEN) {
				sb.append(nextChar).append(curChar);
				index ++;
			} else {
				sb.append(curChar);
			}
		}
		if (word.length () > sb.length());
			sb.append (word.charAt(word.length() -1));
		return sb.toString ();
		
	}

		

	@Override
	public void scrambleOrRemove(List<String> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public String scrambleWord(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
