package cwo.pakidermo.dgom.com.mx.cwo.adapter;






import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cwo.pakidermo.dgom.com.mx.cwo.R;
import cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes;
import cwo.pakidermo.dgom.com.mx.cwo.download.util.DownloadReceiver;
import cwo.pakidermo.dgom.com.mx.cwo.to.VideoContent;

/**
 * Created by beto on 07/01/18.
 */

public class VideoListAdapter extends ArrayAdapter<VideoContent> {

    private static final String TAG = "VideoListAdapter";
    private Activity context;
    private List<VideoContent> data  = new ArrayList<VideoContent>();

    private DownloadManager downloadManager;


    public VideoListAdapter(@NonNull Activity context, int resource,List<VideoContent> items) {
        super(context, resource);
        this.context = context;
        this.data = items;
    }



    public void addItem(VideoContent item){
        data.add(item);
        this.notifyDataSetChanged();
    }

    public void setItems(List<VideoContent> data){
        this.data = data;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount(){
        return data.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_video_list, parent, false);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt_video_title);
        TextView txtTime = (TextView) rowView.findViewById(R.id.txt_time);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img_thumnail);
        ImageView icoLock = (ImageView) rowView.findViewById(R.id.img_lock);
        ImageView icoDownload = (ImageView) rowView.findViewById(R.id.img_download);
        ImageView icoTrash = (ImageView) rowView.findViewById(R.id.img_trash);

        icoLock.setVisibility(View.GONE);
        icoDownload.setVisibility(View.GONE);
        icoTrash.setVisibility(View.GONE);

        final VideoContent vc = data.get(position);



        txtTitle.setText(vc.getName());
        txtTime.setText(vc.getTime());
        Picasso.with(context)
                .load(vc.getVideo_thumnail())
               // .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView);

        Log.d(TAG,"Thumnail: " + vc.getVideo_thumnail());

        Log.d(TAG, "Payment type: " + vc.getPayment_type());


        //Si el video es privado y no ha pagado pone el candado
        if(vc.getPayment_type() == AppConstantes.ACCESS_PRIVATE && !AppConstantes.subscribed){
            icoLock.setVisibility(View.VISIBLE);
        }



        /*
         //TODO Desactivada para esta version
        //Si ya pago el usuario puede descargar el video
        if(AppConstantes.subscribed){
            icoDownload.setVisibility(View.VISIBLE);
            icoDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(),"Descargar video",Toast.LENGTH_SHORT).show();
                    if(DownloadReceiver.haveStoragePermission(context)) {
                        downLoadVideo(vc);
                    }else{
                        Toast.makeText(context,"No tiene permisos para descargar",Toast.LENGTH_LONG).show();
                    }
                }
            });

            if(DownloadReceiver.isVideoDownloaded(context,vc.getUiid())){
                icoDownload.setVisibility(View.GONE);
            }
        }
        */

        return rowView;
    }

    //Handler handlerUpdate;
    //Runnable runnableUpdate;

    private void downLoadVideo(VideoContent vc){
        Log.d(TAG, "Solicitud de descarga de video: " + vc.getName());

        //Si ya está descargando un video
        if(DownloadReceiver.getDownloadID(context) != -1){
            Toast.makeText(context,R.string.download_on_downloading, Toast.LENGTH_SHORT).show();
            Log.d(TAG, context.getString(R.string.download_on_downloading));
            return;
        }

        if(DownloadReceiver.isVideoDownloaded(context, vc.getUiid())){
            Toast.makeText(context,R.string.download_allready_downloaded, Toast.LENGTH_SHORT).show();
            Log.d(TAG, context.getString(R.string.download_allready_downloaded));
            return;
        }

        //https://www.youtube.com/watch?v=uCARv_U2AlI
        Log.d(TAG,"Url del video a descargar: " + vc.getVideo_url());
        Uri url = Uri.parse(vc.getVideo_url());
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(url);

        String title =  String.format(context.getString(R.string.download_title), context.getString(R.string.app_name));
        String body = String.format(context.getString(R.string.download_body) , vc.getName());

        request.setTitle( title );
        request.setDescription( body );

        //Guarda el archivo en el directorio indicado
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, vc.getUiid() + ".zip" );

        //Obtiene el id de la descarga
        long id = downloadManager.enqueue( request );
        DownloadReceiver.setDownloadID(context, id);

        //Obtiene el Json del video
        Gson gson = new Gson();
        String json = gson.toJson(vc);
        DownloadReceiver.setDownloadJSON(context,json);



        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(DownloadReceiver.getInstance(), filter);
    }

}
