package com.mygdx.game.flappybirbphoenix;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Jogo extends ApplicationAdapter {
	//textures & btach variables
	SpriteBatch batch;
	Texture birb_img; //passaro
	Texture bg_img; //backgorund

	Texture[] birb_frames;
	float frame = 0;

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

	//physics
	float gravity = 0;

	@Override
	public void create () {
		//initialize batch & textures
		batch = new SpriteBatch();
		bg_img = new Texture("fundo.png");
		//birb_img = new Texture("passaro1.png");
		birb_frames = new Texture[3];
		birb_frames[0] = new Texture("passaro2.png");
		birb_frames[1] = new Texture("passaro2.png");
		birb_frames[2] = new Texture("passaro3.png");

		//get actual device dimensions
		device_width = Gdx.graphics.getWidth();
		device_height = Gdx.graphics.getHeight();
	}

	@Override
	public void render () {

		batch.begin();

		BackgroundManager();
		BirbManager();

		//move bg
		bg_offset_x--;

		batch.end();
	}

	private void BirbManager() {
		//draw birb using offsets & gravity
		batch.draw(birb_frames[(int) frame],
				(device_width /2) + birb_offset_x,
				(device_height /2) + birb_offset_y - gravity,
				birb_frames[(int) frame].getWidth() * birb_size,
				birb_frames[(int) frame].getHeight() * birb_size);

		gravity++;

		frame += Gdx.graphics.getDeltaTime() * 10;
		if(frame > birb_frames.length)
			frame = 0;
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
