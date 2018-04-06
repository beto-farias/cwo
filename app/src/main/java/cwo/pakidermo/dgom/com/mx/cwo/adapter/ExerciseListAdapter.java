package cwo.pakidermo.dgom.com.mx.cwo.adapter;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cwo.pakidermo.dgom.com.mx.cwo.R;
import cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes;
import cwo.pakidermo.dgom.com.mx.cwo.to.Exercise;
import cwo.pakidermo.dgom.com.mx.cwo.to.ExerciseDataType;
import cwo.pakidermo.dgom.com.mx.cwo.to.ExerciseSeparator;
import cwo.pakidermo.dgom.com.mx.cwo.to.ExerciseTitle;
import cwo.pakidermo.dgom.com.mx.cwo.to.VideoContent;
import cwo.pakidermo.dgom.com.mx.cwo.to.VideoContentDownloaded;
import cwo.pakidermo.dgom.com.mx.cwo.utils.AnimateCounter;

/**
 * Created by beto on 07/01/18.
 */

public class ExerciseListAdapter extends ArrayAdapter<ExerciseDataType> {

    private static final String TAG = "ExerciseListAdapter";
    private Activity context;
    private List<ExerciseDataType> data  = new ArrayList<ExerciseDataType>();



    public enum RowType {
        TYPE_ITEM, TYPE_SEPARATOR, TYPE_TITLE
    }

    private DownloadManager downloadManager;



    public ExerciseListAdapter(@NonNull Activity context, int resource, List<ExerciseDataType> items) {
        super(context, resource);
        this.context = context;
        this.data = items;
    }



    public void addItem(ExerciseDataType item){
        data.add(item);
        this.notifyDataSetChanged();
    }

    public void setItems(List<ExerciseDataType> data){
        this.data = data;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount(){
        return data.size();
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;

    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getDataType();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView;

        int type = getItemViewType(position);

        //Renglon
        if(type == Exercise.TYPE_EXERCISE) {
            rowView = getRowView(position,parent);
        }else if (type == Exercise.TYPE_SEPARATOR){
            //Separador
            rowView = getRowSeparatorView(position,parent);
        }else{
            rowView = getRowTitle(position,parent);
        }

        return rowView;
    }


    private View getRowTitle(int position, ViewGroup parent) {

        ExerciseTitle item = (ExerciseTitle) data.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_exercise_title, parent, false);


        TextView txtNumber = (TextView) rowView.findViewById(R.id.txt_exercise_number);
        TextView txtOfficeWorkout = (TextView) rowView.findViewById(R.id.txt_exercise_office_work);
        TextView txtChallenge = (TextView) rowView.findViewById(R.id.txt_exercise_challenges);
        TextView txtStreching = (TextView) rowView.findViewById(R.id.txt_exercise_streching);
        TextView txtTactfit = (TextView) rowView.findViewById(R.id.txt_exercise_tactfit);

        Log.d(TAG, txtNumber.toString());

      txtNumber.setText(item.getTotal() + "");
        txtOfficeWorkout.setText(item.getOffice() + "");
        txtChallenge.setText( item.getChallenges() + "");
        txtStreching.setText( item.getStreching() + "");
        txtTactfit.setText(item.getTactfit() + "");

        //Solo que haya más de 0 se anima
        if(item.getTotal() >0) {

            AnimateCounter animateCounter = new AnimateCounter.Builder(txtNumber)
                    .setCount(0, item.getTotal())
                    .setDuration(500)
                    .build();


            animateCounter.execute();
        }
        return rowView;
    }


    /**
     *
     * @param position
     * @param parent
     * @return
     */
    private View getRowView(int position, ViewGroup parent) {

        Exercise item = (Exercise) data.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_exercise_row, parent, false);

        ImageView imgThumnail = (ImageView) rowView.findViewById(R.id.img_exercise_thumnail);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt_exercise_name);
        TextView txtFeha = (TextView) rowView.findViewById(R.id.txt_exercise_date);

        Picasso.with(context)
                .load(item.getImgUrl())
                //.networkPolicy(NetworkPolicy.OFFLINE)
                .into(imgThumnail);

        txtTitle.setText(item.getName());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(item.getDate());
        String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        txtFeha.setText(
                cal.get(Calendar.DAY_OF_MONTH) +
                " " + month +
                " " + cal.get(Calendar.YEAR) +
                " " + numberFormat2Digits(cal.get(Calendar.HOUR_OF_DAY)) +
                ":" + numberFormat2Digits(cal.get(Calendar.MINUTE))
        );

        return rowView;
    }

    private String numberFormat2Digits(int n){
        if(n < 10){
            return "0" + n;
        }else{
            return "" + n;
        }
    }

    /**
     * Calcula el renglon del video
     * @param position
     * @param parent
     * @return
     */
    private View getRowSeparatorView(final int position, ViewGroup parent){

        ExerciseSeparator item = (ExerciseSeparator) data.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_exercise_separator, parent, false);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt_fecha_separator);
        TextView txtTime = (TextView) rowView.findViewById(R.id.txt_separator_count);

        String countRutinas = String.format(context.getString(R.string.rutinas_realizadas_count), ""+ item.getCount());

        txtTitle.setText(item.getDate());
        txtTime.setText(countRutinas);


        return rowView;
    }

}
