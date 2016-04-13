package edu.nps.moves.mmowgli.modules.actionplans;

import org.vaadin.alump.scaleimage.ScaleImage;

import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.components.MmowgliComponent;
import edu.nps.moves.mmowgli.db.Media;
import edu.nps.moves.mmowgli.db.Media.MediaType;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.markers.HibernateClosed;
import edu.nps.moves.mmowgli.markers.HibernateOpened;
import edu.nps.moves.mmowgli.markers.MmowgliCodeEntry;
import edu.nps.moves.mmowgli.utility.MediaLocator;

public class MediaPanel2 extends _MediaPanel2 implements MmowgliComponent
{
  private static final long serialVersionUID = -8030240768527111648L;
  
  public static String WIDTH = "309px";
  private String PLAYER_HEIGHT = "207px"; 
  private int PLAYERINDEX_IN_LAYOUT = 1;
  
  NativeButton zoom;
  Media m;
  ClickListener scaler;
  Object apId;
  int idx = -1;

  ScaleImage scaledImage;
  Component mediaPlayer;
  Label placeHolder;

  private boolean titleFocused=false;
  private boolean captionFocused = false;
  
  MediaPanel2(Media m, Object apId, int idx, ClickListener replaceListener)
  {
    this.idx = idx;
    this.m = m;
    
    zoom = new NativeButton();
    
    ThisFocusHandler fHandler = new ThisFocusHandler();
    caption.addFocusListener(fHandler);
    title.addFocusListener(fHandler);     
    canButt.addClickListener(fHandler);
    saveButt.addClickListener(fHandler);
  }
  
  public void setIndex(int i)
  {
    indexLab.setValue("<b style='font-size:150%'>"+i+"</b>");    
  }
  
  class ThisFocusHandler implements FocusListener,ClickListener
  {
    private static final long serialVersionUID = -5412529699678903650L;

    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void focus(FocusEvent event)
    {
      String s = "";
      if(event.getSource() == caption) {
        if(caption.isReadOnly())
          return; 
        caption.selectAll();
        s = "caption ";
        captionFocused=true;
      }
      else if(event.getSource() == title) {
        if(title.isReadOnly())
          return;
        title.selectAll();
        s = "title ";
        titleFocused=true;
      }
      HSess.init();
      captionSavePan.setVisible(true);
      String substring = m.getType()==MediaType.IMAGE?" is editing image "+s+"number ":" is editing video "+s+"number ";
      sendStartEditMessage(Mmowgli2UI.getGlobals().getUserTL().getUserName()+substring+(idx+1));
      HSess.close();
    }
   
    @Override
    @MmowgliCodeEntry
    @HibernateOpened
    @HibernateClosed
    public void buttonClick(ClickEvent event)
    {
      HSess.init();
      if(event.getSource()  == canButt) {
        m = Media.getTL(m.getId());  // might have changed under us, and we don't update fields being edited
        setValueIfNonNull(caption,m.getDescription());
        setValueIfNonNull(title,m.getTitle());
      }
      else { // Save
        m.setDescription(nullOrString(caption.getValue()));
        m.setTitle(nullOrString(title.getValue()));
        Media.updateTL(m);
      }
      captionSavePan.setVisible(false);
      titleFocused = false;
      captionFocused = false;
      HSess.close();
    }
  }
  
  public void setIdx(int idx)
  {
    this.idx = idx;
  }
  
  private String nullOrString(Object o)
  {
    if(o == null)
      return null;
    return o.toString();
  }
  
  public void setReadOnly(boolean ro)
  {
    caption.setReadOnly(ro);
    title.setReadOnly(ro);
  }
  
  public void setMedia(Media m)
  {
    this.m = m;
    MediaType mt = m.getType();
    if(MediaType.IMAGE == mt)
      setImageMedia(m);
    else if(MediaType.YOUTUBE == mt || MediaType.HTML5VIDEO == mt)
      setVideoMedia(m);
  }
  
  public void mediaUpdatedOobTL()
  {
    Media oobM = getOobMediaTL(null);
    if(oobM == null)  // may be null if image removed
      return;
    if(!titleFocused)
      setTitleVal(oobM);
    if(!captionFocused)
      setCaptionVal(oobM);
  }

  private Media getOobMediaTL(Media oobM)
  {
    if(oobM != null)
      return oobM;
    return (Media)HSess.get().get(Media.class,m.getId()); // this can be null if the media was deleted
  }
  
  public Media getMedia()
  {
    return m;
  }

