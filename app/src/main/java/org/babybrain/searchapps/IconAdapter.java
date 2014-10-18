package org.babybrain.searchapps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IconAdapter extends BaseAdapter {
    private Context context;
    private Apps apps;
    private LayoutInflater inflater;
    private ImageView imageView;
    private TextView textView;
    private App app;

    public IconAdapter(Context c, Apps a) {
        context = c;
        apps = a;
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    }

    public int getCount() {
        return apps.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup parent) {
        if(view == null){
            view = inflater.inflate(R.layout.icon, null);
//            view = inflater.inflate(R.layout.icon, parent, false); // no labels for some reason
        }
        app = apps.get(position);
        imageView = (ImageView)view.findViewById(R.id.icon_image);
        textView = (TextView)view.findViewById(R.id.icon_text);
        imageView.setImageDrawable(app.icon);
        textView.setText(app.label);
        return view;
    }
}
