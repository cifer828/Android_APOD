package com.example.project4;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidnasa.R;

import java.util.Calendar;


public class NASAApplication extends AppCompatActivity {

    /**
     * Run when the application is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final NASAApplication na = this;
        final Calendar cal = Calendar.getInstance();
        // Disable future days and days before 1995.6.16
        CalendarView calendar = findViewById(R.id.calendarView);
        calendar.setMaxDate(System.currentTimeMillis());
        calendar.setMinDate(803260800);

        // CalenderView listener to change date based on user selection
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                cal.set(year, month, dayOfMonth);
            }
        });

        // Button listener to the submit request
        Button submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View viewParam) {
                // note: month start from 0
                String date = cal.get(Calendar.YEAR) + "-" +
                        (cal.get(Calendar.MONTH) + 1) + "-" +
                        cal.get(Calendar.DATE);
                NASAImage ni = new NASAImage();
                findViewById(R.id.waitText).setVisibility(View.VISIBLE);
                ni.request(date, na); // Done asynchronously in another thread.  It calls ip.pictureReady() in this thread when complete.
            }
        });
    }

    /*
     * This is called by the GetPicture object when the picture is ready.  This allows for passing back the Bitmap picture for updating the ImageView
     */

    /**
     * Called by the NASAImage object when the picture is ready.
     * This allows for passing back the Bitmap picture for updating the ImageView
     *
     * @param apod apod object to display on phone
     */
    public void pictureReady(ApodObj apod) {
        // get layout views
        ImageView pictureView = findViewById(R.id.image);
        TextView dateView = findViewById(R.id.date);
        TextView titleView = findViewById(R.id.title);
        TextView authorView = findViewById(R.id.author);
        TextView descriptionView = findViewById(R.id.description);
        findViewById(R.id.waitText).setVisibility(View.INVISIBLE);

        // successfully get an apod
        if (apod.image != null) {
            pictureView.setImageBitmap(apod.image);

            dateView.setText("Astronomy Picture of the Day " + apod.date);
            titleView.setText(apod.title);
            authorView.setText(apod.copyright);
            descriptionView.setText(apod.explanation);
            // set visibility
            pictureView.setVisibility(View.VISIBLE);
            dateView.setVisibility(View.VISIBLE);
            titleView.setVisibility(View.VISIBLE);
            authorView.setVisibility(View.VISIBLE);
            descriptionView.setVisibility(View.VISIBLE);
        } else {
            // fail to get apod
            pictureView.setImageResource(R.mipmap.ic_launcher);
            pictureView.setVisibility(View.INVISIBLE);
            dateView.setText("Cannot find a picture for date " + apod.date);
            titleView.setVisibility(View.INVISIBLE);
            authorView.setVisibility(View.INVISIBLE);
            descriptionView.setVisibility(View.INVISIBLE);
        }
    }

}
