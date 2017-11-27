package license;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

public class Utils {

    private static Logger logger = null;

    public Utils() {
        try {

            logger = Logger.getLogger("Utils");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean compareDates(Date sysDate, String stDate, String eDate) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //Date date0 = sdf.parse(sysDate);
            Date startDate = sdf.parse(stDate);
            Date endDate = sdf.parse(eDate);

            if ((sysDate.compareTo(startDate) > 0) && ((sysDate.compareTo(endDate) < 0))) {
                logger.debug("sysDate is after Start date and before End date");
                // System.out.println("sysDate is after Start date and before End date");
            } else {
                //logger.debug("License is Expired");
                //   System.out.println("return false");
                return false;
            }

        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return true;
    }

    /*
     * Check if the requested feature is there in the license key string
     */
    public boolean checkFeature(String pFeature, String[] key) {

        try {

            String[] s2 = new String[50];
            logger.debug("checkFeature(): Feature = " + pFeature);
            //  System.out.println("checkFeature(): Feature = " + pFeature);
            for (int m = 6; m < key.length; m++) {
                // System.out.println("checkFeature(): par2 =" + key[m]);
                s2 = key[m].split("=");
                //  System.out.println("checkFeature(): s2= " + s2[0] + " " + s2[1]);
                //for (int i=0; i<labels.length; i++){
                if (pFeature != null) {
                    if (s2[0].equalsIgnoreCase(pFeature)) {
                        if (s2[1].equalsIgnoreCase("enabled")) {
                            logger.debug("checkFeature(): feature enabled");
                            //    System.out.println("checkFeature(): feature enabled");
                            return true;
                        } else {
                            logger.debug("checkFeature(): feature not enabled. Returning False");
                            //   System.out.println("checkFeature(): feature not enabled. Returning False");
                            return false;
                        }
                    }
                }
                //}
            }

        } catch (NullPointerException e1) {
            e1.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("checkFeature(): Feature " + pFeature + " not found.....returning false");
        //  System.out.println("checkFeature(): Feature " + pFeature + " not found.....returning false");
        return false;
    }

}
