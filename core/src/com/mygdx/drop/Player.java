package com.mygdx.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class Player extends Actor {

    Rectangle bounds;
    float speedy, gravity;
    AssetManager manager;

    Player()
    {
        setX(200);
        setY(280 / 2 - 64 / 2);
        setSize(64,45);
        bounds = new Rectangle();

        speedy = 0;
        gravity = 850f;

        /*addListener( new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                impulso();
                return false;
            }
        });*/
    }

    @Override
    public void act(float delta)
    {
        moveBy(0, speedy * delta);

        speedy -= gravity * delta;

        if (getY() > 480 - 45)
            setY(480 - 45);

        bounds.set(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(manager.get("bird.png", Texture.class), getX(), getY());
    }

    void impulso()
    {
        speedy = 400f;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setManager(AssetManager manager) {
        this.manager = manager;
    }

}
