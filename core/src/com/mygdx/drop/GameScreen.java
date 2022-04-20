package com.mygdx.drop;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {

    final Bird game;



    OrthographicCamera camera;
    Stage stage;
    Player player;
    Rectangle pause;
    Array<Pipe> obstacles;
    long lastObstacleTime;
    float score;
    
    boolean paused;

    public GameScreen(final Bird gam) {
        this.game = gam;



        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // create a Rectangle to logically represent the player
        player = new Player();
        player.setManager(game.manager);

        stage = new Stage();
        stage.addActor(player);

        pause = new Rectangle();
        pause.x = 800 - 80;
        pause.y = 480 - 80;
        pause.width = 64;
        pause.height = 64;

        // create the obstacles array and spawn the first raindrop
        obstacles = new Array<Pipe>();
        spawnObstacle();

        paused = false;

        score = 0;

    }

    private void spawnObstacle() {
        float holey = MathUtils.random(50, 230);
        Pipe pipe1 = new Pipe();
        pipe1.setX(800);
        pipe1.setY(holey - 230);
        pipe1.setUpsideDown(true);
        pipe1.setManager(game.manager);
        obstacles.add(pipe1);
        stage.addActor(pipe1);

        Pipe pipe2 = new Pipe();
        pipe2.setX(800);
        pipe2.setY(holey + 200);
        pipe2.setUpsideDown(false);
        pipe2.setManager(game.manager);
        obstacles.add(pipe2);
        stage.addActor(pipe2);

        lastObstacleTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {

        boolean dead = false;

        // clear the screen with a dark blue color. The
        // arguments to clear are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        ScreenUtils.clear(0.3f, 0.8f, 0.8f, 1);

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);

        // Game batch: Background
        game.batch.begin();
        game.batch.draw(game.manager.get("background.png", Texture.class), 0, 0);
        game.batch.end();

        // Stage batch: Actors
        stage.getBatch().setProjectionMatrix(camera.combined);
        stage.draw();

        // Game batch: HUD
        game.batch.begin();
        if(paused)
            game.font12.draw(game.batch, "PAUSED", 400, 240);
        else
            game.batch.draw(game.manager.get("pause.png", Texture.class), pause.x, pause.y);
        game.font12.draw(game.batch, "Score: " + (int)score, 10, 470);
        game.batch.end();


        // process user input
        if (Gdx.input.justTouched()) {
            if(!paused) {
                Vector3 touchPos = new Vector3();
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);
                // Pause game
                if (pause.contains(touchPos.x, touchPos.y))
                    paused = true;
                else
                    player.impulso();
            }
            else
            {
                paused = false;
            }
        }

        if(!paused)
        {

            stage.act();

            score += Gdx.graphics.getDeltaTime();

            // make sure the player stays within the screen bounds
            /*if (player.y > 480 - 45)
                player.y = 480 - 45;*/
            if (player.getBounds().y < 0 - 45) {
                dead = true;
            }

            // check if we need to create a new raindrop
            if (TimeUtils.nanoTime() - lastObstacleTime > 1500000000)
                spawnObstacle();

            // move the obstacles, remove any that are beneath the bottom edge of
            // the screen or that hit the player. In the later case we play back
            // a sound effect as well.
            Iterator<Pipe> iter = obstacles.iterator();
            while (iter.hasNext()) {
                Pipe raindrop = iter.next();
                if (raindrop.getBounds().overlaps(player.getBounds())) {
                    dead = true;
                }
            }

            if(dead)
            {
                game.manager.get("drop.wav", Sound.class).play();
                game.lastScore = (int)score;
                if(game.lastScore > game.topScore)
                    game.topScore = game.lastScore;
                game.setScreen(new GameOverScreen(game));
                dispose();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
        game.manager.get("rain.mp3", Music.class).play();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {

    }

}