package org.application.promisekeeper.Model;

public class ChatModel {
    private String chattingUid;
    private String chattingText;
    private String chatTime;
    private String chatDate;

    public boolean isDayOfFirstMessage() {
        return isDayOfFirstMessage;
    }

    public void setDayOfFirstMessage(boolean dayOfFirstMessage) {
        isDayOfFirstMessage = dayOfFirstMessage;
    }

    private boolean isDayOfFirstMessage;

    public String getChatDate() {
        return chatDate;
    }

    public void setChatDate(String chatDate) {
        this.chatDate = chatDate;
    }

    public String getChatTime() {
        return chatTime;
    }

    public void setChatTime(String chatTime) {
        this.chatTime = chatTime;
    }

    public String getChattingUid() {
        return chattingUid;
    }

    public void setChattingUid(String chattingUid) {
        this.chattingUid = chattingUid;
    }

    public String getChattingText() {
        return chattingText;
    }

    public void setChattingText(String chattingText) {
        this.chattingText = chattingText;
    }
}
