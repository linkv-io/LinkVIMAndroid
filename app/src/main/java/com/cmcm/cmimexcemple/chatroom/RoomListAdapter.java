package com.cmcm.cmimexcemple.chatroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmcm.cmimexcemple.ChatRoomActivity;
import com.cmcm.cmimexcemple.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiaohong on 2020/10/26.
 * desc:
 */
public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.RoomListViewHolder> implements View.OnClickListener {

    private final Context mContext;
    private List<RoomData> mRoomList = new ArrayList<>();
    private final LayoutInflater mInflater;
    // 默认提供的公共房间ID
    public static final String DEFAULT_ROOM_ID = "DEFAULT_ROOM_ID";
    private RoomData mDefaultRoom = new RoomData("default room", DEFAULT_ROOM_ID);


    public RoomListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        // 添加默认房间
        mRoomList.add(mDefaultRoom);
    }

    public void addData(RoomData roomData) {
        mRoomList.add(roomData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoomListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RoomListViewHolder(mInflater.inflate(R.layout.item_room_data, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RoomListViewHolder holder, int position) {
        RoomData roomData = mRoomList.get(position);
        String roomName = roomData.getRoomId();
        // 默认房间显示房间名为默认，其它房间展示房间ID为房间名
        if (DEFAULT_ROOM_ID.equals(roomName)) {
            roomName = mContext.getString(R.string.default_room);
        }
        holder.tvName.setText(String.format(mContext.getString(R.string.title_chat_room), roomName));
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mRoomList.size();
    }

    @Override
    public void onClick(View v) {
        int pos = (int) v.getTag();
        RoomData roomData = mRoomList.get(pos);
        if (roomData != null) {
            ChatRoomActivity.actionStart(mContext, roomData.getRoomId());
        }
    }

    class RoomListViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;

        public RoomListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }

}
