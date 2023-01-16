package com.example.success;

import java.util.ArrayList;

public class Friend {
    private byte[] portraitId;
    private String friendName;
    private int memoryNum;
    public Friend(byte[] portraitId,String friendName,int memoryNum){
        this.friendName = friendName;
        this.portraitId = portraitId;
        this.memoryNum = memoryNum;
    }

    public byte[] getPortraitId() {
        return portraitId;
    }

    public void setPortraitId(byte[] portraitId) {
        this.portraitId = portraitId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public int getMemoryNum() {
        return memoryNum;
    }

    public void setMemoryNum(int memoryNum) {
        this.memoryNum = memoryNum;
    }
}
