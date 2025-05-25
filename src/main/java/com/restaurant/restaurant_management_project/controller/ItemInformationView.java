package com.restaurant.restaurant_management_project.controller;



import com.restaurant.restaurant_management_project.model.RMenuItem;
import com.restaurant.restaurant_management_project.util.enums.FoodUnit;
import com.restaurant.restaurant_management_project.util.enums.ItemCategory;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Consumer;

import static com.restaurant.restaurant_management_project.util.MenuItemValidatorUtils.validateForm;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class ItemInformationView {
    @FXML
    private StackPane imageChose;
    @FXML
    private Text errorQuantityMsg;
    @FXML
    private Text errorPriceMsg;
    @FXML
    private Text errorNameMsg;
    @FXML
    private TextField itemID;
    @FXML
    private TextField itemQuantity;
    @FXML
    private VBox form;
    @FXML
    private MenuButton sideItem;
    @FXML
    private CheckBox itemStatus;
    @FXML
    private MenuButton itemCate;
    @FXML
    private TextField itemPrice;
    @FXML
    private MenuButton itemUnit;
    @FXML
    private ImageView itemImage;
    @FXML
    private Button button2;
    @FXML
    private Button button1;
    @FXML
    private FontIcon closeIcon;
    @FXML
    private TextField itemName;
    Runnable onCancel;
    private RMenuItem data;
    private Stage ownerPopup;
    private Consumer<RMenuItem> dataConsumer;
    private Consumer<RMenuItem> upadateDataConsumer;
    private ObservableList<RMenuItem> menuItemObservableList;
    private Integer side = null ;
    private String imageFormat = "png";
    private static final Image PLACEHOLDER_IMAGE = new Image(ItemView.class.getResource("/image/em.png").toExternalForm());
    public void initialize() {
        Platform.runLater(() -> {
            form.requestFocus(); // Chuyển focus sang container
        });
    }

    public void setUpadateDataConsumer(Consumer<RMenuItem> upadateDataConsumer) {
        this.upadateDataConsumer = upadateDataConsumer;
    }

    public void setMenuItemObservableList(ObservableList<RMenuItem> menuItemObservableList) {
        this.menuItemObservableList = menuItemObservableList;
    }

    public void setOwnerPopup(Stage ownerPopup) {
        this.ownerPopup = ownerPopup;
    }

    public void setData(RMenuItem data) {
        this.data = data;
    }

    public void setDataConsumer(Consumer<RMenuItem> dataConsumer) {
        this.dataConsumer = dataConsumer;
    }
    //update
    public void manageInfo()
    {
        imageChose.setMouseTransparent(true);
        fillData();
        setTextFields(form,false);
        button1.setText("Sửa");
        button2.setText("Xóa");
        button2.setOnAction(event -> delete());
        button1.setOnAction(event -> editInfo());
    }
    private void editInfo()
    {
        setUpMenuButtons();
        imageChose.setMouseTransparent(false);
        setTextFields(form,true);
        button1.setText("Hủy");
        button2.setText("Lưu");
        button1.setOnAction(event -> cancelUpdate());
        button2.setOnAction(event -> updateData());
    }
    //Lưu
    private void updateData()
    {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.initOwner(ownerPopup);
        confirmationAlert.setTitle("Xác nhận sửa");
        confirmationAlert.setHeaderText("Bạn có chắc chắn muốn thay đổi thông món ăn này?");
        confirmationAlert.setContentText("Món ăn: '" + data.getItemName() + "'\nHành động này không thể hoàn tác.");
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            if ( validateForm(itemName.getText(),itemPrice.getText(),errorNameMsg,errorPriceMsg))
            {
                data.setItemName(itemName.getText());
                data.setItemImage(fxImageToBytes(itemImage.getImage(),imageFormat));
                data.setItemCategory(itemCate.getText());
                data.setSideItem(side);
                data.setItemUnit(itemUnit.getText());
                data.setItemPrice(new BigDecimal(itemPrice.getText()));
                data.setItemStatus(itemStatus.isSelected());
                upadateDataConsumer.accept(data);
                manageInfo();
            }
        }
    }
    //Hủy
    private void cancelUpdate()
    {
        fillData();
        setTextFields(form,false);
        emptyMenuButtons();
        manageInfo();
    }
    //Hàm chuyển data lên form
    private void fillData()
    {
        if (data.getItemImage() != null) {
            Image image = new Image(new ByteArrayInputStream(data.getItemImage()));
            Platform.runLater(() -> {
                itemImage.setImage(image);
            });

        } else {
            Platform.runLater(() -> {
                itemImage.setImage(PLACEHOLDER_IMAGE);
            });
        }
        itemID.setText("Mã món ăn: "+data.getItemId());
        itemCate.setText(data.getItemCategory());
        itemName.setText(data.getItemName());
        itemPrice.setText(data.getItemPrice().toString());
        itemUnit.setText(data.getItemUnit());
        System.out.println(data.getSideItem());
        if(data.getSideItem() == null)
        {
            sideItem.setText("Không có");
        }
        else
        {
            Optional<RMenuItem> foundOptional = menuItemObservableList.stream() // Tạo stream từ list
                    .filter(item -> item.getId().equals(data.getSideItem()) ) // Lọc các item có ID khớp
                    .findFirst(); // Lấy phần tử đầu tiên tìm thấy (trả về Optional)
            sideItem.setText(foundOptional.get().getItemName());
        }
        itemStatus.setSelected(data.isItemStatus());
    }

    private void delete()
    {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.initOwner(ownerPopup);
        confirmationAlert.setTitle("Xác nhận Xóa");
        confirmationAlert.setHeaderText("Bạn có chắc chắn muốn xóa món ăn này?");
        // Thêm tên món ăn vào nội dung để rõ ràng hơn
        confirmationAlert.setContentText("Món ăn: '" + data.getItemName() + "'\nHành động này không thể hoàn tác.");
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            dataConsumer.accept(data);
            hidePopup();
        }
    }
    //add
    public void setUpAdd()
    {
        imageChose.getChildren().addFirst(new Text("Chọn ảnh"));
        setTextFields(form,true);
        itemID.setVisible(false);
        setUpMenuButtons();
        button1.setText("Hủy");
        button2.setText("Thêm");
        button1.setOnAction(event -> cancelAdd());
        button2.setOnAction(event -> add());
    }
    public void setOnCancel(Runnable onCancel)
    {
        this.onCancel = onCancel;
    }

    public void add()
    {
        if ( validateForm(itemName.getText(),itemPrice.getText(),errorNameMsg,errorPriceMsg))
        {
            data = new RMenuItem(itemName.getText(),itemStatus.isSelected(),new BigDecimal(itemPrice.getText()),itemCate.getText(),fxImageToBytes(itemImage.getImage(),imageFormat),itemUnit.getText(),side);
            System.out.println(data.getItemName());
            dataConsumer.accept(data);
            hidePopup();
        }
    }
    public void cancelAdd()
    {
        if (onCancel != null) {
            onCancel.run(); // Gọi callback hủy
        }
        hidePopup(); // Ẩn popup
    }
    private void hidePopup() {
        if (ownerPopup != null) {
            ownerPopup.close();
        }
    }
    private void setTextFields(Parent parent,boolean status) {
        if (parent == null) {
            return;
        }
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                // Nếu Node là TextField, khác itemID đặt editable thành  status
                if (!node.getId().equals("itemID"))
                    ((TextField) node).setEditable(status);
            } else if (node instanceof Parent) {
                // Nếu Node là một container khác, duyệt đệ quy vào bên trong
                setTextFields((Parent) node,status);
            }
        }
    }
    @FXML
    private void chooseImageFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn  Ảnh");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Tệp Ảnh", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp");
        FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter(
                "Tất cả tệp", "*.*");
        fileChooser.getExtensionFilters().addAll(imageFilter, allFilter);
        fileChooser.setSelectedExtensionFilter(imageFilter);

        File selectedFile = fileChooser.showOpenDialog(ownerPopup);

        if (selectedFile != null) {
            System.out.println("Đã chọn tệp: " + selectedFile.getAbsolutePath());
            try {
                Image image = new Image(selectedFile.toURI().toString());
                Platform.runLater(()->{
                    itemImage.setImage(image);
                });
                int lastDotIndex = selectedFile.getAbsolutePath().lastIndexOf('.');

                if (lastDotIndex > 0 && lastDotIndex < selectedFile.getAbsolutePath().length() - 1) {
                    // Trích xuất phần mở rộng và chuyển thành chữ thường
                    imageFormat = Optional.of(selectedFile.getAbsolutePath().substring(lastDotIndex + 1).toLowerCase()).get();
                } else {
                   imageFormat = "png";
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi tải ảnh: " + e.getMessage());
                //imageView.setImage(null);
            }
        } else {
            System.out.println("Người dùng đã hủy chọn tệp.");
        }
    }
    //chuyển ảnh thành chuỗi bytes
    public static byte[] fxImageToBytes(Image image, String format) {
        if (image == null) {
            System.err.println("Đối tượng Image đầu vào là null.");
            return null;
        }
        if (format == null || format.trim().isEmpty()) {
            throw new IllegalArgumentException("Định dạng (format) không được rỗng.");
        }
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);

        if (bImage == null) {
            System.err.println("Không thể chuyển đổi JavaFX Image thành BufferedImage.");
            return null;
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            boolean success = ImageIO.write(bImage, format.toLowerCase(), outputStream);

            if (!success) {
                System.err.println("Không tìm thấy writer phù hợp cho định dạng: "+ format);
                return null;
            }
            return outputStream.toByteArray();

        } catch (IOException e) {
            System.err.println("Lỗi I/O khi ghi BufferedImage vào ByteArrayOutputStream: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            // Bắt các lỗi không mong muốn khác
            System.err.println("Lỗi không xác định trong quá trình chuyển đổi Image sang bytes: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    //thêm các menuitem vào menubutton
    private void setUpMenuButtons()
    {
        MenuItem defaultItem = new MenuItem("Không có");
        defaultItem.setOnAction(event -> {
            side = null;
            sideItem.setText(defaultItem.getText());
        });
        sideItem.getItems().add(defaultItem);
        for (RMenuItem item : menuItemObservableList) {

            MenuItem menuItem = new MenuItem(item.getItemName());

            menuItem.setOnAction(event -> {
               side = item.getId();
               sideItem.setText(item.getItemName());
            });
            sideItem.getItems().add(menuItem);
        }
        for (String cate : ItemCategory.getAllDisplayNames()) {

            MenuItem menuItem = new MenuItem(cate);
            menuItem.setOnAction(event -> {
                itemCate.setText(cate);
            });
            itemCate.getItems().add(menuItem);
        }
        for (String unit : FoodUnit.getAllDisplayNames()) {

            MenuItem menuItem = new MenuItem(unit);
            menuItem.setOnAction(event -> {
                itemUnit.setText(unit);
            });
            itemUnit.getItems().add(menuItem);
        }
    }
    private void emptyMenuButtons(){
        itemUnit.getItems().clear();
        itemCate.getItems().clear();
        sideItem.getItems().clear();
    }
}
