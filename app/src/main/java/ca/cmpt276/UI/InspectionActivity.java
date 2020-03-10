package ca.cmpt276.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ca.cmpt276.model.Inspection;
import ca.cmpt276.model.Restaurant;
import ca.cmpt276.model.RestaurantManager;
import ca.cmpt276.model.Violation;

/**
 * The InspectionActivity is launched from the RestaurantActivity when an inspection is clicked.
 * It displays details of the clicked inspection and a list of the violations from that inspection.
 */

public class InspectionActivity extends AppCompatActivity {
    public static final String INSPECTION_ACTIVITY_RES_POS = "InspectionActivity_resPos";
    public static final String INSPECTION_ACTIVITY_INS_POS = "InspectionActivity_insPos";

    RestaurantManager manager = RestaurantManager.getInstance();
    Restaurant restaurant;
    Inspection inspection;
    List<Violation> violations;

    public static Intent makeLaunchIntent(Context context, int resPosition, int insPosition) {
        Intent intent = new Intent(context, InspectionActivity.class);
        intent.putExtra(INSPECTION_ACTIVITY_RES_POS, resPosition);
        intent.putExtra(INSPECTION_ACTIVITY_INS_POS, insPosition);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);

        extractDataFromIntent();
        setScreen();
        populateViolationsListView();
        registerClickCallbackListView();
    }

    public void extractDataFromIntent() {
        Intent intent = getIntent();
        int resPosition = intent.getIntExtra(INSPECTION_ACTIVITY_RES_POS, 0);
        int insPosition = intent.getIntExtra(INSPECTION_ACTIVITY_INS_POS, 0);

        restaurant = manager.retrieve(resPosition);
        inspection = restaurant.getInspections().get(insPosition);
        violations = inspection.getViolations();
    }

    public void setScreen(){
        TextView date= findViewById(R.id.item_inspectionDate);
        int insDate;
        String FormattedDate;
        insDate=Integer.parseInt(inspection.getDate());
        String[] mon={"Jan","Feb","March","April","May","June","July","August","Sept","Oct","Nov","Dec"};
        FormattedDate= mon[((insDate%10000)/100)-1]+" "+insDate%100+", "+insDate/10000;
        date.setText(FormattedDate);

        TextView InspectionType=findViewById(R.id.InspectionType);
        String formatInspectionType=inspection.getType().substring(1,inspection.getType().length()-1);
        InspectionType.setText(formatInspectionType);

        String formatHazardLevel=inspection.getHazardLevel().substring(1,inspection.getHazardLevel().length()-1);
        TextView InspectionHazardLevel=findViewById(R.id.InspectionHazardLevel);
        InspectionHazardLevel.setText(formatHazardLevel);

        TextView inspection_CriticalIssues=findViewById(R.id.inspection_CriticalIssues);
        TextView inspection_NonCriticalIssues=findViewById(R.id.inspection_NonCriticalIssues);
        inspection_CriticalIssues.setText(String.valueOf(inspection.getNumCriticalIssues()));
        inspection_NonCriticalIssues.setText(String.valueOf(inspection.getNumNonCriticalIssues()));
    }

    private void populateViolationsListView() {
        ArrayAdapter<Violation> adapter = new myListAdapter();
        ListView list = findViewById(R.id.violationsListView);
        list.setAdapter(adapter);
    }

    private void registerClickCallbackListView() {
        ListView list = findViewById(R.id.violationsListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Violation clickedViolation = violations.get(position);

                String message = clickedViolation.getDetailedDescription();
                Toast.makeText(InspectionActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private class myListAdapter extends ArrayAdapter<Violation> {
        public myListAdapter() {
            super(InspectionActivity.this, R.layout.violations_item_view, violations);
        }

        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.violations_item_view, parent, false);
            }

            Violation currViolation = violations.get(position);

            String briefDescription = currViolation.getBriefDescription();
            ImageView natureIcon = itemView.findViewById(R.id.nature_icon);
            if(briefDescription.toLowerCase().contains("food")){
                natureIcon.setImageResource(R.drawable.food_icon);
            }
            else if(briefDescription.toLowerCase().contains("equipment")){
                natureIcon.setImageResource(R.drawable.equipment_icon);
            }
            else if(briefDescription.toLowerCase().contains("pest")){
                natureIcon.setImageResource(R.drawable.pest_icon);
            }
            else{
                natureIcon.setImageResource(R.drawable.violation_icon);
            }

            String severity = currViolation.getSeverity();
            ImageView severityIcon = itemView.findViewById(R.id.severity_icon);
            if(severity.trim().equalsIgnoreCase("critical")){
                severityIcon.setImageResource(R.drawable.critical_icon);
            }
            else if(severity.toLowerCase().contains("not")){
                severityIcon.setImageResource(R.drawable.non_critical_icon);
            }

            TextView briefDesc = itemView.findViewById(R.id.item_BriefDescription);
            briefDesc.setText(briefDescription);

            itemView.setBackgroundColor(Color.rgb(231,231,231));
            itemView.setPadding(50,50,50,50);

            return itemView;
        }
    }
}
