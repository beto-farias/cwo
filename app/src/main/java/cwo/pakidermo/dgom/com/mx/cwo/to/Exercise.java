package cwo.pakidermo.dgom.com.mx.cwo.to;

/**
 * Created by beto on 29/01/18.
 */

public class Exercise extends ExerciseDataType{

    private String name;
    private long date;
    private String imgUrl;
    private int feel;
    private int ExerciseType;
    private boolean sync;
    private String uuid;

    public Exercise(){}

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Exercise(VideoContent vc, int feel){
        this.name = vc.getName();
        this.date = System.currentTimeMillis();
        this.imgUrl = vc.getVideo_thumnail();
        this.feel = feel;
        this.ExerciseType = vc.getType();
        this.setDataType(TYPE_EXERCISE);
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public int getExerciseType() {
        return ExerciseType;
    }

    public void setExerciseType(int exerciseType) {
        ExerciseType = exerciseType;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getFeel() {
        return feel;
    }

    public void setFeel(int feel) {
        this.feel = feel;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "name='" + name + '\'' +
                ", date=" + date +
                ", imgUrl='" + imgUrl + '\'' +
                ", feel=" + feel +
                ", ExerciseType=" + ExerciseType +
                '}';
    }
}
