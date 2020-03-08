package ca.cmpt276.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manages brief descriptions list for violations
 */

public class ViolationManager implements Iterable<ViolationsBriefDescription> {

    private List<ViolationsBriefDescription> descriptions = new ArrayList<>();

    public void addDescription(ViolationsBriefDescription description){
        descriptions.add(description);
    }

    public ViolationManager() {
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
        addDescription(new ViolationsBriefDescription(101, "Plans not in accordance"));
        addDescription(new ViolationsBriefDescription(102, "Unapproved food premises"));
        addDescription(new ViolationsBriefDescription(103, "Fail to hold valid permit"));
        addDescription(new ViolationsBriefDescription(104, "Permit not posted"));
        addDescription(new ViolationsBriefDescription(201, "Food contaminated"));
        addDescription(new ViolationsBriefDescription(202, "Unsafe food processed"));
        addDescription(new ViolationsBriefDescription(203, "Food not cooled well"));
        addDescription(new ViolationsBriefDescription(204, "Food not cooked/ heated enough"));
        addDescription(new ViolationsBriefDescription(205, "Cold food stored above 4 \u00B0 c"));
        addDescription(new ViolationsBriefDescription(206, "Hot food stored below 60 \u00B0 c"));
        addDescription(new ViolationsBriefDescription(208, "Foods from unapproved sources"));
        addDescription(new ViolationsBriefDescription(209, "Food might get contaminated"));
        addDescription(new ViolationsBriefDescription(210, "Food not thawed correctly"));
        addDescription(new ViolationsBriefDescription(211, "Frozen food stored above -18 \u00B0 c"));
        addDescription(new ViolationsBriefDescription(212, "Didn't provide acceptable written procedures"));
        addDescription(new ViolationsBriefDescription(301, "Equipment not maintained in sanitary condition"));
        addDescription(new ViolationsBriefDescription(302, "Equipment not properly washed / sanitized"));
        addDescription(new ViolationsBriefDescription(303, "Hot & cold water not adequate for sanitation"));
        addDescription(new ViolationsBriefDescription(304, "Premises not free of pests"));
        addDescription(new ViolationsBriefDescription(305, "Conditions allowing entrance of pests"));
        addDescription(new ViolationsBriefDescription(306, "Premises not sanitized"));
        addDescription(new ViolationsBriefDescription(307, "Not suitable design/material of equipment"));
        addDescription(new ViolationsBriefDescription(308, "Equipment not in good working order"));
        addDescription(new ViolationsBriefDescription(309, "Chemical cleansers stored/labeled improperly "));
        addDescription(new ViolationsBriefDescription(310, "Single use containers used more than once"));
        addDescription(new ViolationsBriefDescription(311, "Premises not maintained as per approved plans"));
        addDescription(new ViolationsBriefDescription(312, "Unnecessary items stored on premises"));
        addDescription(new ViolationsBriefDescription(313, "Live animal on premises"));
        addDescription(new ViolationsBriefDescription(314, "Not provided written sanitation procedures"));
        addDescription(new ViolationsBriefDescription(315, "Cold/hot holding  equipment lack thermometers"));
        addDescription(new ViolationsBriefDescription(401, "Handwashing stations not available"));
        addDescription(new ViolationsBriefDescription(402, "Employee does not wash hands properly"));
        addDescription(new ViolationsBriefDescription(403, "Employee lack good personal hygiene"));
        addDescription(new ViolationsBriefDescription(404, "Employee smoking in prohibited areas"));
        addDescription(new ViolationsBriefDescription(501, "Doesn't have FOODSAFE Level 1 or equipment"));
        addDescription(new ViolationsBriefDescription(502, "Doesn't have FOODSAFE Level 1 in operator's absence"));
    }

}
