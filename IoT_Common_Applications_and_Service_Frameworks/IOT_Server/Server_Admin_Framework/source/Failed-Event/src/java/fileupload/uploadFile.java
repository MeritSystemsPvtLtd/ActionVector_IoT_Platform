/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileupload;

import java.io.File;
import org.apache.commons.io.FileUtils;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Date;

/**
 *
 * @author ananddw
 */
public class uploadFile {

    private File myFile;
    private String myFileContentType;
    private String myFileFileName;
    private String destPath;

    public String execute() throws IOException {
        
        /* Copy file to a safe location */

        File f = null;
        FileInputStream inp = null;
        FileWriter fw = null;
        byte[] bf = null;
        Date date = null;
        File destFile = null;
        File file1 = null;
	File folderPath=null;
        try {

            destPath = System.getProperty("user.home") + System.getProperty("file.separator") + "SA-DeskFailedEvents";
            //create a directory if not exist
            folderPath=new File(destPath);
	    if(!folderPath.isDirectory()){
            folderPath.mkdir();
            }
            destFile = new File(destPath, myFileFileName);
            file1 = new File(destFile.toString());
            boolean b = false;
            date = new Date();
            if (!(file1.exists())) {
                b = file1.createNewFile();

            }
            if (b) {
                FileUtils.copyFile(myFile, destFile);
            } else {
                String fileContent = "";

                f = new File(file1.toString());

                inp = new FileInputStream(f);

                bf = new byte[(int) f.length()];

                inp.read(bf);

                fileContent = new String(bf, "UTF-8");

                String filename = destFile.toString() + date;
                fw = new FileWriter(filename, true);
                fw.write(fileContent);
                fw.close();
                FileUtils.copyFile(myFile, destFile);
            }

            return "success";
        } catch (IOException e) {
            return "error";

        } finally {
            f = null;
            if (inp != null) {
                inp.close();
            }
            inp = null;
            if (fw != null) {
                fw.close();
            }
            fw = null;
            bf = null;
            date = null;
            file1 = null;
            destFile = null;
        }

    }

    public File getMyFile() {
        return myFile;
    }

    public void setMyFile(File myFile) {
        this.myFile = myFile;
    }

    public String getMyFileContentType() {
        return myFileContentType;
    }

    public void setMyFileContentType(String myFileContentType) {
        this.myFileContentType = myFileContentType;
    }

    public String getMyFileFileName() {
        return myFileFileName;
    }

    public void setMyFileFileName(String myFileFileName) {
        this.myFileFileName = myFileFileName;
    }
}
