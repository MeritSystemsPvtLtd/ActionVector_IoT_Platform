/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.adapter.etl.ETLAdapter;

/**
 *
 * @author Abraham
 */
public class TokenParser {

    public TokenParser() {
    }

    public static String trimmedToken(String[] tok, int index, String defVal) {
        String tmp;
        if ( isValidToken(tok, index) ){
            tmp = tok[index].trim();
            return ((tmp.length() == 0) ? defVal : tmp);
        }
        return defVal;
    }

    public static String replaceTrimToken(String[] tok, int index, char oldCh, char newCh, String defVal) {
        String tmp;
        if ( isValidToken(tok, index) ){
            tmp = tok[index].replace(oldCh, newCh).trim();
            return ((tmp.length() == 0) ? defVal : tmp);
        }
        return defVal;
    }

    public static boolean isValidToken(String[] tok, int index) {
        if ( (tok.length > index) && (tok[index] != null) && (tok[index].length() != 0) )
            return true;
        else
            return false;
    }
}