  private Component buildPlayer(Media m)
  {
    if (m.getType() == MediaType.YOUTUBE) 
      return buildYoutubePlayer(m);
    else if(m.getType() == MediaType.HTML5VIDEO)
      return buildHtml5Player(m); 
    else
      return new Label("Unsupported video type");
  }
  private Component buildHtml5Player(Media m)
  {
    Video vid = new Video();
    vid.addStyleName("m-htmlvideo");  // sizes properly
    Resource res = new ExternalResource(m.getUrl());
    vid.setSource(res);
    vid.setCaption(null);
    vid.setSizeFull();
    vid.setPoster(new ExternalResource("http://movesinstitute.org/~jmbailey/videos/vlcsnap-00001.png"));
    vid.setHtmlContentAllowed(true);
    vid.setAltText("Can't play media");
    VerticalLayout playerVL = new VerticalLayout();
    playerVL.addStyleName("m-background-black");
    playerVL.addComponent(vid);
    playerVL.setWidth("305px");
    playerVL.setHeight("210px"); //to match youtube PLAYER_HEIGHT);
    VerticalLayout wrapper = new VerticalLayout();
    wrapper.setSizeUndefined();
    wrapper.setSpacing(true);
    wrapper.setMargin(false);
    wrapper.addComponent(playerVL);
    return wrapper;
  }
  private Component buildYoutubePlayer(Media m)
  {
    try {
      Flash ytp = new Flash();
      ytp.setSource(new ExternalResource("https://www.youtube.com/v/" + m.getUrl()));
      ytp.setParameter("allowFullScreen", "true");
      ytp.setParameter("showRelated", "false");
      ytp.setWidth("305px");
      ytp.setHeight(PLAYER_HEIGHT);

      placeHolder = new Label("Mmowgli Video");
      placeHolder.setWidth(WIDTH);
      placeHolder.setHeight(PLAYER_HEIGHT);

      return ytp;
    }
    catch(Exception e) {
      return new Label("Wrong media type");
    }
  }
  
  private AbsoluteLayout buildImage(Media m)
  {
    MediaLocator mLoc = Mmowgli2UI.getGlobals().getMediaLocator();
    AbsoluteLayout imageStack = new AbsoluteLayout();
    imageStack.setWidth(WIDTH);
    imageStack.setHeight(PLAYER_HEIGHT);
    scaledImage = new ScaleImage();
    scaledImage.setSource(mLoc.locate(m));
    scaledImage.setWidth(WIDTH);
    scaledImage.setHeight(PLAYER_HEIGHT);
    imageStack.addComponent(scaledImage,"top:0px;left:0px");
    zoom.setIcon(mLoc.getActionPlanZoomButt());
    zoom.addStyleName("m-actionplan-zoom-button");
    zoom.addStyleName("borderless");
    imageStack.addComponent(zoom, "top:10px;left:10px");
    return imageStack;
  }
    
  private void setVideoMedia(Media m)
  {
    this.m = m;
    Component comp = buildPlayer(m);
    if(mediaPlayer != null)
      removeComponent(mediaPlayer);
    addComponent(mediaPlayer = comp, PLAYERINDEX_IN_LAYOUT);
    
    setCaptionAndTitle(m);
    
    mediaPlayer = comp;
  }

  private void setCaptionAndTitle(Media m)
  {
    setCaptionVal(m);
    setTitleVal(m);
  }
  
  private void setCaptionVal(Media m)
  {
    boolean isRo = caption.isReadOnly();
    caption.setReadOnly(false);
    setValueIfNonNull(caption,m.getDescription());// "caption" here is "description" in db   
    caption.setReadOnly(isRo);   
  }
  private void setTitleVal(Media m)
  {
    boolean isRo = title.isReadOnly();
    title.setReadOnly(false);
    setValueIfNonNull(title,m.getTitle());
    title.setReadOnly(isRo);
    
  }
  private void setImageMedia(Media m)
  {
    this.m = m;
    Component comp = buildImage(m);
    if(mediaPlayer != null)
      removeComponent(mediaPlayer);
    addComponent(mediaPlayer = comp, PLAYERINDEX_IN_LAYOUT);
    scaledImage.setSource(Mmowgli2UI.getGlobals().getMediaLocator().locate(m));
    if (scaler != null)
      zoom.removeClickListener(scaler);
    zoom.addClickListener(scaler = new Scaler(m));
    
    setCaptionAndTitle(m);
   
    mediaPlayer = comp;
  }

  private void setValueIfNonNull(AbstractTextField comp, String s)
  {
    if(s != null)
      comp.setValue(s);
  }
  
  @Override
  public void initGui()
  {       
    setMedia(m);
  }
 
  public  void sendStartEditMessage(String msg)
  {
  }
  
  public void enableVideo()
  {
    hideVideo(false);
  }
  
  public void disableVideo()
  {
    hideVideo(true);
  }
  
  private void hideVideo(boolean tf)
  {
    if(tf) {
      replaceComponent(mediaPlayer, placeHolder); //PLAYERINDEX_IN_LAYOUT
    }
    else {
      replaceComponent(placeHolder,mediaPlayer);
    }
  }
  class Scaler implements ClickListener
  {
    private static final long serialVersionUID = -6183261170803030233L;

    Media m;

    Scaler(Media m)
    {
      this.m = m;
    }
    
    ScaleImage image;
    // We now skip trying to get the size of the image -- we were trying to do that to manage aspect ratio.
    // 1. Doing ImageIO.read() was failing because some URLs visible from the client (browser) were not visible from the server (JVM).
    // 2. Eliminates the need for ImageScaler plugin.
    // 3. Uses browser's ability to zoom an img element when window size changes.
    // 4. Downside, can't get client code to report size of rendered image.
    
    public void buttonClick(ClickEvent event)
    {
      Resource r = Mmowgli2UI.getGlobals().getMediaLocator().locate(m);
      MediaSubWindow win = new MediaSubWindow(r);
      UI.getCurrent().addWindow(win);
      win.center();
    }
  }
}

