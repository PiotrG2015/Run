package pl.piotrgorczyca.myrunnerapp.adapters;

import pl.piotrgorczyca.myrunnerapp.helper.CalendarHelper;

/**
 * Created by Piotr on 2015-12-26. Enjoy!
 */
public class MailboxListItem {

    private String name;
    //private int mid;
    private String content;
    private int senderId;
    private boolean isNew;
    private String createdAt;
    private boolean isSended;

    public MailboxListItem(String name, String content, int senderId, int isNew, String createdAt) {
        this.setName(name);
        this.setContent(content);
        this.setSenderId(senderId);
        this.setIsNew(isNew);
        this.setCreatedAt(createdAt);
    }

    public MailboxListItem(String content, String created_at, boolean isSended) {
        this.setContent(content);
        this.setCreatedAt(created_at);
        this.setIsSended(isSended);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return (content.length()>30)?content.substring(0,30) + "..." : content;
    }

    public String getFullContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = (isNew==1);
    }

    public String getCreatedAt() {
        return CalendarHelper.countPassedTime(createdAt);
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isSended() {
        return isSended;
    }

    public void setIsSended(boolean isSended) {
        this.isSended = isSended;
    }
}
