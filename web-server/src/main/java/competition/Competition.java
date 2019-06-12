package competition;

import core.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Competition {

    public List<Player> participants = new ArrayList<>();

    public Competition() {
        participants.add(new Player("Test", new Car()));
        participants.get(0).car.track = new Track(100);
        participants.get(0).car.restart();
    }
}
