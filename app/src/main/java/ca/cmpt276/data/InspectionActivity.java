package ca.cmpt276.data;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import ca.cmpt276.model.Inspection;
import ca.cmpt276.model.Restaurant;
import ca.cmpt276.model.RestaurantManager;

public class InspectionActivity extends AppCompatActivity {
    RestaurantManager manager = RestaurantManager.getInstance();
    Restaurant restaurant;
    Inspection inspection;

    public static Intent makeLaunchIntent(Context context, int resPosition, int insPosition) {
        Intent intent = new Intent(context, InspectionActivity.class);
        intent.putExtra("InspectionActivity_resPos", resPosition);
        intent.putExtra("InspectionActivity_insPos", insPosition);

        return intent;
    }

    public void extractDataFromIntent() {
        Intent intent = getIntent();
        int resPosition = intent.getIntExtra("InspectionActivity_resPos", 0);
        int insPosition = intent.getIntExtra("InspectionActivity_insPos", 0);

        restaurant = manager.retrieve(resPosition);
        inspection = restaurant.getInspections().get(insPosition);
    }
    public void setscreen(){
        TextView date= findViewById(R.id.item_inspectionDate);
        int Idate;
        String FormattedDate;
        Idate=Integer.parseInt(inspection.getDate());
        String[] mon={"Jan","Feb","March","April","May","June","July","August","Sept","Oct","Nov","Dec"};
        FormattedDate= mon[((Idate%10000)/100)-1]+" "+Idate%100+", "+Idate/10000;
        date.setText(FormattedDate);
        TextView InspectionType=findViewById(R.id.InspectionType);
        String formatInspectionType=inspection.getType().substring(1,inspection.getType().length()-1);
        InspectionType.setText(formatInspectionType);
        String formatHazardLevel=inspection.getHazardLevel().substring(1,inspection.getHazardLevel().length()-1);
        TextView InspectionHazardLevel=findViewById(R.id.InspectionHazardLevel);
        InspectionHazardLevel.setText(formatHazardLevel);
        TextView Inspectioncritical_issues=findViewById(R.id.Inspectioncritical_issues);
        TextView Inspectionnoncritical_issues=findViewById(R.id.Inspectionnoncritical_issues);
        Inspectioncritical_issues.setText(String.valueOf(inspection.getNumCriticalIssues()));
        Inspectionnoncritical_issues.setText(String.valueOf(inspection.getNumNonCriticalIssues()));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);

        extractDataFromIntent();
        setscreen();
    }
}
