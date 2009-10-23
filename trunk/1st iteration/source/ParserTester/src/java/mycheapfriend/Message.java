/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mycheapfriend;

/**
 *
 * @author michaelglass
 */
public class Message {
        String from, to, subject, body;

    public Message()
    {
        this.from = this.to = this.subject = this.body = "";
    }

    public String getBody() {
        return body;
    }

    public boolean validBody() {
        boolean rval = false;
        //normalize phone numbers
        // \d{10}|(?:\D?\d{3}\D ?(?:\d{7}|(?:\d{3}\D\d{4})))
        String phoneNumPattern = "\\d{10}|(?:\\D?\\d{3}\\D ?(?:\\d{7}|(?:\\d{3}\\D\\d{4})))";

        

        return body.matches(phoneNumPattern);
    }
    
    public void setBody(String body) {
        this.body = body;
    }

    public String getFrom() {
        return from;
    }

    public boolean validFrom() {
        return false;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public boolean validSubject() {
        return false;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTo() {
        return to;
    }

    public boolean validTo() {
        return false;
    }

    public void setTo(String to) {
        this.to = to;
    }


}
