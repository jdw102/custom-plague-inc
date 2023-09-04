package ooga.view;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import ooga.model.actor.PerkModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

import java.util.ResourceBundle;
import java.util.function.BiPredicate;

public class PerkViewTest extends DukeApplicationTest {

    private int testID = 1;
    private PerkView myPerkView;
    private double x = 20;
    private double y = 20;
    private double cost  = 4;
//    private BiPredicate<Double, Integer> activate = new BiPredicate(2, 3);
    private PerkModel myPerkModel;
    private String name = "test";
    private String description = "this is a test perk";
    ResourceBundle languageBundle = ResourceBundle.getBundle("games.Plague.properties.languages.English");
    //ImageView myImage = new ImageView("mosquito.png");
    ResourceBundle settings = ResourceBundle.getBundle("games.Plague.properties.Settings");

    /**
     * Author @EkaEbong
     */


    @Override
    public void start(Stage stage){
        myPerkModel = new PerkModel(testID,  (a,b) -> {return true;}, null, cost);
        PerkPurchaser PerkPurchase = new PerkPurchaser(languageBundle);
        myPerkView = new PerkView(testID,myPerkModel, name, description, new ImageView(), PerkPurchase, cost, settings);
    }





    @Test
    void ButtonScale(){
        myPerkView.adjustScale(2);
        Double scaleX = myPerkView.getButton().getButton().getScaleX();
        Double scaleY = myPerkView.getButton().getButton().getScaleY();
        Assertions.assertEquals(2, scaleY);
        Assertions.assertEquals(2, scaleX);
    }

    //The perk should be available since there are no prerequisites

    @Test
    void testFadeOutButton(){
        myPerkView.update();
        Double currentOpacity = myPerkView.getButton().getButton().getOpacity();
        Assertions.assertNotEquals(0.5, currentOpacity);
    }

    @Test
    void testFadeInButton(){
        myPerkView.update();
        Double currentOpacity = myPerkView.getButton().getButton().getOpacity();
        Assertions.assertEquals(1, currentOpacity);
    }

    @Test
    void testgetName(){
        Assertions.assertEquals(myPerkView.getName(), name);
    }

    @Test
    void testgetDescription(){
        Assertions.assertEquals(myPerkView.getDescription(), description);
    }

    @Test
    void testisActive(){
        Assertions.assertFalse(myPerkView.isActive());
    }

    @Test
    void testIsAvailable(){
        Assertions.assertTrue(myPerkView.isAvailable());
    }

    @Test
    void testgetID(){
        Assertions.assertEquals(testID, myPerkView.getId());
    }

    @Test
    void testCost(){
        Assertions.assertEquals(String.valueOf(cost), myPerkView.getCost());
    }

}

