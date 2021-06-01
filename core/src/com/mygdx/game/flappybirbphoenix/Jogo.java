package com.mygdx.game.flappybirbphoenix;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class Jogo extends ApplicationAdapter {

	//btach
	SpriteBatch batch;

	//device's screen dimensions
	float device_width;
	float device_height;
	int borders;

	//HUD points
	int points = 0;
	BitmapFont points_display;
	int hud_size = 4;

	//logic utils
	boolean passed_pipes = false;
	Random random;

	//physics
	int gravity = 0;
	int hop_force = 20;

	//backgorund texture and position controls
	Texture bg_img;
	float bg_offset_x = 0;
	float bg_offset_y = 0;
	float bg_velocity = 100;

	//birb textures, index, position and size controls
	Texture[] birb_frames;
	float birb_anim_velocity = 10;
	float frame = 0;
	float birb_offset_x = -50;
	float birb_offset_y = 0;
	float birb_size = 1.2f;
	float birb_height;
	float birb_width;

	//pipes textures, position, size and gap controls
	Texture pipe_top;
	Texture pipe_bottom;
	float pipes_spawn_pos_x;
	float pipes_pos_x;
	float pipes_pos_y;
	float pipes_height;
	float pipes_width;
	float pipes_size = 1f;
	float pipes_gap_size = 200;
	float gap_center_pos_y;
	float pipes_velocity = 200;

	//colliders
	ShapeRenderer shapeRenderer;
	Circle birb_collider;
	Rectangle pipe_top_collider;
	Rectangle pipe_bottom_collider;

	//At the beginning there was only darkness, and God above the sea of empty variables...
	//And then, He has spoken: "Let there be light!"
	//So everything was initialized, became clear, and ready to rock!
	@Override
	public void create () {
		InitializeTextures();
		InitializeObjects();
	}

	//"Let there be things to see" ...And so things popped up at view and started to move.
	//God saw it was good, so He wanted to play.
	//"Let me touch it." ...And so He used His finger to hop a birb around.
	@Override
	public void render () {
		DrawTextures();
		GameStateManager();
	}

	@Override
	public void dispose () {

	}

	private void GameStateManager(){
		BackgroundManager();
		BirbManager();
		TouchListener();
		//Rapidly God became bored, so He created challenges to make things more interesting...
		//"I will call it 'Hell!" He said it so, and God thought it was a good name. Indeed...
		PipesManager();
		CollisionDetection();
		PointsManager();
	}

	private void BackgroundManager() {
		//seamless loop scroll bg
		bg_offset_x -= Gdx.graphics.getDeltaTime() * bg_velocity;
		if(bg_offset_x < -device_width) bg_offset_x = 0;
	}

	private void BirbManager() {
		//gravity gradually increases
		gravity++;
	}

	private void TouchListener() {
		//makes birb hop at touch
		boolean touched = Gdx.input.justTouched();
		if(Gdx.input.justTouched()) gravity = -hop_force;
		if(birb_offset_y > 0 || touched) birb_offset_y -= gravity;
	}

	private void PipesManager() {
		//move pipes
		pipes_pos_x -= Gdx.graphics.getDeltaTime() * pipes_velocity;

		//if pipes are out of sight, loop back
		if(pipes_pos_x < -pipes_width) {
			pipes_pos_x = pipes_spawn_pos_x;
			NewRandomPos();
			passed_pipes = false;
		}
	}

	private void CollisionDetection(){

	}

	private void PointsManager() {
		if(pipes_pos_x <= birb_offset_x && !passed_pipes) {
			points++;
			passed_pipes = true;
		}
	}

	private void DrawTextures() {
		batch.begin();
		DrawBackground();
		DrawPipes();
		DrawBirb();
		DrawPoints();
		batch.end();
	}

	private void DrawBackground() {
		//draw bg1
		batch.draw(bg_img, bg_offset_x, bg_offset_y, device_width, device_height);
		//draw bg2
		batch.draw(bg_img, bg_offset_x + device_width, bg_offset_y, device_width, device_height);
	}

	private void DrawPipes() {
		//draw top pipe
		pipes_pos_y = gap_center_pos_y + pipes_gap_size;
		batch.draw(pipe_top, pipes_pos_x, pipes_pos_y, pipes_width, pipes_height);

		//draw bottom pipe
		pipes_pos_y = gap_center_pos_y - pipes_gap_size - pipes_height;
		batch.draw(pipe_bottom, pipes_pos_x, pipes_pos_y, pipes_width, pipes_height);
	}

	private void DrawBirb() {
		//draw birb using offsets & gravity
		batch.draw(birb_frames[(int) frame], birb_offset_x, birb_offset_y - gravity, birb_width, birb_height);


		//sync birb anim frames
		frame += Gdx.graphics.getDeltaTime() * birb_anim_velocity;
		if(frame > birb_frames.length)
			frame = 0;
	}

	private void DrawPoints() {
		points_display.draw(batch, String.valueOf(points), device_width /2, device_height - 100);
	}

	private void InitializeTextures() {
		bg_img = new Texture("fundo.png");
		birb_frames = new Texture[3];
		birb_frames[0] = new Texture("passaro2.png");
		birb_frames[1] = new Texture("passaro2.png");
		birb_frames[2] = new Texture("passaro3.png");
		pipe_top = new Texture("cano_topo_maior.png");
		pipe_bottom = new Texture("cano_baixo_maior.png");
	}

	private void InitializeObjects() {
		//batch things to render
		batch = new SpriteBatch();

		//get device's screen dimensions
		device_width = Gdx.graphics.getWidth();
		device_height = Gdx.graphics.getHeight();

		//set birb texture size
		birb_width = birb_frames[(int) frame].getWidth() * birb_size;
		birb_height = birb_frames[(int) frame].getHeight() * birb_size;

		//set birb starting point
		birb_offset_x= (device_width/2) + birb_offset_x;
		birb_offset_y = device_height/2;

		//utils & HUD
		random = new Random();
		points_display = new BitmapFont();
		points_display.setColor(Color.GOLD);
		points_display.getData().setScale(hud_size);

		//colliders
		shapeRenderer = new ShapeRenderer();
		birb_collider = new Circle();
		pipe_top_collider = new Rectangle();
		pipe_bottom_collider = new Rectangle();

		//set colliders sizes
		pipes_width = pipe_top.getWidth() * pipes_size;
		pipes_height = pipe_top.getHeight() * pipes_size;
		birb_collider.setRadius(birb_width/2);
		pipe_top_collider.setSize(pipes_width, pipes_size);
		pipe_bottom_collider.setSize(pipes_width, pipes_size);

		//set pipes initial position
		pipes_spawn_pos_x = device_width + pipes_width;
		pipes_pos_x = pipes_spawn_pos_x;
		borders = (int) device_height /4 + (int) pipes_gap_size;
		NewRandomPos();
	}

	private void NewRandomPos() {
		//define limits for pipes
		gap_center_pos_y = random.nextInt((int) device_height - borders * 2) * 2 + borders;
	}
}
