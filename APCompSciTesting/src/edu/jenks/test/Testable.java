package edu.jenks.test;

import java.io.IOException;
import java.lang.reflect.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.logging.*;
import edu.jenks.google.drive.Student;
import edu.jenks.util.*;

public abstract class Testable implements Runnable {
	
	private static final String TEST_METHOD_PREFIX = "test";
	protected static final Object[] EMPTY_OJBECT_ARRAY = {};
	
	protected String studentPackage, inputToStudentCode;
	protected Logger feedbackLogger;
	protected int totalPoints, latePenalty = 0;
	protected boolean continueTesting = true;
	
	private Logger gradesLogger;
	private final TestMethodComparator TEST_METHOD_COMPARATOR = new TestMethodComparator();
	private Thread thread;
	private ThreadGroup threadGroup;
	private Student student;
	private String projectName;
	
	public abstract int getPointsAvailable();
	public abstract Map<String, String> buildStudentClassNameToSuperclassName();
	public abstract void setUp() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
	
	private void verifySuperClass(Map<String, String> studentClassNameToSuperclassName) throws ClassNotFoundException, IllegalSuperclassException {
		Iterator<String> keys = studentClassNameToSuperclassName.keySet().iterator();
		while(keys.hasNext() && continueTesting) {
			String studentClassName = keys.next();
			String expectedSuperclassName = studentClassNameToSuperclassName.get(studentClassName);
			String superclassName = Class.forName(studentClassName).getSuperclass().getName();
			if(!expectedSuperclassName.equals(superclassName))
				throw new IllegalSuperclassException("Actual superclass '" + superclassName + "' did not match expected superclass '" + expectedSuperclassName + "'");
		}
		feedbackLogger.log(Level.FINE, "Superclass validated.");
	}
	
	public void start() {
		feedbackLogger.log(Level.INFO, "Begin test of package " + studentPackage + System.lineSeparator() + LoggingUtil.ASTERISKS);
		try {
			verifySuperClass(buildStudentClassNameToSuperclassName());
			setUp();
			feedbackLogger.log(Level.INFO, "Pass - object creation" + System.lineSeparator());
		} catch(Exception e) {
			feedbackLogger.log(Level.SEVERE, "Fail - object creation failed; abort testing: " + e.getMessage());
			continueTesting = false;
		}
		if(thread == null && continueTesting) {
			thread = threadGroup == null ? new Thread(this, student.toString()) : new Thread(threadGroup, this, student.toString());
			thread.start();
		} else
			logEnd();
	}
	
	public void run() {
		long startTime = System.currentTimeMillis();
		test();
		long elapsedMillis = System.currentTimeMillis() - startTime;
		feedbackLogger.log(Level.FINE, "Test time in milliseconds: " + elapsedMillis);
		logEnd();
	}
	
	private double getLatePenalty() {
		double penaltyPoints = 0;
		if(latePenalty > 0)
			penaltyPoints = latePenalty / 100.0 * totalPoints;
		return penaltyPoints;
	}
	
	private void logEnd() {
		double finalPoints = totalPoints;
		double penaltyPoints = getLatePenalty();
		if(penaltyPoints > 0) {
			feedbackLogger.log(Level.WARNING, "Late penalty applied - " + penaltyPoints + System.lineSeparator());
			finalPoints -= penaltyPoints;
		}
		feedbackLogger.log(Level.INFO, "Total for " + studentPackage + ":" + System.lineSeparator() + finalPoints + " points." + System.lineSeparator() + LoggingUtil.ASTERISKS + System.lineSeparator());
		if(getPointsAvailable() == finalPoints)
			feedbackLogger.log(Level.INFO, LogPool.getAllPointsMessage());
		else {
			double pointsAvailableAfterPenalty = getPointsAvailable() - penaltyPoints - finalPoints;
			feedbackLogger.log(Level.INFO, LogPool.getSomePointsMessage(finalPoints, getPointsAvailable(), pointsAvailableAfterPenalty));
		}
		String percent = NumberFormat.getPercentInstance().format(finalPoints / getPointsAvailable());
		logGradesMessage(Level.INFO, student.getLastName() + ", " + student.getFirstName() + ": " + finalPoints + " -> " + percent);
		HighScoreRecorder.LeaderBoard lb = HighScoreRecorder.getInstance().recordScore(student, projectName, percent.substring(0, percent.length() - 1));
		if(lb != null)
			logLeaderBoard(lb);
	}
	
	private void logLeaderBoard(HighScoreRecorder.LeaderBoard leaderBoard) {
		if(leaderBoard == null)
			return;
		StringBuilder sb = new StringBuilder(100);
		if(leaderBoard.LEADING_STUDENTS.size() > 0) {
			if(leaderBoard.POINT_DEFICIT > 0)
				sb.append("You are ").append(leaderBoard.POINT_DEFICIT).append(" points behind the next student.");
			else
				sb.append("You submitted your program after the next student ahead of you.");
			sb.append(System.lineSeparator());
			sb.append("The following students are ahead of you:").append(System.lineSeparator());
			for(Student student : leaderBoard.LEADING_STUDENTS)
				sb.append(student.getFirstName()).append(" ").append(student.getLastName()).append(System.lineSeparator());
			
		} else
			sb.append("Way to go, Top Dog, nobody is ahead of you!");
		feedbackLogger.log(Level.INFO, sb.toString());
	}
	
