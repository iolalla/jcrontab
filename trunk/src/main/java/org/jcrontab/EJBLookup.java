package org.jcrontab;

import java.lang.reflect.Method;
import java.util.Arrays;

import javax.naming.InitialContext;

import org.jcrontab.log.Log;
import javax.ejb.EJBHome; 
import javax.ejb.EJBObject; 
/** 
 * <b>Description:TODO</b>
 * @author      vipup<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  28.09.2010::13:53:28<br> 
 */
public class EJBLookup {
	   static void tryEjb(String strClassNamePar, String strMethodNamePar ,  String[] strExtraInfoPar ) {
	        // This code was sended by 
	        
	            Log.info("Unable to instantiate class '" + strClassNamePar 
	                    + "', trying as Stateless Session EJB");

	            try {
	                // Use default initial context
	                InitialContext ic = new InitialContext() ; 
	                EJBHome home = (EJBHome) ic.lookup(strClassNamePar) ; 

	                // Stateless Session Beans MUST have create() method
	                Method createMethod = home.getClass().getMethod("create", new Class[0]);
	                EJBObject ejb = (EJBObject) createMethod.invoke(home, new Object[0]);

	                Log.info("Invoking method: " + strMethodNamePar 
	                        + " with params:" + Arrays.asList(strExtraInfoPar));

	                if (strExtraInfoPar.length == 1 && (strExtraInfoPar[0] == null 
	                        || "null".equalsIgnoreCase(strExtraInfoPar[0]))) {
	                    Object[] arg = new Object[0];
	                    Class[] argTypes = new Class[0];
	                    Method method = ejb.getClass().getMethod(strMethodNamePar, argTypes);
	                    method.invoke(ejb, arg);                                    
	                } else { 
	                    Object[] arg = {strExtraInfoPar};
	                    Class[] argTypes = {String[].class};
	                    Method method = ejb.getClass().getMethod(strMethodNamePar, argTypes);
	                    method.invoke(ejb, arg);                
	                } 
	            } catch (Exception e2) {
	                Log.error(e2.toString(), e2);            
	            }
	 
		}
}


 