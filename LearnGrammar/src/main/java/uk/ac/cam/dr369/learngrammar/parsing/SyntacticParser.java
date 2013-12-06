package uk.ac.cam.dr369.learngrammar.parsing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

/**
 * Determines the grammatical structure of a given input sentence.
 * @author duncan.roberts
 *
 */
public abstract class SyntacticParser {
	public abstract List<DependencyStructure> toDependencyStructures(String sentences) throws Exception;
	
	public abstract DependencyStructure toDependencyStructure(String sentence) throws Exception;
	
	protected static String callWebservice(String urlPrefix, String sentence) throws Exception {
		String sentenceEnc = URLEncoder.encode(sentence, "UTF-8");
		
		URL url = new URL(urlPrefix + sentenceEnc);
		
		StringBuffer sb;
		BufferedReader rd = null;
		URLConnection conn;
		try {
			conn = url.openConnection();
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			if (rd != null)
				rd.close();
		}
		return sb.toString();
	}

	protected static String runScript(String script, String input) throws Exception {
		// FIXME massive exploit in code below; should sanitise input before running to avoid arbitrary code exec
		String[] parseTask = new String[] {"/bin/bash", script, input};
		Process process = Runtime.getRuntime().exec(parseTask);
		if(process.waitFor() != 0)
			throw new IllegalStateException("WaitFor != 0");
		if(process.exitValue() != 0)
			throw new IllegalStateException("ExitCode != 0");
		
		String line;
//		BufferedReader es = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//		if ((line = es.readLine()) != null) {
//			throw new IllegalStateException("Error: "+line);
//		}
		BufferedReader os = new BufferedReader(new InputStreamReader(process.getInputStream()));
		
		StringBuilder out = new StringBuilder();
		for (line = null; (line = os.readLine()) != null;) {
			out.append(line);
			out.append('\n');
		}
		return out.toString();
	}

}