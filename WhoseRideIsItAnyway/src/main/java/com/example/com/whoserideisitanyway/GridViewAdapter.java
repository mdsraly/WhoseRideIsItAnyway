package com.example.com.whoserideisitanyway;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {

    //Imageloader to load images
    private ImageLoader imageLoader;

    //Context
    private Context context;

    //Array List that would contain the urls and the titles for the images
    private ArrayList<String> images;
    private ArrayList<String> ids;


    public GridViewAdapter (Context context, ArrayList<String> images,ArrayList<String> ids){
        //Getting all the values
        this.context = context;
        this.images = images;
        this.ids = ids;

    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Creating a linear layout
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        //NetworkImageView
        NetworkImageView networkImageView = new NetworkImageView(context);

        //Initializing ImageLoader
        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(images.get(position), ImageLoader.getImageListener(networkImageView, R.drawable.load1, android.R.drawable.ic_dialog_alert));

        //Setting the image url to load
        networkImageView.setImageUrl(images.get(position), imageLoader);
       // mark.setId(ids.get(position));
        TextView textView = new TextView(context);
        textView.setText(ids.get(position));

        //Creating a textview to show the title


        //Scaling the imageview
        networkImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int size = context.getResources().getDimensionPixelSize(R.dimen.gridview);
        networkImageView.setLayoutParams(new GridView.LayoutParams(size,size));

        //Adding views to the layout

        linearLayout.addView(networkImageView);

        //Returnint the layout
        return linearLayout;
    }
}