package fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.application.promisekeeper.Model.ChatModel;
import org.application.promisekeeper.Model.UserModel;
import org.application.promisekeeper.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class chattingFragment extends Fragment {

    private static ArrayList<UserModel> userModels = bringUserModels();
    private List<ChatModel> chatModels2 = new ArrayList<>();
    private RecyclerView chatRecyclerView;
    EditText chatEditText;
    ImageButton chatSendButton;
    boolean dateFlag = false;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatting, container, false);

        chatEditText = (EditText) view.findViewById(R.id.chatting_message_editText);
        chatSendButton = (ImageButton) view.findViewById(R.id.chatting_send_button);

        FirebaseDatabase.getInstance().getReference().child("promise")
                .child(getArguments().getString("promiseDate")+"+"+getArguments().getString("promiseTitle"))
                .child("chatting").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatModels2.clear();
                for(DataSnapshot item :snapshot.getChildren())
                {
                    chatModels2.add(item.getValue(ChatModel.class));
                }

                if(chatModels2.size() == 0)
                {
                    dateFlag = true;
                }
                else
                {
                    if(!chatModels2.get(chatModels2.size()-1).getChatDate().equals(getToDay()))
                    {
                        dateFlag = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        chatRecyclerView = (RecyclerView) view.findViewById(R.id.chatting_recyclerView);
        chatRecyclerView.setAdapter(new ChatRecyclerViewAdapter());
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chatEditText.getText().toString().length() <= 0)
                {
                    return;
                }

                ChatModel chatModel = new ChatModel();
                if(dateFlag)
                {
                    ChatModel chatModel1 = new ChatModel();
                    chatModel1.setChattingText("first Message of " + getToDay());
                    chatModel1.setDayOfFirstMessage(true);
                    chatModel1.setChattingUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    chatModel1.setChatTime(getTime());
                    chatModel1.setChatDate(getToDay());
                    FirebaseDatabase.getInstance().getReference().child("promise")
                            .child(getArguments().getString("promiseDate")+"+"+getArguments().getString("promiseTitle"))
                            .child("chatting").push().setValue(chatModel1);
                    dateFlag = false;
                }

                chatModel.setChattingUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                chatModel.setChattingText(chatEditText.getText().toString());
                chatModel.setChatTime(getTime());
                chatModel.setChatDate(getToDay());

                FirebaseDatabase.getInstance().getReference().child("promise")
                        .child(getArguments().getString("promiseDate")+"+"+getArguments().getString("promiseTitle"))
                        .child("chatting").push().setValue(chatModel);

                chatEditText.setText(null);

            }
        });

        return view;
    }

    class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private List<ChatModel> chatModels = new ArrayList<>();
        private String uid;
        //private String recentDate;

        public ChatRecyclerViewAdapter() {

            FirebaseDatabase.getInstance().getReference().child("promise").child(getArguments().getString("promiseDate")+"+"+getArguments().getString("promiseTitle"))
                    .child("chatting").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chatModels.clear();
                    for(DataSnapshot item :snapshot.getChildren())
                    {
                        chatModels.add(item.getValue(ChatModel.class));
                        //Log.d(TAG, item.getValue(ChatModel.class).getChattingText());
                    }
                    // 리사이클러뷰 갱신
                    notifyDataSetChanged();
                    // 마지막 메시지로
                    chatRecyclerView.scrollToPosition(chatModels.size() - 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        @Override
        public int getItemViewType(int position) {
            if(chatModels.get(position).isDayOfFirstMessage())
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }

        @NonNull
        @Override // item_studyroom을 리사이클러뷰에 연결
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch (viewType)
            {
                case 0:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_date,parent,false);
                    chattingFragment.ChatRecyclerViewAdapter.CustomViewHolder1 holder1 = new chattingFragment.ChatRecyclerViewAdapter.CustomViewHolder1(view);
                    return holder1;

                case 1:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);
                    chattingFragment.ChatRecyclerViewAdapter.CustomViewHolder holder = new chattingFragment.ChatRecyclerViewAdapter.CustomViewHolder(view);

                    return holder;
            }

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);
            chattingFragment.ChatRecyclerViewAdapter.CustomViewHolder holder = new chattingFragment.ChatRecyclerViewAdapter.CustomViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(!chatModels.get(position).isDayOfFirstMessage()) {

                final chattingFragment.ChatRecyclerViewAdapter.CustomViewHolder customViewHolder = (chattingFragment.ChatRecyclerViewAdapter.CustomViewHolder) holder;
                //Picasso.get().load(studyModels.get(position).getProfile()).into(customViewHolder.imageView);

            /* // 이미지 삽입
            Glide.with(customViewHolder.itemView.getContext())
                    .load(studyModels.get(position).getProfile())
                    .error(R.drawable.rank_icon)
                    .into(((StudyRoomActivity.RankRecyclerViewAdapter.CustomViewHolder)holder).imageView);

             */
                //Log.d(TAG,studyModels.get(position).getProfile());


                // 내가보낸 메시지
                if (chatModels.get(position).getChattingUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    customViewHolder.message_imageview.setImageResource(R.drawable.message_icon);
                    for (int i = 0; i < userModels.size(); i++) {
                        if (chatModels.get(position).getChattingUid().equals(userModels.get(i).getUserUid())) {
                            customViewHolder.message_user_name.setText(userModels.get(i).getUserName());
                        }
                    }
                    customViewHolder.message_message.setText(chatModels.get(position).getChattingText());
                    customViewHolder.message_message.setBackgroundResource(R.drawable.message_icon_right);
                    customViewHolder.message_user_time.setText(getTimeOnString(chatModels.get(position).getChatTime()));
                    //customViewHolder.message_linear_time.setGravity(Gravity.RIGHT);
                    customViewHolder.message_profile_linear.setVisibility(View.INVISIBLE);
                    customViewHolder.message_message_linear.setVisibility(View.VISIBLE);
                    customViewHolder.message_message_linear.setGravity(Gravity.RIGHT);
                    customViewHolder.message_linear.setGravity(Gravity.RIGHT);
                }
                // 다른사람이 보낸 메시지
                else {
                    customViewHolder.message_imageview.setImageResource(R.drawable.message_icon);
                    for (int i = 0; i < userModels.size(); i++) {
                        if (chatModels.get(position).getChattingUid().equals(userModels.get(i).getUserUid())) {
                            customViewHolder.message_user_name.setText(userModels.get(i).getUserName());
                        }
                    }
                    customViewHolder.message_message.setText(chatModels.get(position).getChattingText());
                    customViewHolder.message_message.setBackgroundResource(R.drawable.message_icon_left);
                    customViewHolder.message_user_time.setText(getTimeOnString(chatModels.get(position).getChatTime()));
                    //customViewHolder.message_linear_time.setGravity(Gravity.LEFT);
                    customViewHolder.message_profile_linear.setVisibility(View.VISIBLE);
                    customViewHolder.message_message_linear.setVisibility(View.VISIBLE);
                    customViewHolder.message_message_linear.setGravity(Gravity.LEFT);
                    customViewHolder.message_linear.setGravity(Gravity.LEFT);
                }
            }
            else
            {
                final chattingFragment.ChatRecyclerViewAdapter.CustomViewHolder1 customViewHolder1 = (chattingFragment.ChatRecyclerViewAdapter.CustomViewHolder1) holder;
                customViewHolder1.message_date.setText(getDateOnString(chatModels.get(position).getChatDate()));
                customViewHolder1.message_date_linearLayout.setGravity(Gravity.CENTER);
            }
        }

        @Override
        public int getItemCount() {
            return (chatModels != null ? chatModels.size() : 0);
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView message_imageview;
            public TextView message_user_name;
            public TextView message_user_time;
            public TextView message_message;
            public LinearLayout message_profile_linear;
            public LinearLayout message_message_linear;
            public LinearLayout message_linear;
            public LinearLayout message_linear_time;

            public CustomViewHolder(View view) {
                super(view);
                message_imageview =(ImageView) view.findViewById(R.id.item_message_imageView);
                message_user_name = (TextView) view.findViewById(R.id.item_message_textview_name);
                message_user_time = (TextView) view.findViewById(R.id.item_message_textview_time);
                message_message = (TextView) view.findViewById(R.id.item_message_textview_message);
                message_profile_linear = (LinearLayout) view.findViewById(R.id.item_message_linearlayout_profile);
                message_message_linear = (LinearLayout) view.findViewById(R.id.item_message_linearlayout_message);
                message_linear = (LinearLayout) view.findViewById(R.id.message_linear);
                message_linear_time = (LinearLayout) view.findViewById(R.id.item_message_linearlayout_time);
            }
        }

        private class CustomViewHolder1 extends RecyclerView.ViewHolder {

            public TextView message_date;
            public LinearLayout message_date_linearLayout;

            public CustomViewHolder1(@NonNull View view) {
                super(view);

                message_date = (TextView) view.findViewById(R.id.item_message_date);
                message_date_linearLayout = (LinearLayout) view.findViewById(R.id.item_message_date_linear);
            }
        }
    }

    public static ArrayList<UserModel> bringUserModels()
    {
        final ArrayList<UserModel> userModels2 = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModels2.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    userModels2.add(item.getValue(UserModel.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return userModels2;
    }

    public static String getTimeOnString(String message_time)
    {
        String time = message_time.substring(0,5);
        String stringTime = time.replace("+",":");
        return stringTime;
    }

    public static String getDateOnString(String message_Date)
    {
        char[] charDate1 = message_Date.toCharArray();
        char[] charDate = new char[13];
        charDate[0] = charDate1[0];
        charDate[1] = charDate1[1];
        charDate[2] = charDate1[2];
        charDate[3] = charDate1[3];
        charDate[4] = '년';
        charDate[5] = ' ';
        charDate[6] = charDate1[5];
        charDate[7] = charDate1[6];
        charDate[8] = '년';
        charDate[9] = ' ';
        charDate[10] = charDate1[8];
        charDate[11] = charDate1[9];
        charDate[12] = '일';

        return String.valueOf(charDate);
    }

    public static String getToDay()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy+MM+dd");
        return sdf.format(new Date());
    }

    public static String getTime()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("HH+mm+ss");
        return sdf.format(new Date());
    }
}