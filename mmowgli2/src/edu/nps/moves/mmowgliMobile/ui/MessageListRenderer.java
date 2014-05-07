package edu.nps.moves.mmowgliMobile.ui;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

import edu.nps.moves.mmowgli.db.*;
import edu.nps.moves.mmowgliMobile.data.*;
import edu.nps.moves.mmowgliMobile.data.Message;

/**
 * MessageListRenderer.java Created on Feb 26, 2014
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public abstract class MessageListRenderer
{
  public final String NORMAL_STYLE_NAME = "m-messageList-normal";
  public final String ITALIC_STYLE_NAME = "m-messageList-italics";
  public final String BOLD_STYLE_NAME = "m-messageList-bold";
  public final String ITALIC_BOLD_STYLE_NAME = "m-messageList-italic-bold";
  
  abstract public void setMessage(Message msg, MessageHierarchyView messageList, CssLayout layout);

  protected SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy hh:mm");
  protected StringBuilder sb = new StringBuilder();
  
  protected Serializable getPojoId(Message msg)
  {
    if (msg instanceof WrappedCard)
      return ((WrappedCard) msg).getCard().getId();
    if (msg instanceof WrappedUser)
      return ((WrappedUser) msg).getUser().getId();
    if (msg instanceof WrappedActionPlan)
      return ((WrappedActionPlan) msg).getActionPlan().getId();
    return null;
  }

  private void _text(String styleName, StringBuilder sb, Object ... strs)
  {
    sb.append("<span class='");
    sb.append(styleName);
    sb.append("'>");
    for(Object s : strs)
      sb.append(s.toString());
    sb.append("</span>");   
  }
  
  protected void italicText(StringBuilder sb, Object ... strs)
  {
    _text(ITALIC_STYLE_NAME,sb,strs);
  }
  protected void italicBoldText(StringBuilder sb, Object ... strs)
  {
    _text(ITALIC_BOLD_STYLE_NAME,sb,strs);
  }
  protected void normalText(StringBuilder sb, Object ... strs)
  {
    _text(NORMAL_STYLE_NAME,sb,strs);
  }
  protected void boldText(StringBuilder sb, Object ... strs)
  {
    _text(BOLD_STYLE_NAME,sb,strs);
  }
  protected void text(StringBuilder sb, Object ...strs)
  {
    normalText(sb,strs);
  }
  private static UserListRenderer u_rend = null;
  private static ActionPlanListRenderer ap_rend = null;
  private static CardListRenderer c_rend = null;

  public static UserListRenderer u()
  {
    if (u_rend == null)
      u_rend = new UserListRenderer();
    return u_rend;
  }
  public static ActionPlanListRenderer ap()
  {
    if(ap_rend == null)
      ap_rend = new ActionPlanListRenderer();
    return ap_rend;
  }
  public static CardListRenderer c()
  {
    if(c_rend == null)
      c_rend = new CardListRenderer();
    return c_rend;
  }
  
  public static class CardListRenderer extends MessageListRenderer
  {
    private CardListRenderer(){}
    @Override
    public void setMessage(Message message, MessageHierarchyView messageList, CssLayout layout)
    {
      WrappedCard wc = (WrappedCard) message;
      Card c = wc.getCard();
      layout.removeAllComponents();      
      sb.setLength(0);
      
      boldText(sb,c.getId());
      sb.append(". ");
      normalText(sb,c.getText());
      sb.append(" ");
      italicBoldText(sb,c.getAuthorName());
      
      layout.addComponent(new Label(sb.toString(),ContentMode.HTML));
    }
  }

  public static class ActionPlanListRenderer extends MessageListRenderer
  {
    private ActionPlanListRenderer(){}
    @Override
    public void setMessage(Message msg, MessageHierarchyView messageList, CssLayout layout)
    {
      WrappedActionPlan wap = (WrappedActionPlan) msg;
      ActionPlan ap = wap.getActionPlan();
      layout.removeAllComponents();      
      sb.setLength(0);
      
      boldText(sb,ap.getId());
      sb.append(". ");
      normalText(sb,formatter.format(ap.getCreationDate()));
      sb.append(", ");
      italicBoldText(sb,ap.getQuickAuthorList());
      sb.append(". ");
      normalText(sb,ap.getTitle());
      
      layout.addComponent(new Label(sb.toString(),ContentMode.HTML));
    }
  }

  public static class UserListRenderer extends MessageListRenderer
  {
    private UserListRenderer(){}
    @Override
    public void setMessage(Message msg, MessageHierarchyView messageList, CssLayout layout)
    {
      WrappedUser wu = (WrappedUser) msg;
      User u = wu.getUser();
      layout.removeAllComponents();      
      sb.setLength(0);
      
      italicBoldText(sb,u.getUserName());
      sb.append(", ");
      boldText(sb,u.getId());
      sb.append(". ");
      
      normalText(sb,u.getAffiliation());
      sb.append(", ");
      italicText(sb,u.getLocation());
      sb.append(", ");
      boldText(sb,u.getBasicScore());
      sb.append("/");
      boldText(sb,u.getInnovationScore());
      
      layout.addComponent(new Label(sb.toString(),ContentMode.HTML));
    }
  }
}
