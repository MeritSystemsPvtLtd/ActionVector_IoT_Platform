/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package OrderDetails;

//import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ActionSupport;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.util.ServletContextAware;

/**
 *
 * @author raghu
 */
public class AwareImplementer extends ActionSupport implements ServletContextAware,ServletResponseAware,ServletRequestAware{
public  ServletContext  servletContext;
  public HttpServletResponse servletResponse;
  public HttpServletRequest servletRequest;

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

public void setServletResponse(HttpServletResponse servletResponse) {
        this.servletResponse=servletResponse;
    }

    public void setServletRequest(HttpServletRequest servletRequest) {
        this.servletRequest=servletRequest;
    }



}
