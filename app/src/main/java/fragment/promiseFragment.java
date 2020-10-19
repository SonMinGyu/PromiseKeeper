package fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import fragment.Decorator.EventDecorator;
import fragment.Decorator.OneDayDecorator;
import fragment.Decorator.SaturdayDecorator;
import fragment.Decorator.SundayDecorator;

import org.application.promisekeeper.CreatePromise;
import org.application.promisekeeper.Model.PromiseModel;
import org.application.promisekeeper.PromiseOfTheDayActivity;
import org.application.promisekeeper.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class promiseFragment extends Fragment {

    private static final String TAG = "mainmainmain";
    MaterialCalendarView calendarView;
    //String[] result = new String[getPromiseModels.size()];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_promise, container, false);

        calendarView = (MaterialCalendarView) view.findViewById(R.id.fragment_promise_calendar);
        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2020, 0,1))
                .setMaximumDate(CalendarDay.from(2030,11,31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        final OneDayDecorator oneDayDecorator = new OneDayDecorator();

        calendarView.addDecorators(new SundayDecorator(),
                new SaturdayDecorator(), oneDayDecorator);

        /*
        for(int i = 0; i < getPromiseModels.size(); i++)
        {
            String begginnignDate = getPromiseModels.get(i).getPromiseDate();
            String parseDate = begginnignDate.substring(0,10);
            String[] parseDate2 = parseDate.split("\\+");
            String stDate = parseDate2[0] + "," + parseDate2[1] + "," + parseDate2[2];

            result[i] = stDate;
            System.out.println("mainmainmain result " + stDate);
        }

         */

        //String[] result = {"2020,10,05","2020,10,07","2020,10,15","2020,10,20", "2020,10,27"};

        //new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                // 클릭시 새로운 액티비티 띄우면서 그날의 약속 보여주자
                // 지금은 바로 약속잡는 액티비티로 연결, 나중에 그날에 약속이 뭐있는지 recyclerview로 보여주자
                Intent promiseOfTheDay = new Intent(getActivity(), PromiseOfTheDayActivity.class);
                String selectDate = null;
                if(date.getMonth() < 9)
                {
                    selectDate = date.getYear() + "+0" + (date.getMonth()+1) + "+" + date.getDay();
                }
                else if(date.getMonth() >= 9)
                {
                    selectDate = date.getYear() + "+" + (date.getMonth()+1) + "+" + date.getDay();
                }
                promiseOfTheDay.putExtra("selectDate", selectDate);
                getActivity().startActivity(promiseOfTheDay);

            }
        });

        final ArrayList<PromiseModel> promiseModels = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("promise")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        promiseModels.clear();
                        for(DataSnapshot item :dataSnapshot.getChildren())
                        {
                            for(int i = 0; i < item.getValue(PromiseModel.class).getMemberUids().size(); i++)
                            {
                                if(item.getValue(PromiseModel.class).getMemberUids().get(i)
                                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                {
                                    promiseModels.add(item.getValue(PromiseModel.class));
                                }
                            }
                            //System.out.println("mainmainmain bringpromise" + item.getValue(PromiseModel.class).getPromiseDate());
                        }

                        String[] result2 = new String[promiseModels.size()];
                        for(int i = 0; i < promiseModels.size(); i++)
                        {
                            String begginnignDate2 = promiseModels.get(i).getPromiseDate();
                            String parseDate2 = begginnignDate2.substring(0,10);
                            String[] parseDate22 = parseDate2.split("\\+");
                            String stDate = parseDate22[0] + "," + parseDate22[1] + "," + parseDate22[2];

                            result2[i] = stDate;
                            System.out.println("mainmainmain result " + stDate);
                        }

                        calendarView.removeDecorators();
                        calendarView.addDecorators(new SundayDecorator(),
                                new SaturdayDecorator(), oneDayDecorator);
                        new ApiSimulator(result2).executeOnExecutor(Executors.newSingleThreadExecutor());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return view;
    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        String[] Time_Result;

        ApiSimulator(String[] Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환

            for(int i = 0 ; i < Time_Result.length; i++) {
                String[] time = Time_Result[i].split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                System.out.println("mainmainmain timeResult" + Time_Result.length);

                calendar.set(year, month - 1, dayy);

                CalendarDay day = CalendarDay.from(calendar);
                dates.add(day);
            }

            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            /*
            if (isFinishing()) {
                return;
            }

             */
            calendarView.addDecorator(new EventDecorator(Color.GREEN, calendarDays, getActivity()));
        }
    }
}
