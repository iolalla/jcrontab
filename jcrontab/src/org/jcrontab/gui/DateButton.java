/**
 *  This file is part of the jcrontab package
 *  Copyright (C) 2002 Alexandre Pereira Calsavara
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
package org.jcrontab.gui;
 
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;



/**
 * Custom button for entering dates. The <code>DateButton</code> class
 * is just a standard button that defines an additional bound
 * property: "date". The button displays the date property as its
 * label. When clicked, it does not generate an
 * <code>ActionEvent</code>, but displays a {@link DateChooser} dialog
 * instead, that allows you to change the date. When the date is
 * changed, a <code>PropertyChangeEvent</code> is generated, according
 * the contract for bound properties.
 * @author $Author: iolalla $
 * @version $Revision: 1.1 $
 **/
public class DateButton extends JButton
{
    /** Format to use to display the date property. */
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy");

    /** DateChooser instance to use to change the date. */
    private static final DateChooser DATE_CHOOSER = new DateChooser((JFrame)null,"Select Date");

    /** Date property. */
    private Date date;



    /**
     * Called when the button is clicked, in order to fire an
     * <code>ActionEvent</code>. Displays the dialog to change the
     * date instead of generating the event and updates the date
     * property.
     *
     * @param e <code>ActionEvent</code> to fire
     **/
    protected void fireActionPerformed( ActionEvent e ) {
	Date newDate = DATE_CHOOSER.select(date);
	if ( newDate == null )
	    return;
	setDate( newDate );
    }



    /**
     * Constructs a new <code>DateButton</code> object with a given
     * date.
     *
     * @param date initial date
     **/
    public DateButton( Date date ) {
	super( DATE_FORMAT.format(date) );
	this.date = date;
    }



    /**
     * Constructs a new <code>DateButton</code> object with the system
     * date as the initial date.
     **/
    public DateButton() {
	this( new Date() );
    }



    /**
     * Gets the value of the date property.
     *
     * @return the current value of the date property
     **/
    public Date getDate() {
	return date;
    }



    /**
     * Sets the valus of the date property.
     *
     * @param date new value of the date property
     *
     * @return the old value of the date property
     **/
    public void setDate( Date date ) {
	Date old = this.date;
	this.date = date;
	setText( DATE_FORMAT.format(date) );
	firePropertyChange( "date", old, date );
    }
}
