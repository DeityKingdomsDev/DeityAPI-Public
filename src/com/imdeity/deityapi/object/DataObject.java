package com.imdeity.deityapi.object;

import java.util.ArrayList;
import java.util.List;

import com.imdeity.deityapi.records.Database;

/**
 * Handles interaction with MySQL and other data functions
 * 
 * @author vanZeben
 */
public class DataObject {
    
    /**
     * Database class
     */
    private Database mySQL = null;
    
    private static final int NUM_ELEMENTS_PER_PAGE = 5;
    
    public DataObject() throws Exception {
        mySQL = new Database();
    }
    
    /**
     * API call to deal with MySQL
     * 
     * @return
     */
    public Database getMySQL() {
        return mySQL;
    }
    
    /**
     * Data pages utility function
     * 
     * @return
     */
    public PaginationUtils getPaginationUtils() {
        return new PaginationUtils();
    }
    
    public class PaginationUtils {
        public List<String> paginateInput(List<String> inputLines, int currentSelectedPage) {
            List<String> output = new ArrayList<String>();
            // pagination
            int numPages = getNumPages(inputLines);
            currentSelectedPage = getCurrentPage(currentSelectedPage, numPages);
            
            int numStartElementOnCurrentPage = ((currentSelectedPage - 1) * NUM_ELEMENTS_PER_PAGE);
            int numMaxElemetsOnCurrentPage = (((currentSelectedPage) * NUM_ELEMENTS_PER_PAGE) < inputLines.size() ? ((currentSelectedPage) * NUM_ELEMENTS_PER_PAGE) : inputLines.size());
            
            // content
            for (int i = numStartElementOnCurrentPage; i < numMaxElemetsOnCurrentPage; i++) {
                output.add(inputLines.get(i));
            }
            return output;
        }
        
        public int getCurrentPage(int currentSelectedPage, int numPages) {
            if (currentSelectedPage < 1) {
                currentSelectedPage = 1;
            } else if (currentSelectedPage > numPages) {
                currentSelectedPage = numPages;
            }
            return currentSelectedPage;
        }
        
        public int getNumPages(List<String> inputLines) {
            int numPages = 0;
            if (inputLines.size() % NUM_ELEMENTS_PER_PAGE != 0) {
                for (int i = 0; i < 5; i++) {
                    if ((inputLines.size() + i) % NUM_ELEMENTS_PER_PAGE == 0) {
                        numPages = ((inputLines.size() + i) / NUM_ELEMENTS_PER_PAGE);
                    }
                }
            } else {
                numPages = (inputLines.size() / NUM_ELEMENTS_PER_PAGE);
            }
            return numPages;
        }
    }
}
