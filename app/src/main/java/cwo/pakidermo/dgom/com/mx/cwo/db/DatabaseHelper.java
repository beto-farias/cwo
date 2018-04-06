package cwo.pakidermo.dgom.com.mx.cwo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cwo.pakidermo.dgom.com.mx.cwo.to.Exercise;
import cwo.pakidermo.dgom.com.mx.cwo.to.VideoContent;

/**
 * Created by beto on 02/02/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "cwo.db";
    private static final String TABLE_NAME = "views_videos";

    private static final String COL_1 = "ID";
    private static final String COL_2 = "NAME";
    private static final String COL_3 = "DATE";
    private static final String COL_4 = "IMG_URL";
    private static final String COL_5 = "FEEL";
    private static final String COL_6 = "EXERCISE_TYPE";
    private static final String COL_7 = "SYNC";
    private static final String COL_8 = "UUID";


    private static final int DATABASE_VERSION = 4;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Create DB");
        db.execSQL("create table " + TABLE_NAME + " (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT," +
                " DATE LONG," +
                " IMG_URL TEXT," +
                " FEEL INTEGER," +
                " EXERCISE_TYPE INTEGER," +
                " SYNC INTEGER, " +
                " UUID TEXT " +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.d(TAG, "Upgrade DB");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    /**
     * Elimina los datos de la base de datos
     */
    protected void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }



    /**
     * Inserta los datos en la base de datos local
     * @param exercise
     * @return
     */
    protected long insertData(Exercise exercise){


        Log.d(TAG, "Insert Data SQL: " + exercise);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, exercise.getName());
        contentValues.put(COL_3, exercise.getDate());
        contentValues.put(COL_4,exercise.getImgUrl());
        contentValues.put(COL_5, exercise.getFeel());
        contentValues.put(COL_6, exercise.getExerciseType());
        contentValues.put(COL_7,exercise.isSync());
        contentValues.put(COL_8,exercise.getUuid());

        Long res = db.insert(TABLE_NAME,null, contentValues);
        Log.d(TAG, "Insert id: " + res);

        return res;
    }

    protected Cursor getAllData(){
        Log.d(TAG,"getAllData");
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY "+ COL_3 +" DESC", null);
        return res;
    }

    /**
     * Recupera los registros que no se han sincronizado en el web
     * @return
     */
    protected List<Exercise> getDataNotSync(){
        Log.d(TAG,"getDataNotSync");
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                " WHERE " + COL_7 + "= 0 " +
                " ORDER BY "+ COL_3 +" DESC", null);
        List<Exercise> res = parserCursor(cursor);
        return res;
    }



    /**
     * Recupera todos los registros de la base de datos
     * @return
     */
    protected List<Exercise> getExerciseData(){
        Cursor cursor = getAllData();
        List<Exercise> res = parserCursor(cursor);
        return res;
    }

    /**
     * Obtiene la lista de ejercicios obtenida de la consulta a la base de datos
     * @param cursor
     * @return
     */
    private List<Exercise> parserCursor(Cursor cursor){
        List<Exercise> res = new ArrayList<>();
        Exercise ex;
        while(cursor.moveToNext()){
            ex = new Exercise();
            ex.setDataType(Exercise.TYPE_EXERCISE);
            ex.setName(cursor.getString(1));
            ex.setDate(cursor.getLong(2));
            ex.setImgUrl(cursor.getString(3));
            ex.setFeel(cursor.getInt(4));
            ex.setExerciseType(cursor.getInt(5));
            ex.setSync(cursor.getInt(6) > 0);
            res.add(ex);
        }

        return res;
    }

    public long insertExercise(Exercise ex) {
        Log.d(TAG, "Insert Data SQL");

        //Verifica si ya se encuentra el objeto sincronizado
        if(getDataByUUID(ex.getUuid())){
            return 0;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, ex.getName());

        long date = System.currentTimeMillis();// - (1000l * 60l * 60l * 24l * 40l);

        contentValues.put(COL_3, ex.getDate());
        contentValues.put(COL_4,ex.getImgUrl());
        contentValues.put(COL_5, ex.getFeel());
        contentValues.put(COL_6, ex.getExerciseType());
        contentValues.put(COL_7,true);
        contentValues.put(COL_8, ex.getUuid());

        Long res = db.insert(TABLE_NAME,null, contentValues);
        Log.d(TAG, "Insert id: " + res);

        return res;
    }

    private boolean getDataByUUID(String uuid){
        Log.d(TAG,"getDataByUUID " + uuid);
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COL_8 + "= " +
                " '" + uuid + "' " +
                " ORDER BY "+
                COL_3 +
                " DESC";

        Cursor cursor = db.rawQuery(sql, null);
        return cursor.moveToNext();
    }
}
