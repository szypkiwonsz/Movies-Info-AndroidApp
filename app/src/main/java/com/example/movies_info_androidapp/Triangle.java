package com.example.movies_info_androidapp;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
            "void main() {" +
            " gl_Position = vPosition;" +
            "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            " gl_FragColor = vColor;" +
            "}";

    private final int mProgram;

    private FloatBuffer vertexBuffer; // liczba wspolrzednych dla kazdego wierzcholka (3)
    static final int COORDS_PER_VERTEX = 3;

    // wspolrzedne trojkata
    static float triangleCoords[] = {
            // przeciwnie do ruchu wskazowek zegara:
            0.0f, 0.66f, 0.0f, // gorny wierzcholek
            -0.5f, -0.33f, 0.0f, // dolny lewy wierzcholek
            0.5f, -0.33f, 0.0f // dolny prawy wierzcholek
    };

    // kolor (RGB alpha)
    float color[] = { 0.0f, 1.0f, 0.0f, 1.0f };

    private int positionHandle;
    private int colorHandle;
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private int loadShader(int type, String shaderCode){
        // 'type' wskazuje, czy jest to shader wierzcholka, czy fragmentu
        // (GLES20.GL_VERTEX_SHADER lub GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);
        // kompilacja kodu:
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    //konstruktor
    public Triangle() {
        //bufor dla wspolrzednych
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (4 bajty dla kazdej wartosci typu float)
                triangleCoords.length * 4);
        // ustawienie kolejnosci bitow (BigEndian/LittleEndian)
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        // dodanie wspolrzednych
        vertexBuffer.put(triangleCoords);
        // ustawienie kursora na poczatek
        vertexBuffer.position(0);

        // przygotowanie programu OpenGL ES
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        // tworzy pliki wykonywalne OpenGL ES
        GLES20.glLinkProgram(mProgram);
    }

    public void draw() {
        // dodaj program do srodowiska OpenGL ES
        GLES20.glUseProgram(mProgram);
        // pobierz uchwyty na parametry naszych shaderow vertex i fragment
        // (vPosition, vColor)
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        // wlacz uchwyt na wierzcholki trojkata
        GLES20.glEnableVertexAttribArray(positionHandle);
        // Przygotuj wspolrzedne
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        // Ustaw kolor
        GLES20.glUniform4fv(colorHandle, 1, color, 0);
        // Narysuj trojkat
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        // Wylacz tablice wierzcholkow
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
