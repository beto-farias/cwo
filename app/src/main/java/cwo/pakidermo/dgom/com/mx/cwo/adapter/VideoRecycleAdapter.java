package cwo.pakidermo.dgom.com.mx.cwo.adapter;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import cwo.pakidermo.dgom.com.mx.cwo.R;
import cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes;
import cwo.pakidermo.dgom.com.mx.cwo.download.util.DownloadReceiver;
import cwo.pakidermo.dgom.com.mx.cwo.to.VideoContent;
import cwo.pakidermo.dgom.com.mx.cwo.utils.FileUtil;

/**
 * Created by beto on 02/02/18.
 */

public class VideoRecycleAdapter extends  RecyclerView.Adapter<VideoRecycleAdapter.MyHolder> {

    private static final String TAG = "VideoRecycleAdapter";


    private List<VideoContent> values = new ArrayList<>();
    private final OnItemClickListener listener;
    private Activity mActivity;
    private DownloadManager mDownloadManager;

        public VideoRecycleAdapter(Activity activity, List<VideoContent> data, OnItemClickListener listener){
            this.values = data;
            this.listener = listener;
            this.mActivity = activity;
        }

    @Override public void onBindViewHolder(MyHolder holder, int position) {
        holder.bind(values.get(position), listener);
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_video_list,parent,false);
        return new MyHolder(view,parent.getContext());
    }
/*
    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.setData(values.get(position));
    }
*/

    @Override
    public int getItemCount() {
        return values.size();
    }


    /**
     * Interface
     */
    public interface OnItemClickListener {
            void onItemClick(VideoContent item);
    }



    protected class MyHolder extends RecyclerView.ViewHolder{

        TextView txtTitle;
        TextView txtTime;
        ImageView imageView;
        ImageView icoLock;
        ImageView icoDownload;
        ImageView icoTrash;
        ProgressBar progressDownload;

        Context context;

        public MyHolder(View rowView, Context context) {
            super(rowView);
            this.context = context;
            txtTitle = (TextView) rowView.findViewById(R.id.txt_video_title);
            txtTime = (TextView) rowView.findViewById(R.id.txt_time);
            imageView = (ImageView) rowView.findViewById(R.id.img_thumnail);
            icoLock = (ImageView) rowView.findViewById(R.id.img_lock);
            icoDownload = (ImageView) rowView.findViewById(R.id.img_download);
            icoTrash = (ImageView) rowView.findViewById(R.id.img_trash);
            progressDownload = (ProgressBar) rowView.findViewById(R.id.progress_download);
        }

        public void setData(final VideoContent vc) {

            icoLock.setVisibility(View.GONE);
            icoDownload.setVisibility(View.GONE);
            icoTrash.setVisibility(View.GONE);
            progressDownload.setVisibility(View.GONE);

            txtTitle.setText(vc.getName());
            txtTime.setText(vc.getTime());
            Picasso.with(context)
                    .load(vc.getVideo_thumnail())
                    .into(imageView);

            Log.d(TAG,"Thumnail: " + vc.getVideo_thumnail());

            Log.d(TAG, "Payment type: " + vc.getPayment_type());


            //Si el video es privado y no ha pagado pone el candado
            if(vc.getPayment_type() == AppConstantes.ACCESS_PRIVATE && !AppConstantes.subscribed) {
                icoLock.setVisibility(View.VISIBLE);
            }


            //TODO------------------------------
            //Si ya pago el usuario puede descargar el video
            //if(AppConstantes.subscribed){
            icoLock.setVisibility(View.GONE);
            //Si ya se descargo el video quitar la opcion de descargar
            if(!FileUtil.videoExists(vc,context)) {
                icoDownload.setVisibility(View.VISIBLE);
                icoDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "Descargar video", Toast.LENGTH_SHORT).show();
                        if (DownloadReceiver.haveStoragePermission(mActivity)) {

                            downLoadVideo(vc, progressDownload);

                        } else {
                            Toast.makeText(context, "No tiene permisos para descargar", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                if (DownloadReceiver.isVideoDownloaded(mActivity, vc.getUiid())) {
                    icoDownload.setVisibility(View.GONE);
                }
                //}
            }
        }


        public void bind(final VideoContent item, final OnItemClickListener listener) {

            setData(item);
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override public void onClick(View v) {
                    if(listener != null) {
                        listener.onItemClick(item);
                    }
                }

            });

        }

    }


    private void downLoadVideo(VideoContent vc, ProgressBar pb)  {
        new DownloadVideoAsync(pb).execute(vc);

    }




    private class DownloadVideoAsync extends AsyncTask<VideoContent, String, String> {

        ProgressBar mProgressBar;

        public DownloadVideoAsync(ProgressBar pb){
            this.mProgressBar = pb;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("Starting download");
        }

        @Override
        protected String doInBackground(VideoContent... vcs) {
            Log.d(TAG,"downLoadVideo");

            VideoContent vc = vcs[0];
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;

            VideoRecycleAdapter.this.mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            });

            String destinationFilename = FileUtil.getVideoDirPath(vc, VideoRecycleAdapter.this.mActivity);

            File dir = new File(destinationFilename);
            //Si no existe el directorio lo crea
            if(!dir.exists()){
                dir.mkdirs();
            }

            File[] files = dir.listFiles();
            Log.d("Files", "Size: "+ files.length);
            if(files!= null) {
                for (int i = 0; i < files.length; i++) {
                    Log.d("TAG", "FileName:" + files[i].getName());
                }
            }

            destinationFilename = FileUtil.getVideoPath(vc, VideoRecycleAdapter.this.mActivity);

            try {
            URL videoUri = new URL(vc.getVideo_url());

            //String sourceFilename= vc.getVideo_url();
        /* Open a connection to that URL. */
            URLConnection ucon = videoUri.openConnection();

                bis = new BufferedInputStream(ucon.getInputStream());
                long countBytesReaded = 0;
                bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
                byte[] buf = new byte[1024];
                long count;
                long total = ucon.getContentLength();

                mProgressBar.setMax(100);
                int avance = 0;

                while ((count = bis.read(buf)) != -1) {
                    //Escribe el bufffer en el dispositivo
                    bos.write(buf);

                    //manejo del ui------------------
                    countBytesReaded += count;
                    avance = (int) (countBytesReaded * 100 / total);
                    final int finalAvance = avance;
                    VideoRecycleAdapter.this.mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setProgress(finalAvance);
                        }
                    });

                    Log.d(TAG, "Descargando " + ((countBytesReaded/1024)/1024) + " m de " + ((total/1024)/1024) + " m " + avance + "%");

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bis != null) bis.close();
                    if (bos != null) bos.flush();
                    if (bos != null) bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            VideoRecycleAdapter.this.mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.GONE);
                }
            });


            return destinationFilename;
        }

        @Override
        protected void onPostExecute(String file_url) {
            System.out.println("Downloaded");
        }
    }
}
