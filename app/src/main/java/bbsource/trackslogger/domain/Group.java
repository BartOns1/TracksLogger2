package bbsource.trackslogger.domain;

import org.json.JSONException;
import org.json.JSONObject;

public class Group {

        private String groupName;


        public Group(String groupName) {
            this.groupName = groupName;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

    public JSONObject toJSON() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("groupName", this.getGroupName());
        } catch (JSONException e)
        {
            e.printStackTrace();

        }
        return jo;
    }
}

