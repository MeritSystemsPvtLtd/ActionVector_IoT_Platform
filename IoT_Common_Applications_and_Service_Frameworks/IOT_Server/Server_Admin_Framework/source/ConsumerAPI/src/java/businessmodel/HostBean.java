/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/***************************************************************************
 *
 *                            Software Developed by
 *                           Merit Systems Pvt. Ltd.,
 *     #55/C-42/1, Nandi Mansion, Ist Floor 40th Cross, Jayanagar 8th Block
 *                          Bangalore - 560 070, India
 *               Work Created for Merit Systems Private Limited
 *                             All rights reserved
 *
 *          THIS WORK IS SUBJECT TO INDIAN AND INTERNATIONAL COPYRIGHT
 *                              LAWS AND TREATIES
 *       NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED, COPIED,
 *             DISTRIBUTED, REVISED, MODIFIED,TRANSLATED, ABRIDGED,
 *                                  CONDENSED,
 *        EXPANDED, COLLECTED, COMPILED, LINKED, RECAST, TRANSFORMED OR
 *                                   ADAPTED
 *                      WITHOUT THE PRIOR WRITTEN CONSENT
 *          ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION
 *                                COULD SUBJECT
 *               THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 *
 *
 ***************************************************************************/

package businessmodel;
import java.io.*;
/**
 *
 * @author gopal
 * Created on Sep 11, 2012, 11:19:43 AM
 */
public class HostBean implements Serializable{
private String subService;
private String customizedService;
private String service;
private String ip;

    public String getCustomizedService()
    {
        return customizedService;
    }

    public String getIp()
    {
        return ip;
    }

    public String getService()
    {
        return service;
    }

    public String getSubService()
    {
        return subService;
    }

    public void setCustomizedService(String customizedService)
    {
        this.customizedService = customizedService;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public void setService(String service)
    {
        this.service = service;
    }

    public void setSubService(String subService)
    {
        this.subService = subService;
    }


}
