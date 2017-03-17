package ecs189.querying.github;

import ecs189.querying.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Vincent on 10/1/2017.
 */
public class GithubQuerier {

    private static final String BASE_URL = "https://api.github.com/users/";

    public static String eventsAsHTML(String user) throws IOException, ParseException {
        List<JSONObject> response = getEvents(user);
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        int i = 0;
        for (JSONObject event : response) {
            // Get event type
            String type = event.getString("type");
            // Get created_at date, and format it in a more pleasant style
            String creationDate = event.getString("created_at");
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
            SimpleDateFormat outFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = inFormat.parse(creationDate);
            String formatted = outFormat.format(date);

            JSONArray commits = event.getJSONObject("payload").getJSONArray("commits");

            // Add type of event as header
            sb.append("<br/>");
            sb.append("<table style=\"width:60%; table-layout:fixed; border:2px solid black;\">");
            sb.append("<caption style=\"text-align: left; font-size: 1.17em; font-weight: bold;\" class=\"type\">");
            sb.append(type);
            sb.append("<p style=\"text-align: right; display: inline; font-size: .7em;\">");
            sb.append(" on ");
            sb.append(formatted);
            sb.append("</p>");
            sb.append("</caption>");

            // Add list of commits
            sb.append("<col width = \"50\">");
            sb.append("<col width = \"180\">");
            sb.append("<tr>");
            sb.append("<th style=\"height: 10%; text-align: center; background-color: #333333; color: #FFFFFF; padding: 1%;border:2px solid black;\">SHA</th>");
            sb.append("<th style=\"height: 10%; text-align: center; background-color: #333333; color: #FFFFFF; padding: 1%;border:2px solid black;\">Message</th>");
            sb.append("</tr>");

            sb.append("<br/>");
            for (int j = 0; j < commits.length(); j++) {
                sb.append("<tr>");
                sb.append("<td style=\" height: 7%; padding: .7%; background-color: #f2f2f2f2;border:2px solid black;\">" + commits.getJSONObject(j).getString("sha").substring(0,8) + "</td>");
                sb.append("<td style=\" height: 7%; padding: .7%; background-color: #f2f2f2f2;border:2px solid black;\">" + commits.getJSONObject(j).getString("message") + "</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");
            // Add collapsible JSON textbox (don't worry about this for the homework; it's just a nice CSS thing I like)
            sb.append("<a data-toggle=\"collapse\" href=\"#event-" + i + "\">JSON</a>");
            sb.append("<div id=event-" + i + " class=\"collapse\" style=\"height: auto;\"> <pre>");
            sb.append(event.toString());
            sb.append("</pre> </div>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private static List<JSONObject> getEvents(String user) throws IOException {
        int page = 1;
        int prs = 0;
        List<JSONObject> eventList = new ArrayList<JSONObject>();
        while(prs <10) {
            String url = BASE_URL + user + "/events";
            System.out.println(url);
            JSONObject json = Util.queryAPI(new URL(url));
            System.out.println(json);
            JSONArray events = json.getJSONArray("root");
            for (int i = 0; i < events.length(); i++) {
                if (events.getJSONObject(i).get("type").equals("PushEvent")) {
                    eventList.add(events.getJSONObject(i));
                    prs++;
                }
                if (eventList.size() == 10)
                    break;
            }
            page++;
        }
        System.out.println(eventList);
        return eventList;
    }
}