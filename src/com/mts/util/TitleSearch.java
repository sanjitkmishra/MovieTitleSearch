package com.mts.util;
import com.sun.istack.internal.NotNull;

import java.io.*;
import java.util.*;

/**
 * Given a search String, the class is responsible for finding list of movie
 * titles matching 30% of search string as available in a specified file.
 */
public class TitleSearch {
    private static final int MATCHING_THRESHOLD = 30;

    /**
     *  index variable is an InvertedIndex which is a Map of string and Integer set,
     *  String represents the word in the text document, and Integer set stores the
     *  line number where it exists.
     *
     *  lines is a List whose index is same as the file's line number. It holds the
     *  line strings.
     *
     *  (Note: If we want to handle multiple files, then we have to change the Lines
     *  from List to a Map where we have to store the file and line number combination)
     */
    private HashMap<String, HashSet<Integer>> index;
    private List<String> lines;

    public TitleSearch(){
        index = new HashMap<String, HashSet<Integer>>();
        lines = new ArrayList<>();
    }

    /**
     * This builds an index for the fileName provided and fill the lines.
     *
     * @param fileName
     * @throws IOException
     */
    public void buildIndex(@NotNull String fileName) throws IOException{
        try(BufferedReader file = new BufferedReader(new FileReader(fileName)))
        {
            int lineNumber = 0;
            String line;
            while( (line = file.readLine()) != null) {
                String[] words = line.split("\\W+");
                for(String word : words){
                    word = word.toLowerCase();
                    if (!index.containsKey(word)) {
                        index.put(word, new HashSet<Integer>());
                    }
                    index.get(word).add(lineNumber);
                }
                lines.add(line);
                lineNumber++;
            }
        } catch (IOException e){
            System.err.println("File "+fileName+" not found. Skip it");
            throw e;
        }
    }

    /**
     * This method takes the search string, and for each word in the string it
     * gets the line number from the index.
     *
     * From the line number we get the whole line and compare what percentage of
     * the line is matching the search phrase. Currently the matching % is hardcoded
     * to 30%. So it return all lines passing the matching criteria.
     *
     * @param phrase
     * @return
     */
    public List<String> find(@NotNull String phrase) {
        // Checking if search string is null or empty
        if(phrase == null || phrase.isEmpty())
        {
            return new ArrayList<>();
        }

        String[] words = phrase.split("\\W+");
        HashSet<Integer> res = new HashSet<Integer>();
        for(String word: words) {
            HashSet<Integer> indexSet = index.get(word.toLowerCase());
            if(indexSet != null && !indexSet.isEmpty()) {
                res.addAll(indexSet);
            }
        }

        if(res.isEmpty()) {
            System.err.println("Not found");
            return new ArrayList<>();
        }

        List<String> foundTitles = new ArrayList<>();
        for(int num : res){
            String title = lines.get(num);
            System.out.println("\t"+ title);
            int percentage =  calculatePercentageMatch(title, phrase);
            if(percentage > MATCHING_THRESHOLD)
            {
                foundTitles.add(title);
            }
        }
        return foundTitles;
    }

    /**
     * Building map with title and search string to compare
     * @param title
     * @param searchString
     * @return percentage of match
     */
    private int calculatePercentageMatch(String title, String searchString)
    {
        HashMap<String, Integer> titleCounts = countWords(title);
        HashMap<String, Integer> searchCounts = countWords(searchString);

        int titleTotal = 0, searchTotal = 0;
        for(Integer i : titleCounts.values()) {
            titleTotal += i;
        }

        // iterate over words in title, find the number of matching words in searchString
        for(Map.Entry<String, Integer> entry : titleCounts.entrySet()) {
            if(searchCounts.containsKey(entry.getKey())) {
                if(searchCounts.get(entry.getKey()) >= entry.getValue()) {
                    searchTotal += entry.getValue();
                } else {
                    searchTotal += searchCounts.get(entry.getKey());
                }
            }
        }

        return searchTotal * 100 / titleTotal;
    }

    // Adding word count into a map
    private HashMap<String, Integer> countWords(String str) {
        HashMap<String, Integer> counts = new HashMap<String, Integer>();
        for(String s : str.split("\\s+")) {
            if(!s.isEmpty()) {
                if(counts.containsKey(s)) {
                    counts.put(s, counts.get(s) + 1);
                } else {
                    counts.put(s, 1);
                }
            }
        }
        return counts;
    }
}