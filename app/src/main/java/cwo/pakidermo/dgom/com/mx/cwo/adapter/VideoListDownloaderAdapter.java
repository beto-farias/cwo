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
import java.util.List;

import cwo.pakidermo.dgom.com.mx.cwo.R;
import cwo.pakidermo.dgom.com.mx.cwo.download.util.DownloadReceiver;
import cwo.pakidermo.dgom.com.mx.cwo.to.VideoContent;
import cwo.pakidermo.dgom.com.mx.cwo.to.VideoContentDownloaded;
import cwo.pakidermo.dgom.com.mx.cwo.utils.FileUtil;

/**
 * Created by beto on 07/01/18.
 */

public class VideoListDownloaderAdapter extends ArrayAdapter<VideoContentDownloaded> {

    private static final String TAG = "VLDownloaderAdapter";
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private Activity context;
    private List<VideoContent> data  = new ArrayList<VideoContent>();

    private DownloadManager downloadManager;

    public enum RowType {
        TYPE_ITEM, TYPE_SEPARATOR
    }



    public VideoListDownloaderAdapter(@NonNull Activity context, int resource, List<VideoContent> items) {
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
    public int getViewTypeCount() {
        return RowType.values().length;

    }

    @Override
    public int getItemViewType(int position) {
        if( data.get(position) instanceof VideoContentDownloaded ){
            return TYPE_SEPARATOR;
        }else{
            return TYPE_ITEM;
        }
    }

    @Override
    public int getCount(){
        return data.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView;

        //Renglon
        if(getItemViewType(position) == TYPE_ITEM) {
            rowView = getRowView(position,parent);
        }else{
            //Separador
            rowView = getRowSeparatorView(position,parent);
        }

        return rowView;
    }

    private View getRowSeparatorView(int position, ViewGroup parent) {

        VideoContentDownloaded item = (VideoContentDownloaded) data.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_video_separator, parent, false);

        ImageView imgThumnailSeparator = (ImageView) rowView.findViewById(R.id.img_thumnail_separator);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt_separator_title);

        txtTitle.setText(item.getsTitle());


        return rowView;
    }

    /**
     * Calcula el renglon del video
     * @param position
     * @param parent
     * @return
     */
    private View getRowView(final int position, ViewGroup parent){



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

        final VideoContent vc = data.get(position);


        txtTitle.setText(vc.getName());
        txtTime.setText(vc.getTime());
        Picasso.with(context)
                .load(vc.getVideo_thumnail())
                //.networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView);

        Log.d(TAG,"Thumnail: " + vc.getVideo_thumnail());

        icoTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(context)
                        .setMessage(R.string.are_you_shure_you_whant_to_delete_video)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteVideo( vc,  position);
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();



            }
        });

        return rowView;
    }


    private void deleteVideo(VideoContent vc, int position){
        DownloadReceiver.removeVideoDownloadedJson(context, vc);
        data.remove(position);
        FileUtil.deleteVideoFile(vc,context);
        notifyDataSetChanged();
    }


}
