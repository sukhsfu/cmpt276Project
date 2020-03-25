package ca.cmpt276.UI;
import ca.cmpt276.UI.MainActivity;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static ca.cmpt276.UI.MainActivity.Hazards;

class Mydata {
    public final long id;
    public final String text;

    public Mydata(long id, String text) {
        this.id = id;
        this.text = text;
    }
}


public class jadapter extends RecyclerView.Adapter<jadapter.vholder> {
    private List<Mydata> mydata;
    private OnNoteListener monNoteListener;


    public interface OnNoteListener{

        void onNoteClick(int position);
    }

    public jadapter(List<String> data,OnNoteListener monNoteListener){
        setHasStableIds(true);
        mydata = new ArrayList<>();
        this.monNoteListener=monNoteListener;
        for ( int i=0;i<data.size();i++){
            mydata.add(new Mydata(i,data.get(i)));

        }


    }

    @Override
    public vholder onCreateViewHolder( ViewGroup parent, int viewType) {
        LayoutInflater flate= LayoutInflater.from(parent.getContext());
        View view=flate.inflate(R.layout.restauranttab,parent,false);
        return new vholder(view,monNoteListener);
    }

    @Override
    public void onBindViewHolder( vholder holder, int position) {
        String title =mydata.get(position).text;
        holder.txxt.setText(title);
        if(title.contains("Lee Yuen Seafood Restaurant")) {
            holder.image.setImageResource(R.mipmap.leeyuan);
        }
        else if(title.contains("A&W")){
            holder.image.setImageResource(R.mipmap.aw);
        }
        else if(title.contains("Top In Town Pizza")||title.contains("Top in Town Pizza")){
            holder.image.setImageResource(R.mipmap.toppizza);
        }
        else if(title.contains("104 Sushi & Co")){
            holder.image.setImageResource(R.mipmap.sushi);
        }
        else if(title.contains("Zugba Flame Grilled Chicken")){
            holder.image.setImageResource(R.mipmap.zubra);
        }
        else if(title.contains("7Eleven")){
            holder.image.setImageResource(R.mipmap.seven);
        }
        else if(title.contains("Quizno's")){
            holder.image.setImageResource(R.mipmap.quizno);
        }
        else if(title.contains("Starbucks")){
            holder.image.setImageResource(R.mipmap.starbucks);
        }
        else if(title.contains("Papa John's")){
            holder.image.setImageResource(R.mipmap.papa);
        }
        else if(title.contains("Pizza Hut")){
            holder.image.setImageResource(R.mipmap.pizzahut);
        }
        else if(title.contains("Northview Golf & Country Club")){
            holder.image.setImageResource(R.mipmap.north);
        }

        else{
            holder.image.setImageResource(R.mipmap.restaurant);
        }



        holder.Hazardimage.setBackgroundColor((Hazards.get(position)).intValue());
        if(Color.GREEN == Hazards.get(position).intValue()){
            holder.face.setImageResource(R.drawable.smile);
        }
        else if (Color.RED == Hazards.get(position).intValue()){
            holder.face.setImageResource(R.drawable.sad);
        }
        else if(Color.YELLOW == Hazards.get(position).intValue()){
            holder.face.setImageResource(R.drawable.normal);
        }
    }

    @Override
    public int getItemCount() {
        return mydata.size();
    }

    public class vholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        ImageView face;
        TextView txxt;
        TextView Hazardimage;
        OnNoteListener onNoteLister;
        public vholder( View itemView,OnNoteListener  onNoteLister) {
            super(itemView);
            image = itemView.findViewById(R.id.restauranticon);
            Hazardimage=itemView.findViewById(R.id.hazard);
            txxt=itemView.findViewById(R.id.restauranttext);
            face = itemView.findViewById(R.id.imageView4);

            this.onNoteLister=onNoteLister;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteLister.onNoteClick(getAdapterPosition());

        }
    }
    @Override
    public long getItemId(int position) {
        return mydata.get(position).id;
    }

}
