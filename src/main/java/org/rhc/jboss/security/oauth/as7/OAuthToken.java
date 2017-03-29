package org.rhc.jboss.security.oauth.as7;

import java.util.List;

/**
 * Represents OAuth token
 *
 * Created by ajurcenk on 2/15/17.
 */

public class OAuthToken {


    private List<String> groups;

    private String winAccountName  = "";


    public OAuthToken(final String winAccountName, final List<String> groups) {

        this.winAccountName = winAccountName;
        this.groups = groups;
    }


    public String getWinAccountName() {

        return winAccountName;
    }

    public List<String> getGroups() {
        return groups;
    }

}
