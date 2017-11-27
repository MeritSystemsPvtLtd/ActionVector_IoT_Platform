
package dbmanager;
import java.io.*;
import java.util.Date;
import javax.servlet.http.*;
import controller.DBConstants;
import org.apache.log4j.Logger;

public final class Logwriter
{

    static String szDebugFile = "";
    static private String szDebugDir = "";
    static int iGIFTLoglevel = 0;
    static String szFile_Separator = System.getProperty("file.separator");
    static File FileDir;
    static File FileDir1;
    static Logger esclog = Logger.getLogger(Logwriter.class.getName());
    static Logger esclog1 = Logger.getLogger("security");

        public Logwriter() {
		super();
	}
   /*   static public Logwriter instance() {
    try{
      if(null == _instance) {
         _instance = new Logwriter();
      }
     
    }
    catch(Exception e){
    e.printStackTrace();
    }
     return _instance;
   }*/


	public static void setDebugFileName(String s,int iLoglevel)
	{
		String szFileDirPath = "";
		try{
			
		szFileDirPath = s+szFile_Separator+"proChara"+szFile_Separator+"Log";
		FileDir = new File(szFileDirPath);
      		if(!(FileDir.exists()))
        	{  
	             FileDir.mkdirs();
 	        }
		szDebugFile = FileDir+szFile_Separator+"ActionVector.log";
		iGIFTLoglevel = iLoglevel;
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	
	
	public static void setReportFileName(String s,int iLoglevel)
	{
		String szFileDirPath = "";
		try{
			
		szFileDirPath = s+szFile_Separator+"gawork"+szFile_Separator+"reports";
		FileDir1 = new File(szFileDirPath);
      		if(!(FileDir1.exists()))
        	{  
	             FileDir1.mkdirs();
 	        }
		//szDebugFile = FileDir+szFile_Separator+"ActionVector.log";
		//iGIFTLoglevel = iLoglevel;
		} catch(Exception e){
			e.printStackTrace();
		}
	}	
	public static void setArchiveLogFile(int iYear,int iDay,int iMonth)
	{
		//System.out.println("The ArchiveLog file is getting called");

		File FileObj = null;
		File FYearFile = null;
		File FArchiveFile = null;
		OutputStream OpStream = null;
		InputStream IpStream = null;

		int iNewMonth = -1;
		String szYearString = "";

		FileObj = new File(szDebugFile);
		iNewMonth = iMonth -1;
		szYearString = "ActionVector."+iYear;

		if(iNewMonth==0)
		{	
			iNewMonth = 12;
		}
		if(FileObj.exists())
		{
			File FNewFile = new File(FileDir+szFile_Separator+"ActionVector.0"+iNewMonth);
			if(!FNewFile.exists())
			{
				FileObj.renameTo(FNewFile);	
			}
		}
		if(iNewMonth==12)
		{
			//System.out.println(" Inside the changing the new Year....");
			FYearFile = new File(FileDir+szFile_Separator+szYearString);
			if(!FYearFile.exists())
			{
				FYearFile.mkdir();
			   	try
			    	{
					for( int j = 1; j <= 12 ; j++)
					{	
						File sourcefile =  new File(FileDir+szFile_Separator+"ActionVector.0"+j);
						File FDestFile = new File(FileDir+szFile_Separator+szYearString+szFile_Separator+"ActionVector.00"+j);
						if(sourcefile.exists())
						{
							sourcefile.renameTo(FDestFile);
						
						}
						/*try
						{
					        	IpStream = new FileInputStream(FileDir+szFile_Separator+"GIFTask.0"+j);
						}
						catch(Exception esource)
						{
							esource.getMessage();
						}
						try
						{
							FArchiveFile = new File(FileDir+szFile_Separator+szYearString+szFile_Separator+"GIFTask.00"+j);
						}
						catch(Exception edest)
						{
							edest.getMessage();
						}

						try
						{
							OpStream = new FileOutputStream(FArchiveFile);
							int i;
							System.out.println(" writing into log file....");
							while((i =IpStream.read()) != -1)
							{	
								OpStream.write(i);
							}
							sourcefile.delete();
						}
						catch(Exception ecopy)
						{
							ecopy.getMessage();
						}*/
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			/*	finally
				{
					try
					{
						OpStream.close();
						IpStream.close();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}*/
			}
		}
	}


	public static void writeDebug(String s)
	{
		writeDebug(s,szDebugFile);
	}
	
	public static void writeDebug(String s , String szLogFile)
	{
		try{
		esclog.info(new Date()+":"+s);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		/*FileOutputStream FOpStream = null;
		File FObj = null;
		System.out.println("LogFile path is   "+szLogFile+"   String s is   "+s);
		try
		{
			FObj = new File(szLogFile);
			if (!FObj.exists())
			{
				FOpStream = new FileOutputStream(FObj);
			}
			else
			{
				FOpStream = new FileOutputStream(szLogFile,true);
			}
			s+="\n";
			FOpStream.write((new Date( System.currentTimeMillis()).toString() + " : " + s).getBytes());
		}
		catch(Exception e)
		{
			System.out.println("Error Writting Debug File :"+e);
		}
		finally
		{
			try
			{
				if (FOpStream != null)
				{
					FOpStream.close();
				}

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}*/
	}
        public static void printLog(String s)
	{
		esclog.info(new Date()+":"+s);
	}
        
        
	public static void writeDebug(String s,HttpSession session)
	{
		writeDebug(s,1,session);
	}
	public static void writeDebug(String s,int iLoglevel)
	{
		if (iLoglevel <= iGIFTLoglevel)
		{
			writeDebug(s,szDebugFile);
		}
	}

	public static void writeDebug(String s ,int iLoglevel,HttpSession session)
	{
		
		try
		{
		if(iLoglevel >=3){
			esclog.debug(new Date()+":"+s);
		}else{
			esclog.info(new Date()+":"+s);
		}
		}catch(Exception ex){
		      ex.printStackTrace();
		}
		/*String szHomeDir="";
		FileOutputStream FOpStream = null;
		String szLogFile ="";
		valuestore ValueStoreObj = null;
		Login usercheck = null;
		String szUserName = "";
		try
		{
			ValueStoreObj = (valuestore) session.getAttribute("directory");
//			System.out.println("ValueStoreObj  :logwriter  :"+ValueStoreObj);
		}
		catch(Exception e)
		{
		}
		try
		{
			usercheck = (Login)session.getAttribute("GAUserCheck");
//			System.out.println("usercheck  :logwriter  :"+usercheck);
		}
		catch(Exception e)
		{
		}
		if(usercheck != null)
		{
			szUserName = usercheck.getUsername();
			szUserName  = szUserName.trim();
		}
		if(ValueStoreObj != null)
		{
			szHomeDir=ValueStoreObj.getHomedir();
			//System.out.println("szHomeDir  :logwriter  :"+szHomeDir+" logLevel:" +ValueStoreObj.getLogging_Level());
	//		szHomeDir=szHomeDir.trim();
			try
			{
			iGIFTLoglevel = Integer.parseInt(ValueStoreObj.getLogging_Level());
			}
		catch(Exception e)
		{iGIFTLoglevel=5;}
		}
		
		if (iLoglevel <= iGIFTLoglevel && ValueStoreObj != null)
		{
			szLogFile =szHomeDir+szFile_Separator+"proChara"+szFile_Separator+"Log"+szFile_Separator+"ActionVector.log";
			szDebugDir = szHomeDir+szFile_Separator+"proChara"+szFile_Separator+"Log";
			try
			{
// Test For theExistance Of File
				File FileObj = new File(szDebugDir);
				if(!(FileObj.exists()))
				{  
					FileObj.mkdirs();
				}
				File FObj = new File(szLogFile);
				if (!FObj.exists())
				{
					FObj.createNewFile();
					FOpStream = new FileOutputStream(FObj);
				}
				else
				{
					FOpStream = new FileOutputStream(szLogFile,true);
				}
				s+="\n";
				FOpStream.write( (new Date ( System.currentTimeMillis() ).toString() + " : "+szUserName+" : " + s).getBytes() );
			}
			catch(Exception e)
			{
				//System.out.println("Error Writing Debug File :"+e);
				System.out.println(s);
			}
			finally
			{
				try
				{
					FOpStream.close();
				}
				catch(Exception e)
				{
				}
			}
		}//end of if*/
	}

	
	/*public static void writeStatement(String s ,int iLoglevel,HttpSession session)
	{
		String szHomeDir="";
		FileOutputStream FOpStream = null;
		String szLogFile ="";
		valuestore ValueStoreObj = null;
		Login usercheck = null;
		String szUserName = "";
		try
		{
			ValueStoreObj = (valuestore) session.getAttribute("directory");
			
		}
		catch(Exception e)
		{
		}
		try
		{
			usercheck = (Login)session.getAttribute("GAUserCheck");
		}
		catch(Exception e)
		{
		}
		if(usercheck != null)
		{
			szUserName = usercheck.getUsername();
			szUserName  = szUserName.trim();
		}
		if(ValueStoreObj != null)
		{
			szHomeDir=ValueStoreObj.getHomedir();
			szHomeDir=szHomeDir.trim();
			iGIFTLoglevel = Integer.parseInt(ValueStoreObj.getLogging_Level());
		}
		
		if (iLoglevel <= iGIFTLoglevel && ValueStoreObj != null)
		{
			szLogFile =szHomeDir+szFile_Separator+"ActionVector"+szFile_Separator+"Log"+szFile_Separator+"Statement.log";
			szDebugDir = szHomeDir+szFile_Separator+"ActionVector"+szFile_Separator+"Log";
			try
			{
// Test For theExistance Of File
				File FileObj = new File(szDebugDir);
				if(!(FileObj.exists()))
				{  
					FileObj.mkdirs();
				}
				File FObj = new File(szLogFile);
				if (!FObj.exists())
				{
					FObj.createNewFile();
					FOpStream = new FileOutputStream(FObj);
				}
				else
				{
					FOpStream = new FileOutputStream(szLogFile,true);
				}
				s+="\n";
				FOpStream.write( (new Date ( System.currentTimeMillis() ).toString() + " : "+szUserName+" : " + s).getBytes() );
			}
			catch(Exception e)
			{
				//System.out.println("Error Writing Debug File :"+e);
				//System.out.println(s);
			}
			finally
			{
				try
				{
					FOpStream.close();
				}
				catch(Exception e)
				{
				}
			}
		}//end of if
	}

	
	*/
	
	
	public static void checkLogArchieve(java.util.Date DCurrentDate , java.util.Date DLogFileDate)
	{
		
		/*System.out.println("Current Date	:"+DCurrentDate);
		System.out.println("Log File Date	:"+DLogFileDate);*/

		int iCheck = DCurrentDate.compareTo(DLogFileDate);
		//System.out.println("Checking the Log Dates	 :"+iCheck);
		
	}
}
