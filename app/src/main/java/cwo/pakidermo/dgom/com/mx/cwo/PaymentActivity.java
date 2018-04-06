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

public class PaymentActivity extends Activity   implements PaymentListener.PaymentNotificationInterface, PaymentListener.LoadProductsListener {




    private static final String TAG = "PaymentActivity";
    private PaymentListener mPaymentListener;

    private TextView txtPrice3m;
    private TextView txtPrice12m;
    private TextView txtPrecio3mTotal;
    private TextView txtPrecio12mTotal;

    //Analiticos
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        mPaymentListener = new PaymentListener(this, this, this);

        txtPrice3m = (TextView) findViewById(R.id.txt_price_3m);
        txtPrice12m = (TextView) findViewById(R.id.txt_price_12m);

        txtPrecio3mTotal = (TextView) findViewById(R.id.txt_precio_3m_total);
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




//----------- botones de compras

    public void buyAction3m(View v){
        Log.d(TAG, "Botón comprar Subscripción 3 meses.");
        mPaymentListener.pagarSubscripcion(AppConstantes.SKU_3_MESES);
    }

    public void buyAction12m(View v){
        Log.d(TAG, "Botón comprar Subscripción 12 meses.");
        mPaymentListener.pagarSubscripcion(AppConstantes.SKU_12_MESES);
    }

    @Override
    public void paymentReceibed(String sku) {
        Intent i = new Intent(PaymentActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }


    @Override
    public void notifyProductsLoaded() {
        double price;
        double monthlyPrice;
        String code;
        if(AppConstantes.productosDisponibles.containsKey(AppConstantes.SKU_3_MESES)) {
            price = AppConstantes.productosDisponibles.get(AppConstantes.SKU_3_MESES).getPriceAmountMicros()/1000000;
            monthlyPrice = price/3;

            code =  AppConstantes.productosDisponibles.get(AppConstantes.SKU_3_MESES).getPriceCurrencyCode();

            txtPrice3m.setText( code + AppConstantes.decimalFormat.format(monthlyPrice) );

            txtPrecio3mTotal.setText(String.format(getString(R.string.precio_total), code, AppConstantes.decimalFormat.format(price)));
        }

        if(AppConstantes.productosDisponibles.containsKey(AppConstantes.SKU_12_MESES)) {
            price = AppConstantes.productosDisponibles.get(AppConstantes.SKU_12_MESES).getPriceAmountMicros()/1000000;
            monthlyPrice = price/12;

            code =  AppConstantes.productosDisponibles.get(AppConstantes.SKU_3_MESES).getPriceCurrencyCode();
            txtPrice12m.setText( code + AppConstantes.decimalFormat.format(monthlyPrice) );
            txtPrecio12mTotal.setText(String.format(getString(R.string.precio_total), code, AppConstantes.decimalFormat.format(price)));
        }
    }
}
