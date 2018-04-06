package cwo.pakidermo.dgom.com.mx.cwo.app;

import com.google.firebase.auth.FirebaseUser;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cwo.pakidermo.dgom.com.mx.cwo.payments.util.SkuDetails;
import cwo.pakidermo.dgom.com.mx.cwo.to.VideoContent;

/**
 * Created by beto on 07/01/18.
 */

public class AppConstantes {


    public static final List<VideoContent> videoData = new ArrayList<VideoContent>();

    public static final String PREFS_NAME = "CWO_PREFS";

    //La aplicacion está en modo debug
    public static final boolean DEBUG = false;

    public static final int OFFICE_WORKOUT_TYPE = 1;
    public static final int CHALLENGES_TYPE = 2;
    public static final int STRECH_TYPE = 3;
    public static final int TACTFIT_TYPE = 4;



    public static final int ACCESS_OPEN = 1;
    //public static final int ACCESS_PREVIEW = 2;
    public static final int ACCESS_PRIVATE = 2;


    public static final String VIDEO_EXTRA = "VIDEO_EXTRA";

    public static final String VIDEO_FEELINGS[] = {"Muy cansado", "Cansado", "Bien", "Muy bien", "Excelente"};

    //public static final String CONTENT_URL = "http://notei.com.mx/celebritywo/cwo.json";
    //public static final String JSON_DATA = "http://notei.com.mx/celebritywo/cwo.json";

    public static final String JSON_DATA = "http://api.celebrityworkout.com.mx/v1/web/service";
    public static final String JSON_UPDATE = "http://api.celebrityworkout.com.mx/v1/versioning/update_android.json";
    //public static final String JSON_UPDATE = "http://notei.com.mx/celebritywo/update_android.json";
    public static final boolean PAYMENT_DEBUG = true;
    public static final String APP_PREFERENCE = "APP_PREFERENCE" ;
    public static final String IS_ICON_CREATED = "IS_ICON_CREATED";
    public static final String DOWNLOADING_JSON = "DOWNLOADING_JSON"; //Json del objeto que se está descargando
    public static final String DOWNLOADED_JSON_LIBRARY = "DOWNLOADED_JSON_LIBRARY"; //Json de los videos que ya se descargaron



    public static String DOWNLOAD_ID = "DOWNLOAD_ID";
    public static FirebaseUser FIREBASE_USER;


    //public static final String ANDROID_STORE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlsXCBlhYWkKz0qouVFDBE322R/BH5+inPut15sUMgeYqL1iWuMqB8E8awXyerrSer3Q+O2zHaE8fa1L9d7+Ik7xW3nHth4kQAwkG+fivJ3cQE0xmeG6pud2Qunu5z9Oba3X3+SbCf0bhAshnUInZuIoLQXqEyFNPmKSAoc9JfLVMvdQ5qv94qF7tA2m2ohy6SYptVYShbFLIy8OHkCcmKNM2vGBaFwXmtLftZHpMBE/5aO3TGTlWEKt6FOc3UZwVvFbENCrRI48a2Q+xiBN64MCyzqmxJy2A7y4EvqIUM7IAbsRr4gEhvdp9xrqviUxooKX4dYvnz2shsKWSPRDoawIDAQAB";

    //STORE DE CWO REYNA
    public static final String ANDROID_STORE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiksZeOHplX+GgZavjGNVnq+plJlkmne2h2ZOm5rGJhlGz4pAJVZfQE43I04XoJdoi9nhN8hx9cfRmE1FItveD5UroOOmJ1zWF7X+l6J3Lm2o16aJp8x2VT0vp2c1Epm0F2rwNevqFx7RqwZQgYP0Nxq9XLYH2W1vq41MKlLq0t+/I7J0q/+8Y9mD/Gaxnnb2ib/wRC0iwg6jMJfqRqol9td/5RLkKOtvbw5xTz3dQQeViCC6WOHX4SoUs4gQ4271iW2eF9khIrBRVdiVrX9W/9kRl/8qIQkWTDNNwLScIvhIXtELHuk5UbQVipXUy7oAmmiBnm3ZvUogGYbSFkOV7wIDAQAB";


    public static final String SKU_3_MESES = "cw_3_meses";
    public static final String SKU_12_MESES = "cw_12_meses";
    public static final String SKU_OFERTA = "cw_3_meses";




    // PAGOS

    public static  boolean subscribed;
    public static String actualSKU;
    public static boolean autoRenew;


    public static Map<String, SkuDetails> productosDisponibles;

    public static final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

}
