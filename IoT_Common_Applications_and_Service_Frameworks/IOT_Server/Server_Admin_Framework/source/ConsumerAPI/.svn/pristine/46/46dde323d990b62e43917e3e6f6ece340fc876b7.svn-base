/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AV_Action;

/**
 *
 * @author gopal
 */
public class AV_Action {

    protected AV_Model model;

    public AV_Action(AV_Model modelcls) {
        this.model = modelcls;
    }

    public Object perform(javax.servlet.http.HttpServletRequest req, javax.servlet.ServletContext Application) {
        boolean bReturn = false;
        try {
            model.performRequestAction(req, Application);
            bReturn = true;
        } catch (Exception e) {
            bReturn = false;
            e.printStackTrace();
        }
        return new Boolean(bReturn);
    }

    public Object perform(javax.servlet.http.HttpServletRequest req) {
        boolean bReturn = false;
        try {
            model.performRequestAction(req);
            bReturn = true;
        } catch (Exception e) {
            bReturn = false;
            e.printStackTrace();
        }
        return new Boolean(bReturn);
    }

    public Object perform(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse res, javax.servlet.ServletContext sc) {
        //System.out.println("We are in GIFET_GAAction.java\nmodel-> "+model+"\nreq= "+req+"\nres = "+res+"\nsc = "+sc);
        boolean bReturn = false;
        try {
            model.performRequestAction(req, res, sc);
            bReturn = true;
        } catch (Exception e) {
            bReturn = false;
            e.printStackTrace();
        }
        return new Boolean(bReturn);
    }
}
