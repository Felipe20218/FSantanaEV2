package cl.duoc.librovivo.service;

import cl.duoc.librovivo.model.*;

public class MaterialService {

    public static final String TIPO_LIBRO = "Libro";
    public static final String TIPO_AUDIOLIBRO = "AudioLibro";

    public Material crearMaterial(String tipo, String titulo, String anioTexto,
                                  String copiasTexto, String autor, boolean buenEstado,
                                  boolean reservado, String duracionTexto)
            throws ValidacionException {

        if (tipo == null) {
            throw new ValidacionException("Tipo: debe seleccionar Libro o AudioLibro.");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new ValidacionException("Título: no puede estar vacío ni tener solo espacios.");
        }
        int anio = leerEntero("Año de publicación", anioTexto);
        if (anio < 1450 || anio > 2026) {
            throw new ValidacionException("Año de publicación: debe estar entre 1450 y 2026.");
        }
        int copias = leerEntero("Copias disponibles", copiasTexto);
        if (copias <= 0) {
            throw new ValidacionException("Copias disponibles: debe ser mayor que cero.");
        }

        try {
            if (TIPO_LIBRO.equals(tipo)) {
                if (autor == null || autor.isBlank()) {
                    throw new ValidacionException("Autor: no puede estar vacío ni tener solo espacios.");
                }
                Libro libro = new Libro(titulo.trim(), anio, copias, autor.trim(), buenEstado);
                if (reservado) {
                    libro.reservar();
                }
                return libro;
            }
            int duracion = leerEntero("Duración", duracionTexto);
            if (duracion <= 0) {
                throw new ValidacionException("Duración: debe ser mayor que cero minutos.");
            }
            return new AudioLibro(titulo.trim(), anio, copias, duracion);
        } catch (IllegalArgumentException e) {
            // red de seguridad: el modelo también valida y explica el motivo
            throw new ValidacionException(e.getMessage());
        }
    }

    private int leerEntero(String campo, String texto) throws ValidacionException {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new ValidacionException(campo + ": debe ser un número entero.");
        }
    }
}