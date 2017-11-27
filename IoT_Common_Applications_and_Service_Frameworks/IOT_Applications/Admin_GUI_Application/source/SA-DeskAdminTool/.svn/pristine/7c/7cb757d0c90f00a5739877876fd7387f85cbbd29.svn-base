/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package OrderDetails;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author raghu
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.log4j.Logger;

public class HttpRequestPoster {

    /**
     * Sends an HTTP GET request to a url
     *
     * @param endpoint - The URL of the server. (Example: " http://www.yahoo.com/search")
     * @param requestParameters - all the request parameters (Example: "param1=val1&param2=val2"). Note: This method will add the question mark (?) to the request - DO NOT add it yourself
     * @return - The response from the end point
     */
    static Logger log = Logger.getLogger(HttpRequestPoster.class);

    public static String sendGetRequest(String endpoint, String requestParameters) {
        String result = null;
        if (endpoint.startsWith("http://")) {
            // Send a GET request to the servlet
            try {
                // Construct data
                log.info("=========== Taking the StringBuffer class ==========");
                StringBuffer data = new StringBuffer();

                // Send data
                String urlStr = endpoint;

                log.info("*************** Checking the requestParameters ***************");
                if (requestParameters != null && requestParameters.length() > 0) {
                    urlStr += "?" + requestParameters;
                    log.debug("********** Generating the urlStr ****************");
                }
                URL url = new URL(urlStr);
                URLConnection conn = url.openConnection();
                log.debug("================ Connection opened ================");

                // Get the response
                log.info("******************* Getting the response ******************");
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                result = sb.toString();

            } catch (Exception e) {
                //e.printStackTrace();
                log.error("******************* ERROR ****************", e);
            }
        }
        return result;
    }

    /**
     * Reads data from the data reader and posts it to a server via POST request.
     * data - The data you want to send
     * endpoint - The server's address
     * output - writes the server's response to output
     * @throws Exception
     */
    public static void postData(Reader data, URL endpoint, Writer output) throws Exception {
        HttpURLConnection urlc = null;
        try {
            urlc = (HttpURLConnection) endpoint.openConnection();
            try {
                urlc.setRequestMethod("POST");
            } catch (ProtocolException e) {
                throw new Exception("Shouldn't happen: HttpURLConnection doesn't support POST??", e);
            }
            urlc.setDoOutput(true);
            urlc.setDoInput(true);
            urlc.setUseCaches(false);
            urlc.setAllowUserInteraction(false);
            urlc.setRequestProperty("Content-type", "text/xml; charset=" + "UTF-8");

            OutputStream out = urlc.getOutputStream();

            try {
                log.info("=============== Storing the file ===============");
                Writer writer = new OutputStreamWriter(out, "UTF-8");
                pipe(data, writer);
                writer.close();
            } catch (IOException e) {
                throw new Exception("IOException while posting data", e);
            } finally {
                if (out != null) {
                    out.close();
                }
            }

            InputStream in = urlc.getInputStream();
            try {
                log.info("************** Getting the file ****************");
                Reader reader = new InputStreamReader(in);
                pipe(reader, output);
                reader.close();
                log.debug("=================== Reader object closed ================");
            } catch (IOException e) {
                throw new Exception("IOException while reading response", e);
            } finally {
                if (in != null) {
                    in.close();
                }
            }

        } catch (IOException e) {
            throw new Exception("Connection error (is server running at " + endpoint + " ?): " + e);
        } finally {
            if (urlc != null) {
                urlc.disconnect();
            }
        }
    }

    /**
     * Pipes everything from the reader to the writer via a buffer
     */
    private static void pipe(Reader reader, Writer writer) throws IOException {
        char[] buf = new char[1024];
        int read = 0;
        log.info("=============== File Checking ===============");
        while ((read = reader.read(buf)) >= 0) {
            writer.write(buf, 0, read);
        }
        writer.flush();
    }

    public static String sendPostRequest(String URL, String data) {

        String result = null;

        try {

            // Send the request
            log.info("================== Sending the URL =================");
            URL url = new URL(URL);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            //write parameters
            writer.write(data);
            writer.flush();

            // Get the response
            StringBuffer answer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                answer.append(line);
            }
            writer.close();
            reader.close();

            result = answer.toString();
            answer = null;

        } catch (MalformedURLException ex) {
            // ex.printStackTrace();
            log.error("****************** ERROR ****************", ex);
        } catch (IOException ex) {
            // ex.printStackTrace();
            log.error("****************** ERROR ****************", ex);
        }
        return result;
    }

    public static Object[] sendPostRequestforFileDownload(String URL, String data) {

        BufferedReader reader = null;
        Object[] Filedetails = new Object[2];
        try {

            // Send the request
            log.info("*************** Sending the URL ********************");
            URL url = new URL(URL);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            //write parameters
            writer.write(data);
            writer.flush();

            // Get the response


            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            Filedetails[0] = reader;
            Filedetails[1] = conn.getContentLength();
            writer.close();
            // reader.close();
        } catch (MalformedURLException ex) {
            //ex.printStackTrace();
            log.error("******************** ERROR **************", ex);
        } catch (IOException ex) {
            //ex.printStackTrace();
            log.error("******************** ERROR **************", ex);
        }
        return Filedetails;
    }
}

