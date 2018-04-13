package cwo.pakidermo.dgom.com.mx.cwo;

//https://www.androidhive.info/2014/05/android-working-with-volley-library-1/

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

import cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes;
import cwo.pakidermo.dgom.com.mx.cwo.app.AppController;
import cwo.pakidermo.dgom.com.mx.cwo.payments.util.IabHelper;
import cwo.pakidermo.dgom.com.mx.cwo.payments.util.IabResult;
import cwo.pakidermo.dgom.com.mx.cwo.payments.util.Inventory;
import cwo.pakidermo.dgom.com.mx.cwo.payments.util.Purchase;
import cwo.pakidermo.dgom.com.mx.cwo.to.UpdateApp;
import cwo.pakidermo.dgom.com.mx.cwo.to.VideoContent;
import cwo.pakidermo.dgom.com.mx.cwo.utils.DaysExcercisesUtils;
import cwo.pakidermo.dgom.com.mx.cwo.utils.OfferScreenUtils;

public class SplashActivity extends Activity {

    private static final String TAG = "SplashActivity";
    private long SPLASH_SCREEN_TIME = 1000;
    private FirebaseAuth mAuth;
    private IabHelper mHelper;


    private static final int GET_DATA_TYPE = 1;
    private static final int GET_UPDATE = 2;
    private   int pendantProccess = 0;
    private static final String SO = "android";

    private TextView txtNoInternetAccess;

