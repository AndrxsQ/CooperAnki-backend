package com.coopera2075.cooperanki.entity;

// Estados de una convocatoria. OPEN y ACTIVE son los que ya existían en los datos de V2;
// CLOSED es el que deja de admitir inversiones.
public enum CallStatus {
    OPEN,
    ACTIVE,
    CLOSED
}
