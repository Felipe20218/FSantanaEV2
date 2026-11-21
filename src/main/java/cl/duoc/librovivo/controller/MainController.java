package cl.duoc.librovivo.controller;

import cl.duoc.librovivo.model.Material;
import cl.duoc.librovivo.persistence.MaterialJsonDAO;
import cl.duoc.librovivo.persistence.MaterialRepository;
import cl.duoc.librovivo.persistence.PersistenciaException;
import cl.duoc.librovivo.service.MaterialService;
import cl.duoc.librovivo.service.ValidacionException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.NumberFormat;
import java.util.Locale;

public class MainController {

    @FXML
    private ComboBox<String> cmbTipo;
    @FXML private TextField txtTitulo, txtAnio, txtCopias, txtAutor, txtDuracion;
    @FXML private CheckBox chkBuenEstado, chkReservado;
    @FXML private Label lblMensaje;
    @FXML private TableView<Material> tblMateriales;
    @FXML private TableColumn<Material, String> colTipo, colTitulo;
    @FXML private TableColumn<Material, Number> colAnio, colCopias, colCosto;

    private final MaterialService servicio = new MaterialService();
    private final MaterialRepository repositorio = new MaterialRepository(new MaterialJsonDAO());


    @FXML
    private void initialize() {
        cmbTipo.setItems(FXCollections.observableArrayList(
                MaterialService.TIPO_LIBRO, MaterialService.TIPO_AUDIOLIBRO));

        cmbTipo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Seleccione tipo" : item);
            }
        });
        cmbTipo.valueProperty().addListener((obs, anterior, nuevo) -> actualizarCamposEspecificos(nuevo));
        actualizarCamposEspecificos(null);

        configurarColumnas();
        tblMateriales.setItems(repositorio.getMateriales());

        try {
            repositorio.cargar();
        } catch (PersistenciaException e) {
            mostrarMensaje("Error al leer los archivos: " + e.getMessage(), true);
        }
    }

    private void configurarColumnas() {

        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anioPublicacion"));
        colCopias.setCellValueFactory(new PropertyValueFactory<>("copiasDisponibles"));

        colCosto.setCellValueFactory(c -> new ReadOnlyObjectWrapper<Number>(c.getValue().calcularCostoPrestamo()));
        NumberFormat pesos = NumberFormat.getCurrencyInstance(Locale.of("es", "CL"));
        colCosto.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number valor, boolean empty) {
                super.updateItem(valor, empty);
                setText(empty || valor == null ? null : pesos.format(valor));
            }
        });
    }

    @FXML
    private void onRegistrar() {
        try {
            Material nuevo = servicio.crearMaterial(
                    cmbTipo.getValue(), txtTitulo.getText(), txtAnio.getText(), txtCopias.getText(),
                    txtAutor.getText(), chkBuenEstado.isSelected(), chkReservado.isSelected(),
                    txtDuracion.getText());
            repositorio.agregar(nuevo);
            limpiarFormulario();
            mostrarMensaje(nuevo.getTitulo() + " (" + nuevo.getTipo() + ") registrado correctamente.", false);
        } catch (ValidacionException e) {
            mostrarMensaje(e.getMessage(), true);
        } catch (PersistenciaException e) {
            mostrarMensaje("Error al guardar los archivos: " + e.getMessage(), true);
        }
    }

    @FXML
    private void onEliminar() {
        Material seleccionado = tblMateriales.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarMensaje("Debe seleccionar un material de la tabla para eliminarlo.", true);
            return;
        }
        try {
            repositorio.eliminar(seleccionado);
            mostrarMensaje("Material eliminado: " + seleccionado.getTitulo()
                    + " (" + seleccionado.getTipo() + ").", false);
        } catch (PersistenciaException e) {
            mostrarMensaje("Error al guardar los archivos: " + e.getMessage(), true);
        }
    }

    private void actualizarCamposEspecificos(String tipo) {
        boolean esLibro = MaterialService.TIPO_LIBRO.equals(tipo);
        boolean esAudio = MaterialService.TIPO_AUDIOLIBRO.equals(tipo);
        txtAutor.setDisable(!esLibro);
        chkBuenEstado.setDisable(!esLibro);
        chkReservado.setDisable(!esLibro);
        txtDuracion.setDisable(!esAudio);
    }

    private void limpiarFormulario() {
        cmbTipo.setValue(null);
        txtTitulo.clear();
        txtAnio.clear();
        txtCopias.clear();
        txtAutor.clear();
        txtDuracion.clear();
        chkBuenEstado.setSelected(false);
        chkReservado.setSelected(false);
    }


    private void mostrarMensaje(String texto, boolean esError) {
        lblMensaje.setText(texto);
        lblMensaje.setStyle(esError ? "-fx-text-fill: #c0392b;" : "-fx-text-fill: #1e8449;");
    }
}
