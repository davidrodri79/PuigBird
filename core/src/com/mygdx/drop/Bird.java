package com.mygdx.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;


public class Bird extends Game {

	AssetManager manager;
	SpriteBatch batch;
	BitmapFont font12;
	BitmapFont font25;
	int topScore;
	int lastScore;


	public void create() {

		manager = new AssetManager();

		manager.load("bird.png", Texture.class);
		manager.load("pipe_up.png", Texture.class);
		manager.load("pipe_down.png", Texture.class);
		manager.load("pause.png", Texture.class);
		manager.load("background.png", Texture.class);
		manager.load("drop.wav", Sound.class);
		manager.load("rain.mp3", Music.class);

		manager.finishLoading();

		batch = new SpriteBatch();
		// Use LibGDX's default Arial font.
		//font = new BitmapFont();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("8bitOperatorPlus-Bold.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
		params.size = 22;
		params.borderWidth = 2;
		params.borderColor = Color.BLACK;
		params.color = Color.WHITE;
		font12 = generator.generateFont(params); // font size 12 pixels
		params.size = 50;
		params.borderWidth = 5;
		font25 = generator.generateFont(params); // font size 25 pixels
		generator.dispose(); // don't forget to dispose to avoid memory leaks!

		topScore = 0;
		lastScore = 0;

		this.setScreen(new MainMenuScreen(this));
	}

	public void render() {
		super.render(); // important!
	}

	public void dispose() {
		batch.dispose();
		font12.dispose();
		font25.dispose();
		manager.dispose();
	}

}