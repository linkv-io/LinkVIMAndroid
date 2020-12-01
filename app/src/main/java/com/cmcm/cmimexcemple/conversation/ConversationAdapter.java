package com.cmcm.cmimexcemple.conversation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmcm.cmimexcemple.GlobalParams;
import com.cmcm.cmimexcemple.PrivateChatActivity;
import com.cmcm.cmimexcemple.R;
import com.cmcm.cmimexcemple.event.LoginSucceedEvent;
import com.cmcm.cmimexcemple.event.RefreshConversationListEvent;
import com.im.imlogic.IMMsg;
import com.im.imlogic.LVIMSDK;
import com.im.imlogic.LVIMSession;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiaohong on 2020/9/1.
 * desc:
 */
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> implements View.OnClickListener {
    private final LayoutInflater mInflater;
    Context mContext;

    public ConversationAdapter(@NonNull Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);

    }

    private List<LVIMSession> msgList = new ArrayList<>();

    /**
     * 添加消息并刷新界面
     *
     * @param conversatons 待显示的会话列表
     */
    public void addConversations(List<LVIMSession> conversatons) {
        msgList.clear();
        msgList.addAll(conversatons);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationViewHolder(mInflater.inflate(R.layout.item_conversation, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        LVIMSession conversation = msgList.get(position);
        IMMsg lastMsg = conversation.getLastMsg();
        String otherId = getOtherId(lastMsg.fromID, lastMsg.toID);
        String msgContent = lastMsg.getMsgContent();
        holder.tvUser.setText(otherId);
        holder.tvMsg.setText(msgContent);
        int unreadCount = conversation.getUnreadCount();
        if (unreadCount > 0) {
            holder.tvUnreadCount.setText(unreadCount + "");
            holder.tvUnreadCount.setVisibility(View.VISIBLE);
        } else {
            holder.tvUnreadCount.setVisibility(View.GONE);
        }
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    // 根据 fromID 和 toID找出对方用户ID
    private String getOtherId(String fromID, String toID) {
        String otherId = fromID;
        if (otherId.equals(GlobalParams.loginUserId)) {
            otherId = toID;
        }
        return otherId;
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    @Override
    public void onClick(View v) {
        int pos = (int) v.getTag();
        LVIMSession session = msgList.get(pos);
        IMMsg lastMsg = session.getLastMsg();
        String otherId = getOtherId(lastMsg.fromID, lastMsg.toID);
        PrivateChatActivity.actionStart(mContext, otherId);
        LVIMSDK.sharedInstance().clearPrivateSessionUnreadMsg(otherId);
        EventBus.getDefault().post(new RefreshConversationListEvent());
    }


    static class ConversationViewHolder extends RecyclerView.ViewHolder {
        TextView tvUser;
        TextView tvMsg;
        TextView tvUnreadCount;

        public ConversationViewHolder(View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tv_user);
            tvMsg = itemView.findViewById(R.id.tv_msg);
            tvUnreadCount = itemView.findViewById(R.id.tv_unread_count);
        }
    }


}
