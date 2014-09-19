package edu.nps.moves.mmowgliMobile.data;

import java.util.*;

import edu.nps.moves.mmowgli.db.*;

public class ListEntry extends AbstractPojo {

    private static final long serialVersionUID = 1L;

    private Date timestamp = new Date();

    private List<ListEntryField> fields;

    /**
     * The status of the message, by default it is set as undefined
     */
    private EntryStatus status = EntryStatus.UNDEFINED;

    public ListEntry (ActionPlan ap)
    {
      setFields(Arrays.asList(
          new ListEntryField("From", ap.getTitle()),
          new ListEntryField("To", "blah ap to"),
          new ListEntryField("Subject", "blah ap subject"),
          new ListEntryField("Body", "blah ap body")));
          this.timestamp = ap.getCreationDate();      
    }
    public ListEntry (Card card)
    {
      setFields(Arrays.asList(
          new ListEntryField("From", card.getAuthorName()),
          new ListEntryField("To", "blah card to"),
          new ListEntryField("Subject", card.getCardType().getTitle()),
          new ListEntryField("Body", card.getText())));
      this.timestamp = card.getCreationDate();
    }
    public ListEntry (User user)
    {
      if(user == null)
        System.out.println("bp");
      setFields(Arrays.asList(
          new ListEntryField("From", user.getUserName()),
          new ListEntryField("To", "blah user to"),
          new ListEntryField("Subject", "blah user subject"),
          new ListEntryField("Body", "blah user body")));
         this.timestamp = user.getRegisterDate();      
    }
    /**
     * Constructor
     * 
     * @param from
     *            Email address from who the email is sent
     * @param to
     *            The recipients email address
     * @param subject
     *            The subject of the email
     */
    public ListEntry(String from, String to, String subject) {
        setFields(Arrays.asList(new ListEntryField("From", from),
                new ListEntryField("To", to),
                new ListEntryField("Subject", subject)));
    }

    /**
     * Constructor
     * 
     * @param from
     *            Email address from who the email is sent
     * @param to
     *            The recipients email address
     * @param subject
     *            The subject of the email
     * @param content
     *            The body part of the email
     */
    public ListEntry(String from, String to, String subject, String content) {
        setFields(Arrays.asList(new ListEntryField("From", from),
                new ListEntryField("To", to),
                new ListEntryField("Subject", subject), new ListEntryField("Body",
                        content)));
    }

    /**
     * @return the fields
     */
    public List<ListEntryField> getFields() {
        return fields;
    }

    /**
     * @param fields
     *            the fields to set
     */
    public void setFields(List<ListEntryField> fields) {
        this.fields = fields;
    }

    /**
     * @return the status
     */
    public EntryStatus getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(EntryStatus status) {
        this.status = status;
    }

    /**
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp
     *            the timestamp to set
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Fetches a field by matching its caption
     * 
     * @param field
     *            The field caption to match
     */
    public ListEntryField getMessageField(String field) {
        if (field != null) {
            for (ListEntryField f : fields) {
                if (f.getCaption().toLowerCase().equals(field.toLowerCase())) {
                    return f;
                }
            }
        }
        return null;
    }
}
