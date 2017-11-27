
/**
 * *********************************************************************
 * Software Developed by Merit Systems Pvt. Ltd., No. 42/1, 55/c, Nandi Mansion,
 * 40th Cross, Jayanagar 8th Block Bangalore - 560 070, India Work Created for
 * Merit Systems Private Limited All rights reserved
 *
 * THIS WORK IS SUBJECT TO INDIAN AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES
 * NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED, COPIED, DISTRIBUTED,
 * REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED,
 * COMPILED, LINKED, RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN
 * CONSENT ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD
 * SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 * *********************************************************************
 */

package com.merit.dashboard.model;

import java.util.ArrayList;

public class PojoObject {

	private ArrayList<String> hostListAvailable=null;

	/**
	 * @param hostListAvailable the hostListAvailable to set
	 */
	public void setHostListAvailable(ArrayList<String> hostListAvailable) {
		this.hostListAvailable = hostListAvailable;
	}

	/**
	 * @return the hostListAvailable
	 */
	public ArrayList<String> getHostListAvailable() {
		return hostListAvailable;
	}
}
