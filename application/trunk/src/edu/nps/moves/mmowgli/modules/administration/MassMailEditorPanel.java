package edu.nps.moves.mmowgli.modules.administration;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.hbnutil.HbnContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.db.MailJob;
import edu.nps.moves.mmowgli.db.MailJob.Receivers;
import edu.nps.moves.mmowgli.hibernate.HSess;

public class MassMailEditorPanel extends _MassMailEditorPanel implements ClickListener, ValueChangeListener
{
  private static final long serialVersionUID = 6401443773214556617L;
  
  private Window win;
  private MailJob mailJob = null;
  private boolean editorDirty = false;
  private boolean fillingFields = false;
  
  public MassMailEditorPanel(Window w)
  {
    win = w;
    Container existingCont = new HbnContainer<MailJob>(MailJob.class,HSess.getSessionFactory());
    existingCombo.setContainerDataSource(existingCont);
    existingCombo.setItemCaptionPropertyId("subject");
    
    cancelButt.addClickListener(this);
    newMessageButt.addClickListener(this);
    openMailJobButt.addClickListener(this);

    saveButt.addClickListener(this);
    
    recipientOptionGroup.select("All signups");
    
    existingCombo.addValueChangeListener(this);
    recipientOptionGroup.addValueChangeListener(this);
    subjectTF.addValueChangeListener(this);
    richTextArea.addValueChangeListener(this);
    customTF.addValueChangeListener(this);
  }
  
  public static void showDialog()
  {
    Window win=new Window();
    win.setCaption("Mass Mail Edit Window");
    win.setContent(new MassMailEditorPanel(win));
    win.setWidth("850px");
    win.setHeight("780px");
    UI.getCurrent().addWindow(win);
    win.center();
  }
  
 /*
   * All Button clicks come here
   */
  @Override
  public void buttonClick(ClickEvent event)
  {
    Button butt = event.getButton();
    if(butt == this.cancelButt)
      handleCancel(event);
    else if(butt == this.newMessageButt)
      handleNewMessage(event);
    else if(butt == openMailJobButt)
      handleOpenJobWindow(event);
    else if(butt == this.saveButt)
      handleSave(event);
  }

  @Override
  public void valueChange(ValueChangeEvent event)
  {
    Property<?> prop = event.getProperty();
    if(prop == existingCombo) {
      handleSelectExisting();      
      return;
    }
    else if(prop == recipientOptionGroup) {
      customTF.setEnabled(recipientOptionGroup.getValue().toString().equalsIgnoreCase("custom list"));
      if(!fillingFields)
        diddleSaveButt(true);
    }
    else if(prop == richTextArea) {
      if(!fillingFields)
        diddleSaveButt(true);
    }
    else if(prop == subjectTF) {
      if(!fillingFields)
        diddleSaveButt(true);
    }
    else if(prop == customTF) {
      if(!fillingFields)
        diddleSaveButt(true);
    }
    if(!fillingFields)
      editorDirty=true;
  }

  @SuppressWarnings("serial")
  private boolean askDirtyEditorOK(final Continuer continuer)
  {
    if (editorDirty) {
      ConfirmDialog.show(UI.getCurrent(), "Stop:", "Do you want to discard pending edits?", "Yes", "No, cancel",
          new ConfirmDialog.Listener()
          {
            public void onClose(ConfirmDialog dialog)
            {
              if (dialog.isConfirmed())
                continuer.further();
            }
          });
      return false;
    }
    return true;
  }
  
  private void handleCancel(ClickEvent e)
  {
    if(!askDirtyEditorOK(new Continuer(){public void further(){_handleCancel();}}))
      return;
    _handleCancel();
  }
  
  private void _handleCancel()
  {
    UI.getCurrent().removeWindow(win);
  }
  
  private void handleNewMessage(ClickEvent e)
  {
    if(!askDirtyEditorOK(new Continuer(){public void further(){_handleNewMessage();}}))
      return;
    _handleNewMessage();
  }
  
  private void _handleNewMessage()
  {
    subjectTF.setEnabled(true);
    richTextArea.setEnabled(true);
    recipientsHL.setEnabled(true);
    
    clearWidgets();
    mailJob = null;
    diddleSaveButt(true);
    diddleCreateWidgets(false);
    editorDirty=false;
  }

  private void handleOpenJobWindow(ClickEvent e)
  {
    if (editorDirty)
      Notification.show("Save your message first");
    //else if (mailJob == null)
      //Notification.show("First choose and select an existing message or create and save a new one.");
    else {
      MassMailJobPanel.show(mailJob);
      _handleCancel(); // close
    }
  }

  private void handleSave(ClickEvent e)
  {
    HSess.init();
    if(mailJob != null)
      saveExistingTL();
    else
      saveNewTL();
    diddleCreateWidgets(true);
    editorDirty=false;
    existingCombo.setValue(mailJob.getId());
    
    diddleSaveButt(false);
    HSess.close();
  }
      
  private void handleSelectExisting()
  {
    boolean valid  = existingCombo.getValue() != null && existingCombo.getValue().toString().length()>0;
    if(!valid )
      return;
    if(!askDirtyEditorOK(new Continuer(){public void further(){_handleSelectExisting();}}))
      return;
    _handleSelectExisting();
  }
  
  private void _handleSelectExisting()
  {
    Object key = HSess.checkInit();
    
    Long id =(Long) existingCombo.getValue();
    mailJob = (MailJob)HSess.get().get(MailJob.class, id);
    fillFieldsTL(mailJob);
    diddleCreateWidgets(false);
    editorDirty=false;
    
    subjectTF.setEnabled(true);
    recipientsHL.setEnabled(true);
    richTextArea.setEnabled(true);
    
    HSess.checkClose(key);
  }
  
  private void diddleSaveButt(boolean save)
  {
    saveButt.setEnabled(save);
  }
  
  private void diddleCreateWidgets(boolean b)
  {
    newMessageButt.setEnabled(b);
    existingCombo.setEnabled(b);
  }
  
  private void fillFieldsTL(MailJob mj)
  {
    fillingFields = true;
    subjectTF.setValue(mj.getSubject());
    recipientOptionGroup.setValue(mj.getReceivers().toString());
    richTextArea.setValue(mj.getText());
    fillingFields = false;
  }
  
  private void fillObject()
  {
    mailJob.setSubject(subjectTF.getValue().trim());
    mailJob.setText(richTextArea.getValue().trim());
    System.out.println(mailJob.getText());
    mailJob.setReceivers(Receivers.fromString(recipientOptionGroup.getValue().toString()));
  }
  
  private void clearWidgets()
  {
    subjectTF.setValue(null);
    richTextArea.setValue(null);
  }
  
  /* update the database object whose Id is in mailJob with the contents of the fields */
  private void saveExistingTL()
  {
    mailJob = (MailJob)HSess.get().merge(mailJob);
    fillObject();
    HSess.get().update(mailJob);
  }
  
  private void saveNewTL()
  {
    mailJob = new MailJob();
    fillObject();
    HSess.get().save(mailJob);
  }
  
  interface Continuer {
    public void further();
  }
}
