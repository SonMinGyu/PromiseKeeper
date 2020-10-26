package fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.application.promisekeeper.FindByAddressActivity;
import org.application.promisekeeper.FindByMapActivity;
import org.application.promisekeeper.FindCenterOfFriendsActivity;
import org.application.promisekeeper.Model.PromiseModel;
import org.application.promisekeeper.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class findPlaceFragment extends Fragment {

    TextView placeText;
    Button findByMapButton;
    Button findByAddressButton;
    Button recommandPlaceButton;
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;

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

        recommandPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent recommandIntent = new Intent(getActivity(), FindCenterOfFriendsActivity.class);
                getActivity().startActivity(recommandIntent);
            }
        });

        findByAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent findByAddressIntent = new Intent(getActivity(), FindByAddressActivity.class);
                findByAddressIntent.putExtra("promiseKey", getArguments().getString("promiseDate")+ "+"
                        + getArguments().getString("promiseTitle"));
                getActivity().startActivityForResult(findByAddressIntent, SEARCH_ADDRESS_ACTIVITY);
                //getActivity().startActivity(findByAddressIntent);
            }
        });

        return view;
    }

    /*
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case SEARCH_ADDRESS_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    String data = intent.getExtras().getString("data");
                    if (data != null) {

                        final Geocoder geocoder = new Geocoder(getActivity());
                        List<Address> list = null;

                        String str = data;
                        try {
                            list = geocoder.getFromLocationName(
                                    str, // 지역 이름
                                    10); // 읽을 개수
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
                        }

                        if (list != null) {
                            if (list.size() == 0) {
                                //tv.setText("해당되는 주소 정보는 없습니다");
                            } else {
                                //tv.setText(list.get(0).toString());
                                //          list.get(0).getCountryName();  // 국가명
                                //          list.get(0).getLatitude();        // 위도
                                //          list.get(0).getLongitude();    // 경도
                            }
                        }

                        Map<String, Object> taskMap = new HashMap<String, Object>();
                        taskMap.put("promisePlace", data);
                        taskMap.put("promisePlaceLatitude", list.get(0).getLatitude());
                        taskMap.put("promisePlaceLongitude", list.get(0).getLongitude());
                        FirebaseDatabase.getInstance().getReference().child("promise")
                                .child(getArguments().getString("promiseDate")+ "+"
                                        + getArguments().getString("promiseTitle")).updateChildren(taskMap);

                        //Toast.makeText(getApplicationContext(), "약속 장소가 설정되었습니다!", Toast.LENGTH_SHORT).show();

                        Toast.makeText(getActivity(), "실행", Toast.LENGTH_SHORT).show();
                        System.out.println("mainmainmain save");
                        //et_address.setText(data);
                    }
                }
                break;
        }
    }

     */
}
