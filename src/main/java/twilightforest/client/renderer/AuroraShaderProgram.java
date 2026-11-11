package twilightforest.client.renderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AuroraShaderProgram {

    private final int programId;
    private final boolean valid;

    public AuroraShaderProgram(String vertexPath, String fragmentPath) {
        int program = 0;
        int vertexShader = 0;
        int fragmentShader = 0;
        boolean linked = false;

        try {
            vertexShader = compile(GL20.GL_VERTEX_SHADER, vertexPath);
            fragmentShader = compile(GL20.GL_FRAGMENT_SHADER, fragmentPath);

            program = GL20.glCreateProgram();
            GL20.glAttachShader(program, vertexShader);
            GL20.glAttachShader(program, fragmentShader);
            GL20.glLinkProgram(program);

            if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
                throw new IOException("Failed to link shader program: " + GL20.glGetProgramInfoLog(program, 4096));
            }

            linked = true;
        } catch (IOException e) {
            FMLLog.severe("[TwilightForest] Failed to load shader %s / %s: %s", vertexPath, fragmentPath, e);
            if (program != 0) {
                GL20.glDeleteProgram(program);
                program = 0;
            }
        } finally {
            if (vertexShader != 0) {
                GL20.glDeleteShader(vertexShader);
            }
            if (fragmentShader != 0) {
                GL20.glDeleteShader(fragmentShader);
            }
        }

        this.programId = program;
        this.valid = linked;
    }

    private static int compile(int type, String resourcePath) throws IOException {
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, readResource(resourcePath));
        GL20.glCompileShader(shader);

        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new IOException(
                    "Failed to compile shader " + resourcePath + ": " + GL20.glGetShaderInfoLog(shader, 4096));
        }

        return shader;
    }

    private static String readResource(String resourcePath) throws IOException {
        InputStream stream = AuroraShaderProgram.class.getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IOException("Shader resource not found: " + resourcePath);
        }

        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        }

        return builder.toString();
    }

    public boolean isValid() {
        return this.valid;
    }

    public void bind() {
        GL20.glUseProgram(this.programId);
    }

    public void unbind() {
        GL20.glUseProgram(0);
    }

    public void setUniform1i(String name, int value) {
        int location = GL20.glGetUniformLocation(this.programId, name);
        if (location >= 0) {
            GL20.glUniform1i(location, value);
        }
    }

    public void setUniform1f(String name, float value) {
        int location = GL20.glGetUniformLocation(this.programId, name);
        if (location >= 0) {
            GL20.glUniform1f(location, value);
        }
    }

    public void setUniform3f(String name, float x, float y, float z) {
        int location = GL20.glGetUniformLocation(this.programId, name);
        if (location >= 0) {
            GL20.glUniform3f(location, x, y, z);
        }
    }
}
