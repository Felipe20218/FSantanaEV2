package cl.duoc.librovivo.persistence;

import cl.duoc.librovivo.model.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

public class MaterialJsonDAO implements MaterialDAO {

    private static final Path ARCHIVO_LIBROS = Path.of("libros.json");
    private static final Path ARCHIVO_AUDIOLIBROS = Path.of("audiolibros.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public List<Material> cargarTodos() throws PersistenciaException {
        Type tipoLibros = new TypeToken<List<Libro>>() {}.getType();
        Type tipoAudio = new TypeToken<List<AudioLibro>>() {}.getType();
        List<Libro> libros = leer(ARCHIVO_LIBROS, tipoLibros);
        List<AudioLibro> audios = leer(ARCHIVO_AUDIOLIBROS, tipoAudio);

        List<Material> todos = new ArrayList<>();
        todos.addAll(libros);
        todos.addAll(audios);
        return todos;
    }

    @Override
    public void guardarTodos(List<Material> materiales) throws PersistenciaException {
        List<Libro> libros = new ArrayList<>();
        List<AudioLibro> audios = new ArrayList<>();
        for (Material m : materiales) {
            if (m instanceof Libro libro) {
                libros.add(libro);
            } else if (m instanceof AudioLibro audio) {
                audios.add(audio);
            }
        }
        escribir(ARCHIVO_LIBROS, libros);
        escribir(ARCHIVO_AUDIOLIBROS, audios);
    }

    private <T> List<T> leer(Path archivo, Type tipoLista) throws PersistenciaException {
        if (!Files.exists(archivo)) {
            return new ArrayList<>();   // primera ejecución: sin error
        }
        try {
            List<T> lista = gson.fromJson(Files.readString(archivo), tipoLista);
            return lista == null ? new ArrayList<>() : lista;
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException("No se pudo leer " + archivo.getFileName() + ".", e);
        }
    }

    private void escribir(Path archivo, List<?> datos) throws PersistenciaException {
        try {
            Files.writeString(archivo, gson.toJson(datos));
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo guardar " + archivo.getFileName() + ".", e);
        }
    }
}