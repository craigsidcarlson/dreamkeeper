package com.craig.dreamkeeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.craig.dreamkeeper.model.DreamContent;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    public static final int NUM_DREAMS_PER_LOG = 1;
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private LinkedHashMap<UUID, DreamContent.Dream> _listDataChild;

    public ExpandableListAdapter(Context context, LinkedHashMap<UUID, DreamContent.Dream> listChildData) {
        this._context = context;
        this._listDataChild = listChildData;
        this._listDataHeader = getHeaders();
    }

    public void syncDreams(LinkedHashMap<UUID, DreamContent.Dream> dreams, boolean draft){
        this._listDataChild = dreams;
        this._listDataHeader = getHeaders();

        notifyDataSetChanged();
    }

    public List<String> getHeaders(){
        List<String> headers = new ArrayList<String>();

        for(UUID id :  this._listDataChild.keySet()){
            DreamContent.Dream d =  this._listDataChild.get(id);
            headers.add(d.getDate());
        }
   /*     if(draft){
            headers.get(headers.size() - 1).append(" (draft)");
        }*/

        return headers;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        List<DreamContent.Dream> l = new ArrayList<DreamContent.Dream>(this._listDataChild.values());
        return l.get(groupPosition).getDreamContent();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return NUM_DREAMS_PER_LOG;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
