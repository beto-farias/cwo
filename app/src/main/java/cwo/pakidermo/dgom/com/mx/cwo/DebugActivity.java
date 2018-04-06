package cwo.pakidermo.dgom.com.mx.cwo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

import java.util.Date;
import java.util.List;

import cwo.pakidermo.dgom.com.mx.cwo.db.DatabaseHelper;
import cwo.pakidermo.dgom.com.mx.cwo.db.UserDataController;
import cwo.pakidermo.dgom.com.mx.cwo.download.util.DownloadReceiver;
import cwo.pakidermo.dgom.com.mx.cwo.to.Exercise;
import cwo.pakidermo.dgom.com.mx.cwo.utils.DaysExcercisesUtils;
import cwo.pakidermo.dgom.com.mx.cwo.utils.OfferScreenUtils;

public class DebugActivity extends Activity {

    EditText txtDebug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        txtDebug = (EditText) findViewById(R.id.txt_debug);

        infoDiasEjercicio();

        infoOfferScreen();

        infoDownloadedVideos();

        databaseExercise();

    }

    private void databaseExercise(){
        append("----------- EXERCICES -------------");
        UserDataController udc = new UserDataController(this);
        List<Exercise> exList = udc.getExerciseData();

        for (Exercise ex: exList) {
            append(ex.toString());
        }
    }

    private void infoOfferScreen(){
        append("----------- OFFER SCREEN INFO -------------");

        append("Se debe mostrar la pantalla de oferta: " + OfferScreenUtils.showOfferScreen(this));
        append("La fecha que se mostro la pantalla de oferta: " + new Date(OfferScreenUtils.getLastShwedOfferScreen(this) ));
    }

    private void infoDownloadedVideos(){
        append("----------- DOWNLOADED INFO -------------");

        append("Descargando id: " + DownloadReceiver.getDownloadID(this));
        append("Descargando json: " + DownloadReceiver.getDownloadJSON(this));

        append("Lista de videos descargados: " + DownloadReceiver.getSavedVideoContentLibrary(this));
    }

    private void infoDiasEjercicio(){

        append("----------- DAYS OF EXERCISE -------------");

        int days = DaysExcercisesUtils.numberOfDaysInRow(getApplicationContext());
        append("Dias in a row: " + days);

        int horasDesdeUltimoEjercicio = DaysExcercisesUtils.numberOfHoursSinceLastExercice(getApplicationContext());
        append("Horas desde el ultimo ejercicio: " + horasDesdeUltimoEjercicio);


        Date fechaInicio = new Date( DaysExcercisesUtils.getStartExerciceDate(this));

        append("Fecha de inicio in a row: " + fechaInicio);

        Date fechaLastExcercice = new Date( DaysExcercisesUtils.getLastExerciceDate(this));

        append("Fecha de ultimo ejericicio: " + fechaLastExcercice);
    }


    private void append(String txt){
        txtDebug.append(txt + "\n");
    }
}
