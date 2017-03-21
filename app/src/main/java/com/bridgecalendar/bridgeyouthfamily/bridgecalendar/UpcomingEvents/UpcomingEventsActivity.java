package com.bridgecalendar.bridgeyouthfamily.bridgecalendar.UpcomingEvents;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.bridgecalendar.bridgeyouthfamily.bridgecalendar.EventResponse.Event;
import com.bridgecalendar.bridgeyouthfamily.bridgecalendar.EventResponse.EventListListener;
import com.bridgecalendar.bridgeyouthfamily.bridgecalendar.EventResponse.EventResponseManager;
import com.bridgecalendar.bridgeyouthfamily.bridgecalendar.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

/**
 * Created by Arya on 1/14/2017.
 */

public class UpcomingEventsActivity extends AppCompatActivity implements EventListListener {
    private List<Event> mEventList;
    WeekView mWeekView;
    EventResponseManager eventResponseManager;
    boolean networkCalled = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upcoming_events_activity);
        mWeekView = (WeekView) findViewById(R.id.weekView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEventList = new ArrayList<>();
        eventResponseManager = new EventResponseManager();
        eventResponseManager.attachListener(this);
        if (!networkCalled) {
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            int nextMonth = currentMonth + 1;
            int previousMonth = currentMonth - 1;
            String currentMonthString = "" + currentMonth;
            String nextMonthString = "" + nextMonth;
            String previousMonthString = "" + previousMonth;
            if ((currentMonth) < 10) {
                currentMonthString = String.format("%02d", (currentMonth));
            }
            if ((nextMonth) < 10) {
                nextMonthString = String.format("%02d", (nextMonth));
            }
            if ((previousMonth) < 10) {
                previousMonthString = String.format("%02d", (previousMonth));
            }
            eventResponseManager.getSearchList("bridgekelowna@gmail.com", currentYear + "-" + previousMonthString + "-01T00:00:31-08:00", currentYear + "-" + nextMonthString + "-31T23:59:31-08:00");
            networkCalled = true;

        }

        mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                List<WeekViewEvent> weekViewEvents = new ArrayList<>();

                for (Event event : mEventList) {
                    if (eventMatches(event.toWeekViewEvent(), newYear, newMonth)) {
                        weekViewEvents.add(event.toWeekViewEvent());

                    }
                }

                return weekViewEvents;
            }
        });
        mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {

                Toast.makeText(UpcomingEventsActivity.this, "Location: " + event.getLocation(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private boolean eventMatches(WeekViewEvent event, int year, int month) {
        return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month - 1) && (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month - 1);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void setEventList(List<Event> eventList) {
        mEventList.clear();

        List<Event> tempList = eventList;
        SharedPreferences prefs = this.getSharedPreferences("bridge", Context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet("filterList", null);
        if (set != null && set.size() != 0) {

            List<String> filterList = new ArrayList<>(set);
            List<Event> tempList2 = new ArrayList<>();

            for (Event event : tempList) {
                if (filterList.contains(event.getEventLocation())) {
                    tempList2.add(event);

                }
            }

            mEventList.addAll(tempList2);
        } else {
            mEventList.addAll(tempList);
        }
        mWeekView.notifyDatasetChanged();
    }
}
