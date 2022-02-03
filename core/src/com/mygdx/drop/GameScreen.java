package com.mygdx.drop;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {

    final Bird game;

    Texture birdImage;
    Texture pipeUpImage;
    Texture pipeDownImage;
    Texture pauseImage;
    Texture backgroundImage;
    Sound dropSound;
    Music rainMusic;
    OrthographicCamera camera;
    Rectangle player;
    Rectangle pause;
    Array<Rectangle> obstacles;
    long lastObstacleTime;
    float score;

    float speedy;
    float gravity;

    boolean paused;

    public GameScreen(final Bird gam) {
        this.game = gam;

        // load the images for the droplet and the player, 64x64 pixels each
        birdImage = new Texture(Gdx.files.internal("bird.png"));
        pipeUpImage = new Texture(Gdx.files.internal("pipe_up.png"));
        pipeDownImage = new Texture(Gdx.files.internal("pipe_down.png"));
        pauseImage = new Texture(Gdx.files.internal("pause.png"));
        backgroundImage = new Texture(Gdx.files.internal("background.png"));



        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // create a Rectangle to logically represent the player
        player = new Rectangle();
        player.x = 200; //800 / 2 - 64 / 2; // center the player horizontally
        player.y = 480 / 2 - 64 / 2; //20; // bottom left corner of the player is 20 pixels above
        // the bottom screen edge
        player.width = 64;
        player.height = 45;

        pause = new Rectangle();
        pause.x = 800 - 80;
        pause.y = 480 - 80;
        pause.width = 64;
        pause.height = 64;

        // create the obstacles array and spawn the first raindrop
        obstacles = new Array<Rectangle>();
        spawnObstacle();

        paused = false;

        speedy = 0;
        gravity = 850f;

        score = 0;

    }

    private void spawnObstacle() {
        float holey = MathUtils.random(50, 230);
        Rectangle pipe1 = new Rectangle();
        pipe1.x = 800;
        pipe1.y = holey - 230;
        pipe1.width = 64;
        pipe1.height = 230;
        obstacles.add(pipe1);

        Rectangle pipe2 = new Rectangle();
        pipe2.x = 800;
        pipe2.y = holey + 200;
        pipe2.width = 64;
        pipe2.height = 230;
        obstacles.add(pipe2);

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

        // begin a new batch and draw the player and
        // all drops
        game.batch.begin();
        game.batch.draw(backgroundImage, 0, 0);
        game.batch.draw(birdImage, player.x, player.y);
        /*for (Rectangle raindrop : obstacles) {
            game.batch.draw(pipeUpImage, raindrop.x, raindrop.y);
        }*/
        for(int i = 0; i < obstacles.size; i++)
        {
                game.batch.draw( i % 2 == 0 ? pipeUpImage : pipeDownImage, obstacles.get(i).x, obstacles.get(i).y);
        }
        if(paused)
            game.font.draw(game.batch, "PAUSED", 400, 240);
        else
            game.batch.draw(pauseImage, pause.x, pause.y);
        game.font.draw(game.batch, "Score: " + (int)score, 10, 470);
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
                    speedy = 400f;
            }
            else
            {
                paused = false;
            }
        }

        if(!paused)
        {
            player.y += speedy * Gdx.graphics.getDeltaTime();

            speedy -= gravity * Gdx.graphics.getDeltaTime();

            score += Gdx.graphics.getDeltaTime();

            /*if (Gdx.input.isKeyPressed(Keys.LEFT))
                player.x -= 200 * Gdx.graphics.getDeltaTime();
            if (Gdx.input.isKeyPressed(Keys.RIGHT))
                player.x += 200 * Gdx.graphics.getDeltaTime();
    */
            // make sure the player stays within the screen bounds
            if (player.y > 480 - 45)
                player.y = 480 - 45;
            if (player.y < 0 - 45) {
                dead = true;
            }
            /*if (player.x > 800 - 64)
                player.x = 800 - 64;*/

            // check if we need to create a new raindrop
            if (TimeUtils.nanoTime() - lastObstacleTime > 1500000000)
                spawnObstacle();

            // move the obstacles, remove any that are beneath the bottom edge of
            // the screen or that hit the player. In the later case we play back
            // a sound effect as well.
            Iterator<Rectangle> iter = obstacles.iterator();
            while (iter.hasNext()) {
                Rectangle raindrop = iter.next();
                raindrop.x -= 200 * Gdx.graphics.getDeltaTime();
                if (raindrop.x  < -64)
                    iter.remove();
                if (raindrop.overlaps(player)) {
                    dead = true;
                }
            }

            if(dead)
            {
                dropSound.play();
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
        rainMusic.play();
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
        birdImage.dispose();
        pipeUpImage.dispose();
        pipeDownImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }

}