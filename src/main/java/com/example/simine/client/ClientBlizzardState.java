package com.example.simine.client;

public class ClientBlizzardState {
    /** Сервер сказал: метель идёт. */
    public static boolean active = false;
    /** Плавная сила метели 0..1 (нарастает и затухает). */
    public static float intensity = 0.0F;
    /** Плавное "игрок на улице" 0..1 (под крышей туман и громкий ветер пропадают). */
    public static float exposure = 0.0F;
}
