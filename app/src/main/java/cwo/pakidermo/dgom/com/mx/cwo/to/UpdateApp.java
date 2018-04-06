package cwo.pakidermo.dgom.com.mx.cwo.to;

/**
 * Created by beto on 14/01/18.
 */

public class UpdateApp {

    private String app;
    private String os;
    private String version;
    private String url;


    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public String toString() {
        return "UpdateApp{" +
                "app='" + app + '\'' +
                ", os='" + os + '\'' +
                ", version='" + version + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
