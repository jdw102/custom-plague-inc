package ooga.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class RegionShapeGenerator {

  public RegionShapeGenerator() {
  }

  public BorderPane makePane() {
    HashMap<Integer, ArrayList<Rectangle>> hashMap = null;
    try {
      hashMap = hashMapGenerator("src/main/java/ooga/view/example.csv");
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    Shape shape1 = regionGenerator(hashMap, 1);

    BorderPane root = new BorderPane();
    shape1.setFill(Color.RED);
    shape1.setOpacity(0.3);

    root.getChildren().addAll(shape1);
    return root;
  }

  Shape regionGenerator(HashMap<Integer, ArrayList<Rectangle>> hashMap, int regionID) {
    Shape shape = Shape.union(hashMap.get(regionID).get(0), hashMap.get(regionID).get(0));
    for (Rectangle rect : hashMap.get(regionID)) {
      shape = Shape.union(shape, rect);
      shape.setSmooth(true);
    }
    return shape;
  }

  HashMap<Integer, ArrayList<Rectangle>> hashMapGenerator(String filepath)
      throws FileNotFoundException {
    int textColumns = 0;
    int textRows = 0;
    File file = new File(filepath);
    // create an object of Scanner associated with the file
    Scanner sc = new Scanner(file);
    // read each line and count number of columns
    while (sc.hasNextLine()) {
      textColumns = sc.nextLine().split(" ").length;
      textRows += 1;
    }

    Scanner scan = new Scanner(file);

    HashMap<Integer, ArrayList<Rectangle>> hashMap = new HashMap<>();
    while (scan.next() != null) {
      for (int i = 0; i < textRows && scan.hasNextLine(); i++) {
        for (int j = 0; j < textColumns && scan.hasNext(); j++) {
          if (!hashMap.containsKey(Integer.parseInt(scan.next()))) {
            ArrayList<Rectangle> arrayList = new ArrayList<>();
            arrayList.add(new Rectangle(j * 25, i * 25, 25, 25));
            hashMap.put(Integer.parseInt(scan.next()), arrayList);
          } else {
            ArrayList<Rectangle> arrayList = new ArrayList<>();
            arrayList.add(new Rectangle(j * 10, i * 10, 10, 10));
            hashMap.put(scan.nextInt(), arrayList);
          }
        }
      }
    }
    // close scanner
    sc.close();
    scan.close();
    return hashMap;
  }

}
