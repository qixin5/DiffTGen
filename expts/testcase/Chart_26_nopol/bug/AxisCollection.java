/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -------------------
 * AxisCollection.java
 * -------------------
 * (C) Copyright 2003-2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: AxisCollection.java,v 1.2.2.1 2005/10/25 20:37:34 mungady Exp $
 *
 * Changes
 * -------
 * 03-Nov-2003 : Added standard header (DG);
 *
 */

package org.jfree.chart.axis;

import java.util.List;

import org.jfree.chart.util.RectangleEdge;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import myprinter.FieldPrinter;

/**
 * A collection of axes that have been assigned to the TOP, BOTTOM, LEFT or 
 * RIGHT of a chart.  This class is used internally by JFreeChart, you won't
 * normally need to use it yourself.
 */
public class AxisCollection {
    
    public static Map oref_map = new HashMap();

	public static int eid_7au3e = 0;

	public static void addToORefMap(String msig, Object obj) {
		List l = (List) oref_map.get(msig);
		if (l == null) {
			l = new ArrayList();
			oref_map.put(msig, l);
		}
		l.add(obj);
	}

	public static void clearORefMap() {
		oref_map.clear();
		eid_7au3e = 0;
	}

	/** The axes that need to be drawn at the top of the plot area. */
    private List axesAtTop;
    
    /** The axes that need to be drawn at the bottom of the plot area. */
    private List axesAtBottom;
    
    /** The axes that need to be drawn at the left of the plot area. */
    private List axesAtLeft;
    
    /** The axes that need to be drawn at the right of the plot area. */
    private List axesAtRight;
   
    /**
     * Creates a new empty collection.
     */ 
    public AxisCollection() {
        this.axesAtTop = new java.util.ArrayList();
        this.axesAtBottom = new java.util.ArrayList();
        this.axesAtLeft = new java.util.ArrayList();
        this.axesAtRight = new java.util.ArrayList();
    }
    
    /**
     * Returns a list of the axes (if any) that need to be drawn at the top of 
     * the plot area.
     * 
     * @return A list of axes.
     */
    public List getAxesAtTop() {
        return this.axesAtTop;
    }
    
   /**
    * Returns a list of the axes (if any) that need to be drawn at the bottom 
    * of the plot area.
    * 
    * @return A list of axes.
    */
   public List getAxesAtBottom() {
        return this.axesAtBottom;
    }
    
    /**
     * Returns a list of the axes (if any) that need to be drawn at the left 
     * of the plot area.
     * 
     * @return A list of axes.
     */
    public List getAxesAtLeft() {
        return this.axesAtLeft;
    }
    
    /**
    * Returns a list of the axes (if any) that need to be drawn at the right 
    * of the plot area.
    * 
    * @return A list of axes.
    */
    public List getAxesAtRight() {
        return this.axesAtRight;    
    }
    
    /**
     * Adds an axis to the collection.
     * 
     * @param axis  the axis (<code>null</code> not permitted).
     * @param edge  the edge of the plot that the axis should be drawn on 
     *              (<code>null</code> not permitted).
     */
    public void add_7au3e(Axis axis, RectangleEdge edge) {
        if (axis == null) {
            throw new IllegalArgumentException("Null 'axis' argument.");   
        }
        if (edge == null) {
            throw new IllegalArgumentException("Null 'edge' argument.");   
        }
        if (edge == RectangleEdge.TOP) {
            this.axesAtTop.add(axis);
        }
        else if (edge == RectangleEdge.BOTTOM) {
            this.axesAtBottom.add(axis);
        }
        else if (edge == RectangleEdge.LEFT) {
            this.axesAtLeft.add(axis);
        }
        else if (edge == RectangleEdge.RIGHT) {
            this.axesAtRight.add(axis);
        }
    }

	/**
	 * Adds an axis to the collection.
	 * @param axis   the axis (<code>null</code> not permitted).
	 * @param edge   the edge of the plot that the axis should be drawn on  (<code>null</code> not permitted).
	 */
	public void add(Axis axis, RectangleEdge edge) {
		Object o_7au3e = null;
		String c_7au3e = "org.jfree.chart.axis.AxisCollection";
		String msig_7au3e = "add(Axis$RectangleEdge)" + eid_7au3e;
		try {
			add_7au3e(axis, edge);
			addToORefMap(msig_7au3e, null);
			FieldPrinter.print(this, eid_7au3e, c_7au3e, msig_7au3e, 1, 5);
			addToORefMap(msig_7au3e, this);
			FieldPrinter.print(axis, eid_7au3e, c_7au3e, msig_7au3e, 2, 5);
			addToORefMap(msig_7au3e, axis);
			FieldPrinter.print(edge, eid_7au3e, c_7au3e, msig_7au3e, 3, 5);
			addToORefMap(msig_7au3e, edge);
		} catch (Throwable t7au3e) {
			FieldPrinter.print(t7au3e, eid_7au3e, c_7au3e, msig_7au3e, 0, 5);
			addToORefMap(msig_7au3e, t7au3e);
			throw t7au3e;
		} finally {
			eid_7au3e++;
		}
	}

}
