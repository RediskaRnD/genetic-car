package rediska;

import competition.Competition;
import core.Car;
import core.Sensor;
import core.Track;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tools.Point;

import java.util.HashMap;

@CrossOrigin
@Controller
public class TrackController {

    private Track track = new Track(1000);

    @Autowired
    private Competition competition;

    @GetMapping("/getdata")
    @ResponseBody
    public HashMap<String, Point[][]> index() {
        HashMap<String, Point[][]> stringHashMap = new HashMap<>();
        track = new Track(track.getLength());
        Point[][] points = track.getTrack();
        stringHashMap.put("track", points);
        return stringHashMap;
    }

    @GetMapping("/")
    public String track(RedirectAttributes ra) {

        ra.addAttribute("a", track.getAngle());
        ra.addAttribute("l", track.getLength());
        ra.addAttribute("wMin", track.getWidthMin());
        ra.addAttribute("wMax", track.getWidthMax());
        return "redirect:/track";
    }

    @GetMapping("/track")
    public String track(@RequestParam(name = "a", required = false, defaultValue = "110") double angle,
                        @RequestParam(name = "l", required = false, defaultValue = "50") int length,
                        @RequestParam(name = "wMin", required = false, defaultValue = "60") double widthMin,
                        @RequestParam(name = "wMax", required = false, defaultValue = "200") double widthMax
    ) {
        track = new Track(length);
        track.setAngle(angle);
        track.setWidthMin(widthMin);
        track.setWidthMax(widthMax);
        track = new Track(track.getLength());
        return "/track";
    }

    @GetMapping("/car")
    @ResponseBody
    public String getParams(@RequestParam(defaultValue = "1") int keys) {
        Car car = competition.participants.get(0).car;
        car.update(1.0 / 60);
        competition.participants.get(0).keys |= keys; // ???? TODO

        JSONObject jsonObject = new JSONObject();
        JSONArray sensors = new JSONArray();
        for (Sensor s : car.sensors) {
            sensors.put(Double.isFinite(s.distance) ? s.distance : -1);
        }
        jsonObject.put("sensors", sensors);
        jsonObject.put("angle", car.getWheelAngle());

        return jsonObject.toString();
    }
}