package com.fpgrowth;
import java.io.BufferedReader;
import java.io.FileWriter;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class FPAlg {
    protected static String m_phosAA = "S";
    protected static int m_length = 21;
    public int m_numPhosPeptides;
    public int m_numNonPhosPeptides;
    public String[] AA = {"G", "A", "S", "P", "V", "T", "C", "L", "I", "X",
        "N", "O", "B", "D", "Q", "K", "Z", "E", "M", "H",
        "F", "Y", "R", "W"};
    String tuple = "";
    public Map<Motif, FastVector> motif_Foreground_dataset = new HashMap();//<motif,F'>
    public Map<Motif, FastVector> motif_Background_dataset = new HashMap();//<motif,B'>
    /**
     * Load the foreground data and background data
     */
    FastVector phosData = new FastVector();
    FastVector nonPhosData = new FastVector();
    FastVector finalResult = new FastVector();

    public void loadFrgData(String foreground) {
        try {
            File pData = new File(foreground);
            BufferedReader fg = new BufferedReader(new FileReader(pData));
            tuple = fg.readLine();
            phosData.addElement(tuple);

            /**
             * Initialize the global variables here
             */
            m_length = tuple.length();
            m_phosAA = tuple.substring((m_length - 1) / 2, (m_length - 1) / 2 + 1);
            while ((tuple = fg.readLine()) != null) {
                phosData.addElement(tuple);
            }
            m_numPhosPeptides = phosData.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadBkgData(String background) {
        try {
            File npData = new File(background);
            BufferedReader bg = new BufferedReader(new FileReader(npData));
            while ((tuple = bg.readLine()) != null) {
                nonPhosData.addElement(tuple);
            }
            m_numNonPhosPeptides = nonPhosData.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that finds all phosphorylation motifs for the given foreground set
     * and background set.
     *
     * @param the foreground data, background data, the threshold
     */
    public FastVector findMotifs(String foreground, String background, double minSupp, double minSig) {

        FastVector result = new FastVector();
        loadFrgData(foreground);
        loadBkgData(background);
        System.out.println(m_numPhosPeptides + "\t" + m_numNonPhosPeptides);
        int minCount = (int) (m_numPhosPeptides * minSupp);
        if (m_numPhosPeptides * minSupp > minCount) {
            minCount++;
        }

        /**
         * Find all phosphorylation motifs in an Apriori manner
         */
        double timeteststart = System.currentTimeMillis();
        FastVector kMinusOneSets, kSets;
        double start = System.currentTimeMillis();
        // We find all frequent itemsets on the phosphorylation data first
        kSets = singletons(minCount, minSig);
        double tbegin = System.currentTimeMillis();
        int L = 0;
        int countthetotal = 0;
        do {
            countthetotal += kSets.size();
            result.addElement(kSets);
            kMinusOneSets = kSets;
            kSets = this.mergeAllItemSets(kMinusOneSets, L, minCount, minSig);
            L++;
        } while (kSets.size() > 0);

        double tend = System.currentTimeMillis();
        int countfre = 0;
        for (int i = 0; i < result.size(); i++) {
            countfre += ((FastVector) result.elementAt(i)).size();
        }

        System.out.println("Frequent motif size:" + countfre);


        // Then, we calculate the odd ratio and p value using background data
        FastVector newResult = new FastVector();
        for (int i = 0; i < result.size(); i++) {
            FastVector ls = (FastVector) result.elementAt(i);
            ls = this.deleteMotifs(ls, minCount, minSig);
            if (ls.size() > 0) {
                newResult.addElement(ls);
            }
        }
        double tend2 = System.currentTimeMillis();
        System.out.println("time " + (tend2 - start));

        int countAll = 0;
        for (int i = 0; i < newResult.size(); i++) {
            countAll += ((FastVector) newResult.elementAt(i)).size();
        }
        System.out.println(countAll);


        try {
            FileWriter fw1 = new FileWriter("DetailedFinalResultByC-Motif");
            FileWriter fw2 = new FileWriter("ResultByC-Motif");
            for (int i = 0; i < newResult.size(); i++) {
                FastVector fv = (FastVector) newResult.elementAt(i);
                fw1.write("****************" + fv.size() + "****************************\n");
                for (int j = 0; j < fv.size(); j++) {
                    fw1.write(((Motif) (fv.elementAt(j))).toString() + ":" + ((Motif) (fv.elementAt(j))).global_significanceValue + "---" + ((Motif) (fv.elementAt(j))).local_significanceValue + "\n");
                    String m_items = "";
                    String[] ms = ((Motif) (fv.elementAt(j))).m_items;
                    for (int k = 0; k < ms.length; k++) {
                        m_items += ms[k];
                    }
                    fw2.write(m_items + "\n");

                }
            }
            fw1.close();
            fw2.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return newResult;
    }

    /**
     * return the foreground dataset contain a certain motif
     */
    public FastVector findForeground(Motif m) {
        FastVector foreResult = new FastVector();
        for (int i = 0; i < phosData.size(); i++) {
            String instance = (String) phosData.elementAt(i);
            if (m.containedBy(instance)) {
                foreResult.addElement(i);
            }
        }
        return foreResult;

    }

    /**
     * return the background dataset that contain a certain motif
     */
    public FastVector findBackground(Motif m) {
        FastVector backResult = new FastVector();
        for (int i = 0; i < nonPhosData.size(); i++) {
            String instance = (String) nonPhosData.elementAt(i);
            if (m.containedBy(instance)) {
                backResult.addElement(i);
            }
        }
        return backResult;
    }

    /**
     * Generate all motifs of of singletons.
     */
    public FastVector singletons(int minCount, double minSig) {
        FastVector setOfSingletons = new FastVector();
        Motif current;
        FastVector dataSet = new FastVector();
        FastVector dataSet1 = new FastVector();
        for (int i = 0; i < m_length; i++) {
            if (i != (m_length - 1) / 2) {
                for (int j = 0; j < AA.length; j++) {
                    current = new Motif(m_length, m_phosAA);
                    current.m_items[i] = AA[j];
                    current.m_index.addElement(new Integer(i));

                    dataSet = findForeground(current);
                    if (dataSet.size() >= minCount) {
                        dataSet1 = findBackground(current);
                        current.local_significanceValue = current.upDateSignificanceValue(dataSet.size(), dataSet1.size(), m_numPhosPeptides, m_numNonPhosPeptides);
                        current.global_significanceValue = current.local_significanceValue;
                        motif_Foreground_dataset.put(current, dataSet);//<current,F'>
                        motif_Background_dataset.put(current, dataSet1);//<current,B'>

                        setOfSingletons.addElement(current);
                    }
                }
            }
        }

        return setOfSingletons;
    }

    /**
     * Deletes all motifs that don't have minimum support threshold and the p
     * value is larger than the threshold.
     *
     * @return the reduced set of item sets
     */
    public FastVector deleteMotifs(FastVector itemSets, int minCount, double threshold) {
        FastVector fore = new FastVector();
        FastVector back = new FastVector();
        FastVector newVector = new FastVector(itemSets.size());
        for (int i = 0; i < itemSets.size(); i++) {
            Motif current = (Motif) itemSets.elementAt(i);
            if (current.global_significanceValue >= threshold && current.local_significanceValue >= threshold) {
                newVector.addElement(current);
            }
        }
        return newVector;
    }

    /**
     * Merges all motifs in the set of (k-1)-motifs to create the (k)-motifs and
     * updates the counters.
     */
    public FastVector mergeAllItemSets(FastVector itemSets, int level, int minCount, double minSig) {
        FastVector newVector = new FastVector(itemSets.size());
        Motif result;
        FastVector result_Foreground = new FastVector();
        FastVector result_Background = new FastVector();

        int numFound, k;
        int length = itemSets.size();
        for (int i = 0; i < length; i++) {
            Motif first = (Motif) itemSets.elementAt(i);
            out:
            for (int j = i + 1; j < length; j++) {
                Motif second = (Motif) itemSets.elementAt(j);
                result = new Motif(m_length, m_phosAA);

                // Find and copy common prefix of size 'level'
                numFound = 0;
                k = 0;
                while (numFound < level) {
                    if (first.m_items[k].equals(second.m_items[k])) {
                        if ((!first.m_items[k].equals("_")) && (k != (m_length - 1) / 2)) {
                            numFound++;
                            result.m_index.addElement(new Integer(k));
                        }
                        result.m_items[k] = first.m_items[k];
                    } else {
                        break out;
                    }
                    k++;
                }
                // Check difference
                while (k < first.m_items.length) {
                    if ((!first.m_items[k].equals("_")) && (!second.m_items[k].equals("_"))) {
                        if (k != (m_length - 1) / 2) {
                            break;
                        } else {
                            result.m_items[k] = first.m_items[k];
                        }
                    } else {
                        if (!first.m_items[k].equals("_")) {
                            result.m_items[k] = first.m_items[k];
                        } else {
                            result.m_items[k] = second.m_items[k];
                        }

                        if ((!first.m_items[k].equals("_")) || (!second.m_items[k].equals("_"))) {
                            result.m_index.addElement(new Integer(k));
                        }

                    }
                    k++;
                }
                if (k == first.m_items.length) {
                    FastVector firstOfForeground = iteratorMap(first, motif_Foreground_dataset);
                    FastVector secondOfForeground = iteratorMap(second, motif_Foreground_dataset);
                    result_Foreground = intersection(firstOfForeground, secondOfForeground);
                    if (result_Foreground.size() >= minCount) {
                        newVector.addElement(result);
                        motif_Foreground_dataset.put(result, result_Foreground);
                        FastVector firstOfBackground = iteratorMap(first, motif_Background_dataset);
                        FastVector secondOfBackground = iteratorMap(second, motif_Background_dataset);
                        result_Background = intersection(firstOfBackground, secondOfBackground);
                        motif_Background_dataset.put(result, result_Background);
                        result.global_significanceValue = result.upDateSignificanceValue(result_Foreground.size(), result_Background.size(), m_numPhosPeptides, m_numNonPhosPeptides);



                        double significanceValue;
                        significanceValue = result.upDateSignificanceValue(result_Foreground.size(), result_Background.size(), firstOfForeground.size(), firstOfBackground.size());
                        if (significanceValue < result.local_significanceValue) {
                            result.local_significanceValue = significanceValue;
                        }

                        significanceValue = result.upDateSignificanceValue(result_Foreground.size(), result_Background.size(), secondOfForeground.size(), secondOfBackground.size());
                        if (significanceValue < result.local_significanceValue) {
                            result.local_significanceValue = significanceValue;
                        }


                        //Check the other sub-motifs to calculate the corresponding local significance values  
                        for (int ff = 0; ff < result.m_index.size() - 2; ff++) {
                            int position = ((Integer) result.m_index.elementAt(ff)).intValue();
                            Motif temp = new Motif(m_length, m_phosAA);
                            for (int tt = 0; tt < m_length; tt++) {
                                if (tt == position || result.m_items[tt].equals("_") || tt == (m_length - 1) / 2) {
                                    continue;
                                } else {
                                    temp.m_items[tt] = result.m_items[tt];
                                    temp.m_index.addElement(new Integer(tt));
                                }
                            }
                            FastVector temp_foreground = iteratorMap(temp, motif_Foreground_dataset);
                            FastVector temp_background = iteratorMap(temp, motif_Background_dataset);
                            significanceValue = result.upDateSignificanceValue(result_Foreground.size(), result_Background.size(), temp_foreground.size(), temp_background.size());

                            if (significanceValue < result.local_significanceValue) {
                                result.local_significanceValue = significanceValue;


                            }


                        }
                    }
                }
            }
        }


        System.out.println("**************************************");
        return newVector;
    }

    /**
     * iterator map to find the sub-motif induced forground and background
     */
    public FastVector iteratorMap(Motif m, Map<Motif, FastVector> map) {
        FastVector dataSet = new FastVector();
        Set set = map.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry mapentry = (Map.Entry) iterator.next();
            if (Equal(m, (Motif) mapentry.getKey())) {
                dataSet = (FastVector) mapentry.getValue();
                break;
            }
        }
        return dataSet;
    }

    public boolean Equal(Motif first, Motif second) {
        boolean it = false;
        if (first.m_index.size() != second.m_index.size()) {
            return it;
        } else {
            for (int i = 0; i < first.m_index.size(); i++) {
                String temp1 = first.m_items[(Integer) first.m_index.elementAt(i)];
                String temp2 = second.m_items[(Integer) first.m_index.elementAt(i)];
                if (!temp1.equals(temp2)) {
                    return it;
                }
            }
            it = true;
            return it;
        }
    }

    public FastVector intersection(FastVector m, FastVector n) {
        FastVector setOfIntersection = new FastVector();
        int outer = 0, inner = 0;
        out:
        while (outer < m.size()) {
            int first = (Integer) m.elementAt(outer);
            in:
            while (inner < n.size()) {
                int second = (Integer) n.elementAt(inner);
                if (first < second) {
                    outer++;
                    break in;
                } else if (first > second) {
                    inner++;
                    continue in;
                } else {
                    outer++;
                    inner++;
                    setOfIntersection.addElement(first);
                    continue out;

                }
            }
            if (inner == n.size()) {
                break out;
            }
        }

        return setOfIntersection;
    }

//    public static void main(String[] args) {
//
//
//        ITree itr; // 存储所有项集的树
//        File f;
//        FPTree pt = new FPTree();
//        CreateFPTree c = new CreateFPTree();
//        int support = 5000;
//        double gSigThreshold = 0.3;
//        double lSigThreshold = 0.3;
//        String fileName = "/home/ymcdull/desktop/1.pep";
////       String fileName = "/home/ymcdull/workspace/DataMiningFPGrowth/mushroom.data";
//        
//        // 对background文件处理：将数据存入到Link<Link<String>> transRecordBackground中
//        String transFile2 = "/home/ymcdull/desktop/2.pep";
////       String transFile2 = "/home/ymcdull/workspace/DataMiningFPGrowth/datafile.dat";
//        List<List<String>> transRecordsBg = readTransData(transFile2);
//
//        FastVector fv = new FastVector();
//        try {
//            if (support < 0) {
//                System.out.println("支持度阈值不能是负值");
//                System.exit(1);
//            }
//        } catch (Exception e) {
//            System.out.println("参数输入错误");
//            System.exit(1);
//        }
//        c.SetFPTree(pt);
//
//        // FastVector result = new FastVector();
//        loadFrgData(foreground);
//        loadBkgData(background);
//        f = new File(fileName);
//        double programStart = System.currentTimeMillis();
//        System.out.println(m_numPhosPeptides + "\t" + m_numNonPhosPeptides);
//        int minCount = (int) (m_numPhosPeptides * minSupp);
//        if (m_numPhosPeptides * minSupp > minCount) {
//            minCount++;
//        }
//
//
//
//
//
//
//        try {
//            // Change the form of data, and save them to phosData and
//            // nonPhosData
//            BufferedReader fg = new BufferedReader(new FileReader(f));
//            String s = fg.readLine();
//
//            while (s != null) {
//                // System.out.println(qop++);
//                // slabel=new String(s);
//                fv.addElement(s);
//                s = fg.readLine();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        FPGrowth fpg = new FPGrowth();
//
//        c.start(fv); // 从事务文件f中产生FP_Tree pt
//        itr = new ITree();
//        fpg.Start(pt, itr, support, gSigThreshold, lSigThreshold, fv,
//                transRecordsBg); // 用FP-Growth算法产生项集存储在项集树（ITree）itr
//        System.out.println("----频繁项集----");
//
//        // itr.print(itr.head, new Stack());
//
//        // Test for print all patterns met the requirements of support and
//        // local+global significances
//        for (int i = 0; i < fpg.resultPatterns.size(); i++) {
//            System.out.print((i + 1) + ":");
//            LinkedList<String> LItem = fpg.resultPatterns.get(i);
//            int value = fpg.resultCount.get(i);
//            for (String item : LItem) {
//                System.out.print(item + "-");
//            }
//            System.out.println(value);
//        }
//        double programEnd = System.currentTimeMillis();
//        System.out.println("The time is:" + (programEnd - programStart));
//
//
//
//
//
//
//
//
//        FPAlg alg = new FPAlg();
//
//        double startTime = System.currentTimeMillis();
//        String foreground = "/home/ymcdull/desktop/test1.pep";
//        String background = "/home/ymcdull/desktop/test2.pep"; 
//        //String foreground = "db\\segments(Nining21peptides)_P.txt";
//        //String background = "db\\segments(Nining21peptides)_N.txt"; 
////        FastVector res = alg.findMotifs(foreground, background, 0.21, 1.0E-6);
//        FastVector res = alg.findMotifs(foreground, background, 0.01, 0.3);
//        //Vector res = alg.singletons();
//
//        int size = res.size();
//        int total = 0;
//        int count = 0;
//        System.out.println("#Num" + "\t" + "Motif" + "\t" + "P Value");
//        for (int i = 0; i < size; i++) {
//            //System.out.println(((FastVector)res.elementAt(i)).size()); 
//            FastVector ls = (FastVector) res.elementAt(i);
//            total = total + ls.size();
//            for (int j = 0; j < ls.size(); j++) {
//                String str = "" + (count + 1) + "\t";
//                Motif mf = (Motif) ls.elementAt(j);
//
//                str = str + mf.getMotifAsAString() + "\t";
//                str = str + mf.getPvalueAsAString();
//
//                System.out.println(str);
//
//                count++;
//            }
//        }
//
//
//        System.out.println("Number of reported motifs:" + total);
//        double endTime = System.currentTimeMillis();
//        double running_time = (endTime - startTime) / (double) 1000;
//        System.out.println("Running Time:" + running_time);
//
//    }


}
