package competition;

import core.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Competition {

    public String name;
    public List<double[]> results;

    public List<Player> participants = new ArrayList<>();
    public List<Track> tracks = new ArrayList<>();

    public Competition() {
        this("Sikaw");
    }

    public Competition(String name) {
        this.name = name;
    }
}
