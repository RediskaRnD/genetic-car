package tensor;
// запуск python'a из java

import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;

import java.io.File;
import java.io.IOException;

public class TensorflowSavedModel {

    public static void main(String[] args) {
        SavedModelBundle model = SavedModelBundle.load("python/model", "serve");
        Tensor<Integer> tensor = model.session().runner()
                .fetch("z")
                .feed("x", Tensor.<Integer>create(3, Integer.class))
                .feed("y", Tensor.<Integer>create(3, Integer.class))
                .run().get(0).expect(Integer.class);
        System.out.println(tensor.intValue());
//        File file = new File("python/model/gde ja.txt");
//        try {
//            file.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}