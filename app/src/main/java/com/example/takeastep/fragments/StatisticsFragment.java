package com.example.takeastep.fragments;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.example.takeastep.R;
import com.example.takeastep.activities.user.MainActivity;
import com.example.takeastep.activities.user.network.ApiClient;
import com.example.takeastep.models.Covid19ReportItem;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsFragment extends Fragment {

    TextView today_cases, today_recovered, today_deaths, total_cases, total_recovered, total_deaths, population, last_update_date, last_update_time;
    String[] categories = {
            "WorldWide",
            "Kuwait",
            "Saudi Arabia",
            "Egypt",
            "UAE",
            "Syrian Arab Republic",
            "Oman",
            "Qatar",
            "Iraq",
            "Lebanon",
            "Jordan",
            "Yemen",
            "USA",
            "UK",
    };
    String mCategory;
    ArrayAdapter<String> spinnerAdapter;
    AutoCompleteTextView spinner;

    public StatisticsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //getActivity().getSupportFragmentManager().popBackStack();

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new HomeFragment()).commit();

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_statistics, container, false);

        MainActivity.navigationView.setCheckedItem(R.id.statics);
        MainActivity.navigationView.getCheckedItem().setTitle("Covid-19 Statistics");

        today_cases = view.findViewById(R.id.today_cases);
        today_recovered = view.findViewById(R.id.today_recovered);
        today_deaths = view.findViewById(R.id.today_deaths);
        total_cases = view.findViewById(R.id.total_cases);
        total_recovered = view.findViewById(R.id.total_recovered);
        total_deaths = view.findViewById(R.id.total_deaths);
        population = view.findViewById(R.id.population);
        last_update_date = view.findViewById(R.id.last_update_date);
        last_update_time = view.findViewById(R.id.last_update_time);
        spinner = view.findViewById(R.id.spinner);
        /// Covid 19 API
        prepareSpinner();
        getGeneralReport();
        return view;

    }

    private void prepareSpinner() {
        spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.select_dialog_item, categories);
        spinner.setAdapter(spinnerAdapter);
        spinner.setText(categories[0],false);
        spinner.setOnItemClickListener((parent, view1, position, id) -> {
                if (position == 0)
                    getGeneralReport();
                else
                    getOneCountryReport(categories[position]);
            });
        spinner.setOnDismissListener(() -> spinner.clearFocus());
    }

    private void getOneCountryReport(String country) {
        ApiClient.getINSTANCE().getOneCountryReport(country).enqueue(new Callback<Covid19ReportItem>() {
            @Override
            public void onResponse(Call<Covid19ReportItem> call, Response<Covid19ReportItem> response) {
                Log.w("Covid_API_Response", response.body() + "");
                if (response.isSuccessful()) {
                    Covid19ReportItem covid19ReportItem = response.body();
                    DecimalFormat decimalFormat = new DecimalFormat("#,###");
                    today_cases.setText(covid19ReportItem.getTodayCases()==null?"0":String.format("%,d", covid19ReportItem.getTodayCases()));
                    today_recovered.setText(covid19ReportItem.getTodayRecovered()==null?"0":String.format("%,d", covid19ReportItem.getTodayRecovered()));
                    today_deaths.setText(covid19ReportItem.getTodayDeaths()==null?"0":String.format("%,d", covid19ReportItem.getTodayDeaths()));
                    total_cases.setText(covid19ReportItem.getTotalCases()==null?"0":String.format("%,d", covid19ReportItem.getTotalCases()));
                    total_recovered.setText(covid19ReportItem.getTotalRecovered()==null?"0":String.format("%,d", covid19ReportItem.getTotalRecovered()));
                    total_deaths.setText(covid19ReportItem.getTotalDeaths()==null?"0":String.format("%,d", covid19ReportItem.getTotalDeaths()));
                    population.setText(covid19ReportItem.getPopulation()==null?"0":String.format("%,d", covid19ReportItem.getPopulation()));

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(covid19ReportItem.getTime());
                    String date = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
                    String time = (calendar.get(Calendar.HOUR) == 0 ? "12" : calendar.get(Calendar.HOUR)) + ":" + (calendar.get(Calendar.MINUTE) > 9 ? calendar.get(Calendar.MINUTE) : "0" + calendar.get(Calendar.MINUTE)) + (calendar.get(Calendar.AM_PM) == Calendar.AM ? " AM" : " PM");

                    last_update_date.setText(date);
                    last_update_time.setText(time);
                }
            }

            @Override
            public void onFailure(Call<Covid19ReportItem> call, Throwable t) {
                if (t.getMessage().contains("Unable to resolve host"))
                    Snackbar.make(view, R.string.no_internet_connection, Snackbar.LENGTH_LONG)
                            .setAction(R.string.go_to_setting, v -> requireContext().startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK)))
                            .show();
            }
        });
    }

    private void getGeneralReport() {
        ApiClient.getINSTANCE().getGeneralReport().enqueue(new Callback<Covid19ReportItem>() {
            @Override
            public void onResponse(Call<Covid19ReportItem> call, Response<Covid19ReportItem> response) {
                Log.w("Covid_API_Response2", response.body() + "");
                if (response.isSuccessful()) {
                    Covid19ReportItem covid19ReportItem = response.body();
                    DecimalFormat decimalFormat = new DecimalFormat("#,###");
                    today_cases.setText(String.format("%,d", covid19ReportItem.getTodayCases()));
                    today_recovered.setText(String.format("%,d", covid19ReportItem.getTodayRecovered()));
                    today_deaths.setText(String.format("%,d", covid19ReportItem.getTodayDeaths()));
                    total_cases.setText(String.format("%,d", covid19ReportItem.getTotalCases()));
                    total_recovered.setText(String.format("%,d", covid19ReportItem.getTotalRecovered()));
                    total_deaths.setText(String.format("%,d", covid19ReportItem.getTotalDeaths()));
                    population.setText(String.format("%,d", covid19ReportItem.getPopulation()));

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(covid19ReportItem.getTime());
                    String date = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
                    String time = (calendar.get(Calendar.HOUR) == 0 ? "12" : calendar.get(Calendar.HOUR)) + ":" + (calendar.get(Calendar.MINUTE) > 9 ? calendar.get(Calendar.MINUTE) : "0" + calendar.get(Calendar.MINUTE)) + (calendar.get(Calendar.AM_PM) == Calendar.AM ? " AM" : " PM");

                    last_update_date.setText(date);
                    last_update_time.setText(time);

                }
            }

            @Override
            public void onFailure(Call<Covid19ReportItem> call, Throwable t) {
                if (t.getMessage().contains("Unable to resolve host"))
                    Snackbar.make(view, R.string.no_internet_connection, Snackbar.LENGTH_LONG)
                            .setAction(R.string.go_to_setting, v -> requireContext().startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK)))
                            .show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}