	public void test() {
		Method[] testMethods = findAndOrderTestMethods();
		for(int index = 0; index < testMethods.length && continueTesting; index++) {
			Method method = testMethods[index];
			String methodName = method.getName();
			feedbackLogger.log(Level.FINE, "Begin test of " + methodName);
			try {
				inputToStudentCode = null;
				method.invoke(this, EMPTY_OJBECT_ARRAY);
			} catch(Exception e) {
				logException(methodName, e);
				if(inputToStudentCode != null && inputToStudentCode.length() > 0) {
					feedbackLogger.log(Level.SEVERE, "Your cretinous code blew up - input that caused Exception: " + inputToStudentCode + System.lineSeparator());
				}
				continueTesting = false;
			}
			feedbackLogger.log(Level.FINE, "End test of " + methodName);
		}
		if(!continueTesting)
			feedbackLogger.warning("Testing aborted due to prerequisite failure - no more tests for you!");
	}
	
	private Method[] findAndOrderTestMethods() {
		Class<? extends Testable> executingObjectClass = getClass();
		Method[] declaredMethods = executingObjectClass.getDeclaredMethods();
		List<Method> testMethods = new ArrayList<Method>(declaredMethods.length);
		for(int index = 0; index < declaredMethods.length && continueTesting; index++) {
			Method method = declaredMethods[index];
			String methodName = method.getName();
			int modifiers = method.getModifiers();
			boolean isPublic = Modifier.isPublic(modifiers);
			boolean isStatic = Modifier.isStatic(modifiers);
			if(isPublic && !isStatic && methodName.startsWith(TEST_METHOD_PREFIX))
				testMethods.add(method);
		}
		Collections.sort(testMethods, TEST_METHOD_COMPARATOR);
		return testMethods.toArray(new Method[testMethods.size()]);
	}
	
	public void logException(String method, Exception e) {
		StringBuilder sb = new StringBuilder(100);
		String notice = StringUtil.buildString('*', 20);
		sb.append("Unhandled exception on method: ").append(method).append(notice).append(System.lineSeparator());
		sb.append(e.toString());
		String message = e.getMessage();
		if(message != null)
			sb.append(" - ").append(message);
		Throwable t = e.getCause();
		if(t != null) {
			String throwableCause = t.toString();
			sb.append("; CAUSE: ").append(throwableCause);
			if(ThreadDeath.class.getName().contentEquals(throwableCause))
				sb.append("; You probably have an infinite loop!  I am exhausted from running millions of iterations of your code - give me a break!");
		}
		sb.append(System.lineSeparator()).append(notice).append("End unhandled exception").append(notice).append(System.lineSeparator());
		feedbackLogger.log(Level.WARNING, sb.toString());
	}
	
	public void logPass(String message) {
		feedbackLogger.log(Level.INFO, "Pass: " + message + System.lineSeparator());
	}
	
	public void logFailPrereq() {
		logFail("testing skipped due to previous failures.");
	}
	
	public void logFail(String message) {
		feedbackLogger.log(Level.WARNING, "Fail: " + message + System.lineSeparator());
	}
	
	public void logPass(String message, String expected, String actual) {
		Level level = Level.INFO;
		message = "Pass: " + message;
		logExpectedActual(level, message, expected, actual);
	}
	
	public void logPass(String message, double expected, double actual) {
		logPass(message, String.valueOf(expected), String.valueOf(actual));
	}
	
	public void logFail(String message, double expected, double actual, int points) {
		logFail(message, String.valueOf(expected), String.valueOf(actual), points);
	}
	
	public void logFail(String message, Object expected, Object actual, int points) {
		logFail(message, expected == null ? "null" : expected.toString(), actual == null ? "null" : actual.toString(), points);
	}
	
	public void logFail(String message, boolean expected, boolean actual, int points) {
		logFail(message, Boolean.toString(expected), Boolean.toString(actual), points);
	}
	
	public void logFail(String message, String expected, String actual, int points) {
		Level level = Level.WARNING;
		message = "Fail (-" + points + "): " + message;
		logExpectedActual(level, message, expected, actual);
	}
	
	public void logExpectedActual(Level level, String message, long expected, long actual) {
		logExpectedActual(level, message, String.valueOf(expected), String.valueOf(actual));
	}
	
	public void logExpectedActual(Level level, String message, String expected, String actual) {
		StringBuilder sb = new StringBuilder(100);
		sb.append(message).append(System.lineSeparator());
		sb.append("\tExpected: ").append(expected).append(System.lineSeparator());
		sb.append("\tActual: " ).append(actual).append(System.lineSeparator());
		feedbackLogger.log(level, sb.toString());
	}
	
	public void logInfo(String message) {
		feedbackLogger.log(Level.INFO, message + System.lineSeparator());
	}
	
	public void setLogFilePathFeedback(String path, Student student) throws IOException {
		feedbackLogger = Logger.getLogger(student + " Feedback: " + getClass().getName());
		LoggingUtil.initLocalFileLogger(feedbackLogger, path);
	}
	
	public Logger getGradesLogger() {
		return gradesLogger;
	}
	
	public void setGradesLogger(Logger gradesLogger) {
		this.gradesLogger = gradesLogger;
	}
	
	public void logGradesMessage(Level level, String msg) {
		synchronized(gradesLogger) {
			gradesLogger.log(level, msg);
		}
	}
	
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	
	public ThreadGroup getThreadGroup() {
		return threadGroup;
	}
	public void setThreadGroup(ThreadGroup threadGroup) {
		this.threadGroup = threadGroup;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
}
