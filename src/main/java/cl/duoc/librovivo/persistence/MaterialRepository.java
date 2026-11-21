package cl.duoc.librovivo.persistence;

import cl.duoc.librovivo.model.Material;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MaterialRepository {

    private final MaterialDAO dao;
    private final ObservableList<Material> materiales = FXCollections.observableArrayList();

    public MaterialRepository(MaterialDAO dao) {
        this.dao = dao;
    }

    public void cargar() throws PersistenciaException {
        materiales.setAll(dao.cargarTodos());
    }

    public void agregar(Material material) throws PersistenciaException {
        materiales.add(material);
        try {
            dao.guardarTodos(materiales);
        } catch (PersistenciaException e) {
            materiales.remove(material);   // deshacer
            throw e;
        }
    }

    public void eliminar(Material material) throws PersistenciaException {
        int posicion = materiales.indexOf(material);
        materiales.remove(material);
        try {
            dao.guardarTodos(materiales);
        } catch (PersistenciaException e) {
            materiales.add(posicion, material);   // deshacer
            throw e;
        }
    }

    public ObservableList<Material> getMateriales() {
        return materiales;
    }
}
