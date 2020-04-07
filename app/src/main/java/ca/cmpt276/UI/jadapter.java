package ca.cmpt276.UI;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static ca.cmpt276.UI.MainActivity.Hazards;
import static ca.cmpt276.UI.MainActivity.favourite;
import static ca.cmpt276.UI.MainActivity.numcritical;
class Mydata {
    public final int id;
    public final String text;

    public Mydata(int id, String text) {
        this.id = id;
        this.text = text;
    }
}


public class jadapter extends RecyclerView.Adapter<jadapter.vholder>implements Filterable {
    private List<Mydata> mydata ;
    private List<Mydata> mydatafiltered ;
    private OnNoteListener monNoteListener;


    public interface OnNoteListener {

        void onNoteClick(int position);
    }

    public jadapter(List<String> data, OnNoteListener monNoteListener) {
        setHasStableIds(true);
        mydata = new ArrayList<>();
        mydatafiltered=new ArrayList<>();
        this.monNoteListener = monNoteListener;
        for (int i = 0; i < data.size(); i++) {
            mydata.add(new Mydata(i, data.get(i)));

        }



    }

    @Override
    public vholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater flate = LayoutInflater.from(parent.getContext());
        View view = flate.inflate(R.layout.restauranttab, parent, false);
        return new vholder(view, monNoteListener);
    }

    @Override
    public void onBindViewHolder(vholder holder, int position) {
        String title = mydatafiltered.get(position).text;
        int positionid = mydatafiltered.get(position).id;
        holder.txxt.setText(title);
        if (title.contains("Lee Yuen Seafood Restaurant")) {
            holder.image.setImageResource(R.mipmap.leeyuan);
        } else if (title.contains("A&W")) {
            holder.image.setImageResource(R.mipmap.aw);
        } else if (title.contains("Top In Town Pizza") || title.contains("Top in Town Pizza")) {
            holder.image.setImageResource(R.mipmap.toppizza);
        } else if (title.contains("104 Sushi & Co")) {
            holder.image.setImageResource(R.mipmap.sushi);
        } else if (title.contains("Zugba Flame Grilled Chicken")) {
            holder.image.setImageResource(R.mipmap.zubra);
        } else if (title.contains("7Eleven")) {
            holder.image.setImageResource(R.mipmap.seven);
        } else if (title.contains("Quizno's")) {
            holder.image.setImageResource(R.mipmap.quizno);
        } else if (title.contains("Starbucks")) {
            holder.image.setImageResource(R.mipmap.starbucks);
        } else if (title.contains("Papa John's")) {
            holder.image.setImageResource(R.mipmap.papa);
        } else if (title.contains("Pizza Hut")) {
            holder.image.setImageResource(R.mipmap.pizzahut);
        } else if (title.contains("Northview Golf & Country Club")) {
            holder.image.setImageResource(R.mipmap.north);
        } else {
            holder.image.setImageResource(R.mipmap.restaurant);
        }


        holder.Hazardimage.setBackgroundColor((Hazards.get(positionid)).intValue());
        if (Color.GREEN == Hazards.get(positionid).intValue()) {
            holder.face.setImageResource(R.drawable.smile);
        } else if (Color.RED == Hazards.get(positionid).intValue()) {
            holder.face.setImageResource(R.drawable.sad);
        } else if (Color.YELLOW == Hazards.get(positionid).intValue()) {
            holder.face.setImageResource(R.drawable.normal);
        }
        boolean find= favourite.get(positionid);
        holder.checkBox.setChecked(find);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            SharedPreferences sharedPreferences= v.getContext().getSharedPreferences("favourites", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.remove("favourite_"+positionid);
                if(holder.checkBox.isChecked()){
                    editor.putBoolean("favourite_"+positionid,true);
                }
                else{
                    editor.putBoolean("favourite_"+positionid,false);
                }
                editor.apply();
            }
        });



    }

    @Override
    public int getItemCount() {
        if (mydatafiltered==null)
            return 0;
        return mydatafiltered.size();
    }

    public class vholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        ImageView face;
        TextView txxt;
        TextView Hazardimage;
        OnNoteListener onNoteLister;
        CheckBox checkBox;

        public vholder(View itemView, OnNoteListener onNoteLister) {
            super(itemView);
            image = itemView.findViewById(R.id.restauranticon);
            Hazardimage = itemView.findViewById(R.id.hazard);
            txxt = itemView.findViewById(R.id.restauranttext);
            face = itemView.findViewById(R.id.imageView4);
            checkBox = itemView.findViewById(R.id.checkBox);

            this.onNoteLister = onNoteLister;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteLister.onNoteClick(getAdapterPosition());

        }
    }

    @Override
    public long getItemId(int position) {
        return mydatafiltered.get(position).id;
    }

    //took reference from the article-https://www.androidhive.info/2017/11/android-recyclerview-with-search-filter-functionality/
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString1 = charSequence.toString();
                if (charString1.isEmpty()) {
                    mydatafiltered = mydata;
                } else {
                    String[] charStringarr;
                    if (charString1.contains("combined")) {
                        charString1.replace("combined", "");
                        charStringarr = charString1.split(",");
                        charStringarr[0]=charStringarr[0]+"favs";
                        charStringarr[2]=charStringarr[2]+"clr";
                        charStringarr[3]=charStringarr[3]+"xxxaaaxxx";
                    } else {
                        charStringarr = new String[1];
                        charStringarr[0] = charString1;
                    }

                    List<Mydata> filteredList = new ArrayList<>();
                    for(String charString:charStringarr) {
                        if (charString.contains("xxxaaaxxx")) {
                            String check = charString.replace("xxxaaaxxx", "");
                            String checkdigits = check.replaceAll("\\D+", "");
                            if (check.toLowerCase().contains("less") || check.contains("<")) {
                                for (Mydata row : mydata) {
                                    if (numcritical.get(row.id) <= Integer.parseInt(checkdigits)) {
                                        filteredList.add(row);

                                    }
                                }
                            } else if (check.toLowerCase().contains("greater") || check.contains(">") || check.toLowerCase().contains("more")) {
                                for (Mydata row : mydata) {
                                    if (numcritical.get(row.id) >= Integer.parseInt(checkdigits)) {
                                        filteredList.add(row);

                                    }
                                }
                            }
                        } else if (charString.equalsIgnoreCase("lowclr")) {
                            for (Mydata row : mydata) {
                                if (Hazards.get(row.id) == Color.GREEN) {
                                    filteredList.add(row);

                                }
                            }

                        } else if (charString.equalsIgnoreCase("moderateclr")) {
                            for (Mydata row : mydata) {
                                if (Hazards.get(row.id) == Color.YELLOW) {
                                    filteredList.add(row);

                                }
                            }

                        } else if (charString.equalsIgnoreCase("highclr")) {
                            for (Mydata row : mydata) {
                                if (Hazards.get(row.id) == Color.RED) {
                                    filteredList.add(row);

                                }
                            }

                        }else if(charString.contains("favs")){
                            String check=charString.replace("favs","");
                            for (Mydata row : mydata) {
                                if (row.text.toLowerCase().contains(check.toLowerCase())&&favourite.get(row.id)) {
                                    filteredList.add(row);

                                }
                            }
                        }
                        else {
                            for (Mydata row : mydata) {
                                if (row.text.toLowerCase().contains(charString.toLowerCase())) {
                                    filteredList.add(row);

                                }
                            }
                        }
                    }
                    mydatafiltered = filteredList;

                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mydatafiltered;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mydatafiltered = (ArrayList<Mydata>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
