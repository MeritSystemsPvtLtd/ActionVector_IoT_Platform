
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package businessmodel;

import Model.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import org.apache.log4j.Logger;

/**
 *
 * @author niteshc
 */
public class GetAggregatedMetricValue {

    static Logger log = Logger.getLogger(GetAggregatedMetricValue.class);
    static Calendar cal = Calendar.getInstance();
    static String[] str = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public static String getAggregatedMetricValue(javax.servlet.http.HttpServletRequest request, String customername,
            String service, String resourceid, String metrictype, String resourcetype, String timeperiod,
            String slot, int selectedYear, String selectedMonth, int selectedWeek, int selectedDate, int selectedHour) throws SQLException {

        Connection con = null;
        ResultSet rs = null;
        Statement st = null;
        StringBuffer sbr = new StringBuffer("");
        String serviceQuery = "";
        boolean recFound = false;
        long metricValue = 0;
        String metricValues = null;
        String currentDay = null;
        String query = null;
        ArrayList days = new ArrayList();
        // int hour = 0;
        long sum = 0;

        try {
            int i = 0, MM = 0;
            for (i = 0; i < str.length; i++) {
                if (selectedMonth.equalsIgnoreCase(str[i])) {
                    MM = i;
                }
            }
            cal.set(selectedYear, MM, selectedDate);
            int noDaysInWeek = cal.get(Calendar.DAY_OF_WEEK);
            //int daysInCurrentMonth = cal.get(Calendar.DAY_OF_MONTH);
            int selectedMonthNos = cal.get(Calendar.MONTH);
            int noOfMonths = 0;
            int noOfWeeks = 1;
            int noOfHour = 1;
            int noOfDays = 1;

            con = DatabaseConnection.getAVSAConnection(request);
            if (con != null) {
                //log.info("Connection is not null before getting resource configuration");
                st = con.createStatement();
                try {
                    if (resourceid == null || (resourceid.equals("null")) || (resourceid.equals(""))) {
                        resourceid = service;
                    }
                    if (service != null) {
                        if (!(service.equals("")) && !(service.equals("null"))) {
                            serviceQuery = " and service ='" + service + "'";
                        }
                        if (timeperiod.equalsIgnoreCase("Day")) {
                            if (slot.equalsIgnoreCase("hour")) {
                                while (noOfHour <= selectedHour) {
                                    query = "select metricvalue from timewisederivedmetrics where customerid in (select id from"
                                            + " customerinfo where customername='" + customername + "') and metrictype='" + metrictype + "'"
                                            + " and resourceid='" + resourceid + "'" + serviceQuery + " and resourcetype='" + resourcetype + "'"
                                            + " and day=" + selectedDate + " and week=" + selectedWeek + " and month='" + selectedMonth + "' and "
                                            + "year=" + selectedYear + " and hour=" + noOfHour + "";
                                    //  System.out.println("Query===>" + query);
                                    rs = st.executeQuery(query);
                                    if (rs.next()) {
                                        recFound = true;
                                        //    hour = Integer.parseInt(rs.getString("hour"));
                                        //    System.out.println("hour===>" + hour);
                                        metricValue = Integer.parseInt(rs.getString("metricvalue"));
                                        //   System.out.println("metricValue==>" + metricValue);
                                        currentDay = GetAggregatedMetricValue.getDay(selectedYear, selectedMonth, selectedDate);
                                        // if (metricValue != 0) {
                                        sbr.append("{\"" + noOfHour + ',' + currentDay + ',' + selectedDate + '/' + selectedMonth + '/' + selectedYear + "\":\"" + metricValue + "\"},");
                                        //      System.out.println("sbr===>" + sbr);
                                        //  }
                                    }
                                    rs.close();
                                    rs = null;
                                    noOfHour++;
                                }
                                if (!recFound) {
                                    sbr.append("null");
                                }
                                if (recFound) {
                                    sbr.deleteCharAt(sbr.length() - 1);
                                }
                            }
                            if (slot.equalsIgnoreCase("Day")) {
                                query = "select metricvalue from timewisederivedmetrics where customerid in (select id from"
                                        + " customerinfo where customername='" + customername + "') and metrictype='" + metrictype + "'"
                                        + " and resourceid='" + resourceid + "'" + serviceQuery + " and resourcetype='" + resourcetype + "'"
                                        + " and day=" + selectedDate + " and month='" + selectedMonth + "' and year="
                                        + " " + selectedYear + " and week =" + selectedWeek + " and hour in(select max(hour) from timewisederivedmetrics"
                                        + " where customerid in (select id from customerinfo where customername='" + customername + "') and metrictype='" + metrictype + "'"
                                        + " and resourceid='" + resourceid + "'" + serviceQuery + " and resourcetype='" + resourcetype + "'"
                                        + " and day=" + selectedDate + " and month='" + selectedMonth + "' and year="
                                        + " " + selectedYear + " and week =" + selectedWeek + ")";
                                //   System.out.println("Query===>" + query);
                                rs = st.executeQuery(query);
                                if (rs.next()) {
                                    recFound = true;
                                    //   hour = Integer.parseInt(rs.getString("hour"));
                                    //   System.out.println("hour===>" + hour);
                                    metricValue = Integer.parseInt(rs.getString("metricvalue"));
                                    //    System.out.println("metricValue==>" + metricValue);
                                    currentDay = GetAggregatedMetricValue.getDay(selectedYear, selectedMonth, selectedDate);
                                    sbr.append("{\"" + currentDay + ',' + selectedDate + '/' + selectedMonth + '/' + selectedYear + "\":\"" + metricValue + "\"}");
                                    ///       System.out.println("sbr===>" + sbr);
                                }
                                rs.close();
                                rs = null;
                                /*  if (recFound) {
                                 sbr.deleteCharAt(sbr.length() - 1);
                                 }*/
                                if (!recFound) {
                                    sbr.append("null");
                                }
                            }

                        } else if (timeperiod.equalsIgnoreCase("Week")) {
                            if ((slot.equalsIgnoreCase("Week")) || (slot.equalsIgnoreCase("Day"))) {
                                while (noDaysInWeek <= 7 && noDaysInWeek >= 1) {
                                    query = "select day from timewisederivedmetrics where customerid in (select id from"
                                            + " customerinfo where customername='" + customername + "') and metrictype='" + metrictype + "'"
                                            + " and resourceid='" + resourceid + "'" + serviceQuery + " and resourcetype='" + resourcetype + "'"
                                            + " and day=" + (selectedDate - (--noDaysInWeek)) + " and month='" + selectedMonth + "' and year="
                                            + " " + selectedYear + " and week =" + selectedWeek + "";
                                    //      System.out.println("query ==>" + query);
                                    rs = st.executeQuery(query);
                                    if (rs.next()) {
                                        recFound = true;
                                        days.add(Integer.valueOf(rs.getString("day")));
                                        //      System.out.println("day==>" + days);
                                    }
                                    rs.close();
                                    rs = null;
                                    query = null;
                                }
                                for (Object dates : days) {
                                    Integer date = (Integer) dates;
                                    query = "select metricvalue from timewisederivedmetrics where customerid in (select id from"
                                            + " customerinfo where customername='" + customername + "') and metrictype='" + metrictype + "'"
                                            + " and resourceid='" + resourceid + "'" + serviceQuery + " and resourcetype='" + resourcetype + "'"
                                            + " and day=" + date + " and month='" + selectedMonth + "' and year="
                                            + " " + selectedYear + " and week =" + selectedWeek + " and hour in (select max(hour) from timewisederivedmetrics"
                                            + " where customerid in (select id from customerinfo where customername='" + customername + "') and metrictype='" + metrictype + "'"
                                            + " and resourceid='" + resourceid + "'" + serviceQuery + " and resourcetype='" + resourcetype + "'"
                                            + " and day=" + date + " and month='" + selectedMonth + "' and year="
                                            + " " + selectedYear + " and week =" + selectedWeek + ")";
                                    //    System.out.println("query===>" + query);
                                    rs = st.executeQuery(query);
                                    if (rs.next()) {
                                        if (slot.equalsIgnoreCase("Week")) {
                                            //   hour = Integer.parseInt(rs.getString("hour"));
                                            //   System.out.println("hour===>" + hour);
                                            metricValue = Integer.parseInt(rs.getString("metricvalue"));
                                            //        System.out.println("metricValues==>" + metricValue);
                                            sum += metricValue;
                                            //         System.out.println("sum==>" + sum);
                                        }
                                        if (slot.equalsIgnoreCase("Day")) {
                                            //   hour = Integer.parseInt(rs.getString("hour"));
                                            //   System.out.println("hour===>" + hour);
                                            metricValue = Integer.parseInt(rs.getString("metricvalue"));
                                            //      System.out.println("metricValues==>" + metricValue);
                                            if (metricValue != 0) {
                                                currentDay = GetAggregatedMetricValue.getDay(selectedYear, selectedMonth, date);
                                                sbr.append("{\"" + currentDay + ',' + date + '/' + selectedMonth + '/' + selectedYear + "\":\"" + metricValue + "\"},");
                                                //            System.out.println("sbr===>" + sbr);
                                            }
                                        }
                                    }
                                    rs.close();
                                    rs = null;
                                    query = null;
                                }
                                if (slot.equalsIgnoreCase("Week")) {
                                    if (sum != 0) {
                                        sbr.append("{\"" + selectedWeek + '/' + selectedMonth + '/' + selectedYear + "\":\"" + sum + "\"}");
                                        //       System.out.println("sbr===>" + sbr);
                                    }
                                }
                                if (recFound && slot.equalsIgnoreCase("Day")) {
                                    sbr.deleteCharAt(sbr.length() - 1);
                                }
                                if (!recFound) {
                                    sbr.append("null");
                                }
                            }
                        } else if (timeperiod.equalsIgnoreCase("Month")) {
                            if ((slot.equalsIgnoreCase("Month")) || (slot.equalsIgnoreCase("Day"))) {
                                while (noOfDays <= selectedDate) {
                                    query = "select metricvalue from timewisederivedmetrics where customerid in (select id from"
                                            + " customerinfo where customername='" + customername + "') and metrictype='" + metrictype + "'"
                                            + " and resourceid='" + resourceid + "'" + serviceQuery + " and resourcetype='" + resourcetype + "'"
                                            + " and day=" + noOfDays + " and month='" + selectedMonth + "' and year="
                                            + " " + selectedYear + " and hour in(select max(hour) from timewisederivedmetrics where customerid in "
                                            + "(select id from customerinfo where customername='" + customername + "') and metrictype='" + metrictype + "'"
                                            + " and resourceid='" + resourceid + "'" + serviceQuery + " and resourcetype='" + resourcetype + "'"
                                            + " and day=" + noOfDays + " and month='" + selectedMonth + "' and year="
                                            + " " + selectedYear + " )";
                                    //    System.out.println("Query===>" + query);
                                    rs = st.executeQuery(query);
                                    if (rs.next()) {
                                        recFound = true;
                                        if (slot.equalsIgnoreCase("Month")) {
                                            //   hour = Integer.parseInt(rs.getString("hour"));
                                            //  System.out.println("hour===>" + hour);
                                            metricValue = Integer.parseInt(rs.getString("metricvalue"));
                                            //   System.out.println("metricValues==>" + metricValue);
                                            sum += metricValue;
                                            //    System.out.println("sum==>" + sum);
                                        }
                                        if (slot.equalsIgnoreCase("Day")) {
                                            //  hour = Integer.parseInt(rs.getString("hour"));
                                            //    System.out.println("hour===>" + hour);
                                            metricValue = Integer.parseInt(rs.getString("metricvalue"));
                                            //   System.out.println("metricValues==>" + metricValue);
                                            currentDay = GetAggregatedMetricValue.getDay(selectedYear, selectedMonth, noOfDays);
                                            if (metricValue != 0) {
                                                sbr.append("{\"" + currentDay + ',' + noOfDays + '/' + selectedMonth + '/' + selectedYear + "\":\"" + metricValue + "\"},");
                                                //     System.out.println("sbr===>" + sbr);
                                            }
                                        }
                                    }
                                    rs.close();
                                    rs = null;
                                    query = null;
                                    noOfDays++;
                                }
                                if (slot.equalsIgnoreCase("Month")) {
                                    if (sum != 0) {
                                        sbr.append("{\"" + selectedMonth + '/' + selectedYear + "\":\"" + sum + "\"}");
                                        //    System.out.println("sbr===>" + sbr);
                                    }
                                }
                                if (recFound && slot.equalsIgnoreCase("Day")) {
                                    sbr.deleteCharAt(sbr.length() - 1);
                                }
                                if (!recFound) {
                                    sbr.append("null");
                                }
                            } else if (slot.equalsIgnoreCase("Week")) {
                                while (noOfWeeks <= selectedWeek) {
                                    query = "select sum(metricvalue) as metricSum from timewisederivedmetrics where customerid in (select id from"
                                            + " customerinfo where customername='" + customername + "') and metrictype='" + metrictype + "'"
                                            + " and resourceid='" + resourceid + "'" + serviceQuery + " and resourcetype='" + resourcetype + "'"
                                            + " and month='" + selectedMonth + "' and year=" + selectedYear + " and week=" + noOfWeeks + "";
                                    //     System.out.println("Query===>" + query);
                                    rs = st.executeQuery(query);
                                    while (rs.next()) {
                                        recFound = true;
                                        metricValues = rs.getString("metricSum");
                                        //     System.out.println("metricValues===>" + metricValues);
                                        if (metricValues != null) {
                                            sbr.append("{\"" + noOfWeeks + '/' + selectedMonth + '/' + selectedYear + "\":\"" + metricValues + "\"},");
                                            //         System.out.println("sbr===>" + sbr);
                                        } else {
                                            recFound = false;
                                        }
                                    }
                                    rs.close();
                                    rs = null;
                                    noOfWeeks++;
                                }
                                if (!recFound) {
                                    sbr.append("null");
                                }
                                if (recFound) {
                                    sbr.deleteCharAt(sbr.length() - 1);
                                }
                            }
                        } else if (timeperiod.equalsIgnoreCase("Year")) {
                            if ((slot.equalsIgnoreCase("Day")) || (slot.equalsIgnoreCase("Year"))) {
                                while (noOfMonths <= selectedMonthNos) {
                                    String monthName = GetAggregatedMetricValue.getMonth(noOfMonths);
                                    noOfDays = 1;
                                    while (noOfDays <= 31) {
                                        query = "select metricvalue from timewisederivedmetrics where customerid in (select id from"
                                                + " customerinfo where customername='" + customername + "') and metrictype='" + metrictype + "'"
                                                + " and resourceid='" + resourceid + "'" + serviceQuery + " and resourcetype='" + resourcetype + "'"
                                                + " and day=" + noOfDays + " and month='" + monthName + "' and year="
                                                + " " + selectedYear + " and hour in (select max(hour) from timewisederivedmetrics"
                                                + " where customerid in (select id from customerinfo where customername='" + customername + "') and metrictype='" + metrictype + "'"
                                                + " and resourceid='" + resourceid + "'" + serviceQuery + " and resourcetype='" + resourcetype + "'"
                                                + " and day=" + noOfDays + " and month='" + monthName + "' and year="
                                                + " " + selectedYear + ")";
                                        //     System.out.println("Query===>" + query);
                                        rs = st.executeQuery(query);
                                        if (rs.next()) {
                                            recFound = true;
                                            if (slot.equalsIgnoreCase("Day")) {
                                                //    hour = Integer.parseInt(rs.getString("hour"));
                                                //     System.out.println("hour===>" + hour);
                                                metricValue = Integer.parseInt(rs.getString("metricvalue"));
                                                //     System.out.println("metricValues==>" + metricValue);
                                                if (metricValue != 0) {
                                                    currentDay = GetAggregatedMetricValue.getDay(selectedYear, monthName, noOfDays);
                                                    sbr.append("{\"" + currentDay + ',' + noOfDays + '/' + monthName + '/' + selectedYear + "\":\"" + metricValue + "\"},");
                                                    //        System.out.println("sbr===>" + sbr);
                                                }
                                            }
                                            if (slot.equalsIgnoreCase("Year")) {
                                                //     hour = Integer.parseInt(rs.getString("hour"));
                                                //      System.out.println("hour===>" + hour);
                                                metricValue = Integer.parseInt(rs.getString("metricvalue"));
                                                sum += metricValue;
                                                //    System.out.println("metricValues===>" + metricValue);
                                                //    System.out.println("sum==>" + sum);

                                            }
                                        }
                                        rs.close();
                                        rs = null;
                                        noOfDays++;
                                    }
                                    noOfMonths++;
                                }
                                if (slot.equalsIgnoreCase("Year")) {
                                    if (sum != 0) {
                                        sbr.append("{\"" + selectedYear + "\":\"" + sum + "\"}");
                                    }
                                }
                                if (recFound && slot.equalsIgnoreCase("Day")) {
                                    sbr.deleteCharAt(sbr.length() - 1);
                                }
                                if (!recFound) {
                                    sbr.append("null");
                                }
                            } else if (slot.equalsIgnoreCase("Week")) {
                                while (noOfMonths <= selectedMonthNos) {
                                    String monthName = GetAggregatedMetricValue.getMonth(noOfMonths);
                                    noOfWeeks = 1;
                                    while (noOfWeeks <= 6) {
                                        query = "select sum(metricvalue) as metricSum from timewisederivedmetrics where customerid in (select id from"
                                                + " customerinfo where customername='" + customername + "') and metrictype='" + metrictype + "'"
                                                + " and resourceid='" + resourceid + "'" + serviceQuery + " and resourcetype='" + resourcetype + "'"
                                                + " and  month='" + monthName + "' and year="
                                                + " " + selectedYear + " and week=" + noOfWeeks + "";
                                        //     System.out.println("Query===>" + query);
                                        rs = st.executeQuery(query);
                                        while (rs.next()) {
                                            metricValues = rs.getString("metricSum");
                                            //    System.out.println("metricValues===>" + metricValues);
                                            if (metricValues != null) {
                                                recFound = true;
                                                sbr.append("{\"" + noOfWeeks + '/' + monthName + '/' + selectedYear + "\":\"" + metricValues + "\"},");
                                                //      System.out.println("sbr===>" + sbr);
                                            }
                                        }
                                        rs.close();
                                        rs = null;
                                        noOfWeeks++;
                                    }
                                    noOfMonths++;
                                }
                                if (!recFound) {
                                    sbr.append("null");
                                }
                                if (recFound) {
                                    sbr.deleteCharAt(sbr.length() - 1);
                                }
                            } else if (slot.equalsIgnoreCase("Month")) {
                                while (noOfMonths <= selectedMonthNos) {
                                    String monthName = GetAggregatedMetricValue.getMonth(noOfMonths);
                                    query = "select sum(metricvalue) as metricSum from timewisederivedmetrics where customerid in (select id from"
                                            + " customerinfo where customername='" + customername + "') and metrictype='" + metrictype + "'"
                                            + " and resourceid='" + resourceid + "'" + serviceQuery + " and resourcetype='" + resourcetype + "'"
                                            + " and month='" + monthName + "' and year=" + selectedYear + "";
                                    //   System.out.println("query===>" + query);
                                    rs = st.executeQuery(query);
                                    while (rs.next()) {
                                        metricValues = rs.getString("metricSum");
                                        //    System.out.println("metricValues===>" + metricValues);
                                        if (metricValues != null) {
                                            recFound = true;
                                            sbr.append("{\"" + monthName + '/' + selectedYear + "\":\"" + metricValues + "\"},");
                                            //       System.out.println("sbr===>" + sbr);
                                        }
                                    }
                                    rs.close();
                                    rs = null;
                                    noOfMonths++;
                                }
                                if (!recFound) {
                                    sbr.append("null");
                                }
                                if (recFound) {
                                    sbr.deleteCharAt(sbr.length() - 1);
                                }
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error while fetching metricvalue list in GetMetricValue ", e);
                }

            } else {
                return null;
            }

            return sbr.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error while establishing connection", e);
            return null;
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                sbr = null;
                con = null;
                days.clear();
                days = null;
                rs = null;
                serviceQuery = null;
                metricValues = null;
                currentDay = null;
                query = null;
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error while closing connection ", e);
            }
        }
        /*  public static String getDay(String year, String month, String date) {

         String day = null;
         int yyyy = Integer.parseInt(year);
         int dd = Integer.parseInt(date);
         String[] str = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
         int i = 0, MM = 0;
         for (i = 0; i < str.length; i++) {
         if (month.equalsIgnoreCase(str[i])) {
         MM = i;
         }
         }
         cal.set(yyyy, MM, dd);
         String current = cal.getTime().toString();
         String dateParts[] = current.split(" ");
         day = dateParts[0];
         return day;
         }*/
    }

    public static String getMonth(int noOfMonths) {
        String month = null;
        String[] str = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int i = 0;
        for (i = 0; i <= 11; i++) {
            if (noOfMonths == i) {
                month = str[i];
            }
        }
        return month;
    }

    private static String getDay(int yyyy, String month, int date) {
        String day = null;
        String[] str = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int i = 0, MM = 0;
        for (i = 0; i < str.length; i++) {
            if (month.equalsIgnoreCase(str[i])) {
                MM = i;
            }
        }
        cal.set(yyyy, MM, date);
        String current = cal.getTime().toString();
        String dateParts[] = current.split(" ");
        day = dateParts[0];
        return day;
    }
}
