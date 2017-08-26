package org.grobid.trainer.evaluation;

import org.apache.commons.io.FileUtils;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.grobid.trainer.LabelStat;
import org.grobid.trainer.Stats;
import org.grobid.core.exceptions.GrobidException;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class EvaluationUtilitiesTest {

	public File getResourceDir(String resourceDir) {
		File file = new File(resourceDir);
		if (!file.exists()) {
			if (!file.mkdirs()) {
				throw new GrobidException("Cannot start test, because test resource folder is not correctly set.");
			}
		}
		return(file);
	}

	@Test
    public void testMetricsAllGood() throws Exception {
        String result = "a I-<1> I-<1>\nb <1> <1>\nc I-<2> I-<2>\nd I-<1> I-<1>\ne <1> <1>\n";
        //System.out.println(result);
        Stats wordStats = EvaluationUtilities.tokenLevelStats(result);
		Stats fieldStats = EvaluationUtilities.fieldLevelStats(result);

		LabelStat labelstat1 = wordStats.getLabelStat("<1>");
		LabelStat labelstat2 = wordStats.getLabelStat("<2>");

        assertThat(labelstat1.getObserved(), is(4));
        assertThat(labelstat2.getObserved(), is(1));
        assertThat(labelstat1.getExpected(), is(4));
        assertThat(labelstat2.getExpected(), is(1));

        labelstat1 = fieldStats.getLabelStat("<1>");
		labelstat2 = fieldStats.getLabelStat("<2>");

        assertThat(labelstat1.getObserved(), is(2));
        assertThat(labelstat2.getObserved(), is(1));
        assertThat(labelstat1.getExpected(), is(2));
        assertThat(labelstat2.getExpected(), is(1));
    }

    @Test
    public void testMetricsAllFalse() throws Exception {
        String result = "a I-<1> I-<2>\nb <1> <2>\nc <1> I-<2>\nd <1> <2>\ne <1> <2>\n";
		//System.out.println(result);
        Stats wordStats = EvaluationUtilities.tokenLevelStats(result);
		Stats fieldStats = EvaluationUtilities.fieldLevelStats(result);

        LabelStat labelstat1 = wordStats.getLabelStat("<1>");
		LabelStat labelstat2 = wordStats.getLabelStat("<2>");

        assertThat(labelstat1.getObserved(), is(0));
        assertThat(labelstat2.getObserved(), is(0));
        assertThat(labelstat1.getFalseNegative(), is(5));
        assertThat(labelstat2.getFalsePositive(), is(5));

        labelstat1 = fieldStats.getLabelStat("<1>");
		labelstat2 = fieldStats.getLabelStat("<2>");

        assertThat(labelstat1.getObserved(), is(0));
        assertThat(labelstat2.getObserved(), is(0));
        assertThat(labelstat1.getExpected(), is(1));
        assertThat(labelstat2.getExpected(), is(0));
    }

	@Test
    public void testMetricsMixed1() throws Exception {
    	// label of c is false
    	// token 80 precision for label <1>, 0 for label <2>
    	// field: ??
        String result = "a I-<1> I-<1>\nb <1> <1>\nc I-<2> I-<1>\nd I-<1> <1>\ne <1> <1>\n";
        //System.out.println(result);
		Stats wordStats = EvaluationUtilities.tokenLevelStats(result);
		Stats fieldStats = EvaluationUtilities.fieldLevelStats(result);

        LabelStat labelstat1 = wordStats.getLabelStat("<1>");
		LabelStat labelstat2 = wordStats.getLabelStat("<2>");

        assertThat(labelstat1.getObserved(), is(4));
        assertThat(labelstat2.getObserved(), is(0));
        assertThat(labelstat1.getExpected(), is(4));
        assertThat(labelstat2.getExpected(), is(1));
        assertThat(labelstat1.getFalseNegative(), is(0));
        assertThat(labelstat2.getFalseNegative(), is(1));
		assertThat(labelstat1.getFalsePositive(), is(1));
        assertThat(labelstat2.getFalsePositive(), is(0));

        labelstat1 = fieldStats.getLabelStat("<1>");
		labelstat2 = fieldStats.getLabelStat("<2>");

        assertThat(labelstat1.getObserved(), is(2));
        assertThat(labelstat2.getObserved(), is(0));
        assertThat(labelstat1.getExpected(), is(2));
        assertThat(labelstat2.getExpected(), is(1));
    }

    @Test
    public void testMetricsMixed2() throws Exception {
        String result = "a I-<1> I-<1>\nb <1> <1>\nc <1> I-<2>\nd <1> I-<1>\ne <1> <1>\n";
        //System.out.println(result);
        Stats wordStats = EvaluationUtilities.tokenLevelStats(result);
		Stats fieldStats = EvaluationUtilities.fieldLevelStats(result);

        LabelStat labelstat1 = wordStats.getLabelStat("<1>");
		LabelStat labelstat2 = wordStats.getLabelStat("<2>");

        assertThat(labelstat1.getObserved(), is(4));
        assertThat(labelstat2.getObserved(), is(0));
        assertThat(labelstat1.getExpected(), is(5));
        assertThat(labelstat2.getExpected(), is(0));
        assertThat(labelstat1.getFalseNegative(), is(1));
        assertThat(labelstat2.getFalseNegative(), is(0));
		assertThat(labelstat1.getFalsePositive(), is(0));
        assertThat(labelstat2.getFalsePositive(), is(1));

        labelstat1 = fieldStats.getLabelStat("<1>");
		labelstat2 = fieldStats.getLabelStat("<2>");

        assertThat(labelstat1.getObserved(), is(0));
        assertThat(labelstat2.getObserved(), is(0));
        assertThat(labelstat1.getExpected(), is(1));
        assertThat(labelstat2.getExpected(), is(0));
        assertThat(labelstat1.getFalseNegative(), is(1));
        assertThat(labelstat2.getFalseNegative(), is(0));
		assertThat(labelstat1.getFalsePositive(), is(1));
        assertThat(labelstat2.getFalsePositive(), is(1));
    }

    @Test
    public void testMetricsMixed3() throws Exception {
        String result = "a I-<1> I-<1>\nb I-<2> <1>\nc <2> I-<2>\nd <2> <2>\ne I-<1> I-<1>\nf <1> <1>\ng I-<2> I-<2>\n";
        Stats wordStats = EvaluationUtilities.tokenLevelStats(result);
		Stats fieldStats = EvaluationUtilities.fieldLevelStats(result);

        LabelStat labelstat1 = wordStats.getLabelStat("<1>");
		LabelStat labelstat2 = wordStats.getLabelStat("<2>");

        assertThat(labelstat1.getObserved(), is(3));
        assertThat(labelstat2.getObserved(), is(3));
        assertThat(labelstat1.getExpected(), is(3));
        assertThat(labelstat2.getExpected(), is(4));
        assertThat(labelstat1.getFalseNegative(), is(0));
        assertThat(labelstat2.getFalseNegative(), is(1));
		assertThat(labelstat1.getFalsePositive(), is(1));
        assertThat(labelstat2.getFalsePositive(), is(0));

        labelstat1 = fieldStats.getLabelStat("<1>");
		labelstat2 = fieldStats.getLabelStat("<2>");

        assertThat(labelstat1.getObserved(), is(2));
        assertThat(labelstat2.getObserved(), is(1));
        assertThat(labelstat1.getExpected(), is(2));
        assertThat(labelstat2.getExpected(), is(2));
        assertThat(labelstat1.getFalseNegative(), is(0));
        assertThat(labelstat2.getFalseNegative(), is(1));
		assertThat(labelstat1.getFalsePositive(), is(1));
        assertThat(labelstat2.getFalsePositive(), is(1));
    }

    @Test
    public void testMetricsReal() throws Exception {
    	File textFile = new File(this.getResourceDir("./src/test/resources/").getAbsoluteFile()+"/ex.txt.txt");
		if (!textFile.exists()) {
			throw new GrobidException("Cannot start test, because test resource folder is not correctly set.");
		}
        String result = FileUtils.readFileToString(textFile);
        Stats wordStats = EvaluationUtilities.tokenLevelStats(result);
		Stats fieldStats = EvaluationUtilities.fieldLevelStats(result);

        LabelStat labelstat1 = wordStats.getLabelStat("<body>");
		LabelStat labelstat2 = wordStats.getLabelStat("<headnote>");

        assertThat(labelstat1.getObserved(), is(378));
        assertThat(labelstat2.getObserved(), is(6));
        assertThat(labelstat1.getExpected(), is(378));
        assertThat(labelstat2.getExpected(), is(9));
        assertThat(labelstat1.getFalseNegative(), is(0));
        assertThat(labelstat2.getFalseNegative(), is(3));
		assertThat(labelstat1.getFalsePositive(), is(3));
        assertThat(labelstat2.getFalsePositive(), is(0));

        labelstat1 = fieldStats.getLabelStat("<body>");
		labelstat2 = fieldStats.getLabelStat("<headnote>");

        assertThat(labelstat1.getObserved(), is(3)); // <- should be 2!
        assertThat(labelstat2.getObserved(), is(2));
        assertThat(labelstat1.getExpected(), is(3));
        assertThat(labelstat2.getExpected(), is(3));
        assertThat(labelstat1.getFalseNegative(), is(0));
        assertThat(labelstat2.getFalseNegative(), is(1));
		assertThat(labelstat1.getFalsePositive(), is(1)); // <- should be 2!
        assertThat(labelstat2.getFalsePositive(), is(0));
    }

}