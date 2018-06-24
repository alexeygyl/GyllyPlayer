package com.example.smit.sadr.Adapters;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smit.sadr.AddNewFolder;
import com.example.smit.sadr.MPlayer;
import com.example.smit.sadr.MainActivity;
import com.example.smit.sadr.MusicUnits;
import com.example.smit.sadr.R;

import java.io.File;
import java.util.List;

public class ListNewFolderAdapter  extends ArrayAdapter<String> {
    private final Context context;
    private List<String> listOfFolders;

    public ListNewFolderAdapter(Context context, List<String> listOfFolders) {
        super(context, R.layout.list_new_folders,listOfFolders);
        this.context = context;
        this.listOfFolders =  listOfFolders;

    }

    public  void updateListOfFolders( List<String> listOfFolders){
        this.listOfFolders =  listOfFolders;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_new_folders, parent, false);
        TextView folderNPath = rowView.findViewById(R.id.NewFolderPath);
        TextView itemCount =  rowView.findViewById(R.id.NewFolderItemCount);
        ImageView folderIcon = rowView.findViewById(R.id.NewFolderLogo);
        CheckBox addFolder =  rowView.findViewById(R.id.checkFolder);
        if(listOfFolders.get(position).equalsIgnoreCase("..")) addFolder.setVisibility(View.INVISIBLE);

        File file = new File(AddNewFolder.currentDir+"/"+listOfFolders.get(position));
        if(file.isFile()){
            if(MPlayer.INSTANCE.ifMusicExists(file.getName())){
                addFolder.setChecked(true);
            }
        }
        else if(file.isDirectory()){
            if(MPlayer.INSTANCE.ifFolderExists(file.getPath())){
                addFolder.setChecked(true);
            }
            else if(MPlayer.INSTANCE.isInFolder(file.getPath())){
                addFolder.setChecked(true);
            }
        }



        addFolder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) AddNewFolder.addFolder(position);
                else AddNewFolder.delFolder(position);
            }
        });
        folderNPath.setText(listOfFolders.get(position));
        folderNPath.setTextColor(Color.BLACK);
        folderNPath.setMaxWidth(320);
        File f = new File(AddNewFolder.currentDir+"/"+listOfFolders.get(position));
        if(f.isFile()){
            if(getFileFormat(position).equals("mp3"))folderIcon.setImageResource(R.drawable.mp3_7317);
            else if (getFileFormat(position).equals("wav"))folderIcon.setImageResource(R.drawable.wav_9679);
        }else{
            try{
                Integer count = f.list().length;
                if(position != 0 && !AddNewFolder.currentDir.equals(AddNewFolder.root))
                    itemCount.setText("Items: "+ Long.toString(count) );
            }catch (NullPointerException e){
                if(position != 0 && !AddNewFolder.currentDir.equals(AddNewFolder.root))
                    itemCount.setText("Items: 0");
             }
            itemCount.setTextColor(Color.rgb(150, 150, 150));
        }

        return rowView;
    }

    public  String getFileFormat(int position){
        return  listOfFolders.get(position).substring(
                                            listOfFolders.get(position).lastIndexOf(".")+1,
                                            listOfFolders.get(position).length());
    }

}
