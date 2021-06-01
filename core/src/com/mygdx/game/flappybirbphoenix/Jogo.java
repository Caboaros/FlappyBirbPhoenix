package com.mygdx.game.flappybirbphoenix;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class Jogo extends ApplicationAdapter {
	//region [Variables Setup]
	//btach
	SpriteBatch batch;

	//device's screen dimensions
	float device_width;
	float device_height;
	float screen_relative_size = 1;
	int borders;

	//HUD
	int points = 0;
	int highscore = 0;
	float hud_size = 3f;
	int endgame_ui_pos_y = -200;
	int hud_anim_velocity = 1600;
	BitmapFont points_display;
	BitmapFont retry_display;
	BitmapFont highscore_display;
	Texture game_over_img;

	//logic utils
	boolean passed_pipes = false;
	boolean touched = false;
	int game_state = 0;
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
	float birb_anim_velocity = 15;
	float frame = 0;
	float birb_pos_x = -50;
	float birb_pos_y = 0;
	float birb_size = 1.2f;
	float birb_height;
	float birb_width;

	//pipes textures, position, size and gap controls
	Texture pipe_top;
	Texture pipe_bottom;
	float pipes_spawn_pos_x;
	float pipes_pos_x;
	float pipe_top_pos_y;
	float pipe_bottom_pos_y;
	float pipes_height;
	float pipes_width;
	float pipes_size = 1.2f;
	float pipes_gap_size = 200;
	float gap_center_pos_y;
	float pipes_velocity = 200;

	//colliders
	ShapeRenderer shapeRenderer;
	Circle birb_collider;
	Rectangle pipe_top_collider;
	Rectangle pipe_bottom_collider;

	//Sounds
	Sound hop_sound;
	Sound hit_sound;
	Sound points_sound;

	//endregion [Variables Setup]

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
	@Override
	public void render () {
		DrawTextures();
		GameStateManager();
	}

	@Override
	public void dispose () {

	}

	private void GameStateManager(){
		//"Let me touch it." ...And so He used His finger to hop a birb around.
		touched = Gdx.input.justTouched();

		//"Let's get started!"
		if (game_state == 0) {
			if(touched) {
				game_state = 1;
				//makes birb hop at touch
				gravity = -hop_force;
				hop_sound.play();
			}
		}

		else if(game_state == 1) {

			if(touched) {
				//makes birb hop at touch
				gravity = -hop_force;
				hop_sound.play();
			}

			BackgroundManager();
			PipesManager();
			//Rapidly God became bored. So He created challenges to make things more interesting...
			CollisionDetection();

			//gravity gradually increases the gravity of the situation
			gravity++;
			birb_pos_y -= gravity;
			//"I will call it 'Hell!" He said... and God thought it was a good name. Indeed...
		}
		else if (game_state == 2){

			if (points > highscore) highscore = points;
			DrawUIGameOver();
			if(touched) Retry();
		}

		PointsManager();
		BirbAnim();
	}

	private void BackgroundManager() {
		//seamless loop scroll bg
		bg_offset_x -= Gdx.graphics.getDeltaTime() * bg_velocity;
		if(bg_offset_x < -device_width) bg_offset_x = 0;
	}

	private void PipesManager() {
		//set pipes y position according to gap center
		pipe_top_pos_y = gap_center_pos_y + pipes_gap_size;
		pipe_bottom_pos_y = gap_center_pos_y - pipes_gap_size - pipes_height;

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
		//set colliders positions
		float r = birb_collider.radius;
		birb_collider.setPosition(birb_pos_x + r, birb_pos_y + r);
		pipe_top_collider.setPosition(pipes_pos_x, pipe_top_pos_y);
		pipe_bottom_collider.setPosition(pipes_pos_x, pipe_bottom_pos_y);

		boolean hit_top = Intersector.overlaps(birb_collider, pipe_top_collider);
		boolean hit_bottom = Intersector.overlaps(birb_collider, pipe_bottom_collider);

		//if any hit, end game state
		if (hit_top || hit_bottom || birb_pos_y <= 0) {
			game_state = 2;
			hit_sound.play();
		}
	}

	private void PointsManager() {
		//passed trough pipes? **gain points!**
		if(pipes_pos_x <= birb_pos_x && !passed_pipes) {
			points++;
			passed_pipes = true;
			points_sound.play();
		}
	}

	private void BirbAnim() {
		//sync birb anim frames
		frame += Gdx.graphics.getDeltaTime() * birb_anim_velocity;
		if(frame > birb_frames.length) frame = 0;
	}

	private void DrawTextures() {
		batch.begin();

		//draw bg1 & bg2
		batch.draw(bg_img, bg_offset_x, bg_offset_y, device_width, device_height);
		batch.draw(bg_img, bg_offset_x + device_width, bg_offset_y, device_width, device_height);

		//draw pipes
		batch.draw(pipe_top, pipes_pos_x, pipe_top_pos_y, pipes_width, pipes_height);
		batch.draw(pipe_bottom, pipes_pos_x, pipe_bottom_pos_y, pipes_width, pipes_height);

		//draw birb using position & gravity
		batch.draw(birb_frames[(int) frame], birb_pos_x, birb_pos_y - gravity, birb_width, birb_height);

		//draw points
		points_display.draw(batch, String.valueOf(points), device_width /2, device_height - 100);

		batch.end();
	}

	private void DrawUIGameOver() {
		if(endgame_ui_pos_y < device_height/2)
			endgame_ui_pos_y += Gdx.graphics.getDeltaTime() * hud_anim_velocity;

		batch.begin();
		//draw endgame UI
		batch.draw(game_over_img,
				device_width/2 - game_over_img.getWidth()/2 * hud_size/2,
				endgame_ui_pos_y,
				game_over_img.getWidth() * hud_size/2,
				game_over_img.getHeight() * hud_size/2);

		//draw retry and highscore
		String str_highscore = "BEST: " + String.valueOf(highscore);
		highscore_display.draw(batch,
				str_highscore,
				device_width /2 - str_highscore.length() * hud_size * 4,
				endgame_ui_pos_y - 120);
		String str_retry = "TOUCH TO TRY AGAIN";
		highscore_display.draw(batch,
				str_retry,
				device_width /2 - str_retry.length() * hud_size * 4,
				endgame_ui_pos_y - 60);

		batch.end();
	}

	private void InitializeTextures() {
		bg_img = new Texture("fundo.png");
		birb_frames = new Texture[3];
		birb_frames[0] = new Texture("passaro2.png");
		birb_frames[1] = new Texture("passaro2.png");
		birb_frames[2] = new Texture("passaro3.png");
		pipe_top = new Texture("cano_topo_maior.png");
		pipe_bottom = new Texture("cano_baixo_maior.png");
		game_over_img = new Texture("game_over.png");

	}

	private void InitializeObjects() {
		//batch things to render
		batch = new SpriteBatch();

		//get device's screen dimensions
		device_width = Gdx.graphics.getWidth();
		device_height = Gdx.graphics.getHeight();
		//AdaptativeScreen();

		//set birb texture size
		birb_width = birb_frames[(int) frame].getWidth() * birb_size;
		birb_height = birb_frames[(int) frame].getHeight() * birb_size;

		//set birb starting point
		birb_pos_x = (device_width/2) + birb_pos_x;
		birb_pos_y = device_height/2;

		//utils & UI
		random = new Random();
		points_display = new BitmapFont();
		points_display.setColor(Color.GOLD);
		points_display.getData().setScale(hud_size);

		retry_display = new BitmapFont();
		retry_display.setColor(Color.LIGHT_GRAY);
		retry_display.getData().setScale(hud_size + 1);

		highscore_display = new BitmapFont();
		highscore_display.setColor(Color.GOLDENROD);
		highscore_display.getData().setScale(hud_size);

		//colliders
		shapeRenderer = new ShapeRenderer();
		birb_collider = new Circle();
		pipe_top_collider = new Rectangle();
		pipe_bottom_collider = new Rectangle();

		//set colliders sizes
		birb_collider.setRadius(birb_width/2);
		pipes_width = pipe_top.getWidth() * pipes_size;
		pipes_height = pipe_top.getHeight() * pipes_size;
		pipe_top_collider.setSize(pipes_width, pipes_height);
		pipe_bottom_collider.setSize(pipes_width, pipes_height);

		//set pipes initial position
		pipes_spawn_pos_x = device_width + pipes_width;
		pipes_pos_x = pipes_spawn_pos_x;
		borders = (int) device_height /4 + (int) pipes_gap_size;
		NewRandomPos();

		//set sound files
		hop_sound = Gdx.audio.newSound( Gdx.files.internal("som_asa.wav"));
		hit_sound = Gdx.audio.newSound( Gdx.files.internal("som_batida.wav"));
		points_sound = Gdx.audio.newSound( Gdx.files.internal("som_pontos.wav"));
	}

	private void NewRandomPos() {
		//define new random spawn position in y, with screen spawning limits using ***MAGIC***
		gap_center_pos_y = random.nextInt((int) device_height - borders * 2) * 2 + borders;
	}

	private void Retry(){
		//ResetVariables();
		InitializeObjects();
		InitializeTextures();

	}


	//MAYBE ONE DAY...
/*	private void AdaptativeScreen() {
		//set relative size of device's screen to adapt relative sized content when drawing stuff
		float cubic_screen = device_width * device_height;
		float cubic_bg = bg_img.getWidth() * bg_img.getHeight();
		screen_relative_size = cubic_screen / cubic_bg;
		Gdx.app.log("SCREEN RELATIVE SIZE: ", String.valueOf(screen_relative_size));
		//set all screen relative sizes
		hud_size *= screen_relative_size;
		birb_size *= screen_relative_size;
		pipes_size *= screen_relative_size;
		//pipes_gap_size *= screen_relative_size;
	}

	private float Centralize(Texture tx, BitmapFont bmpf){
		float pos = 0;

		return pos;
	}*/
}
