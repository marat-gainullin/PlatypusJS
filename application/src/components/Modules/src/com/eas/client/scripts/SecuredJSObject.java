/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.client.login.PrincipalHost;
import java.util.Map;
import java.util.Set;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class SecuredJSObject extends SecuredJSObjectFacade {

    public SecuredJSObject(JSObject aDelegate, String aAppElementId, Set<String> aModuleAllowedRoles, Map<String, Set<String>> aPropertiesAllowedRoles, PrincipalHost aPrincipalHost) {
        super(aDelegate, aAppElementId, aModuleAllowedRoles, aPropertiesAllowedRoles, aPrincipalHost);
    }

    @Override
    public Object getMember(String name) {
        Object res = super.getMember(name);
        if (res instanceof JSObject && ((JSObject) res).isFunction()) {
            return new SecuredJSFunction(name, (JSObject) res, appElementId, moduleAllowedRoles, propertiesAllowedRoles, principalHost);
        } else {
            checkPropertyPermission(name);
            return res;
        }
    }

    @Override
    public void setMember(String name, Object value) {
        if (value instanceof JSObject && ((JSObject) value).isFunction()) {
            super.setMember(name, value);
        } else {
            checkPropertyPermission(name);
            super.setMember(name, value);
        }
    }

}
