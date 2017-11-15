package core.engine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import shaders.StaticShader;
import shaders.TerrainShader;
import terrain.Terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {
    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1F;
    // how far you can see [IM]
    private static final float FAR_PLANE = 1000;

    private StaticShader shader = new StaticShader();
    private Matrix4f projectionMatrix;
    private EntityRenderer renderer;
    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    private List<Terrain> terrains = new ArrayList<>();

    // terrain [IM]
    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();

    public MasterRenderer() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        createProjectionMatrix();
        renderer = new EntityRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
    }

    public void render(Light sun, Camera camera) {
        prepare();
        shader.start();
        shader.loadLight(sun);
        shader.loadViewMatrix(camera);
        renderer.render(entities);
        shader.stop();

        terrainShader.start();
        terrainShader.loadLight(sun);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();

        terrains.clear();
        entities.clear();
    }

    void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        // 1F rgba equals MAX(255F)
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0F, 0F, 0F, 1F);
    }

    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getTexturedModel();

        List<Entity> batch = entities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public void processTerrains(Terrain terrain) {
        terrains.add(terrain);
    }

    private void createProjectionMatrix() {
        float aspectRatio = Display.getWidth() / Display.getHeight();
        float yScale = (float) (1f / Math.tan(Math.toRadians(FOV / 2f) * aspectRatio));
        float xScale = yScale / aspectRatio;
        float frustumLength = FAR_PLANE - NEAR_PLANE;
        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = xScale;
        projectionMatrix.m11 = yScale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustumLength);
        projectionMatrix.m23 = -1F;
        projectionMatrix.m32 = -(2 * NEAR_PLANE * FAR_PLANE / frustumLength);
        projectionMatrix.m33 = 0F;
    }

    public void cleanUp() {
        shader.cleanUp();
        terrainShader.cleanUp();
    }
}

