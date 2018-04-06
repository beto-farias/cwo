package cwo.pakidermo.dgom.com.mx.cwo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes;
import cwo.pakidermo.dgom.com.mx.cwo.payments.util.PaymentListener;

public class OfferingActivity extends Activity implements PaymentListener.PaymentNotificationInterface, PaymentListener.LoadProductsListener {

    private static final String TAG = "OfferingActivity";
    private PaymentListener mPaymentListener;

    private TextView txtPriceMonthly;
    private TextView txtPrecio12mTotal;

    //Analiticos
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offering);
        mPaymentListener = new PaymentListener(this, this, this);
        txtPriceMonthly = (TextView) findViewById(R.id.txt_price_monthly);
        txtPrecio12mTotal = (TextView) findViewById(R.id.txt_precio_12m_total);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPaymentListener != null) {
            mPaymentListener.onDestroy();
        }
        mPaymentListener = null;
    }



    public void subscribirAction(View v){
        Log.d(TAG, "Botón comprar Subscripción oferta meses.");
        mPaymentListener.pagarSubscripcion(AppConstantes.SKU_OFERTA);
    }


    public void continuarSinSubscripcionAction(View v){
        Intent i = new Intent(OfferingActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }


    @Override
    public void paymentReceibed(String sku) {
        Intent i = new Intent(OfferingActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void notifyProductsLoaded() {

        double price;
        double monthlyPrice;
        String code;


        if(AppConstantes.productosDisponibles.containsKey(AppConstantes.SKU_12_MESES)) {
            price = AppConstantes.productosDisponibles.get(AppConstantes.SKU_12_MESES).getPriceAmountMicros()/1000000;
            monthlyPrice = price/12;

            code =  AppConstantes.productosDisponibles.get(AppConstantes.SKU_3_MESES).getPriceCurrencyCode();
            txtPriceMonthly.setText( code + AppConstantes.decimalFormat.format(monthlyPrice) );
            txtPrecio12mTotal.setText(String.format(getString(R.string.precio_total), code, AppConstantes.decimalFormat.format(price)));
        }



    }
}
