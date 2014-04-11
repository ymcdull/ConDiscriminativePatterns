package com.fpgrowth;

/**
 * Data Structure for Phosphorylation Motifs</p>
 */
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

//import org.apache.commons.math.distribution.ChiSquaredDistribution;
//import org.apache.commons.math.distribution.ChiSquaredDistributionImpl;
//import org.apache.commons.math.distribution.NormalDistribution;
//import org.apache.commons.math.distribution.NormalDistributionImpl;

//import preProcessing.PhosPhAtProcessing;

public class Motif {

    /**
     * The motif stored as an array of of string(s).
     */
    protected String[] m_items;
    /**
     * Counter for how many phospeptides contain this motif.
     */
    protected int m_counter_foreground;
    /**
     * Counter for how many non-phospeptides contain this motif.
     */
    protected int m_counter_background;
    /**
     * The significance of the motif
     */
    protected double local_significanceValue = 1000000;
    protected double global_significanceValue;
    /**
     * The z score value for the motif
     */
    protected double m_zscore;
    /**
     * The index of items
     */
    protected FastVector m_index;
    /**
     * Tag if the motif is frequent or not
     */
    protected boolean Isfrequent;
    // *
    //  * the set of sub-motif
     
    // protected Set<Motif> KminusOneSets;
    protected FastVector significanceValue;

    /**
     * Constructor
     */
    public Motif(int len, String phosAA) {
        m_items = new String[len];
        for (int i = 0; i < len; i++) {
            m_items[i] = "_";
        }
        m_items[(len - 1) / 2] = phosAA;
        // m_counter_foreground = 0;
        // m_counter_background = 0;
        global_significanceValue = 0;
        m_index = new FastVector();
        // KminusOneSets = new HashSet();
    }

    /**
     * Constructor
     */
    public Motif(String[] array) {

        m_items = array;
        // m_counter_foreground = 0;
        // m_counter_background = 0;
        global_significanceValue = 0;

        m_index = new FastVector();
        // KminusOneSets = new HashSet();
    }
    
    public Motif(Motif preMotif, int m_length, String m_phosAA){
    	global_significanceValue = preMotif.global_significanceValue;
    	int len = preMotif.m_index.size();
        m_items = new String[m_length];
		m_index = new FastVector();
        for (int i = 0; i < m_length; i++) {
            m_items[i] = "_";
        }
        m_items[(m_length - 1) / 2] = preMotif.m_items[(m_length - 1)/2];
        for(int i =0;i<len;i++){
    		int preIndex = (Integer) preMotif.m_index.elementAt(i);
    		String preItem = (String) preMotif.m_items[preIndex];

    		m_index.addElement(preIndex);
    		m_items[preIndex] = preItem;
        }
    }

