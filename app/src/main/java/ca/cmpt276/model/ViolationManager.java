package ca.cmpt276.model;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ca.cmpt276.UI.R;


/**
 * Manages brief descriptions list for violations
 */

public class ViolationManager extends AppCompatActivity implements Iterable<ViolationsBriefDescription> {

    private List<ViolationsBriefDescription> descriptions = new ArrayList<>();

    public void addDescription(ViolationsBriefDescription description){
        descriptions.add(description);
    }

    public ViolationManager() {
    }

    public void populateBriefDescriptions(Context context) {
        //Log.d("ViolationManager", "populating now.");
        populate(context);
    }

    // Returns a brief description of a violation by providing the type code
    public String retrieve(int type){
        String briefDescription = "";
        for(ViolationsBriefDescription description: descriptions){
            if(description.getType() == type){
                briefDescription = description.getDescription();
                //Log.d("ViolationManager_retrieve" , "brief description: " + briefDescription);
                return briefDescription;
            }
        }
        return briefDescription;
    }

    @Override
    public Iterator<ViolationsBriefDescription> iterator(){
        return descriptions.iterator();
    }

    private void populate(Context context){
        //Log.d( "in POPULATE: ",getString(R.string.briefViol_plans));
        addDescription(new ViolationsBriefDescription(101, context.getResources().getString(R.string.briefViol_plans)));
        addDescription(new ViolationsBriefDescription(102,  context.getResources().getString(R.string.briefViol_foodPremises) ));
        addDescription(new ViolationsBriefDescription(103, context.getResources().getString(R.string.briefViol_invalidPermit)));
        addDescription(new ViolationsBriefDescription(104, context.getResources().getString(R.string.briefViol_unpostedPermit)));
        addDescription(new ViolationsBriefDescription(201, context.getResources().getString(R.string.briefViol_contaminated)));
        addDescription(new ViolationsBriefDescription(202, context.getResources().getString(R.string.briefViol_unsafeProcess)));
        addDescription(new ViolationsBriefDescription(203, context.getResources().getString(R.string.briefViol_cooling)));
        addDescription(new ViolationsBriefDescription(204, context.getResources().getString(R.string.briefViol_heating)));
        addDescription(new ViolationsBriefDescription(205, context.getResources().getString(R.string.briefViol_coldTemp)));
        addDescription(new ViolationsBriefDescription(206, context.getResources().getString(R.string.briefViol_hotTemp)));
        addDescription(new ViolationsBriefDescription(208, context.getResources().getString(R.string.briefViol_unapprovedSource)));
        addDescription(new ViolationsBriefDescription(209, context.getResources().getString(R.string.briefViol_foodContamination)));
        addDescription(new ViolationsBriefDescription(210, context.getResources().getString(R.string.briefViol_thawing)));
        addDescription(new ViolationsBriefDescription(211, context.getResources().getString(R.string.briefViol_frozenTemp)));
        addDescription(new ViolationsBriefDescription(212, context.getResources().getString(R.string.briefViol_writtenProc)));
        addDescription(new ViolationsBriefDescription(301, context.getResources().getString(R.string.briefViol_equipUnsanitized)));
        addDescription(new ViolationsBriefDescription(302, context.getResources().getString(R.string.briefViol_equipWashing)));
        addDescription(new ViolationsBriefDescription(303, context.getResources().getString(R.string.briefViol_water)));
        addDescription(new ViolationsBriefDescription(304, context.getResources().getString(R.string.briefViol_pestsFree)));
        addDescription(new ViolationsBriefDescription(305, context.getResources().getString(R.string.briefViol_pestsEntrance)));
        addDescription(new ViolationsBriefDescription(306, context.getResources().getString(R.string.briefViol_sanitizationPremises)));
        addDescription(new ViolationsBriefDescription(307, context.getResources().getString(R.string.briefViol_equipMaterial)));
        addDescription(new ViolationsBriefDescription(308, context.getResources().getString(R.string.briefViol_equipState)));
        addDescription(new ViolationsBriefDescription(309, context.getResources().getString(R.string.briefViol_cleansers)));
        addDescription(new ViolationsBriefDescription(310, context.getResources().getString(R.string.briefViol_singleUse)));
        addDescription(new ViolationsBriefDescription(311, context.getResources().getString(R.string.briefViol_premises)));
        addDescription(new ViolationsBriefDescription(312, context.getResources().getString(R.string.briefViol_unnecessaryItems)));
        addDescription(new ViolationsBriefDescription(313, context.getResources().getString(R.string.briefViol_animals)));
        addDescription(new ViolationsBriefDescription(314, context.getResources().getString(R.string.briefViol_sanitationProc)));
        addDescription(new ViolationsBriefDescription(315, context.getResources().getString(R.string.briefViol_thermometers)));
        addDescription(new ViolationsBriefDescription(401, context.getResources().getString(R.string.briefViol_handwashingStations)));
        addDescription(new ViolationsBriefDescription(402, context.getResources().getString(R.string.briefViol_washHands)));
        addDescription(new ViolationsBriefDescription(403, context.getResources().getString(R.string.briefViol_personalHygiene)));
        addDescription(new ViolationsBriefDescription(404, context.getResources().getString(R.string.briefViol_smoking)));
        addDescription(new ViolationsBriefDescription(501, context.getResources().getString(R.string.briefViol_level1)));
        addDescription(new ViolationsBriefDescription(502, context.getResources().getString(R.string.briefViol_FOODSAFE)));
    }



}
