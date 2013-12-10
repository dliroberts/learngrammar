package uk.ac.cam.dr369.learngrammar.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.cam.dr369.learngrammar.model.Claws2Pos;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation;
import uk.ac.cam.dr369.learngrammar.model.Token;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.FlagSubtype;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.TokenSubtype;
import uk.ac.cam.dr369.learngrammar.util.Utils;

/**
 * Façade for RASP syntactic parser. *nix only.
 * @author duncan.roberts
 */
public class RaspSyntacticParser implements SyntacticParser {
	/** Should match content of an individual token */
	private static final Pattern TOKEN_REGEX = Pattern.compile("\\|([^+\\|]+)(?:\\+([^:\\|]*))?:([1-9][0-9]*)_([^\\|]+)\\| ?");
	
	//                                                                 (TYPE___________    SUBTYPE_______________________________________________ HEAD___________ DEPENDENT______    INITIAL__  )
	private static final Pattern GR_REGEX =         Pattern.compile("\\(\\|([^\\|]+)\\|(?: (\\|[^ \\|:_]+\\||_|\\|[^ \\|:_]+:[0-9]+_[^\\|]+\\|))? \\|([^\\|]+)\\| \\|([^\\|]+)\\|(?: ([^ ]+))?\\)");
	private static final Pattern PASSIVE_GR_REGEX = Pattern.compile("\\(\\|passive\\| " +                                                        "\\|([^\\|]+)\\|" +                         "\\)");

	private static RaspSyntacticParser instance;

	public static RaspSyntacticParser getInstance() {
		if (instance == null)
			instance = new RaspSyntacticParser();
		return instance;
	}

	public DependencyStructure toDependencyStructure(String sentences) throws IOException {
		sentences = sentences.replace('\u2018', '\'').replace('\u2019', '\'') // single left/right quotes
			.replace('\u201c', '"').replace('\u201d', '"'); // double left/right quotes
		
		String parse = Utils.runScript("./rasp.sh", sentences);
		String tokenised = Utils.runScript("./tok_rasp.sh", sentences);
		return getDependencyStructure(parse, tokenised);
	}
	
	public List<DependencyStructure> toDependencyStructures(String sentences) throws IOException {
		sentences = sentences.replace('\n', ' ');
		String parse = Utils.runScript("./rasp.sh", sentences);
		String tokenised = Utils.runScript("./tok_rasp.sh", sentences);
		return getDependencyStructures(parse, tokenised);
	}
	
	private static List<DependencyStructure> getDependencyStructures(String output, String tokenisedSentence) {
		List<DependencyStructure> depStructs = new ArrayList<DependencyStructure>();
		String[] tsa = tokenisedSentence.split("\n");
		List<String> tokSentences = new ArrayList<String>(Arrays.asList(tsa));
		tokSentences.remove(0); // skip 0th line as it's blank
		
		StringBuilder sb = new StringBuilder();
		String[] outLines = output.split("\n");
		int sentence = 0;
		for (int i = 0; i < outLines.length; i++) {
			String line = outLines[i];
			if (line.length() > 0) {
				sb.append(line);
				sb.append('\n');
			}
			if ((line.length() == 0 || i == outLines.length - 1) && sb.length() > 0) {
				depStructs.add(getDependencyStructure1(sb.toString(), tokSentences.get(sentence++)));
				sb = new StringBuilder();
			}
		}
		
		return depStructs;
	}
	
	private static DependencyStructure getDependencyStructure(String output, String tokenisedSentence) {
		return getDependencyStructure1(output, tokenisedSentence.split("\n")[1]); // remove 0th line as it's blank
	}
	
