package competition;

import core.*;
import org.springframework.stereotype.Component;

@Component
public class Competition {

    public Player player = new Player("Rediska");

    public Competition() {

        Track track = new Track(100);
        Car car = new Car(track, player,60, 30, 60, 300);
        car.restart();
        player.car = car;
    }
}
