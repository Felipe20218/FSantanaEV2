package cl.duoc.librovivo.persistence;

import cl.duoc.librovivo.model.Material;
import java.util.List;

public interface MaterialDAO {
    List<Material> cargarTodos() throws PersistenciaException;
    void guardarTodos(List<Material> materiales) throws PersistenciaException;
}
