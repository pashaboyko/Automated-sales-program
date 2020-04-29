package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sample.pojo.Features;
import sample.pojo.Manufactory;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

import static sample.HttpURLConnectionExample.*;

public class EditClothes {

    private Stage dialogStage;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField barcodeField;

    @FXML
    private TextField priceField;

    @FXML
    private ChoiceBox<String> sexBox;

    @FXML
    private TextField nameField;

    @FXML
    private Button addButton;

    @FXML
    private ChoiceBox<String> colorBox;

    @FXML
    private ChoiceBox<Features> categoryBox;

    @FXML
    private ChoiceBox<Manufactory> manufactureBox;

    @FXML
    private ChoiceBox<String> sizeBox;
    @FXML
    private Button canceButton;

    @FXML
    private Label wrongText;

    @FXML
    private AnchorPane wrongPane;

    @FXML
    private TextField quantilyField;

    @FXML
    private DatePicker datePicker;

    private ObservableList<Features> fList = FXCollections.observableArrayList();
    private ObservableList<Manufactory> mList = FXCollections.observableArrayList();


    @FXML
    void initialize() {

        LocalDate maxDate = LocalDate.now();
        datePicker.setDayCellFactory(d ->
                new DateCell() {
                    @Override public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        setDisable(item.isAfter(maxDate));
                    }});


        JSONObject features = new JSONObject();


        try {
            String params = "category=одежда";

            features = HttpURLConnectionExample.sendPOST(HttpURLConnectionExample.POST_URL_SUBCATEGORY, params);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray key = features.names ();
        for (int i = 0; i < key.length (); ++i) {
            String keys = null;
            try {
                keys = key.getString(i);
                fList.add( new Features(Integer.parseInt(keys), features.getString(keys)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        JSONObject manufactory = new JSONObject();



        try {
            String params = "category=одежда";

            manufactory = HttpURLConnectionExample.sendPOST(HttpURLConnectionExample.POST_URL_MANUFACTORY, params);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        key = manufactory.names ();
        for (int i = 0; i < key.length (); ++i) {
            String keys = null;
            try {
                keys = key.getString(i);
                mList.add( new Manufactory(Integer.parseInt(keys), manufactory.getString(keys)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        categoryBox.setItems(fList);
        manufactureBox.setItems(mList);
        colorBox.setItems(FXCollections.observableArrayList(
                "Чёрный", "Фиолетовый", "Синий","Зелёный", "Желтый", "Оранжевый","Красный", "Коричневый", "Белый"));
        sizeBox.setItems(FXCollections.observableArrayList(
                "38", "40", "42","44", "46", "48"));
        sexBox.setItems(FXCollections.observableArrayList(
                "Мужской", "Женский", "Унисекс"));


        addButton.setOnAction(event -> {
            if (priceField.getText().isEmpty()  || nameField.getText().isEmpty() || manufactureBox.getItems() == null || sexBox.getItems().isEmpty() ||sizeBox.getItems() == null |colorBox.getItems() == null || barcodeField.getText().isEmpty() || quantilyField.getText().isEmpty()|| datePicker.getValue() == null || categoryBox.getItems() == null) {
                wrongText.setText("Please fill all fields");
                wrongPane.setVisible(true);

            }
            else{
                try {
                    Double d1 = new Double(priceField.getText());
                    try {

                        Integer d2 = new Integer(quantilyField.getText());

                        String params = String.format("barcode=%s", barcodeField.getText());

                        HttpURLConnectionExample.sendPOST(POST_URL_BARCODE_BOOL, params);

                        params=String.format("name=%s&&barcode=%s&&price=%s&&id_subcategory=%s&&id_manufacturer=%s&&delivery_date=%s&&quantity=%s",nameField.getText(), barcodeField.getText(),priceField.getText(),categoryBox.getValue().getId(),manufactureBox.getValue().getId(),datePicker.getValue(),quantilyField.getText());

                        HttpURLConnectionExample.sendPOST(POST_URL_ADD, params);

                        params=String.format("value=%s&&id_feature=%s&&barcode=%s",colorBox.getValue(), 3, barcodeField.getText());

                        HttpURLConnectionExample.sendPOST(POST_URL_ADD_FEATURES, params);

                        params=String.format("value=%s&&id_feature=%s&&barcode=%s",sexBox.getValue(), 4, barcodeField.getText());

                        HttpURLConnectionExample.sendPOST(POST_URL_ADD_FEATURES, params);

                        params=String.format("value=%s&&id_feature=%s&&barcode=%s",sizeBox.getValue(), 2, barcodeField.getText());

                        HttpURLConnectionExample.sendPOST(POST_URL_ADD_FEATURES, params);

                        addButton.getScene().getWindow().hide();


                    }catch (NumberFormatException e) {
                        quantilyField.clear();
                        wrongText.setText("Quantily is number!!");
                        wrongPane.setVisible(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                        barcodeField.clear();
                        wrongText.setText("BD has this barcode!!");
                        wrongPane.setVisible(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (NumberFormatException e) {
                    priceField.clear();
                    wrongText.setText("Price is number!!");
                    wrongPane.setVisible(true);
                }

            }
        });
        canceButton.setOnAction(event -> {
            dialogStage.close();

        });
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setProduct(Product_value product) {
        JSONObject features = new JSONObject();
        String sex = new String();
        String color = new String();
        String size = new String();


        try {
            String params = String.format("barcode=%s", product.getBarcode());

            features = HttpURLConnectionExample.sendPOST(HttpURLConnectionExample.POST_URL_INFO, params);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            sex = features.getString("пол");
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        try {
            size = features.getString("размер");
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        try {
            color = features.getString("цвет");
        }
        catch (JSONException e){
            e.printStackTrace();
        }


        nameField.setText(product.getName());
        barcodeField.setText(String.valueOf(product.getBarcode()));
        priceField.setText(String.valueOf(product.getPrice()));
        //categoryField.setText(String.valueOf(product.get_category()));
        //manufacturerField.setText(String.valueOf(product.get_manufacturer()));

        //sexField.setText(String.valueOf(sex));

        //colorField.setText(String.valueOf(color));
        //sizeField.setText(String.valueOf(size));

        for (int i = 0 ; i<fList.size(); i++){
            if(fList.get(i).getName().equalsIgnoreCase(product.get_subcategory())) categoryBox.setValue(fList.get(i));
        }

        for (int i = 0 ; i<mList.size(); i++){
            if(mList.get(i).getName().equalsIgnoreCase(product.get_manufacturer())) manufactureBox.setValue(mList.get(i));
        }

        colorBox.setValue(color);
        sizeBox.setValue(size);
        sexBox.setValue(sex);

        quantilyField.setText(String.valueOf(product.getQuantity()));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {

            Date date = formatter.parse(product.getDelivery());
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            datePicker.setValue(localDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}