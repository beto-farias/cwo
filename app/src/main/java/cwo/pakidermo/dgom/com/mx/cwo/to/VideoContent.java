package cwo.pakidermo.dgom.com.mx.cwo.to;

import java.io.Serializable;

/**
 * Created by beto on 07/01/18.
 */

public class VideoContent implements Serializable {

    public String name;
    private String time;
    private String video_url;
    private String video_thumnail;
    private String poster;
    private int featured;
    private String uiid;
    private String celebrity;
    private String trainer;
    private int type;
    private int payment_type;
    private long preview_time;
    private int featured_day;

    public int getFeatured_day() {
        return featured_day;
    }

    public void setFeatured_day(int featured_day) {
        this.featured_day = featured_day;
    }

    public long getPreview_time() {
        return preview_time;
    }

    public void setPreview_time(long preview_time) {
        this.preview_time = preview_time;
    }

    public int getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(int payment_type) {
        this.payment_type = payment_type;
    }

    public String getPoster() {
        return poster.replaceAll(" ", "%20");
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCelebrity() {
        return celebrity;
    }

    public void setCelebrity(String celebrity) {
        this.celebrity = celebrity;
    }

    public String getTrainer() {
        return trainer;
    }

    public void setTrainer(String trainer) {
        this.trainer = trainer;
    }

    public String getUiid() {
        return uiid;
    }

    public void setUiid(String uiid) {
        this.uiid = uiid;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getVideo_thumnail() {
        return video_thumnail.replaceAll(" ", "%20");
    }

    public void setVideo_thumnail(String video_thumnail) {
        this.video_thumnail = video_thumnail;
    }

    public int getFeatured() {
        return featured;
    }

    public void setFeatured(int featured) {
        this.featured = featured;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