    /**
     * Checks if an instance contains a motif.
     *
     * @param instance the string to be tested
     * @return true if the given instance contains this motif
     */
    public boolean containedBy(String instance) {
        /*
         * for (int i = 0; i < instance.length(); i++){ if
         * (!m_items[i].equals("_")) { if
         * (!m_items[i].equals(instance.substring(i,i+1))) return false; }
	      }
         */


        for (int i = 0; i < m_index.size(); i++) {
            int pos = ((Integer) m_index.elementAt(i)).intValue();
            if (!m_items[pos].equals(instance.substring(pos, pos + 1))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Updates the p value of the motif.
     */
    public double upDateSignificanceValue(int m_counter_foreground, int m_counter_background, int m1, int m2) {

        double m_significanceValue;
        if (m_counter_background != 0) {
            m_significanceValue = 1.0 * (m_counter_foreground * m2) / (m_counter_background * m1);
        } else {
            m_significanceValue = ((m_counter_foreground + 0.5) * m2) / ((m_counter_background + 0.5) * m1);
        }
        return m_significanceValue;

    }

    /**
     * chi-square measure
     */
    public static double chi_square(int y_plus, int y_minus, int m1, int m2) {

        double[][] observedValues = new double[2][2];
        double[][] expectedValues = new double[2][2];

        /*
         * constructing observed freq table
         */
        observedValues[0][0] = y_plus;   //y+
        observedValues[0][1] = y_minus;  //y-
        observedValues[1][0] = m1 - y_plus;
        observedValues[1][1] = m2 - y_minus;

        /*
         * computing expected freq values and compute chi-square value
         */

        expectedValues[0][0] = ((double) (y_plus + y_minus) * (double) m1) / (double) (m1 + m2);
        expectedValues[0][1] = ((double) (y_plus + y_minus) * (double) m2) / (double) (m1 + m2);
        expectedValues[1][0] = ((double) (m1 + m2 - y_plus - y_minus) * (double) m1) / (double) (m1 + m2);
        expectedValues[1][1] = ((double) (m1 + m2 - y_plus - y_minus) * (double) m2) / (double) (m1 + m2);

        double x2 = 0;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (expectedValues[i][j] != 0) {
                    x2 = x2
                            + ((expectedValues[i][j] - observedValues[i][j])
                            * (expectedValues[i][j] - observedValues[i][j])) / expectedValues[i][j];
                }
            }
        }
        double pValue = x2;
        return pValue;
    }

    /**
     * odds ratio measure
     */
    public static double odds_ratio(int y_plus, int y_minus, int m1, int m2) {

        /**
         * Continuity correction for sparse tables: This determines the number
         * that is added to each count in a table that contains zero in any cell
         * where a zero would prevent the calculation of the main effect or its
         * variance - for example the odds ratio of a four-fold table (a, b, c,
         * d) is the ratio of cross-products, (a*d)/(b*c), therefore a zero b or
         * c would make it impossible to calculate the odds ratio. The
         * traditional work-around has been to add 0.5 to each count in the
         * table – the so-called 'Haldane correction'. Agresti (1996) recommends
         * adding smaller constants - you may enter your choice. Very small
         * constants bring the Mantel-Haenszel estimate of the pooled odds ratio
         * very close to the conditional maximum likelihood estimate.
         *
         *
         */
        if (y_minus == 0 | m1 == y_plus) {
            return ((double) (y_plus + 0.5) * (double) (m2 - y_minus))
                    / ((double) (y_minus + 0.5) * (double) (m1 - y_plus + 0.5));
        }
        return ((double) y_plus * (double) (m2 - y_minus)) / ((double) y_minus * (double) (m1 - y_plus));
    }

    /**
     * transform the log odds ratio into z-score, then the z-score follows N(0,1)
     */
    public static double z_score(int y_plus, int y_minus, int m1, int m2) {

        /**
         * Log odds ratio
         */
        double lor = Math.log(odds_ratio(y_plus, y_minus, m1, m2));

        if (lor <= 0) {
            return 1;
        }

        /**
         * The standard error
         */
        double se = 1;
        if (y_minus == 0) {
            se = Math.sqrt(1.0 / (double) (y_plus + 0.5) + 1.0 / (double) (y_minus + 0.5)
                    + 1.0 / (double) (m1 - y_plus + 0.5) + 1.0 / (double) (m2 - y_minus + 0.5));
        } else {
            se = Math.sqrt(1.0 / (double) y_plus + 1.0 / (double) y_minus
                    + 1.0 / (double) (m1 - y_plus) + 1.0 / (double) (m2 - y_minus));
        }

        return lor / se;
    }

//    public static double pvalue(double zscore) {
//
//        double value = 1;
//        try {
//            NormalDistribution distribution = new NormalDistributionImpl(0, 1);
//            DecimalFormat df = new DecimalFormat("0.000000000000");
//            value = distribution.cumulativeProbability(zscore);
//            value = 1 - new Double(df.format(value)).doubleValue();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return value;
//    }

    /**
     * Returns the contents of motif as a string.
     *
     * @param instances contains the relevant header information
     * @return string describing the item set
     */
    public String toString() {

        String text = "";

        for (int i = 0; i < m_items.length; i++) {
            text = text + m_items[i];
        }

        /*
         * text = text +"\t"+"Phospeptides:"+m_counter_foreground+
         * "\t"+"non-Phospeptides:"+m_counter_background+ "\t"+"p
         * value:"+m_significanceValue+ "\t"+"z-value:"+this.m_zscore+
                         "\t"+this.m_index.size();
         */

        //"\t"+"UpperBound:"+m_upperBound;

        return text.toString();
    }

    public String getMotifAsAString() {
        String text = "";

        for (int i = 0; i < m_items.length; i++) {
            if (i == (m_items.length - 1) / 2) {
                text = text + "[p" + m_items[i] + "]";
            } else {
                text = text + m_items[i];
            }
        }

        //text = PhosPhAtProcessing.replaceAll(text,"_", "#");

        return text.toString();
    }
    
    public void removeLastElement(){
    	int length = m_index.size();
    	int lastElement = (Integer)m_index.lastElement();
    	m_items[lastElement] = "_";
    	m_index.removeElementAt(length-1);
    	
    }
    
    public boolean isEqual(Motif a){
    	if(a.getMotifAsAString().equals(this.getMotifAsAString()))
    		return true;
    	else
    		return false;
    }
    
    public Motif getSubMotif(int index, int m_length, String m_phosAA){
    	Motif subMotif = new Motif(m_length, m_phosAA);
    	for(int i =0 ;i<this.m_index.size();i++){
    		if (i==index)
    			continue;
    		int preIndex = (Integer) this.m_index.elementAt(i);
    		String preItem = (String) this.m_items[preIndex];
    		subMotif.m_index.addElement(preIndex);
    		subMotif.m_items[preIndex] = preItem;
    	}
    	return subMotif;
    }

    public String getFGCountAsAString() {
        return new Double(this.m_counter_foreground).toString();
    }

    public String getBGCountAsAString() {
        return new Double(this.m_counter_background).toString();
    }

    public String getZscoreAsAString() {
        return new Double(this.global_significanceValue).toString();
    }

    public String getPvalueAsAString() {
        return new Double(this.local_significanceValue).toString();
    }
}
