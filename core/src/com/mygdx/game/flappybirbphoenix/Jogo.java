package com.mygdx.game.flappybirbphoenix;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Jogo extends ApplicationAdapter {
	//textures & btach variables
	SpriteBatch batch;
	Texture bg_img; //backgorund

	//birb textures for anim
	Texture[] birb_frames;
	float frame = 0;

	//device dimensions
	private float device_width;
	private float device_height;

	//birb position and size controls
	float birb_offset_x = -50;
	float birb_offset_y = 0;
	float birb_size = 1;
	float birb_height;
	float birb_width;

	//background position controls
	float bg_offset_x = 0;
	float bg_offset_y = 0;

	//physics
	float gravity = 0;

	@Override
	public void create () {
		InitializeEverything();
	}

	@Override
	public void render () {
		batch.begin();

		BackgroundManager();
		BirbManager();
		TouchListener();

		batch.end();
	}

	@Override
	public void dispose () {

	}

	private void TouchListener() {
		boolean touched = Gdx.input.justTouched();
		if(Gdx.input.justTouched()) gravity = -25;
		if(birb_offset_y > 0 || touched) birb_offset_y -= gravity;
	}

	private void BirbManager() {
		//draw birb using offsets & gravity
		batch.draw(birb_frames[(int) frame], birb_offset_x, birb_offset_y - gravity, birb_width, birb_height);

		gravity++;

		frame += Gdx.graphics.getDeltaTime() * 10;
		if(frame > birb_frames.length)
			frame = 0;
	}

	private void BackgroundManager() {
		//draw bg1
		batch.draw(bg_img, bg_offset_x, bg_offset_y, device_width, device_height);
		//draw bg2
		batch.draw(bg_img, bg_offset_x + device_width, bg_offset_y, device_width, device_height);

		//seamless loop scroll bg
		bg_offset_x--;
		if(bg_offset_x < -device_width) bg_offset_x = 0;
	}

	private void InitializeEverything() {
		//initialize batch & textures
		batch = new SpriteBatch();
		bg_img = new Texture("fundo.png");
		birb_frames = new Texture[3];
		birb_frames[0] = new Texture("passaro2.png");
		birb_frames[1] = new Texture("passaro2.png");
		birb_frames[2] = new Texture("passaro3.png");

		//get actual device dimensions
		device_width = Gdx.graphics.getWidth();
		device_height = Gdx.graphics.getHeight();

		//set birb starting point
		birb_offset_x= (device_width/2) + birb_offset_x;
		birb_offset_y = device_height/2;

		//set birb texture size
		birb_width = birb_frames[(int) frame].getWidth() * birb_size;
		birb_height = birb_frames[(int) frame].getHeight() * birb_size;
	}
}
