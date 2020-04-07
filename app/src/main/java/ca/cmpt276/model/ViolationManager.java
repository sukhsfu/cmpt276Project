package ca.cmpt276.model;

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

    public void populateBriefDescriptions() {
        populate();
    }

    // Returns a brief description of a violation by providing the type code
    public String retrieve(int type){
        String briefDescription = "";
        for(ViolationsBriefDescription description: descriptions){
            if(description.getType() == type){
                briefDescription = description.getDescription();
                return briefDescription;
            }
        }
        return briefDescription;
    }

    @Override
    public Iterator<ViolationsBriefDescription> iterator(){
        return descriptions.iterator();
    }

    private void populate(){
        //Log.d( "in POPULATE: ",getString(R.string.briefViol_plans));
        addDescription(new ViolationsBriefDescription(101, getString(R.string.briefViol_plans) ));
        addDescription(new ViolationsBriefDescription(102,  getString(R.string.briefViol_foodPremises) ));
        addDescription(new ViolationsBriefDescription(103, getString(R.string.briefViol_invalidPermit)));
        addDescription(new ViolationsBriefDescription(104, getString(R.string.briefViol_unpostedPermit)));
        addDescription(new ViolationsBriefDescription(201, getString(R.string.briefViol_contaminated)));
        addDescription(new ViolationsBriefDescription(202, getString(R.string.briefViol_unsafeProcess)));
        addDescription(new ViolationsBriefDescription(203, getString(R.string.briefViol_cooling)));
        addDescription(new ViolationsBriefDescription(204, getString(R.string.briefViol_heating)));
        addDescription(new ViolationsBriefDescription(205, getString(R.string.briefViol_coldTemp)));
        addDescription(new ViolationsBriefDescription(206, getString(R.string.briefViol_hotTemp)));
        addDescription(new ViolationsBriefDescription(208, getString(R.string.briefViol_unapprovedSource)));
        addDescription(new ViolationsBriefDescription(209, getString(R.string.briefViol_foodContamination)));
        addDescription(new ViolationsBriefDescription(210, getString(R.string.briefViol_thawing)));
        addDescription(new ViolationsBriefDescription(211, getString(R.string.briefViol_frozenTemp)));
        addDescription(new ViolationsBriefDescription(212, getString(R.string.briefViol_writtenProc)));
        addDescription(new ViolationsBriefDescription(301, getString(R.string.briefViol_equipUnsanitized)));
        addDescription(new ViolationsBriefDescription(302, getString(R.string.briefViol_equipWashing)));
        addDescription(new ViolationsBriefDescription(303, getString(R.string.briefViol_water)));
        addDescription(new ViolationsBriefDescription(304, getString(R.string.briefViol_pestsFree)));
        addDescription(new ViolationsBriefDescription(305, getString(R.string.briefViol_pestsEntrance)));
        addDescription(new ViolationsBriefDescription(306, getString(R.string.briefViol_sanitizationPremises)));
        addDescription(new ViolationsBriefDescription(307, getString(R.string.briefViol_equipMaterial)));
        addDescription(new ViolationsBriefDescription(308, getString(R.string.briefViol_equipState)));
        addDescription(new ViolationsBriefDescription(309, getString(R.string.briefViol_cleansers)));
        addDescription(new ViolationsBriefDescription(310, getString(R.string.briefViol_singleUse)));
        addDescription(new ViolationsBriefDescription(311, getString(R.string.briefViol_premises)));
        addDescription(new ViolationsBriefDescription(312, getString(R.string.briefViol_unnecessaryItems)));
        addDescription(new ViolationsBriefDescription(313, getString(R.string.briefViol_animals)));
        addDescription(new ViolationsBriefDescription(314, getString(R.string.briefViol_sanitationProc)));
        addDescription(new ViolationsBriefDescription(315, getString(R.string.briefViol_thermometers)));
        addDescription(new ViolationsBriefDescription(401, getString(R.string.briefViol_handwashingStations)));
        addDescription(new ViolationsBriefDescription(402, getString(R.string.briefViol_washHands)));
        addDescription(new ViolationsBriefDescription(403, getString(R.string.briefViol_personalHygiene)));
        addDescription(new ViolationsBriefDescription(404, getString(R.string.briefViol_smoking)));
        addDescription(new ViolationsBriefDescription(501, getString(R.string.briefViol_level1)));
        addDescription(new ViolationsBriefDescription(502, getString(R.string.briefViol_FOODSAFE)));
    }



}
