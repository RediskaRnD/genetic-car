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

import java.util.HashMap;

@CrossOrigin
@Controller
public class TrackController {

    private Track track;
    private double carMaxSpeed = 300;

    @Autowired
    private Competition competition;

    @GetMapping("/getdata")
    @ResponseBody
    public HashMap<String, Object> index() {
        HashMap<String, Object> stringHashMap = new HashMap<>();
        if (track != null) {
            track = new Track(
                    track.getLength(),
                    track.getAngle(),
                    track.getWidthMin(),
                    track.getWidthMax(),
                    track.getDWidth(),
                    track.getIncMin(),
                    track.getIncMax()
            );
        } else {
            track = new Track(2);
        }
        stringHashMap.put("track", track.getTrack());   // TODO проблема в том что даём трек, но не указываем ему параметры
        stringHashMap.put("speed", carMaxSpeed);
        String str = "track?l=" + track.getLength() +
                    "&a=" + track.getAngle() +
                    "&wMin=" + track.getWidthMin() +
                    "&wMax=" + track.getWidthMax() +
                    "&dw=" + track.getDWidth() +
                    "&iMin=" + track.getIncMin() +
                    "&iMax=" + track.getIncMax() +
                    "&speed=" + carMaxSpeed;
        stringHashMap.put("url", str);
        return stringHashMap;
    }

    @GetMapping("/")
    public String track(RedirectAttributes ra) {
        if (track == null) track = new Track(100);
        ra.addAttribute("l", track.getLength());
        ra.addAttribute("a", track.getAngle());
        ra.addAttribute("wMin", track.getWidthMin());
        ra.addAttribute("wMax", track.getWidthMax());
        ra.addAttribute("dw", track.getDWidth());
        ra.addAttribute("iMin", track.getIncMin());
        ra.addAttribute("iMax", track.getIncMax());
        ra.addAttribute("speed", carMaxSpeed);
        return "redirect:/track";
    }

    @GetMapping("/track")
    public String track(@RequestParam(name = "l", required = false, defaultValue = "100") int length,
                        @RequestParam(name = "a", required = false, defaultValue = "110") double angle,
                        @RequestParam(name = "wMin", required = false, defaultValue = "100") double widthMin,
                        @RequestParam(name = "wMax", required = false, defaultValue = "300") double widthMax,
                        @RequestParam(name = "dw", required = false, defaultValue = "50") double dWidth,
                        @RequestParam(name = "iMin", required = false, defaultValue = "100") double incMin,
                        @RequestParam(name = "iMax", required = false, defaultValue = "200") double incMax,
                        @RequestParam(name = "speed", required = false, defaultValue = "300") double carMaxSpeed
    ) {
        track = new Track(length, angle, widthMin, widthMax, dWidth, incMin, incMax);
        this.carMaxSpeed = carMaxSpeed;
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