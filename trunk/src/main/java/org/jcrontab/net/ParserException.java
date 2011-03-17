package org.jcrontab.net;
 

/** 
 * <b>Description:TODO</b>
 * @author      vipup<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  06.09.2010::14:49:43<br> 
 */
public class ParserException extends Exception{

	/**
	 * @author vipup
	 */
	private static final long serialVersionUID = -5131995947493298376L;

	public ParserException(String string, Exception uhe) {
		super(string,uhe);
	}

}


 