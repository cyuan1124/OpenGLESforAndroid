package com.chefmic.openglesforandroid;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

public class AirHockeyRenderer implements GLSurfaceView.Renderer{

    private static final String A_POSITION = "a_Position";
    private static final String U_COLOR = "u_Color";

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;

    private final WeakReference<Context> context;
    private final FloatBuffer vertexData;
    private int uColorLocation;
    private int aPositionLocation;
    private int program;

    public AirHockeyRenderer(@NonNull Context context) {
        this.context = new WeakReference<>(context);

        float[] tableVerticesWithTriagnles = {
                -0.5f, -0.5f,
                0.5f, 0.5f,
                -0.5f, 0.5f,

                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f, 0.5f,

                -0.5f, 0f,
                0.5f, 0f,

                0f, -0.25f,
                0f, 0.25f
        };

        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriagnles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        vertexData.put(tableVerticesWithTriagnles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0f, 0.0f, 0.0f, 0.0f);

        String vertexShaderSource = TextResourceReader.readTextFileFromResource(context.get(), R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context.get(), R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        ShaderHelper.validateProgram(program);

        glUseProgram(program);

        uColorLocation = glGetUniformLocation(program, U_COLOR);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
        glEnableVertexAttribArray(aPositionLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        glUniform4f(uColorLocation, 1.0f, 1f, 1f, 1f);
        glDrawArrays(GL_TRIANGLES, 0, 6);

        // Draw line
        glUniform4f(uColorLocation, 1f, 0f, 0f, 1f);
        glDrawArrays(GL_LINES, 6, 2);

        // Draw mallet
        glUniform4f(uColorLocation, 0f, 0f, 1f, 1f);
        glDrawArrays(GL_POINTS, 8, 1);

        glUniform4f(uColorLocation, 1f, 0f, 0f, 1f);
        glDrawArrays(GL_POINTS, 9, 1);
    }
}
