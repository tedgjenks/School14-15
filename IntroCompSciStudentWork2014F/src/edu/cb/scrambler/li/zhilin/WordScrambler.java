package edu.cb.scrambler.li.zhilin;

import java.util.List;

import edu.jenks.dist.cb.scrambler.Scrambler;

public class WordScrambler implements Scrambler {

	private static final char TOKEN = 'A';

	public WordScrambler(String s) {
		String retVal = new String(s);
		if (s != null && s.length() > 0) {
			retVal = s.substring(0, s.length() - 1);
		}
	}

	public WordScrambler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void scrambleOrRemove(List<String> wordList) {
		String scrambledWord = null;
		for(int index = 0; index < wordList.size(); index++){
			scrambledWord = scrambleWord(wordList.get(index));
			if (wordList.get(index).equals(scrambledWord)){
				wordList.remove(wordList.get(index));
				index--;
			}else{
				wordList.set(index, scrambledWord);
			}
		}
	}

	@Override
	public String scrambleWord(String word) {
		StringBuilder sb = new StringBuilder(word.length());
		for(int index = 0; index < word.length() - 1; index++){
			char curChar = word.charAt(index);
			char nextChar = word.charAt(index + 1);
			if(curChar == TOKEN && nextChar != TOKEN) {
				sb.append(nextChar).append(curChar);
				index++;
			}else{
				sb.append(curChar);
			}
		}
		if(word.length() > sb.length())
			sb.append(word.charAt(word.length() - 1));
		return sb.toString();
	}

}
