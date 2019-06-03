package rediska;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedMetric;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.support.MetricType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@ManagedResource(description = "another metric mbean", objectName = "TrackGeneratorServer:name=TrackGeneratorStats")
public class TrackGeneratorStats {
    @Autowired
    TrackGenerator trackGenerator;

    private  long metric = 2345;
    private final Random rand = new Random(123);

    @ManagedOperation(description = "Say Hello in Console")
    public void sayHello() {
        System.out.println("Hello from TrackGeneratorStats MBean");
    }
    @ManagedAttribute(description = "Track generator Length data")
    public long getTrackLength() {
        return trackGenerator.getLength();
    }
    @ManagedMetric(description = "Some random metric", metricType = MetricType.COUNTER)
    public long getMetric() {
        return metric;
    }
    public void setMetric(long metric) {
        this.metric = metric;
    }

    @Scheduled(fixedDelay = 10)
    void updateHello() {
        metric = rand.nextLong() % 100;
    }

}
