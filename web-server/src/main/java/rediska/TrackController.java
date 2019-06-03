package rediska;

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
    @Autowired
    TrackGenerator trackGenerator;

    @GetMapping("/getdata")
    @ResponseBody
    public HashMap<String, Point[][]> index() {
        HashMap<String, Point[][]> stringHashMap = new HashMap<>();
        Point[][] points = trackGenerator.getTrack();
        stringHashMap.put("track", points);
        return stringHashMap;
    }

    @GetMapping("/")
    public String track(RedirectAttributes ra) {

        ra.addAttribute("a", trackGenerator.getAngle());
        ra.addAttribute("l", trackGenerator.getLength());
        ra.addAttribute("wMin", trackGenerator.getWidthMin());
        ra.addAttribute("wMax", trackGenerator.getWidthMax());
        return "redirect:/track";
    }

    @GetMapping("/track")
    public String track(@RequestParam(name = "a", required = false, defaultValue = "110") double angle,
                        @RequestParam(name = "l", required = false, defaultValue = "50") int length,
                        @RequestParam(name = "wMin", required = false, defaultValue = "60") double widthMin,
                        @RequestParam(name = "wMax", required = false, defaultValue = "200") double widthMax
    ) {
        trackGenerator.setAngle(angle);
        trackGenerator.setLength(length);
        trackGenerator.setWidthMin(widthMin);
        trackGenerator.setWidthMax(widthMax);
        return "/track";
    }
}