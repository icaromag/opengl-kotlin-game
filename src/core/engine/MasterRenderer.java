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
import skybox.SkyboxRenderer;
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

    private static final float RED = 0.5F;
    private static final float GREEN = 0.5F;
    private static final float BLUE = 0.5F;

    private StaticShader shader = new StaticShader();
    private Matrix4f projectionMatrix;
    private EntityRenderer renderer;
    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    private List<Terrain> terrains = new ArrayList<>();

    // terrain [IM]
    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();

    private SkyboxRenderer skyboxRenderer;

    public MasterRenderer(Loader loader) {
        enableCulling();
        createProjectionMatrix();
        renderer = new EntityRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
    }

    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void render(List<Light> lights, Camera camera) {
        prepare();
        shader.start();
        shader.loadSkyColor(RED, GREEN, BLUE);
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);
        renderer.render(entities);
        shader.stop();

        terrainShader.start();
        terrainShader.loadSkyColor(RED, GREEN, BLUE);
        // TODO fix multiple light sources for terrain [IM]
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();

        skyboxRenderer.render(camera);

        terrains.clear();
        entities.clear();
    }

    void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        // 1F rgba equals MAX(255F)
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(RED, GREEN, BLUE, 1F);
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

