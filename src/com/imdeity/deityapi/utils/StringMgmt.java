package com.imdeity.deityapi.utils;

import java.util.List;

/**
 * Useful functions related to strings, or arrays of them.
 */

public class StringMgmt {
    
    /**
     * If a list contains a string, case insensitive
     * 
     * @param arr
     * @param str
     * @return
     */
    public boolean containsIgnoreCase(List<String> arr, String str) {
        for (String s : arr) {
            if (s.equalsIgnoreCase(str)) { return true; }
        }
        return false;
    }
    
    /**
     * Returns the number of changes needed to turn one string into another
     * 
     * @param s
     * @param t
     * @return
     */
    public int getLevenshteinDistance(String s, String t) {
        if ((s == null) || (t == null)) { throw new IllegalArgumentException("Strings must not be null"); }
        
        /*
         * The difference between this impl. and the previous is that, rather
         * than creating and retaining a matrix of size s.length()+1 by
         * t.length()+1, we maintain two single-dimensional arrays of length
         * s.length()+1. The first, d, is the 'current working' distance array
         * that maintains the newest distance cost counts as we iterate through
         * the characters of String s. Each time we increment the index of
         * String t we are comparing, d is copied to p, the second int[]. Doing
         * so allows us to retain the previous cost counts as required by the
         * algorithm (taking the minimum of the cost count to the left, up one,
         * and diagonally up and to the left of the current cost count being
         * calculated). (Note that the arrays aren't really copied anymore, just
         * switched...this is clearly much better than cloning an array or doing
         * a System.arraycopy() each time through the outer loop.) Effectively,
         * the difference between the two implementations is this one does not
         * cause an out of memory condition when calculating the LD over two
         * very large strings.
         */

        int n = s.length(); // length of s
        int m = t.length(); // length of t
        
        if (n == 0) {
            return m;
        } else if (m == 0) { return n; }
        
        int p[] = new int[n + 1]; // 'previous' cost array, horizontally
        int d[] = new int[n + 1]; // cost array, horizontally
        int _d[]; // placeholder to assist in swapping p and d
        
        // indexes into strings s and t
        int i; // iterates through s
        int j; // iterates through t
        
        char t_j; // jth character of t
        
        int cost; // cost
        
        for (i = 0; i <= n; ++i) {
            p[i] = i;
        }
        
        for (j = 1; j <= m; ++j) {
            t_j = t.charAt(j - 1);
            d[0] = j;
            
            for (i = 1; i <= n; ++i) {
                cost = s.charAt(i - 1) == t_j ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left
                // and up +cost
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
            }
            
            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }
        
        // our last action in the above loop was to switch d and p, so p now
        // actually has the most recent cost counts
        return p[n];
    }
    
    /**
     * Joins a list with a space
     * 
     * @param arr
     * @return
     */
    public String join(List<String> arr) {
        return this.join(arr, " ");
    }
    
    /**
     * Joins a string with a specified seperator
     * 
     * @param arr
     * @param separator
     * @return
     */
    public String join(List<String> arr, String separator) {
        if ((arr == null) || (arr.size() == 0)) { return ""; }
        String out = arr.get(0).toString();
        for (int i = 1; i < arr.size(); i++) {
            out += separator + arr.get(i).toString().trim();
        }
        return out;
    }
    
    /**
     * Joins an object array with a space
     * 
     * @param arr
     * @return
     */
    public String join(Object[] arr) {
        return this.join(arr, " ");
    }
    
    /**
     * Joins an object array with a seperator
     * 
     * @param arr
     * @param separator
     * @return
     */
    public String join(Object[] arr, String separator) {
        
        if (arr.length == 0) { return ""; }
        String out = arr[0].toString();
        for (int i = 1; i < arr.length; i++) {
            out += separator + arr[i].toString().trim();
        }
        return out;
    }
    
    /**
     * Shortens the string to fit in the specified size with an elipse "..." at
     * the end.
     * 
     * @return the shortened string
     */
    public String maxLength(String str, int length) {
        if (str.length() < length) {
            return str;
        } else if (length > 3) {
            return str.substring(0, length - 3) + "...";
        } else {
            throw new UnsupportedOperationException("Minimum length of 3 characters.");
        }
    }
    
    /**
     * Removes args from the array
     * 
     * @param arr
     * @param startFromIndex
     * @return
     */
    public String[] remArgs(String[] arr, int startFromIndex) {
        if (arr.length == 0) {
            return arr;
        } else if (arr.length < startFromIndex) {
            return new String[0];
        } else {
            String[] newSplit = new String[arr.length - startFromIndex];
            System.arraycopy(arr, startFromIndex, newSplit, 0, arr.length - startFromIndex);
            return newSplit;
        }
    }
    
    /**
     * Removes the first arg from an array
     * 
     * @param arr
     * @return
     */
    public String[] remFirstArg(String[] arr) {
        return this.remArgs(arr, 1);
    }
}
