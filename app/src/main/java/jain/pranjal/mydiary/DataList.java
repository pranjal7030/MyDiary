package jain.pranjal.mydiary;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hp on 11/8/2019.
 */


public class DataList extends ArrayAdapter<Data> {

    private Activity context;
    private List<Data> dataList;

    public DataList(Activity context, List<Data> dataList)
    {
        super(context, R.layout.datalist_layout, dataList);
        this.context=context;
        this.dataList=dataList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.datalist_layout,null,true);

        TextView textViewYear=(TextView) listViewItem.findViewById(R.id.textViewYear);

        Data data=dataList.get(position);

        textViewYear.setText(data.getDate());

        return listViewItem;
    }
}