    //Analiticos
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);



        txtNoInternetAccess = (TextView) findViewById(R.id.txt_no_internet_access);
        txtNoInternetAccess.setVisibility(View.GONE);


        if(!getSharedPreferences(AppConstantes.APP_PREFERENCE, Activity.MODE_PRIVATE).getBoolean(AppConstantes.IS_ICON_CREATED, false)){
            addShortcut();
            getSharedPreferences(AppConstantes.APP_PREFERENCE, Activity.MODE_PRIVATE).edit().putBoolean(AppConstantes.IS_ICON_CREATED, true);
        }

        mAuth = FirebaseAuth.getInstance();

        //Inicializa las conecciones de datos
        initConnectionData();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

    }

    /**
     * Agrega el shorcut en el aplicativo
     */
    private void addShortcut() {
        //Adding shortcut for MainActivity
        //on Home screen
        Intent shortcutIntent = new Intent(getApplicationContext(),
                SplashActivity.class);

        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, R.string.app_name);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                        R.mipmap.ic_launcher));

        addIntent
                .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra("duplicate", false);  //may it's already there so don't duplicate
        getApplicationContext().sendBroadcast(addIntent);
    }

    private void initConnectionData(){
        //Procesos pendientes antes de iniciar el splash screen
        pendantProccess = 3;

        //Verifica si hay otra version
        getJsonObject(AppConstantes.JSON_UPDATE,this.getApplicationContext() );

        //verifica las compras de la app
        initHelper();

        //Carga los datos de la aplicación
        getJsonArray(AppConstantes.JSON_DATA,this.getApplicationContext() );
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        AppConstantes.FIREBASE_USER = currentUser;
    }

    /**
     *
     */
    private void activateSplash(){
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                Intent i;
                //verificar si el usuario esta logeado
                if (AppConstantes.FIREBASE_USER == null){
                   i = new Intent(SplashActivity.this, WelcomeActivity.class);
                }else{


                    //Pone la fecha de inicio del ejercicio hace 10 días
                    //DaysExcercisesUtils.setTempData(getApplicationContext());

                    //Verifica cuantos dias lleva el usuario haciendo ejercicio
                    int days = DaysExcercisesUtils.numberOfDaysInRow(getApplicationContext());
                    boolean yaPagoElUsuario = AppConstantes.subscribed;
                    Log.d(TAG, "yaPagoElUsuario: " + yaPagoElUsuario);

                    //------------- DEFINE LA PANTALLA QUE VA A MOSTRAR ------------------------
                    //Si no ha pagado y es tiempo de mostrar la pantalla
                    if(!yaPagoElUsuario && OfferScreenUtils.showOfferScreen(getApplicationContext())) {
                        i = new Intent(SplashActivity.this, OfferingActivity.class);
                        OfferScreenUtils.setShownOfferScreen(getApplicationContext());
                    }else if(days > 0){ //Si lleva una racha de ejercicio
                        i = new Intent(SplashActivity.this, DayCounterActivity.class);
                    }else {
                        i = new Intent(SplashActivity.this, MainActivity.class);
                    }
                }



                startActivity(i);
                finish();
            }
        }, SPLASH_SCREEN_TIME);
    }

    /**
     * Metodo para recuperar el contenido de la aplicación
     * @param json
     */
    private void setData(String json){
        Gson gson = new Gson();
        VideoContent[]data = gson.fromJson(json, VideoContent[].class);

        Log.d(TAG, "data size: " + data.length);
        AppConstantes.videoData.clear();
        AppConstantes.videoData.addAll(Arrays.asList(data));

        continueScreen("Contenido de la aplicación");
    }

    /**
     * Metodo para recuperar el si hay una nueva version de la aplicación
     * @param json
     */
    private void setUpdateData(String json){
        Gson gson = new Gson();
        UpdateApp update = gson.fromJson(json, UpdateApp.class);
        Log.d(TAG, "Update: " + update);

        String versionName = "";
        int versionCode = -1;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //Verifica que se consulte android y que la version sea diferente
        //if(update.getOs().equalsIgnoreCase(SO) && !update.getVersion().equalsIgnoreCase(versionCode+"")){

        //Si la version que esta publicada en internet es mayor que la version actual
        if(versionCode < Integer.parseInt(update.getVersion())){

            //Si es android y la version del despliegue es diferente pide actualizar la aplicacion
            new AlertDialog.Builder(this)
                    .setTitle(R.string.app_update_title)
                    .setMessage(R.string.app_update_question)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Toast.makeText(MainActivity.this, "Yaay", Toast.LENGTH_SHORT).show();

                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }



                        }})
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                        }}).show();
        }else{
            //Continua el splash
            continueScreen("Validación de actualización de app");
        }

    }






    private void continueScreen(String msg){

        Log.d(TAG, "Se libera el proceso de: " + msg);

        pendantProccess--;

        //Si ya no hay tareas pendientes, inicia el splash
        if(pendantProccess == 0){
            activateSplash();
        }
    }


    private void getJsonObject(String url, Context context){
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        Log.d(TAG, "Cargando Update");


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Update response: " + response.toString());
                        setUpdateData(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Update Response error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                //This indicates that the reuest has either time out or there is no connection
                    Log.d(TAG, "Time out or Not connection error");
                    messageError("Time out or Not connection error");
                } else if (error instanceof AuthFailureError) {
                // Error indicating that there was an Authentication Failure while performing the request
                    Log.d(TAG, "Authentication error");
                    messageError("Authentication error");
                } else if (error instanceof ServerError) {
                //Indicates that the server responded with a error response
                    Log.d(TAG, "Server error");
                    messageError("Server error");
                } else if (error instanceof NetworkError) {
                //Indicates that there was network error while performing the request
                    Log.d(TAG, "Network error");
                    messageError("Network error");
                } else if (error instanceof ParseError) {
                // Indicates that the server response could not be parsed
                    Log.d(TAG, "Parse error");
                    messageError("Parse error");
                }else{
                    Log.d(TAG, error.getMessage());
                    messageError(error.getMessage());
                }
            }


        });

        /*
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        */
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void getJsonArray(String url, Context context ){
        // Tag used to cancel the request
        String tag_json_arry = "json_array_req";

        //String url = "https://api.androidhive.info/volley/person_array.json";

        JsonArrayRequest req = new JsonArrayRequest(url,new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        setData(response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    //This indicates that the reuest has either time out or there is no connection
                    Log.d(TAG, "Time out or Not connection error");
                    messageError("Time out or Not connection error");
                } else if (error instanceof AuthFailureError) {
                    // Error indicating that there was an Authentication Failure while performing the request
                    Log.d(TAG, "Authentication error");
                    messageError("Authentication error");
                } else if (error instanceof ServerError) {
                    //Indicates that the server responded with a error response
                    Log.d(TAG, "Server error");
                    messageError("Server error");
                } else if (error instanceof NetworkError) {
                    //Indicates that there was network error while performing the request
                    Log.d(TAG, "Network error");
                    messageError("Network error");
                } else if (error instanceof ParseError) {
                    // Indicates that the server response could not be parsed
                    Log.d(TAG, "Parse error");
                    messageError("Parse error");
                }else{
                    Log.d(TAG, error.getMessage());
                    messageError(error.getMessage());
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req, tag_json_arry);
    }

// PAYMENTS ------------------------------

    //--------------- init in app purchase

    private void initHelper(){
        Log.d(TAG, "Mhelpere created");
        mHelper = new IabHelper(this, AppConstantes.ANDROID_STORE_KEY);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(AppConstantes.PAYMENT_DEBUG);


        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished");

                if(!result.isSuccess()){
                    complain("Problem setting up in-app billing: " + result);
                    continueScreen("Paymen-> Problem setting up in-app billing: " + result);
                    return;
                }


                if (mHelper == null) {
                    continueScreen("Payment -> start setup - mHelpper null");
                    return;
                }

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }

            }
        });
    }



    //------------ LISTENER de eventos de compras


    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) {
                continueScreen("Payment -> onQueryInventoryFinished - mHelpper null");
                return;
            }

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                continueScreen("Payment -> Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            //Posibles objetos que tiene el usuario comprados

            Purchase _3mSubscription = inventory.getPurchase(AppConstantes.SKU_3_MESES);

            if(_3mSubscription != null && _3mSubscription.isAutoRenewing()){
                AppConstantes.subscribed = true;
                AppConstantes.actualSKU = AppConstantes.SKU_3_MESES;
            }else{
                AppConstantes.subscribed = false;
                AppConstantes.actualSKU = "";
            }


            Log.d(TAG, "Subscribed: " + AppConstantes.subscribed);
            Log.d(TAG, "Actual SKU: " + AppConstantes.actualSKU);

            continueScreen("Payment -> ok");

        }

    };

    //-----------------

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    private void messageError(String msg){
        txtNoInternetAccess.setVisibility(View.VISIBLE);
        txtNoInternetAccess.setText(msg);
    }

}
