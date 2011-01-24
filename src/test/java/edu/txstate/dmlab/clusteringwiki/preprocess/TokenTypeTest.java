package edu.txstate.dmlab.clusteringwiki.preprocess;

/**
 *  ClusteringWiki - personalized and collaborative clustering of search results
 *  Copyright (C) 2010  Texas State University-San Marcos
 *  
 *  Contact: http://dmlab.cs.txstate.edu
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import junit.framework.Assert;

import org.junit.Test;

import edu.txstate.dmlab.clusteringwiki.util.TokenUtils;

public class TokenTypeTest {

	@Test
	public void testTokenTypes(){
		short[] tt = new short[14];
		tt[0] = ITokenType.TF_WORD;
		tt[1] = ITokenType.TF_NUMERIC;
		tt[2] = ITokenType.TF_PUNCTUATION;
		tt[3] = ITokenType.TF_ALL_CAPS;
		tt[4] = ITokenType.TF_CAPITALIZED;
		tt[5] = ITokenType.TF_PHRASE_SEPARATOR;
		tt[6] = ITokenType.TF_SENTENCE_SEPARATOR;
		tt[7] = ITokenType.TF_STOP_WORD;
		tt[8] = ITokenType.TF_QUERY_TERM;
		tt[9] = ITokenType.TF_WORD | ITokenType.TF_CAPITALIZED;
		tt[10] = ITokenType.TF_PUNCTUATION | ITokenType.TF_PHRASE_SEPARATOR;
		tt[11] = ITokenType.TF_PUNCTUATION | ITokenType.TF_SENTENCE_SEPARATOR;
		tt[12] = ITokenType.TF_WORD | ITokenType.TF_STOP_WORD;
		tt[13] = ITokenType.TF_WORD | ITokenType.TF_QUERY_TERM;
		
		int i = 0;
		String s = "";
		for(short t : tt){
			s += i++ + ".";
			s += " tt: " + TokenUtils.getTokenType(t);
			s += " ttw: " + TokenUtils.isWord(t);
			s += " ttn: " + TokenUtils.isNumeric(t);
			s += " ttp: " + TokenUtils.isPunctuation(t);
			s += " ct: " + TokenUtils.getCapitalizationType(t);
			s += " cta: " + TokenUtils.isAllCaps(t);
			s += " ctc: " + TokenUtils.isCapitalized(t);
			s += " st: " + TokenUtils.getSeparationType(t);
			s += " stp: " + TokenUtils.isPhraseSeparator(t);
			s += " sts: " + TokenUtils.isSentenceSeparator(t);
			s += " sw: " + TokenUtils.isStopWord(t);
			s += " qw: " + TokenUtils.isQueryWord(t);
			s += "\n";
		}
		
		String expected = "0. tt: 1 ttw: true ttn: false ttp: false ct: 0 cta: false ctc: false st: 0 stp: false sts: false sw: false qw: false\n" +
			"1. tt: 2 ttw: false ttn: true ttp: false ct: 0 cta: false ctc: false st: 0 stp: false sts: false sw: false qw: false\n" +
			"2. tt: 3 ttw: false ttn: false ttp: true ct: 0 cta: false ctc: false st: 0 stp: false sts: false sw: false qw: false\n" +
			"3. tt: 0 ttw: false ttn: false ttp: false ct: 16 cta: true ctc: false st: 0 stp: false sts: false sw: false qw: false\n" +
			"4. tt: 0 ttw: false ttn: false ttp: false ct: 32 cta: false ctc: true st: 0 stp: false sts: false sw: false qw: false\n" +
			"5. tt: 0 ttw: false ttn: false ttp: false ct: 0 cta: false ctc: false st: 256 stp: true sts: false sw: false qw: false\n" +
			"6. tt: 0 ttw: false ttn: false ttp: false ct: 0 cta: false ctc: false st: 512 stp: false sts: true sw: false qw: false\n" +
			"7. tt: 0 ttw: false ttn: false ttp: false ct: 0 cta: false ctc: false st: 0 stp: false sts: false sw: true qw: false\n" +
			"8. tt: 0 ttw: false ttn: false ttp: false ct: 0 cta: false ctc: false st: 0 stp: false sts: false sw: false qw: true\n" +
			"9. tt: 1 ttw: true ttn: false ttp: false ct: 32 cta: false ctc: true st: 0 stp: false sts: false sw: false qw: false\n" +
			"10. tt: 3 ttw: false ttn: false ttp: true ct: 0 cta: false ctc: false st: 256 stp: true sts: false sw: false qw: false\n" +
			"11. tt: 3 ttw: false ttn: false ttp: true ct: 0 cta: false ctc: false st: 512 stp: false sts: true sw: false qw: false\n" +
			"12. tt: 1 ttw: true ttn: false ttp: false ct: 0 cta: false ctc: false st: 0 stp: false sts: false sw: true qw: false\n" +
			"13. tt: 1 ttw: true ttn: false ttp: false ct: 0 cta: false ctc: false st: 0 stp: false sts: false sw: false qw: true\n";
		
		Assert.assertEquals(expected, s);
	}
	
}
