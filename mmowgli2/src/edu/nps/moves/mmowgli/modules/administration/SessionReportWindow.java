/*
  Copyright (C) 2010-2014 Modeling Virtual Environments and Simulation
  (MOVES) Institute at the Naval Postgraduate School (NPS)
  http://www.MovesInstitute.org and http://www.nps.edu
 
  This file is part of Mmowgli.
  
  Mmowgli is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  Mmowgli is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with Mmowgli, in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/

package edu.nps.moves.mmowgli.modules.administration;

import java.util.Arrays;

import com.vaadin.data.Item;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.AppMaster;

/**
 * SessionReportWindow.java created on Jan 29, 2015
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class SessionReportWindow extends Window
{
  private static final long serialVersionUID = -1106920231938294885L;

  public static void showSessionReport()
  {
    Window me = new SessionReportWindow("Logged-in Player Report");
    UI.getCurrent().addWindow(me);
    me.center();
  }
  
  private int widestRowSize = 0;
  private Table table;
  public SessionReportWindow(String caption)
  {
    super(caption);
    setWidth("700px");
    setHeight("400px");
    VerticalLayout vLay = new VerticalLayout();
    vLay.setSizeFull();
    setContent(vLay);
    
    String header = AppMaster.instance().getSessionReportHeader();
    String[] _headerArr = header.split("\t");
    widestRowSize=_headerArr.length;
    
    String me = AppMaster.instance().getLocalNodeReportRaw().toString();
    me = AppMaster.instance().getServerName()+"\n"+me;
    String[][] localReport = parseReport(me);
        
    String all = AppMaster.instance().getCompletePlayerReportRaw().toString();
    String[][] remoteReports = parseReport(all);
    
    String[] headerArr;
    if(widestRowSize>_headerArr.length) {
      headerArr = new String[widestRowSize];
      Arrays.fill(headerArr," ");
      for(int i=0;i<_headerArr.length;i++)
        headerArr[i]=_headerArr[i];       
    }
    else
      headerArr = _headerArr;
    
    table = new Table();
    table.setSizeFull();
    
    for(int col=0; col<headerArr.length; col++)
      table.addContainerProperty(headerArr[col], String.class, null);
    
    addReportToTable(headerArr, localReport);
    addReportToTable(headerArr, remoteReports);
    
    vLay.addComponent(table);
  }
  
  @SuppressWarnings("unchecked")
  private void addReportToTable(String[] header, String[][] report)
  {
    
    for(int r=0;r<report.length;r++) {
      Item row = table.getItem(table.addItem());    
      String[] rowArray = report[r];
      for(int col=0; col<rowArray.length;col++) {
        row.getItemProperty(header[col]).setValue(rowArray[col]);
      }
    }    
  }
  
  String[][] parseReport(String s)
  {
    String[] rows = s.split("\n");
    String[][] grid = new String[rows.length][];
    int r=0;
    for(String row : rows) {
      String[] cols = row.split("\t");
      if(cols.length > widestRowSize)
        widestRowSize = cols.length;
      grid[r++]=cols;
    }
    return grid;
  }
}
