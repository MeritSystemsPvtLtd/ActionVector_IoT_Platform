/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.prokosha.ganglia.dataparser;

import java.util.HashMap;

/**
 *
 * @author Abraham
 */
public interface GangliaNodeListener {

    public void nodeStart(String node, String nodeName, HashMap avList);
    public void nodeEnd(String node);

}
