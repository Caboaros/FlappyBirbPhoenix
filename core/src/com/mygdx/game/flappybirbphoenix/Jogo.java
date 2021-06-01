package com.mygdx.game.flappybirbphoenix;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Jogo extends ApplicationAdapter {
	//variaveis de texturas
	SpriteBatch batch;
	Texture birb_img; //passaro
	Texture bg_img; //backgorund

	//device dimensions
	private float device_width;
	private float device_height;

	//birb position and size controls
	int birb_offset_x = -50;
	int birb_offset_y = 0;
	float birb_size = 1;

	//background position controls
	int bg_offset_x = 0;
	int bg_offset_y = 0;


	@Override
	public void create () {
		//initialize batch & textures
		batch = new SpriteBatch();
		bg_img = new Texture("fundo.png");
		birb_img = new Texture("passaro1.png");

		//get actual device dimensions
		device_width = Gdx.graphics.getWidth();
		device_height = Gdx.graphics.getHeight();
	}

	@Override
	public void render () {

		batch.begin();

		BackgroundManager();
		BirbManager();

		bg_offset_x--;

		batch.end();
	}

	private void BirbManager() {
		//draw birb
		batch.draw(birb_img,
				(device_width /2) + birb_offset_x,
				device_height /2 + birb_offset_y,
				birb_img.getWidth() * birb_size,
				birb_img.getHeight() * birb_size);
	}

	private void BackgroundManager() {
		//draw bg1
		batch.draw(bg_img,
				0 + bg_offset_x,
				0 + bg_offset_y,
				device_width,
				device_height);

		//draw bg2
		batch.draw(bg_img,
				0 + bg_offset_x + device_height,
				0 + bg_offset_y,
				device_width,
				device_height);
	}

	@Override
	public void dispose () {

	}
}
