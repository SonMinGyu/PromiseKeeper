package fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.application.promisekeeper.MainActivity;
import org.application.promisekeeper.Model.UserModel;
import org.application.promisekeeper.R;

import java.util.ArrayList;
import java.util.List;

import static org.application.promisekeeper.MainActivity.bringUserName;

public class friendsFragment extends Fragment {

    private static final String TAG = "mainmainmain";
    TextView nullText;

    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_friends_recyclerview);
        recyclerView.setAdapter(new FriendsRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        nullText = (TextView) view.findViewById(R.id.fragment_friends_nullText);
        /*
        for(int i = 0; i < userModels2.size(); i++)
        {
            System.out.println("mainmainmain size" + Integer.toString(userModels2.size()));
            if(userModels2.get(i).getUserUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            {
                name = userModels2.get(i).getUserName();

                System.out.println("mainmainmain name" + name);
            }
        }

        userKey = FirebaseAuth.getInstance().getCurrentUser().getUid() + "+" + name;

        final List<UserModel> friendsList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("users").child(userKey).child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendsList.clear();
                for(DataSnapshot item :dataSnapshot.getChildren())
                {
                    friendsList.add(item.getValue(UserModel.class));
                }

                if(friendsList.size() != 0)
                {
                    nullText.setVisibility(View.GONE);
                }
                else
                {
                    nullText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

         */

        return view;
    }

    class FriendsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private List<UserModel> friendsFragmentUserModels = new ArrayList<>();

        public FriendsRecyclerViewAdapter() {

            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    friendsFragmentUserModels.clear();
                    //keyList.clear();
                    for(DataSnapshot item :dataSnapshot.getChildren())
                    {
                        friendsFragmentUserModels.add(item.getValue(UserModel.class));
                        //keyList.add(item.getValue(StudyModel.class).studyKey);
                    }

                    if(friendsFragmentUserModels.size() != 0)
                    {
                        nullText.setVisibility(View.GONE);
                    }
                    else
                    {
                        nullText.setVisibility(View.VISIBLE);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @NonNull
        @Override // item_studyroom을 리사이클러뷰에 연결
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);

            CustomViewHolder holder = new CustomViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final CustomViewHolder customViewHolder = (CustomViewHolder) holder;

            /*
            Glide.with(customViewHolder.itemView.getContext())
                    .load(studyModels.get(position).getProfile())
                    .error(R.drawable.studyroom_logo)
                    .into(((CustomViewHolder)holder).imageView);

             */

            customViewHolder.friendName.setText(friendsFragmentUserModels.get(position).getUserName());
        }

        @Override
        public int getItemCount() {
            return (friendsFragmentUserModels != null ? friendsFragmentUserModels.size() : 1);
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView friendName;

            public CustomViewHolder(View view) {
                super(view);
                imageView =(ImageView) view.findViewById(R.id.item_friend_imageView);
                friendName = (TextView) view.findViewById(R.id.item_friend_nameText);

                /*
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            String studyKey = keyList.get(position);
                            Log.d(TAG, keyList.get(position));
                            Intent intent = new Intent(getActivity(), StudyRoomActivity.class);
                            intent.putExtra("studykey", studyKey);
                            getActivity().startActivity(intent);
                            //Toast.makeText(getContext(),"클릭",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                 */

            }
        }
    }
}

