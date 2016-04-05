package edu.nps.moves.mmowgli.modules.administration;

import java.util.Collection;

import com.vaadin.data.hbnutil.HbnContainer;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.db.MailJob;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.modules.gamemaster.GameEventLogger;
import edu.nps.moves.mmowgli.utility.MailJobber;

public class MassMailJobPanel extends _MassMailJobPanel implements SelectionListener
{
  private static final long serialVersionUID = -2223465617366209803L;
  private String[] columns = {"subject","receivers","complete","status","whenStarted","whenCompleted"};

  public static void show (MailJob job)
  {
    Window win=new Window();
    win.setCaption("Mass Mail Job Window");
    win.setContent(new MassMailJobPanel(job,win));
    win.setWidth("950px");
    win.setHeight("780px");
    UI.getCurrent().addWindow(win);
    win.center();    
  }
  
  private Window win;
  public MassMailJobPanel(MailJob job, Window win)
  {
    this.win = win;
    if(job != null)
     fillWidgets(job);
    
    topGridLay.setColumnExpandRatio(1, 1.0f);
    HbnContainer<MailJob> cont = new HbnContainer<MailJob>(MailJob.class,HSess.getSessionFactory());
    grid.setContainerDataSource(cont);
    grid.setColumns((Object[])columns);
    grid.getColumn("subject").setMaximumWidth(300.d);
    grid.setSelectionMode(SelectionMode.SINGLE);

    grid.addSelectionListener(this);
    if(job != null)
      grid.select(job.getId());
    
    closeButt.addClickListener(closeListener);
    scheduleButt.addClickListener(scheduleListener);
    
    if(job == null && cont.size()>0) {
      Collection<?> ids = cont.getItemIds();
      grid.select(ids.toArray()[0]);
    }
  }
  
  @SuppressWarnings("serial")
  ClickListener closeListener = new ClickListener()
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      UI.getCurrent().removeWindow(win);
    }
  };
      
  @SuppressWarnings("serial")
  ClickListener scheduleListener = new ClickListener()
  {
    @Override
    public void buttonClick(ClickEvent event)
    {
      MailJobber.submitJob(getSelected());
      GameEventLogger.logMassMailJobSubmitted(Mmowgli2UI.getGlobals().getUserName());
    }
  };
  
  private void fillWidgets(MailJob job)
  {
    subjectLabel.setValue(job.getSubject());
    toLabel.setValue(job.getReceivers().toString());
    textLabel.setValue(job.getText());
  }
  
  @Override
  public void select(SelectionEvent event)
  {
    MailJob job = getSelected();
    fillWidgets(job);
  }
  
  private MailJob getSelected()
  {
    Object id = grid.getSelectedRow();
    if(id == null) return null;
    
    @SuppressWarnings("rawtypes")
    HbnContainer.EntityItem o = (HbnContainer.EntityItem)grid.getContainerDataSource().getItem(id);
    MailJob job = (MailJob) o.getPojo();
    return job;
  }
}
