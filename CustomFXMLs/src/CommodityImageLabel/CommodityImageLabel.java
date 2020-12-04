package CommodityImageLabel;

import java.io.IOException;
import java.text.DecimalFormat;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author robbob
 */
public class CommodityImageLabel extends AnchorPane {

    @FXML
    protected Label directionLabel;

    @FXML
    protected Label commodityLabel;

    @FXML
    protected Label percentChangeLabel;

    protected DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    protected String currentStyle = "";

    public CommodityImageLabel() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("CommodityImageLabel.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

    }

    public void setPercentChange(double percentChange) {

    }

}