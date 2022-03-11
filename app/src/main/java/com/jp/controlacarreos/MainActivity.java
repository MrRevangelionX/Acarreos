package com.jp.controlacarreos;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinnerCountry, spinnerCity;
    Button btnListo, btnInsertar;
    EditText txtScan, txtQuery;
    ArrayList<String> countryList = new ArrayList<>();
    ArrayList<String> cityList = new ArrayList<>();
    ArrayAdapter<String> countryAdapter;
    ArrayAdapter<String> cityAdapter;
    RequestQueue requestQueue;
    String countryCode, countryName, cityCode, cityName, scanner;
    String [] codeSucursal, codeProyecto, arrScan;
    String HOST_ADDRESS = "http://192.168.21.2:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);
        spinnerCountry = findViewById(R.id.spinnerCountry);
        spinnerCity = findViewById(R.id.spinnerCity);
        btnListo = findViewById(R.id.btnLISTO);
        btnInsertar = findViewById(R.id.btnInsertar);
        txtScan = findViewById(R.id.txtScan);
        txtQuery = findViewById(R.id.txtQuery);
        String url = HOST_ADDRESS + "/ControlAcarreos/info/sucursales.php";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, null, response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("sucursales");
                        for(int i=0; i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            countryCode = jsonObject.optString("codsucursal");
                            countryName = jsonObject.optString("nomsucursal");
                            countryList.add(countryCode + " - " + countryName);
                            countryAdapter = new ArrayAdapter<>(MainActivity.this,
                                    android.R.layout.simple_spinner_item, countryList);
                            countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCountry.setAdapter(countryAdapter);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {

                });
        requestQueue.add(jsonObjectRequest);
        spinnerCountry.setOnItemSelectedListener(this);

        final internalDB internaldb = new internalDB(getApplicationContext());

        btnInsertar.setOnClickListener(view -> {
            scanner = txtScan.getText().toString();
            arrScan = scanner.split(",");

            try {
                internaldb.agregarCheckpoint("9",codeSucursal[0],codeProyecto[0],arrScan[0], arrScan[1], arrScan[2]);
                Toast.makeText(getApplicationContext(), R.string.dataGuardada, Toast.LENGTH_SHORT).show();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            syncSQL();
            txtScan.setText("");
            txtScan.requestFocus();
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getId() == R.id.spinnerCountry){
            cityList.clear();
            String selectedCountry = adapterView.getSelectedItem().toString();
            codeSucursal = selectedCountry.split(" ");
            String url = HOST_ADDRESS + "/ControlAcarreos/info/proyectos.php?sucursal="+codeSucursal[0];
            requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    url, null, response -> {
                        try {
                            JSONArray jsonArray = response.getJSONArray("proyectos");
                            for(int i1 = 0; i1 <jsonArray.length(); i1++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i1);
                                cityCode = jsonObject.optString("codproyecto");
                                cityName = jsonObject.optString("nomproyecto");
                                cityList.add(cityCode + " - " + cityName);
                                cityAdapter = new ArrayAdapter<>(MainActivity.this,
                                        android.R.layout.simple_spinner_item, cityList);
                                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerCity.setAdapter(cityAdapter);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {

                    });
            requestQueue.add(jsonObjectRequest);
            spinnerCity.setOnItemSelectedListener(this);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void iniciarRegistro(View view) {
        spinnerCountry.setEnabled(false);
        spinnerCity.setEnabled(false);
        btnListo.setBackgroundColor(Color.argb(50,0,0,0));
        btnListo.setEnabled(false);
        txtScan.setVisibility(View.VISIBLE);
        txtQuery.setVisibility(View.VISIBLE);
        btnInsertar.setVisibility(View.VISIBLE);
        String selectedCity = spinnerCity.getSelectedItem().toString();
        codeProyecto = selectedCity.split(" ");
        txtScan.requestFocus();
    }

    public void syncSQL(){
        String url = HOST_ADDRESS + "/ControlAcarreos/sync/insertuser.php";
        JSONObject parametros = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String ahorita = sdf.format(new Date());

        try {
            parametros.put("codempresa","9");
            parametros.put("codsucursal",codeSucursal[0]);
            parametros.put("codproyecto",codeProyecto[0]);
            parametros.put("transOwner",arrScan[0]);
            parametros.put("transPlate",arrScan[1]);
            parametros.put("transCapacity",arrScan[2]);
            parametros.put("checkpoint",ahorita);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, parametros, response -> {
            try {
                JSONArray jsonArray = response.getJSONArray("sucursales");
                for(int i=0; i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    countryCode = jsonObject.optString("codsucursal");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

        });
        requestQueue.add(jsonObjectRequest);
    }
}