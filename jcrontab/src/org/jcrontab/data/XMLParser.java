/**
 *  This file is part of the jcrontab package
 *  Copyright (C) 2001-2003 Israel Olalla
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free
 *  Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA 02111-1307, USA
 *
 *  For questions, suggestions:
 *
 *  iolalla@yahoo.com
 *
 */
package org.jcrontab.data;
 
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.io.*;
import java.util.*;
import java.text.*;
import org.jcrontab.data.CrontabEntryBean;

/** 
 * Manages the creation and execution of all the scheduled tasks 
 * of jcrontab. This class is the core of the jcrontab
 * @author $Author: iolalla $
 * @version $Revision: 1.3 $
 */

public class XMLParser extends DefaultHandler {

	// Flags to help us capture the contents 
	// of a tagged element.
	private Vector list = new Vector();
	private CrontabEntryBean ceb = null;

	// Buffer for collecting data from
	// the "characters" SAX event.
	private CharArrayWriter contents = new CharArrayWriter();
    private String formatDate = "dd/MM/yy";


	// Override methods of the DefaultHandler class
	// to gain notification of SAX Events.
	//
    // See org.xml.sax.ContentHandler for all available events.
    /**
    *@see org.xml.sax.helpers.DefaultHandler
    */
	public void startElement( String namespaceURI,
			 	  String localName,
				  String qName,
				  Attributes attr ) throws SAXException {
		contents.reset();
        if ( localName.equals("crontabentry")) {
                ceb = new CrontabEntryBean();
                ceb.setId(Integer.parseInt(attr.getValue("id")));
                list.addElement( ceb );
        }
        if ( localName.equals("enddate") || localName.equals("startdate")) {
            if ( attr.getValue("format") != null) 
                    formatDate = attr.getValue("format");
		}
	}
	/**
    *@see org.xml.sax.helpers.DefaultHandler
    */
	public void endElement( String namespaceURI,
			 	  String localName,
				  String qName ) throws SAXException {
        if ( localName.equals( "seconds" ) ) {
			ceb.setSeconds(contents.toString().trim());
		}
        if ( localName.equals( "minutes" ) ) {
			ceb.setMinutes(contents.toString().trim());
		}	
        if ( localName.equals( "hours" ) ) {
			ceb.setHours(contents.toString().trim());
		}	
        if ( localName.equals( "daysofweek" ) ) {
			ceb.setDaysOfWeek(contents.toString().trim());
		}	
        if ( localName.equals( "daysofmonth" ) ) {
			ceb.setDaysOfMonth(contents.toString().trim());
		}
        if ( localName.equals( "years" ) ) {
			ceb.setYears(contents.toString().trim());
		}	
        if ( localName.equals( "class" ) ) {
			ceb.setClassName(contents.toString().trim());
		}	
        if ( localName.equals( "method" ) ) {
			ceb.setMethodName(contents.toString().trim());
		}
		if ( localName.equals("startdate" )) {
            ParsePosition parsePosition = new ParsePosition(0);
            SimpleDateFormat sdf = new SimpleDateFormat(formatDate);
            Date date = sdf.parse(contents.toString().trim(), parsePosition);
			ceb.setStartDate(date);
		}
        if ( localName.equals("enddate" )) {
            ParsePosition parsePosition = new ParsePosition(0);
            SimpleDateFormat sdf = new SimpleDateFormat(formatDate);
            Date date = sdf.parse(contents.toString().trim(), parsePosition);
			ceb.setEndDate(date);
		}
        if ( localName.equals("parameters" )) {
            String[] result = contents.toString().trim().split("\\s");
			ceb.setExtraInfo(result);
		}
        if ( localName.equals("description" )) {
			ceb.setDescription(contents.toString().trim());
		}
	}
    
	private Vector getList() {
        	return list;
	}
    /**
    * Convert the array of CrontabEntryBean to a valid xml representation of
    * them, basically calling to the toXML() method of the CrontabEntryBean
    * @param String xmlFile the xmlFile where the CrontabEntryBean are stored
    * @return String The xml representing all this Beans
    */
	public CrontabEntryBean[] unMarshall(String xmlFile) throws Exception {
			// Create SAX 2 parser...
			XMLReader xr = XMLReaderFactory.createXMLReader();
			// Set the ContentHandler...
			XMLParser parser = new XMLParser();
			xr.setContentHandler( parser );
			// Parse the file...
			xr.parse( new InputSource( 
					  new FileReader(xmlFile)));
                      
			Vector items = parser.getList();
            CrontabEntryBean[] cebs = new CrontabEntryBean[items.size()];
            for (int i = 0; i < cebs.length; i++) {
               cebs[i] = (CrontabEntryBean)items.get(i);
            }
            return cebs;
	}
    /**
    * Convert the array of CrontabEntryBean to a valid xml representation of
    * them, basically calling to the toXML() method of the CrontabEntryBean
    * @param CrontabEntryBean[] the CrontabEntryBeans
    * @return String The xml representing all this Beans
    */
    public String marshall(CrontabEntryBean[] cebs) {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<!DOCTYPE crontab SYSTEM \"crontab.dtd\">");
        sb.append("<crontab>");
        for (int i=0; i<cebs.length;i++)
            sb.append(cebs[i].toXML());
        sb.append("</crontab>");
        return sb.toString();
    }
}
