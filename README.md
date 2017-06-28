# Configuring Business Central and KIE Server for OAuth Support

This java project is an extension to Business Central and KIE Server to work with ServiceMaster's OAuth security APIs.

Build the project:
```sh
mvn clean install
```

###  Add dependencies to JBoss EAP
* Create module structure
    * /modules/org/rhc/jboss/security/oauth/as7/main
    * /modules/org/rhc/securityapi/client/main
* Add built jar (jboss-oauth-1.0.0.jar) to first module
* Add properties jar to second module
* Add module.xml so that JBoss EAP knows how to load these modules and their dependencies
* Add these lines to add this module to the jboss-deployment-structure.xml for Business Central ($EAP_HOME/standalone/deployments/business-central.war/WEB-INF/jboss-deployment-structure.xml) or KIE Server ($EAP_HOME/standalone/deployments/kie-server.war/WEB-INF/jboss-deployment-structure.xml)
```
      <module name="org.apache.xerces"/>
      <module name="org.rhc.jboss.security.oauth.as7"/>
```
Now we have made this jar and its classes available within Business Central or KIE Server's classpath.

## For Business Central

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

### Pull in security domain in jboss-web.xml

Add the following to WEB-INF/jboss-web.xml
```
<jboss-web>
  <security-domain>oauth</security-domain>
</jboss-web>
```



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

## For KIE Server

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

### Pull in security domain in jboss-web.xml

Add the following to WEB-INF/jboss-web.xml
```
<jboss-web>
  <security-domain>oauth</security-domain>
</jboss-web>
```

### Restrict resources (web.xml)

Restrict web-resources to a role or roles in the KIE Server's web.xml.  Below, we are adding a new auth-constraint, restricting these resources to only users with role "Workflow_Testers"

```
    <security-constraint>

         <web-resource-collection>
            <web-resource-name>Allowed methods</web-resource-name>
            <url-pattern>/services/rest/*</url-pattern>
            <http-method>OPTIONS</http-method>
         </web-resource-collection>


         <web-resource-collection>
            <web-resource-name>REST web resources</web-resource-name>
            <url-pattern>/services/rest/*</url-pattern>
            <http-method>GET</http-method>
            <http-method>PUT</http-method>
            <http-method>POST</http-method>
         </web-resource-collection>

         <auth-constraint>
            <role-name>Workflow_Testers</role-name>
         </auth-constraint>

    </security-constraint>

    <security-role>
         <role-name>Workflow_Testers</role-name>
    </security-role>


```

### Add new LoginConfig (web.xml)
We must create a new login-config using the method CUST_TOKEN (which is custom and we will need to register separately).  This login-config is linked to the 'oauth' realm.

```
  <login-config>
    <auth-method>CUST_TOKEN?identity-header=Authorization</auth-method>
    <realm-name>oauth</realm-name>
  </login-config>
```

### Register Authentication Mechanism
* Add META-INF/services/io.undertow.servlet.ServletExtension
* This should be a text file containing one line:
```
org.rhc.jboss.security.oauth.as7.OAuthTokenServletExtension
```
This will tell EAP to load this ServletExtension for BusinessCentral which has a method handleDeployment() which will register our OAuthTokenAuthenticationMechanism

### Managing Exclusions
By default, we are excluding the following types of resources from OAuth protection:

* .png
* .jpg
* .svg
* .ico
* .css
* maven resources (i.e. pom.xml, .jar files) (NOTE: these are protected instead by BASIC Auth)

You can add to or change these exclusions (matching regex) in /opt/jboss-as/jboss-eap-7.0/standalone/deployments/business-central/WEB-INF/web.xml
```
  <login-config>
      <auth-method><![CDATA[FORM?defaultPage=business-central&checkToke..[...]..&exclusionPatterns=.*\.png|.*\.jpg|.*\.svg|.*\.ico|.*\.css|.*maven2.*]]></auth-method>
      [...]
  </login-config>
```