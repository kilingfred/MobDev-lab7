package com.example.lab7;

import android.os.Bundle;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Date;
import java.text.DateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class CurrencyExchanger extends AppCompatActivity {
    float salesRate_A = 1;
    float purchaseRate_A = 1;
    float salesRateNB_A = 1;
    float salesRate_B = 1;
    float purchaseRate_B = 1;
    float salesRateNB_B = 1;
    String cur_A = "";
    String cur_B = "";

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.privatbank.ua/p24api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    public interface API {
        @GET("exchange_rates")
        Call<ExchangeRatesResponse> getExchangeRates(@Query("json") boolean json, @Query("date") String date);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.currency_exchanger);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        Currency usd = new Currency("USD");
        Currency gbp = new Currency("DBP");
        Currency PLN = new Currency("PLN");
        Currency eur = new Currency("EUR");
        Spinner conv1 = findViewById(R.id.cur1);
        Spinner conv2 = findViewById(R.id.cur2);
        String[] conv_list_args = {"UAH - Гривня", "USD - Доллар США", "PLN - Польський злотий", "GBP - Фунт Стрелінга", "EUR - Євро"};
        ArrayList<Currency> conv1_list = new ArrayList<Currency>();
        for (String name : conv_list_args) {
            conv1_list.add(new Currency(name));
        }
        ArrayList<Currency> true_currencies = new ArrayList<Currency>();
        API api = retrofit.create(API.class);
        CalendarView calendar = findViewById(R.id.calendarView);

        // Get today's date in milliseconds
        long today = Calendar.getInstance().getTimeInMillis();
        Date minDate = Date.valueOf("2014-12-01");
        // Set maxDate to today
        calendar.setMaxDate(today);
        calendar.setMinDate(minDate.getTime());
        String[] params = {"USD", "GBP", "PLN", "EUR"};
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day_of_month) {
                String day = "";
                String mon = "";
                if(month < 10) mon = "0" + month; else mon = String.valueOf(month);
                if(day_of_month < 10) day = "0" + day_of_month; else day = String.valueOf(day_of_month);
                Call<ExchangeRatesResponse> call = api.getExchangeRates(true, String.valueOf(day + "." + mon + "." + year));
                Log.d("CALENDAR_DATE",String.valueOf(day + "." + mon + "." + year));
                call.enqueue(new Callback<ExchangeRatesResponse>() {
                    @Override
                    public void onResponse(Call<ExchangeRatesResponse> call, Response<ExchangeRatesResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d("API_RESPONSE", "Received exchange rates");
                            List<Currency_model> exchangeRates = response.body().getExchangeRate();
                            if (exchangeRates != null) {
//                                     for (Currency_model currency : exchangeRates) {
//                                         Log.d("API_RESPONSE", "Currency: " + currency.getCurrency() +
//                                                 ", Sale Rate: " + currency.getSaleRate() +
//                                                 ", Purchase Rate: " + currency.getPurchaseRate());
//                                     }
                                for (Currency_model currency : exchangeRates) {
                                    String name = currency.getCurrency();
                                    if (name.equals("USD")) {
                                        usd.setPrice1(currency.getSaleRate());
                                        usd.setPrice2(currency.getPurchaseRate());
                                        usd.setPrice3(currency.getSaleRateNB());

                                        // ✅ Log after setting the values
                                        Log.d("API_DEBUG", "USD Rates Set: SaleRate=" + usd.getPrice1() + ", PurchaseRate=" + usd.getPrice2());

                                        // ✅ Update UI after values are set
                                        runOnUiThread(() -> {
                                            Toast.makeText(CurrencyExchanger.this, "Курс валют оновлено!", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                    if (name.equals("GBP")) {
                                        gbp.setPrice1(currency.getSaleRate());
                                        gbp.setPrice2(currency.getPurchaseRate());
                                        gbp.setPrice3(currency.getSaleRateNB());
                                    }
                                    if (name.equals("EUR")) {
                                        eur.setPrice1(currency.getSaleRate());
                                        eur.setPrice2(currency.getPurchaseRate());
                                        eur.setPrice3(currency.getSaleRateNB());
                                    }
                                    if (name.equals("PLN")) {
                                        PLN.setPrice1(currency.getSaleRate());
                                        PLN.setPrice2(currency.getPurchaseRate());
                                        PLN.setPrice3(currency.getSaleRateNB());
                                    }
                                }
                                runOnUiThread(() -> {
                                    Toast.makeText(CurrencyExchanger.this, "Курс валют оновлено!", Toast.LENGTH_SHORT).show();

                                    // Re-trigger selection
                                    int pos1 = conv1.getSelectedItemPosition();
                                    int pos2 = conv2.getSelectedItemPosition();

                                    conv1.setSelection(0, false); // Set to first item
                                    conv2.setSelection(0, false);

                                    conv1.setSelection(pos1, true); // Set back to selected item
                                    conv2.setSelection(pos2, true); // Re-trigger selection
                                });
                            } else {
                                Log.e("API_ERROR", "exchangeRates is null");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ExchangeRatesResponse> call, Throwable t) {
                        Toast.makeText(CurrencyExchanger.this, "Не вдалося отримати курс валют", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        conv1.setAdapter(new Currency_Adapter(this, R.layout.currency_item, conv1_list));
        conv2.setAdapter(new Currency_Adapter(this, R.layout.currency_item, conv1_list));
        conv1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Currency selectedCurrency = (Currency) parent.getItemAtPosition(position);
                String item = selectedCurrency.getName();

                switch (item) {
                    case "USD - Доллар США":
                        salesRate_A = usd.getPrice1();
                        purchaseRate_A = usd.getPrice2();
                        salesRateNB_A = usd.getPrice3();
                        cur_A = usd.getName();
                        break;
                    case "PLN - Польський злотий":
                        salesRate_A = PLN.getPrice1();
                        purchaseRate_A = PLN.getPrice2();
                        salesRateNB_A = PLN.getPrice3();
                        cur_A = PLN.getName();
                        break;
                    case "GBP - Фунт Стрелінга":
                        salesRate_A = gbp.getPrice1();
                        purchaseRate_A = gbp.getPrice2();
                        salesRateNB_A = gbp.getPrice3();
                        cur_A = gbp.getName();
                        break;
                    case "EUR - Євро":
                        salesRate_A = eur.getPrice1();
                        purchaseRate_A = eur.getPrice2();
                        salesRateNB_A = eur.getPrice3();
                        cur_A = eur.getName();
                        break;
                    case "UAH - Гривня":
                        salesRate_A = 1;
                        purchaseRate_A = 1;
                        salesRateNB_A = 1;
                        cur_A = "UAH";
                        break;
                }

                Log.d("SPINNER_UPDATE", "Selected: " + item +
                        " | SalesRate_A: " + salesRate_A +
                        " | PurchaseRate_A: " + purchaseRate_A);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Log.e("Result", "USD: SalesRate = " + usd.getPrice1() + " PurchaseRate = " + usd.getPrice2());
        TextView kurs = findViewById(R.id.kurs);
        conv2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Currency selectedCurrency = (Currency) parent.getItemAtPosition(position);
                String item = selectedCurrency.getName();
                switch (item) {
                    case "USD - Доллар США":
                        salesRate_B = usd.getPrice1();
                        purchaseRate_B = usd.getPrice2();
                        salesRateNB_B = usd.getPrice3();
                        cur_B = usd.getName();
                        break;
                    case "PLN - Польський злотий":
                        salesRate_B = PLN.getPrice1();
                        purchaseRate_B = PLN.getPrice2();
                        salesRateNB_B = PLN.getPrice3();
                        cur_B = PLN.getName();
                        break;
                    case "GBP - Фунт Стрелінга":
                        salesRate_B = gbp.getPrice1();
                        purchaseRate_B = gbp.getPrice2();
                        salesRateNB_B = gbp.getPrice3();
                        cur_B = gbp.getName();
                        break;
                    case "EUR - Євро":
                        salesRate_B = eur.getPrice1();
                        purchaseRate_B = eur.getPrice2();
                        salesRateNB_B = eur.getPrice3();
                        cur_B = eur.getName();
                        break;
                    case "UAH - Гривня":
                        salesRate_B = 1;
                        purchaseRate_B = 1;
                        salesRateNB_B = 1;
                        cur_B = "UAH";
                }
                Log.d("SPINNER_UPDATE", "Selected: " + item +
                        " | SalesRate_A: " + salesRate_B +
                        " | PurchaseRate_A: " + purchaseRate_B);
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        Button button = findViewById(R.id.button);
        EditText input = findViewById(R.id.sum);
        TextView result = findViewById(R.id.result);
        RadioGroup radioGroup = findViewById(R.id.choise);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radioButton) {
                    if (salesRate_A == 1)
                        kurs.setText("From " + cur_A + " to " + cur_B + " = " + String.valueOf(salesRate_B));
                    else if (salesRate_B == 1)
                        kurs.setText("From " + cur_A + " to " + cur_B + " = " + String.valueOf(salesRate_A));
                    else if(cur_A.equals(cur_B))
                        kurs.setText("From " + cur_A + " to " + cur_B + " = " + String.format("%.2f", 1.0));
                    else
                        kurs.setText("From " + cur_A + " to UAH = " + String.valueOf(salesRate_A) + " to " + cur_B + " = " + String.valueOf(purchaseRate_B));
                } else if (i == R.id.radioButton2) {
                    if (purchaseRate_A == 1)
                        kurs.setText("From " + cur_A + " to " + cur_B + " = " + String.valueOf(purchaseRate_B));
                    else if (purchaseRate_B == 1)
                        kurs.setText("From " + cur_A + " to " + cur_B + " = " + String.valueOf(purchaseRate_A));
                    else if(cur_A.equals(cur_B))
                        kurs.setText("From " + cur_A + " to " + cur_B + " = " + String.format("%.2f", 1.0));
                    else
                        kurs.setText("From " + cur_A + " to UAH = " + String.valueOf(purchaseRate_A) + " to " + cur_B + " = " + String.valueOf(salesRate_B));
                } else if (i == R.id.radioButton3) {
                    if (salesRateNB_A == 1)
                        kurs.setText("From " + cur_A + " to " + cur_B + " = " + String.valueOf(salesRateNB_B));
                    else if (salesRateNB_B == 1)
                        kurs.setText("From " + cur_A + " to " + cur_B + " = " + String.valueOf(salesRateNB_B));
                    else if(cur_A.equals(cur_B))
                        kurs.setText("From " + cur_A + " to " + cur_B + " = " + String.format("%.2f", 1.0));
                    else
                        kurs.setText("From " + cur_A + " to UAH = " + String.valueOf(salesRateNB_A) + " to " + cur_B + " = " + String.valueOf(salesRateNB_B));
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        float res = 0;
                        String inputText = input.getText().toString();
                        if (inputText.isEmpty()) {
                            result.setText("Введіть суму!");
                            return;
                        }

                        float sum = Float.parseFloat(inputText);
                        int checkedId = radioGroup.getCheckedRadioButtonId();
                        if (checkedId == -1) {
                            result.setText("Оберіть тип операції!");
                            return;
                        }

                        if(salesRate_A == 0 || salesRate_B == 0 || purchaseRate_A == 0 || purchaseRate_B == 0) {
                            result.setText("Виберіть дату!");
                            return;
                        }

                        Log.d("CALCULATION", "Selected Rate A: Sales " + salesRate_A + ", Purchase " + purchaseRate_A + ", NB " + salesRateNB_A);
                        Log.d("CALCULATION", "Selected Rate B: Sales " + salesRate_B + ", Purchase " + purchaseRate_B + ", NB " + salesRateNB_B);

                        // Check if converting FROM UAH to another currency
                        if (salesRate_A == 1) {
                            // Convert from UAH to target currency (already working)
                            if (checkedId == R.id.radioButton) {
                                res = sum / salesRate_B;
                                kurs.setText("From " + cur_A + " to " + cur_B + " = " + String.format("%.2f", res));
                            } else if (checkedId == R.id.radioButton2) {
                                res = sum / purchaseRate_B;
                                kurs.setText("From " + cur_A + " to " + cur_B + " = " + String.format("%.2f", res));
                            } else if (checkedId == R.id.radioButton3) {
                                res = sum / salesRateNB_B;
                                kurs.setText("From " + cur_A + " to " + cur_B + " = " + String.format("%.2f", res));
                            }
                        } else if(cur_A.equals(cur_B)) {
                            res = sum;
                            kurs.setText("From " + cur_A + " to " + cur_B + " = " + String.format("%.2f", res));
                        } else if(salesRate_B == 1){
                            // Convert from another currency to UAH
                            if (checkedId == R.id.radioButton) {
                                res = sum * salesRate_A;  // Multiply by A's sales rate
                                kurs.setText("From " + cur_A + " to " + cur_B + " = " + String.format("%.2f", res));
                            } else if (checkedId == R.id.radioButton2) {
                                res = sum * purchaseRate_A;  // Multiply by A's purchase rate
                                kurs.setText("From " + cur_A + " to " + cur_B + " = " + String.format("%.2f", res));
                            } else if (checkedId == R.id.radioButton3) {
                                res = sum * salesRateNB_A;  // Multiply by A's NB sales rate
                                kurs.setText("From " + cur_A + " to " + cur_B + " = " + String.format("%.2f", res));
                            }
                        }
                        else {
                            if (checkedId == R.id.radioButton) {
                                res = (sum * salesRate_A) / (purchaseRate_B);
                                kurs.setText("From " + cur_A + " to UAH = " + String.format("%.2f", (sum * salesRate_A)) + " to " + cur_B + " = " + String.format("%.2f", res));
                            } else if (checkedId == R.id.radioButton2) {
                                res = sum * purchaseRate_A / salesRate_B;
                                kurs.setText("From " + cur_A + " to UAH = " + String.format("%.2f", (sum * purchaseRate_A)) + " to " + cur_B + " = " + String.format("%.2f", res));
                            } else if (checkedId == R.id.radioButton3) {
                                res = sum * salesRateNB_A / salesRateNB_B;
                                kurs.setText("From " + cur_A + " to UAH = " + String.format("%.2f",(sum * salesRateNB_A)) + " to " + cur_B + " = " + String.format("%.2f", res));
                            }
                        }

                        Log.d("CALCULATION", "Result: " + res);
                        result.setText(String.format("%.2f", res));
                    }
                });

            }
        });
    }
}