package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.application.promisekeeper.FindByMapActivity;
import org.application.promisekeeper.Model.PromiseModel;
import org.application.promisekeeper.R;

import java.util.ArrayList;


public class findPlaceFragment extends Fragment {

    TextView placeText;
    Button findByMapButton;
    Button findByAddressButton;
    Button recommandPlaceButton;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_place, container, false);

        placeText = (TextView) view.findViewById(R.id.fragment_find_place_placeText);
        findByMapButton = (Button) view.findViewById(R.id.fragment_find_place_findByMapButton);
        findByAddressButton = (Button) view.findViewById(R.id.fragment_find_place_findByAddressButton);
        recommandPlaceButton = (Button) view.findViewById(R.id.fragment_find_place_recommandPlace);

        final ArrayList<PromiseModel> promiseModels = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("promise")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        promiseModels.clear();
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            for(int i = 0; i < item.getValue(PromiseModel.class).getMemberUids().size(); i++)
                            {
                                if(item.getValue(PromiseModel.class).getMemberUids().get(i).equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                {
                                    if (getArguments().getString("promiseDate").equals(item.getValue(PromiseModel.class).getPromiseDate())
                                            && getArguments().getString("promiseTitle").equals(item.getValue(PromiseModel.class).getPromiseTitle())) {

                                        placeText.setText(item.getValue(PromiseModel.class).getPromisePlace());
                                        //System.out.println("findPlaceFragment uids" + item.getValue(PromiseModel.class).getMemberUids().get(0));
                                    }
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        findByMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent findByMapIntent = new Intent(getActivity(), FindByMapActivity.class);
                findByMapIntent.putExtra("promiseKey", getArguments().getString("promiseDate")+ "+"
                        + getArguments().getString("promiseTitle"));
                getActivity().startActivity(findByMapIntent);
            }
        });

        return view;
    }
}
