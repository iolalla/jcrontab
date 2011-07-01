package org.jcrontab;

import net.sf.jsr107cache.Cache;

import org.jcrontab.data.CrontabEntryBean;

import cc.co.llabor.cache.Manager;

/**
 * <b>Description:TODO</b>
 * 
 * @author vipup<br>
 * <br>
 *         <b>Copyright:</b> Copyright (c) 2006-2008 Monster AG <br>
 *         <b>Company:</b> Monster AG <br>
 * 
 *         Creation: 29.06.2011::21:31:02<br>
 */
public class CrontabRegistry {
	

	public static void registerLastExecution(CrontabBean crontabBean, int taskId) {
		// TODO ID could be inconsistent - refactor it!
		String key = calcKey(crontabBean);
		CrontabBean colBean = null;
		try{
			Cache collector = Manager.getCache("CrontabRegistry");
			synchronized (Cache.class) {
				if (collector.containsKey(key)) {// update clone
					colBean = (CrontabBean)collector.get(key);
					if (colBean!=null && !crontabBean.equals(colBean)){ // avoid call itself,  check for "impossible" null is theMUST!!!
						colBean.registerLastExecution(taskId);
					}else { // store itself into regisry
						colBean = crontabBean;					
					}
				} else { // store itself into regisry
					colBean = crontabBean;				
				}
				colBean.setLastResult(taskId);
				collector.put(key, colBean);
			}
		}catch(Throwable e){/* ignore any statistics errors */
			e.printStackTrace();
		}
	}

	public static CrontabBean getBean(CrontabEntryBean crontabEntryBean) {
		String key = calcKey(crontabEntryBean);
		CrontabBean retval  = null;
		try{
			Cache collector = Manager.getCache("CrontabRegistry");
			retval = (CrontabBean)collector.get(key);
			int parsedID = Integer.parseInt( key);
			crontabEntryBean.setId(parsedID);
			retval.setId(parsedID);
		}catch(Throwable e){
			e.printStackTrace();
		}
		return retval;
	}

	private static String calcKey(CrontabEntryBean crontabEntryBean) {
		String className = crontabEntryBean.getClassName();
		String methodName = crontabEntryBean.getMethodName();
		String[] aExtraInfo = crontabEntryBean.getExtraInfo();
		String valTmp = calcVal(className, methodName, aExtraInfo);
		return ""+valTmp .hashCode();
	}

	private static String calcVal(String className, String methodName, String[] aExtraInfo) {
		String extraInfo = "";		
		if (aExtraInfo!=null)
		for (String ex:aExtraInfo){
			extraInfo+="&"+ex;
		}
		String valTmp = "jcrontab:/" + className+"/"+methodName+"/"+extraInfo;
		return valTmp;
	}

	private static String calcKey(CrontabBean crontabBean) {
		String className = crontabBean.getClassName();
		String methodName = crontabBean.getMethodName();
		String[] aExtraInfo = crontabBean.getExtraInfo();
		String valTmp = calcVal(className, methodName, aExtraInfo);
		return ""+valTmp .hashCode(); 
	}

}
