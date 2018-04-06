package cwo.pakidermo.dgom.com.mx.cwo.payments.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes;

import static cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes.SKU_12_MESES;
import static cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes.SKU_3_MESES;
import static cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes.SKU_OFERTA;

/**
 * Created by beto on 25/01/18.
 */

public class PaymentListener implements IabBroadcastReceiver.IabBroadcastListener{


    public interface LoadProductsListener{
        /**
         * Notifica que ya termino la carga de productos
         */
        public void notifyProductsLoaded();
    }


    private static final String TAG = "PaymentListener";
    private IabBroadcastReceiver mBroadcastReceiver;
    private static final int RC_REQUEST = 10501;
    private IabHelper mHelper;
    private Activity mActivity;
    private PaymentNotificationInterface mPaymentObserver;
    private LoadProductsListener mLoadProductsObserver;


    public PaymentListener(Activity activity, PaymentNotificationInterface paymentObserver, LoadProductsListener loadProductsObserver){
        this.mActivity = activity;
        this.mPaymentObserver = paymentObserver;
        this.mLoadProductsObserver = loadProductsObserver;

        productosDelUsuario();
    }



    @Override
    public void receivedBroadcast() {
        Log.d(TAG, "receivedBroadcast");
    }

    public void onDestroy() {
        //Desregistra el receiber
        mActivity.unregisterReceiver(mBroadcastReceiver);
        if (mHelper != null) {
            try {
                mHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        }
        mHelper = null;
    }

    /**
     * Metodo que se manda llamar para hacer un pago
     * @param sku SKU del producto que se quiere comprar (Este debe estar configurado en el store)
     */
    public void pagarSubscripcion(String sku){
        Log.d(TAG, "Botón comprar Subscripción: " + sku);
        if(!mHelper.subscriptionsSupported()){
            complain("Las suscripciones no estan soportadas en el telefono");
            return;
        }

        if (mHelper != null) {
            mHelper.flagEndAsync();
        }

        String payload = "";

        List<String> oldSku = null;

        //Si ya tiene una subscripcion, Y no esta comprando la misma subscripcion --> quiere hacer un pgrade!
        if(!TextUtils.isEmpty(AppConstantes.actualSKU) && !AppConstantes.actualSKU.equalsIgnoreCase(sku)){
            oldSku = new ArrayList<>();
            oldSku.add(AppConstantes.actualSKU);

            Log.d(TAG,"Ya se tiene una suscripcion anterior del mismo producto");
        }

        try {
            mHelper.launchPurchaseFlow(
                    mActivity,
                    sku,
                    IabHelper.ITEM_TYPE_SUBS,
                    oldSku,
                    RC_REQUEST,
                    mPurchaseFinishedListener,
                    payload);



        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
            complain("Error al lanzar el flujo de compra: " + e.getMessage());

        }
    }


    //--------------------------------------------



    //--------------- init in app purchase

    private void productosDelUsuario(){
        Log.d(TAG, "Mhelpere created");
        mHelper = new IabHelper(mActivity, AppConstantes.ANDROID_STORE_KEY);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(AppConstantes.PAYMENT_DEBUG);


        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished");

                if(!result.isSuccess()){
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }


                if (mHelper == null) {
                    return;
                }

                mBroadcastReceiver = new IabBroadcastReceiver(PaymentListener.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                mActivity.registerReceiver(mBroadcastReceiver, broadcastFilter);



                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {

                    //Pregunta por los productos adquiridos por el usuario
                   // mHelper.queryInventoryAsync(mGotInventoryListener);

                    //Pregunta por los productos disponibles en la tienda
                    ArrayList<String> skuList = new ArrayList<String> ();

                    ArrayList<String> subsList = new ArrayList<String> ();
                    subsList.add(SKU_OFERTA);
                    subsList.add(SKU_3_MESES);
                    subsList.add(SKU_12_MESES);


                    mHelper.queryInventoryAsync(true, skuList, subsList, mQueryAvailableInventoryFinishedListener);


                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }

            }
        });
    }

    //Listener de productos disponibles
    IabHelper.QueryInventoryFinishedListener mQueryAvailableInventoryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
            Log.d(TAG, "QueryInventoryFinishedListener");
            if (result.isFailure()) {
                Log.d(TAG, "Querying Inventory Failed: " + result);
                return;
            }

            AppConstantes.productosDisponibles = inv.mSkuMap;

            //Recorre el objeto
            Iterator<String> iter = inv.mSkuMap.keySet().iterator();
            while(iter.hasNext()){
                String sku = iter.next();
                Log.d(TAG, "Title: " + inv.getSkuDetails(sku).getTitle());
                Log.d(TAG, "Description: " + inv.getSkuDetails(sku).getDescription());
                Log.d(TAG, "Price = " + inv.getSkuDetails(sku).getPrice());
            }

            if(mLoadProductsObserver != null){
                mLoadProductsObserver.notifyProductsLoaded();
            }


        }
    };


    //Listener de productos comprados por el usuario
    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) {
                return;
            }

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            //Posibles objetos que tiene el usuario comprados




            Purchase _offeringSubscription = inventory.getPurchase(SKU_OFERTA);
            Purchase _3mSubscription = inventory.getPurchase(AppConstantes.SKU_3_MESES);
            Purchase _12mSubscription = inventory.getPurchase(AppConstantes.SKU_12_MESES);

            Log.d(TAG, "_offeringSubscription: " + _offeringSubscription);
            Log.d(TAG, "_3mSubscription: " + _3mSubscription);
            Log.d(TAG, "_12mSubscription: " + _12mSubscription);


            if(_3mSubscription != null && _3mSubscription.isAutoRenewing()){
                AppConstantes.subscribed = true;
                AppConstantes.actualSKU = AppConstantes.SKU_3_MESES;
            } else if(_12mSubscription != null && _12mSubscription.isAutoRenewing()){
                AppConstantes.subscribed = true;
                AppConstantes.actualSKU = AppConstantes.SKU_12_MESES;
            }else if(_offeringSubscription != null && _offeringSubscription.isAutoRenewing()){
                AppConstantes.subscribed = true;
                AppConstantes.actualSKU = SKU_OFERTA;
            }else{
                AppConstantes.subscribed = false;
                AppConstantes.actualSKU = "";
            }


            Log.d(TAG, "Subscribed: " + AppConstantes.subscribed);
            Log.d(TAG, "Actual SKU: " + AppConstantes.actualSKU);

        }

    };





    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null)
                return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                //setWaitScreen(false);
                return;
            }

            Log.d(TAG,"Compra completa");

            Log.d(TAG,"Payload " + purchase.getDeveloperPayload());

            if(purchase.getSku().equals(AppConstantes.SKU_3_MESES)){
                AppConstantes.actualSKU = AppConstantes.SKU_3_MESES;
                AppConstantes.autoRenew = purchase.isAutoRenewing();
            }

            //Notifica a la interface del pago
            mPaymentObserver.paymentReceibed(AppConstantes.actualSKU);
        }
    };



    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            if (mHelper == null){
                return;
            }

            if (result.isSuccess()) {
                Log.d(TAG, "Consumption successful. Provisioning.");
            }
            else {
                complain("Error while consuming: " + result);
            }
            Log.d(TAG, "End consumption flow.");
        }
    };

    //-------------- SUPPORT METHODS -------


    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(mActivity);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }



    public interface PaymentNotificationInterface{
        public void paymentReceibed(String sku);
    }
}
