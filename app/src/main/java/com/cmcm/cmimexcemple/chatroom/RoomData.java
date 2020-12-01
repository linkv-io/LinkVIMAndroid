package com.cmcm.cmimexcemple.chatroom;

/**
 * Created by Xiaohong on 2020/10/26.
 * desc: 房间数据模型
 */
public class RoomData {
    // 房间名称
    private String name;

    private String roomId;

    public RoomData(String name, String roomId) {
        this.name = name;
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
