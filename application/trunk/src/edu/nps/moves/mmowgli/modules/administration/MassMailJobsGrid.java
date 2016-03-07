package edu.nps.moves.mmowgli.modules.administration;

import com.vaadin.data.hbnutil.HbnContainer;

import edu.nps.moves.mmowgli.db.MailJob;
import edu.nps.moves.mmowgli.hibernate.HSess;

public class MassMailJobsGrid extends _MassMailJobsGrid
{
  private static final long serialVersionUID = 2023290574677891973L;
  private String[] columns = {"subject","receivers","complete","status","whenStarted","whenCompleted"};
  public MassMailJobsGrid()
  {
    HbnContainer<MailJob> cont = new HbnContainer<MailJob>(MailJob.class,HSess.getSessionFactory());
    grid.setContainerDataSource(cont);
    grid.setColumns((Object[])columns);
    grid.getColumn("subject").setMaximumWidth(300.d);
  }
}
