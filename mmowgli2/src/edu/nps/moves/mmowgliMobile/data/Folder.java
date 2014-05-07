package edu.nps.moves.mmowgliMobile.data;

import java.io.Serializable;
import java.util.*;

import org.hibernate.Session;

import com.vaadin.data.Container;

import edu.nps.moves.mmowgli.db.*;

/**
 * A folder can contain other folders or messages. A folder cannot contain both
 * folders and subfolders.
 */
public class Folder extends AbstractPojo {

    private static final long serialVersionUID = 1L;

   // private List<AbstractPojo> children = new ArrayList<AbstractPojo>();
    private Container container;
    private Class<?> pojoClass;
    
    /**
     * Constructor
     * 
     * @param parent
     *            The parent folder
     */
   // private /*public*/ Folder() {
   //     this("");
   // }

    /**
     * Constructor
     * 
     * @param name
     *            The name of the folder
     */
    private/*public*/ Folder(String name) {
        this.name = name;
    }

    public Folder(String name, Container cntr, Class<?> pojoClass)
    {
      this(name);
      this.container = cntr;
      this.pojoClass = pojoClass;
    }
    
    private ArrayList<AbstractPojo> childList;
    /**
     * @return the children
     */
  public List<AbstractPojo> getChildren()
  {
    if (childList == null) {
      @SuppressWarnings("unchecked")
      Collection<Serializable> coll = (Collection<Serializable>) container.getItemIds();
      childList = new ArrayList<AbstractPojo>(coll.size());
      handleType(coll);
//      for (Serializable ser : coll) {
//        WrappedCard c = new WrappedCard(Card.get(ser, MobileVHib.getVHSession()));
//        c.setParent(this);
//        childList.add(c);
//      }
    }
    return childList;
  }
    private void handleType(Collection<Serializable> coll)
    {
      AbstractPojo apojo;
      Session sess = MobileVHib.getVHSession();
      for (Serializable ser : coll) {
        if(this.pojoClass == Card.class)
          apojo = new WrappedCard(Card.get(ser, sess));
        else if(pojoClass == ActionPlan.class)
          apojo = new WrappedActionPlan(ActionPlan.get(ser,sess));
        else //if(pojoClass == User.class)
          apojo = new WrappedUser(User.get(ser, sess));
        apojo.setParent(this);
        childList.add(apojo);
      }      
    }
    
    /**
     * @param children
     *            the children to set
     */
  //  public void setChildren(List<AbstractPojo> children) {
 //       this.children = children;
  //  }
    
    public void setContainer(Container c)
    {
      this.container = c;
    }
    public Container getContainer()
    {
      return container;
    }
    public void setPojoClass(Class<?> c)
    {
      this.pojoClass = c;
    }
    public Class<?> getPojoClass()
    {
      return pojoClass;
    }
}
