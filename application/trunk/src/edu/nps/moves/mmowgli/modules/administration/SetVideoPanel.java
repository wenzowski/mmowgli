package edu.nps.moves.mmowgli.modules.administration;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.modules.administration.VideoChangerComponent.ImDoneListener;
import edu.nps.moves.mmowgli.utility.BrowserWindowOpener;

public class SetVideoPanel extends _SetVideoPanel
{
  private static final long serialVersionUID = -7602553761674664899L;
  private Window win;
  private ImDoneListener doneListener;
  private Media media;
  
  public static String HTML5TAG = "HTML5 Video";
  public static String YOUTUBETAG = "YouTube Video";
  
  public static SetVideoPanel show(ImDoneListener lis)
  {
    Window win = showCommon(lis);
    win.center();
    return (SetVideoPanel)win.getContent();   
  }
  
  public static Window show(int xpos, int ypos)
  {
    Window win = showCommon(null);
    win.setPosition(xpos, ypos); 
    return win;   
  }
  
  private static Window showCommon(ImDoneListener lis)
  {
    Window win=new Window();
    win.setCaption("New Video Window");
    win.setContent(new SetVideoPanel(win,lis));
    win.setWidth("440px");
    win.setHeight(550+60,Unit.PIXELS); // 60=top & bottom window frame
    UI.getCurrent().addWindow(win); 
    return win;
  }
  
  public SetVideoPanel(Window win, ImDoneListener lis)
  {
    this.win = win;
    this.doneListener = lis;
    typeOptionGroup.removeAllItems();
    typeOptionGroup.addItem(HTML5TAG);
    typeOptionGroup.addItem(YOUTUBETAG);
    typeOptionGroup.setValue(HTML5TAG);
    html5FL.setEnabled(true);
    html5FL.setCaption(HTML5TAG);
    youTubeFL.setEnabled(false);
    youTubeFL.setCaption(YOUTUBETAG);
    
    typeOptionGroup.addValueChangeListener(typeListener);
    cancelButt.addClickListener(cancelListener);
    saveButt.addClickListener(saveListener);
    urlTestButt.addClickListener(urlTestHandler);
    youTubeIdTestButt.addClickListener(youTubeTestListener);
    
    heightTF.setConverter(new StringToIntegerConverter());
    widthTF.setConverter(new StringToIntegerConverter());
    urlTF.setValue(null);
    posterTF.setValue(null);
    heightTF.setValue(null);
    widthTF.setValue(null);
    
    heightTF.addValidator(posIntValidator);
    widthTF.addValidator(posIntValidator);
    urlTF.addValidator(urlValidator);
    posterTF.addValidator(posterUrlValidator);
    youtubeIdTF.addValidator(youtubeIdValidator);
  }
  
  private IntegerRangeValidator posIntValidator = new IntegerRangeValidator("Must be blank or positive integer",null,null);  
  private StringLengthValidator urlValidator = new StringLengthValidator("Full url is required", 10, 500, false) ;
  private StringLengthValidator posterUrlValidator = new StringLengthValidator("Full url is needed unless null", 10, 500, true) ;
  private StringLengthValidator youtubeIdValidator = new StringLengthValidator("Approx. 10 digit video ID required here",5,50,false);

  public Media getMedia()
  {
    return media;
  }
  
  @SuppressWarnings("serial")
  private ValueChangeListener typeListener = new ValueChangeListener() {
    @Override
    public void valueChange(com.vaadin.data.Property.ValueChangeEvent event)
    { 
     String s = event.getProperty().getValue().toString();
     html5FL.setEnabled(s.equals(HTML5TAG));
     youTubeFL.setEnabled(s.equals(YOUTUBETAG));       
    } 
  };
  
  @SuppressWarnings("serial")
  private ClickListener cancelListener = new ClickListener() {
    @Override
    public void buttonClick(ClickEvent event)
    {
      media = null;
      win.close();
      if(doneListener != null)
        doneListener.done();
    }   
  };
  
  @SuppressWarnings("serial")
  private ClickListener saveListener = new ClickListener() {
    @Override
    public void buttonClick(ClickEvent event)
    {
      String err = checkFields();
      if(err==null) {
        String typ = typeOptionGroup.getValue().toString();
        if(typ.equals(HTML5TAG)) {
          media = Media.newHtml5Video(urlTF.getValue(),posterTF.getValue(),titleTF.getValue(),
                                      descriptionTF.getValue(),longOrNull(widthTF.getValue()),longOrNull(heightTF.getValue()));
        }
        else if(typ.equals(YOUTUBETAG)) {
          media = Media.newYoutubeMedia(youtubeIdTF.getValue(), titleTF.getValue(), descriptionTF.getValue(),
                                          longOrNull(widthTF.getValue()), longOrNull(heightTF.getValue()));
        }
      
        win.close();
        if(doneListener != null)
          doneListener.done();
      }
      else
        Notification.show("Can't save: "+err);
    }    
  };
  
  @SuppressWarnings("serial")
  private ClickListener urlTestHandler = new ClickListener() {
    @Override
    public void buttonClick(ClickEvent event)
    {
      String url = urlTF.getValue();
      if(url == null)
        return;
      BrowserWindowOpener.open(url.trim(), "mmowgliVideoTest");
    }   
  };
  
  @SuppressWarnings("serial")
  private ClickListener youTubeTestListener = new ClickListener() {
    @Override
    public void buttonClick(ClickEvent event)
    {
      String id = youtubeIdTF.getValue();
      if(id == null)
        return;
      BrowserWindowOpener.open("https://www.youtube.com/watch?v="+id.trim(), "mmowgliVideoTest");
    }   
  };
 
  private Long longOrNull(String s)
  {
    if(s == null) return null;

    try {
      return Long.parseLong(s.trim());
    }
    catch(Throwable t) {
      System.err.println("String '"+s+"' does not contain a parseable long");
      return null;
    }
  }
  
  // null return == passed
  private String checkFields()
  {
    try {
      String s = widthTF.getValue();
      if(s != null)
        Integer.parseInt(s);
      s = heightTF.getValue();
      if(s != null)
        Integer.parseInt(s);
    }
    catch(Throwable t) {
      return "Bad number conversion";
    }
    
    String typ = typeOptionGroup.getValue().toString();
    if(typ.equals(HTML5TAG)) {
      return (urlTF.getValue() != null && urlTF.getValue().length()>10) ? null : "Need proper url";     
    }
    else if(typ.equals(YOUTUBETAG)) {
      return (youtubeIdTF.getValue() != null && youtubeIdTF.getValue().length()>5) ? null : "Need proper youtube ID";
    }
    return "Unknown video type";
  }
}