	private static DependencyStructure getDependencyStructure1(String output, String tokenisedSentence) {
		List<GrammaticalRelation> grs = new ArrayList<GrammaticalRelation>();
		List<Token> tokens = new ArrayList<Token>();
		Map<String, Token> tokenMap = new HashMap<String, Token>();
		
		tokenisedSentence = tokenisedSentence.substring("^ ".length()); // trim off start-of-sentence marker
		
		String[] lines = output.split("\n");
		
		String tokensStr = lines[0];
		tokensStr = tokensStr.substring(1, tokensStr.lastIndexOf(')', tokensStr.lastIndexOf(';')));
		String[] tokensArr = tokensStr.split(" ");
		for (String tokenStr : tokensArr) {
			Token token = toToken(tokenStr, tokenisedSentence);
			tokens.add(token);
			tokenMap.put(tokenStr.substring(1, tokenStr.length()-1), token);
		}
		
		String line;
		for (int i = 2; i < lines.length; i++) {
			line = lines[i];
			GrammaticalRelation gr = toGr(line, tokenMap);
			grs.add(gr);
		}
		return new DependencyStructure(grs, tokens);
	}
	
	private static Token toToken(String tokenStr, String tokenisedSentence) {
		Matcher tokenMatcher = TOKEN_REGEX.matcher(tokenStr);
		if (!tokenMatcher.matches())
			throw new IllegalStateException("Cannot parse: "+tokenStr);
		if (tokenMatcher.groupCount() != 4)
			throw new IllegalStateException("Can't parse: "+tokenStr);
		
		String lemma = tokenMatcher.group(1);
		String suffix = tokenMatcher.group(2);
		int index = Integer.parseInt(tokenMatcher.group(3)) - 1; // Normalise to zero-indexed
		String tag = tokenMatcher.group(4);
		String word = tokenisedSentence.split(" ")[index];
		return new Token(lemma, suffix, index, Claws2Pos.valueOfByLabel(tag), null, word);
	}
	
	private static GrammaticalRelation toGr(String gr, Map<String, Token> tokens) {
		Matcher tokenMatcher = GR_REGEX.matcher(gr);
		Matcher passiveTokenMatcher = PASSIVE_GR_REGEX.matcher(gr);
		boolean passive = passiveTokenMatcher.matches();
		if (passive) {
			if (passiveTokenMatcher.groupCount() != 1)
				throw new IllegalStateException("Can't parse: "+gr);
			String headStr = passiveTokenMatcher.group(1);
			return new GrammaticalRelation(GrammaticalRelation.GrType.valueOfByLabel("passive"), null, null, tokens.get(headStr), null);
		}
		else {
			if (tokenMatcher.groupCount()        != 5 || !tokenMatcher.matches())
				throw new IllegalStateException("Cannot parse: "+gr);
			String dependentStr = tokenMatcher.group(4);
			String initialGrValue = tokenMatcher.group(5);
			String subtypeStr = tokenMatcher.group(2);
			String type = tokenMatcher.group(1);
			String headStr = tokenMatcher.group(3);
			GrammaticalRelation.Subtype subtype;
			if (subtypeStr == null || subtypeStr.equals("_")) {
				subtype = null;
			}
			else if (subtypeStr.matches("\\|[^ \\|:_]+:[0-9]+_[^\\|]+\\|")) {
				subtype = new TokenSubtype(tokens.get(subtypeStr.substring(1, subtypeStr.length() - 1)));
			}
			else if (subtypeStr.matches("\\|[^ \\|:_]+\\|")) {
				subtype = new FlagSubtype(subtypeStr.substring(1, subtypeStr.length()-1));
			}
			else {
				throw new IllegalStateException("Invalid subtype: "+subtypeStr);
			}
			return new GrammaticalRelation(GrammaticalRelation.GrType.valueOfByLabel(type), subtype, initialGrValue, tokens.get(headStr), tokens.get(dependentStr));
		}
	}

	@Override
	public boolean useCorpus() {
		return false; // not currently supported. I think a RASP-parseable corpus is out there though... Brown corpus?
	}

	@Override
	public Collection<DependencyStructure> getCorpus() {
		throw new IllegalStateException("No corpus available for RASP parser.");
	}
}