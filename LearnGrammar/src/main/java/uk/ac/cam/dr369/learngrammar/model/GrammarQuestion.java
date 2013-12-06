package uk.ac.cam.dr369.learngrammar.model;

import java.util.Collection;
import java.util.Collections;

import uk.ac.cam.dr369.learngrammar.util.Utils;

/**
 * An individual question in a grammar test. The user is asked to identify which of the provided sentences exemplifies some grammatical feature.
 * 
 * @author duncan.roberts
 *
 */
public class GrammarQuestion {
	private final String questionText;
	private final String correctAnswer;
	private final Collection<String> incorrectAnswers;

	public GrammarQuestion(String questionText, String correctAnswer, Collection<String> incorrectAnswers) {
		this.questionText = questionText;
		this.correctAnswer = correctAnswer;
		this.incorrectAnswers = Collections.unmodifiableCollection(incorrectAnswers);
	}
	public String getQuestionText() {
		return questionText;
	}
	public String getCorrectAnswer() {
		return correctAnswer;
	}
	public Collection<String> getIncorrectAnswers() {
		return incorrectAnswers;
	}
	@Override
	public String toString() {
		return "Question: " + questionText + ". Correct: "+correctAnswer+"; incorrect: "+Utils.formatForPrinting(incorrectAnswers);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((correctAnswer == null) ? 0 : correctAnswer.hashCode());
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
		GrammarQuestion other = (GrammarQuestion) obj;
		if (correctAnswer == null) {
			if (other.correctAnswer != null)
				return false;
		} else if (!correctAnswer.equals(other.correctAnswer))
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
}