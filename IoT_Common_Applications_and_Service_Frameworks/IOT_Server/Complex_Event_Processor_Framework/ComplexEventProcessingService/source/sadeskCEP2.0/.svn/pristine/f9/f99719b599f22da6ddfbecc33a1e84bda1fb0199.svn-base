package com.prokosha.sadeskCeP;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;

public abstract class Comparator implements Serializable
{

    public static Logger log = Logger.getLogger(Comparator.class.getName());
    private static final long serialVersionUID = 4329030593418015332L;
    private static final String RESULT_FILE_NAME = "/root/Desktop/resultCep.txt";
    private int numberOfTestCasesPassed = 0;
    private int numberOfTestCasesFailed = 0;
    FileOutputStream fos = null;

    public Comparator()
    {
        super();
    }

    public void compareObject(Object obj) throws IOException
    {
        boolean result = compare(obj);
        log.info("The Result after comparing objects in Comparator is: " + result);

        if (result)
        {
            numberOfTestCasesPassed++;
            writeResultToFile("Passed: " + obj+"\n");
        }

        if (!result)
        {
            numberOfTestCasesFailed++;
            writeResultToFile("Failed: " + obj+"\n");
        }

    }

    public void writeResultToFile(String data) throws IOException
    {

        if (fos == null)
        {
            fos = new FileOutputStream(RESULT_FILE_NAME, true);
        }

        fos.write(data.getBytes());
        fos.flush();
    }

    public abstract boolean compare(Object obj);

    public void closeTask() throws IOException
    {
        log.info("Closing Task Details in Comparator.");
        try{
        writeResultToFile("Total Tasks:" + (numberOfTestCasesPassed + numberOfTestCasesFailed)+"\n");
        writeResultToFile("Passed:" + (numberOfTestCasesPassed)+"\n");
        writeResultToFile("Failed:" + (numberOfTestCasesFailed)+"\n");
       }catch(IOException e)
       {e.printStackTrace();}

    }
}
