# Configuring Business Central for OAuth Support

###  Add dependencies to JBoss EAP
* Create module structure
    * /modules/org/rhc/jboss/security/oauth/as7/main
    * /modules/org/rhc/securityapi/client/main
* Add built jar (jboss-oauth-1.0.0.jar) to first module
* Add properties jar to second module
* Add module.xml so that JBoss EAP knows how to load these modules and their dependencies
* Add these lines to add this module to the jboss-deployment-structure.xml for Business Central ($EAP_HOME/standalone/deployments/business-central.war/WEB-INF/jboss-deployment-structure.xml)
```
      <module name="org.apache.xerces"/>
      <module name="org.rhc.jboss.security.oauth.as7"/>
```
Now we have made this jar and its classes available within Business Central's classpath

### Add oauth security domain to standalone.xml

```
<security-domain name="oauth" cache-type="default">
    <authentication>
        <login-module code="org.rhc.jboss.security.oauth.as7.OAuthManageResourceLoginModule" module="org.rhc.jboss.security.oauth.as7" flag="sufficient">
        </login-module>
    </authentication>
</security-domain>
```
This creates a custom login module within the domain 'oauth' that we will be able to use later to verify roles

### Configure LoginConfig in deployment descriptor (web.xml)

```
﻿<login-config>
    <auth-method><![CDATA[FORM?defaultPage=business-central&checkTokenUrl=token&resource=http://localhost:8080/business-central&clientId=bc&useStateParam=true&stateExpirationTimeInSeconds=60&tokenAuthorizeUrl=http://localhost:3000/adfs/oauth2/authorize&groupsMapping=AppDynamics_Server_Monitoring_User->admin;fromGroupB->toGroupB1]]></auth-method>
    <realm-name>oauth</realm-name>
    <form-login-config>
      <form-login-page>/login.jsp</form-login-page>
      <form-error-page>/login.jsp?message=Login failed: Invalid UserName or Password</form-error-page>
    </form-login-config>
</login-config>

```
This tells EAP that we want to use our custom 'FORM' authentication mechanism to handle authentication, using our 'oauth' realm.

### Register Authentication Mechanism
* Add META-INF/services/io.undertow.servlet.ServletExtension
* This should be a text file containing one line:
```
org.rhc.jboss.security.oauth.as7.OAuthTokenWithRedirectionAuthenticationMechanismServletExtension
```
This will tell EAP to load this ServletExtension for BusinessCentral which has a method handleDeployment() which will register our CustomFormAuthenticationMechanism and SsoSessionListener