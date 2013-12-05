package uk.ac.cam.dr369.learngrammar.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

// TODO give file:File field, populated during serialization, so that it can be resaved over the same file.
public class LearningTask implements Serializable {
	private static final long serialVersionUID = 8886215064416364092L;

	private static final Pattern QUESTION_TEXT_PATTERN = Pattern.compile("\t<question-text>(.*)</question-text>");
	private static final Pattern ANSWER_PATTERN = Pattern.compile("\t\t<answer>(.*)</answer>");
	private static final Pattern COUNTER_PATTERN = Pattern.compile("\t<next-index>([0-9]*)</next-index>");
	public static final int INCORRECT_ANSWERS = 3;
	
	private final String questionText;
	private int counter;
	private List<String> correctAnswers;
	private List<String> incorrectAnswers;
	private final File fromFile;
	
	private transient ListIterator<String> correctAnswerIterator;
	private transient ListIterator<String> incorrectAnswerIterator;

	public LearningTask(String questionText, List<String> correctAnswers, List<String> incorrectAnswers) {
		this(questionText, correctAnswers, incorrectAnswers, 0, null);
	}
	private LearningTask(String questionText, List<String> correctAnswers, List<String> incorrectAnswers, int counter, File fromFile) {
		this.questionText = questionText;
		this.fromFile = fromFile;
		initAnswers(correctAnswers, incorrectAnswers, counter);
	}
	private void initAnswers(List<String> correctAnswers, List<String> incorrectAnswers, int counter) {
		this.correctAnswers = new ArrayList<String>(correctAnswers);
		this.incorrectAnswers = new ArrayList<String>(incorrectAnswers);
		correctAnswerIterator = this.correctAnswers.listIterator(counter);
		incorrectAnswerIterator = this.incorrectAnswers.listIterator(counter * INCORRECT_ANSWERS);
		this.counter = counter;
	}
	public void resetQuestions() {
		initAnswers(correctAnswers, incorrectAnswers, 0);
	}
	public int questionCount() {
		return counter;
	}
	public boolean hasNextQuestion() {
		return counter < correctAnswers.size() && counter < (incorrectAnswers.size() / INCORRECT_ANSWERS);
	}
	public GrammarQuestion getNextQuestion() {
		if (correctAnswerIterator == null)
			correctAnswerIterator = correctAnswers.listIterator(counter);
		if (incorrectAnswerIterator == null)
			incorrectAnswerIterator = incorrectAnswers.listIterator(counter * INCORRECT_ANSWERS);
		String correct = correctAnswerIterator.next();
		Collection<String> incorrect = new ArrayList<String>();
		for (int i = 0; i < INCORRECT_ANSWERS; i++) {
			incorrect.add(incorrectAnswerIterator.next());
		}
		counter++;
		return new GrammarQuestion(questionText, correct, incorrect);
	}
	public String getQuestionText() {
		return questionText;
	}
	public Collection<String> getCorrectAnswers() {
		return correctAnswers;
	}
	public Collection<String> getIncorrectAnswers() {
		return incorrectAnswers;
	}
	public File getFile() {
		return fromFile;
	}
	@Override
	public String toString() {
		return questionText;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((correctAnswers == null) ? 0 : correctAnswers.hashCode());
		result = prime * result + counter;
		result = prime * result + ((incorrectAnswers == null) ? 0 : incorrectAnswers.hashCode());
		result = prime * result + ((questionText == null) ? 0 : questionText.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LearningTask other = (LearningTask) obj;
		if (correctAnswers == null) {
			if (other.correctAnswers != null)
				return false;
		} else if (!correctAnswers.equals(other.correctAnswers))
			return false;
		if (counter != other.counter)
			return false;
		if (incorrectAnswers == null) {
			if (other.incorrectAnswers != null)
				return false;
		} else if (!incorrectAnswers.equals(other.incorrectAnswers))
			return false;
		if (questionText == null) {
			if (other.questionText != null)
				return false;
		} else if (!questionText.equals(other.questionText))
			return false;
		return true;
	}
	public void toFile(File file) throws IOException {
		DataOutputStream dos = null;
		try {
			StringBuilder sb = new StringBuilder();
			dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			sb.append("<learning-task>\n");
			sb.append("\t<question-text>"); sb.append(StringEscapeUtils.escapeHtml(questionText)); sb.append("</question-text>\n");
			if (counter > 0) {
				sb.append("\t<next-index>"); sb.append(counter); sb.append("</next-index>\n");
			}
			sb.append("\t<correct-answers>\n");
			int i = 0;
			for (String correct : correctAnswers) {
				sb.append("\t\t<answer>"); sb.append(StringEscapeUtils.escapeHtml(correct)); sb.append("</answer>\n");
				if (i++ % 20 == 0) {
					// there are limits on the length of the String passed to writeUTF. So we divide it up.
					// See http://bugzilla.qos.ch/show_bug.cgi?id=100
					dos.writeUTF(sb.toString());
					sb = new StringBuilder();
				}
			}
			sb.append("\t</correct-answers>\n");
			sb.append("\t<incorrect-answers>\n");
			for (String incorrect : incorrectAnswers) {
				sb.append("\t\t<answer>"); sb.append(StringEscapeUtils.escapeHtml(incorrect)); sb.append("</answer>\n");
				if (i++ % 20 == 0) {
					dos.writeUTF(sb.toString());
					sb = new StringBuilder();
				}
			}
			sb.append("\t</incorrect-answers>\n");
			sb.append("</learning-task>\n");
			dos.writeUTF(sb.toString());
		}
		finally {
			if (dos != null)
				dos.close();
		}
	}

	public static LearningTask fromFile(File file) throws IOException {
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
			List<String> correct = new ArrayList<String>();
			List<String> incorrect = new ArrayList<String>();
			String questionText = null;
			int counter = 0;
			boolean c = true;
			while (dis.available() > 0) {
				String[] lines = dis.readUTF().split("\n");
				for (String line : lines) {
					Matcher qtm = QUESTION_TEXT_PATTERN.matcher(line);
					Matcher am = ANSWER_PATTERN.matcher(line);
					Matcher cm = COUNTER_PATTERN.matcher(line);
					if (qtm.matches()) {
						questionText = StringEscapeUtils.unescapeHtml(qtm.group(1));
					}
					else if (line.contains("<correct-answers>")) {
						c = true;
					}
					else if (line.contains("<incorrect-answers>")) {
						c = false;
					}
					else if (line.contains("<next-index>")) {
						counter = Integer.parseInt(cm.group(1));
					}
					else if (am.matches()) {
						String answer = StringEscapeUtils.unescapeHtml(am.group(1));
						if (c)
							correct.add(answer);
						else
							incorrect.add(answer);
					}
				}
			}
			return new LearningTask(questionText, correct, incorrect, counter, file);
		}
		finally {
			if (dis != null)
				dis.close();
		}
	}
}