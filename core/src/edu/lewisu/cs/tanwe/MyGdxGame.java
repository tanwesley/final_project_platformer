package edu.lewisu.cs.tanwe;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import edu.lewisu.cs.cpsc41000.common.Boundary;
import edu.lewisu.cs.cpsc41000.common.EdgeHandler;
import edu.lewisu.cs.cpsc41000.common.ImageBasedScreenObject;
import edu.lewisu.cs.cpsc41000.common.ImageBasedScreenObjectDrawer;
import edu.lewisu.cs.cpsc41000.common.PlatformCharacter;
import edu.lewisu.cs.cpsc41000.common.labels.ActionLabel;



public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	PlatformCharacter pc;
	ImageBasedScreenObjectDrawer artist;
	ArrayList<ImageBasedScreenObject> walls;
	ImageBasedScreenObject goal;
	ArrayList<ImageBasedScreenObject> bouncyPlatforms;
	ArrayList<ImageBasedScreenObject> gooPlatforms; // players cannot jump on these
	ArrayList<Boundary> boundaries;
	EdgeHandler edgy;
	OrthographicCamera cam;
	OrthographicCamera titleCam;
	float WIDTH, HEIGHT;
	ActionLabel title;
	ActionLabel startGameLabel;
	int scene;
	Texture background;

	int WORLDWIDTH, WORLDHEIGHT;
	Music lifeOnMars;

	@Override
	public void create () {
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		batch = new SpriteBatch();

		Texture img = new Texture("spaceman.png");
		background = new Texture("background.png");
		WORLDWIDTH = background.getWidth();
		WORLDHEIGHT = background.getHeight();

		// player character settings
		pc = new PlatformCharacter(img,150,0,false);
		pc.setMaxSpeed(400);
		pc.setAcceleration(800);
		pc.setDeceleration(1600);

		// Building a test level with platforms
		walls = new ArrayList<ImageBasedScreenObject>();
		Texture wallTex = new Texture("wall.png");
		walls.add(new ImageBasedScreenObject(wallTex,200,0,true));
		walls.add(new ImageBasedScreenObject(wallTex,500,0,true));
		walls.add(new ImageBasedScreenObject(wallTex,500,wallTex.getHeight(),true));
		walls.add(new ImageBasedScreenObject(wallTex,500+wallTex.getWidth(),0,true));
		walls.add(new ImageBasedScreenObject(wallTex,900,0,true));
		walls.add(new ImageBasedScreenObject(wallTex,1000,200,true));
		walls.add(new ImageBasedScreenObject(wallTex,1500,0,true));
		walls.add(new ImageBasedScreenObject(wallTex,1200,0,true));
		walls.add(new ImageBasedScreenObject(wallTex,1100,0,true));
		walls.add(new ImageBasedScreenObject(wallTex,1100,wallTex.getHeight(),true));
		walls.add(new ImageBasedScreenObject(wallTex,1100,wallTex.getHeight()*2,true));

		//player continues to bounce on these surfaces
		Texture bouncyTex = new Texture("bouncy.png");
		bouncyPlatforms = new ArrayList<ImageBasedScreenObject>();
		bouncyPlatforms.add(new ImageBasedScreenObject(bouncyTex,800,100,true));

		// slow the player down
		Texture gooTex = new Texture("goo.png");
		gooPlatforms = new ArrayList<ImageBasedScreenObject>();
		gooPlatforms.add(new ImageBasedScreenObject(gooTex,1800,0,true));
		gooPlatforms.add(new ImageBasedScreenObject(gooTex,1800+gooTex.getWidth(),0,true));

		pc.setPlatforms(walls);
		artist = new ImageBasedScreenObjectDrawer(batch);
		cam = new OrthographicCamera(WIDTH,HEIGHT);
		cam.translate(WIDTH/2,HEIGHT/2);
		cam.update();
		titleCam = new OrthographicCamera(WIDTH,HEIGHT);
		titleCam.translate(WIDTH/2,HEIGHT/2);
		titleCam.update();
		batch.setProjectionMatrix(cam.combined);
		edgy = new EdgeHandler(pc,cam,batch,0,WORLDWIDTH,0,WORLDHEIGHT,100,
			EdgeHandler.EdgeConstants.PAN, EdgeHandler.EdgeConstants.PAN);
		
		scene = 0;
		title = new ActionLabel("LIFE ON MARS?",50,400,"fonts/arial_large_font.fnt");
		startGameLabel = new ActionLabel("Press ESC to begin",150,100,"fonts/arial.fnt");
		lifeOnMars = Gdx.audio.newMusic(Gdx.files.internal("music/lifeonmars.mp3"));
		lifeOnMars.setLooping(true);
		lifeOnMars.setVolume(0.1f);
		lifeOnMars.play();
	}

	public void renderMain() {
		Gdx.gl.glClearColor(1,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		float dt = Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(1,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if (Gdx.input.isKeyJustPressed(Keys.SPACE) && pc.onSolid()) {
			pc.jump();
		}
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			scene = 0;
			return;
		}
		if (Gdx.input.isKeyPressed(Keys.W) && pc.onSolid()) {
			pc.jump();
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
			pc.accelerateAtAngle(180);
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			pc.accelerateAtAngle(270);
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			pc.accelerateAtAngle(0);
		}

		pc.applyPhysics(dt);
		Vector2 bounce;

		for (ImageBasedScreenObject wall : walls) {
			if (pc.overlaps(wall)) {
				System.out.println("collision");
				bounce = pc.preventOverlap(wall);
				if (bounce != null) {
					pc.rebound(bounce.angle(),0.01f);
				}
			}
		}

		for (ImageBasedScreenObject bouncy : bouncyPlatforms) {
			if (pc.overlaps(bouncy)) {
				System.out.println("boing");
				bounce = pc.preventOverlap(bouncy);
				if (bounce != null) {
					pc.rebound(bounce.angle(),1f);
				}
			}
		}

		for (ImageBasedScreenObject goo : gooPlatforms) {
			if (pc.overlaps(goo)) {
				bounce = pc.preventOverlap(goo);
			}
		}


		edgy.enforceEdges();
		batch.begin();
		batch.draw(background, 0,0);
		artist.draw(pc);

		for (ImageBasedScreenObject wall : walls) {
			artist.draw(wall);
		}

		for (ImageBasedScreenObject bouncy : bouncyPlatforms) {
			artist.draw(bouncy);
		}

		for (ImageBasedScreenObject goo : gooPlatforms) {
			artist.draw(goo);
		}

		batch.end();
	}	

	public void renderTitleScreen() {
		Gdx.gl.glClearColor(1,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			scene = 1;
		} else {
			batch.setProjectionMatrix(titleCam.combined);
			batch.begin();
			batch.draw(background,0,0);
			title.draw(batch,1f);
			startGameLabel.draw(batch,1f);
			batch.end();
		}
	}

	@Override
	public void render () {
		if (scene == 1) {
			renderMain();
		} else {
			renderTitleScreen();
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
