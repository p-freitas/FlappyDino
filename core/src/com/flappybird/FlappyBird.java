package com.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.Color;
import java.util.Random;

import static java.awt.Color.*;

public class FlappyBird extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture[] dinos;
	private Texture fundo;

	private Texture canoBaixo;
	private Texture canoTopo;
	private Random random;
	private BitmapFont fonte;
	private Circle dinoCirculo;
	private Rectangle retanguloCanoTopo;
	private Rectangle retanguloCanoBaixo;
	//private ShapeRenderer shape;

	private Texture gameover;
	private BitmapFont mensagem;

	//Atributos de configuração
	private float larguraDispositivo;
	private float alturaDispositivo;

	private float variacao = 0;
	private float velocidadeQueda = 0 ;
	private float posicaoInicialVertical;
	private float posicaoMovimentoCanoHorizontal;
	private float espacoEntreCanos;
	private float deltaTime;
	private float alturaEntreCanosRandom;

	private int estadoJogo=0; //0 = jogo não iniciado e 1 = jogo iniciado 3= gameover
	private int pontuacao=0;
	private boolean marcouPonto = false;

	//câmera
	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIGHT = 768;
	private final float VIRTUAL_HEIGHT = 1024;

	@Override
	public void create () {
		fonte = new BitmapFont();
		fonte.getData().setScale(6);
		fonte.getColor();

		mensagem = new BitmapFont();
		mensagem.setColor(com.badlogic.gdx.graphics.Color.WHITE);
		mensagem.getData().setScale(3);


		dinoCirculo = new Circle();
		retanguloCanoBaixo = new Rectangle();
		retanguloCanoTopo = new Rectangle();
		//shape = new ShapeRenderer();

		batch = new SpriteBatch();
		dinos = new Texture[4];
		dinos[0] = new Texture("dino0.png");
		dinos[1] = new Texture("dino1.png");
		dinos[2] = new Texture("dino2.png");
		dinos[3] = new Texture("dino3.png");

		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo.png");
		canoTopo = new Texture("cano_topo.png");
		random = new Random();

		gameover = new Texture("game_over.png");

		larguraDispositivo = VIRTUAL_WIGHT;
		alturaDispositivo = VIRTUAL_HEIGHT;
		posicaoInicialVertical = 500;

		posicaoMovimentoCanoHorizontal = larguraDispositivo;

		espacoEntreCanos = 380;

		/**************** Configurando a câmera***************/

		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIGHT/2, VIRTUAL_HEIGHT/2, 0);
		viewport = new StretchViewport(VIRTUAL_WIGHT, VIRTUAL_HEIGHT, camera);


	}

	@Override
	public void render () {

		camera.update();

		//Limpar frames anteriores
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		deltaTime = Gdx.graphics.getDeltaTime();

		variacao += deltaTime * 10;
		if (variacao > 3) variacao = 0;
		if (estadoJogo == 0){
			if (Gdx.input.justTouched()){
				estadoJogo = 1;
			}
		}else {

			velocidadeQueda++;

			if (posicaoInicialVertical > 0 || velocidadeQueda < 0)
				posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;

			if (estadoJogo ==1){
				posicaoMovimentoCanoHorizontal -= deltaTime * 200;

				if (Gdx.input.justTouched()) {
					velocidadeQueda = -18;
				}

				//verificando se o cano saiu todo da tela
				if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
					posicaoMovimentoCanoHorizontal = larguraDispositivo;
					alturaEntreCanosRandom = random.nextInt(400) - 200;
					marcouPonto = false;
				}

				//verificando pontuação
				if(posicaoMovimentoCanoHorizontal < 120){
					if (!marcouPonto ){
						pontuacao++;
						marcouPonto = true;
					}
				}

			}else{ //tela GameOver -> 2

				if (Gdx.input.justTouched()){
					estadoJogo = 0;
					pontuacao = 0;
					velocidadeQueda = 0;
					posicaoInicialVertical = 500;
					posicaoMovimentoCanoHorizontal = larguraDispositivo;
				}

			}


		}
		//Configurando dados de projeçãao da câmera
		batch.setProjectionMatrix( camera.combined );

		batch.begin();

		batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo );
		batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 +
				espacoEntreCanos / 2 + alturaEntreCanosRandom);
		batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal,
				alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandom);

		batch.draw(dinos[ (int)variacao] ,120, posicaoInicialVertical);
		fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2, alturaDispositivo - 50 );

		if (estadoJogo == 2){
			batch.draw(gameover, larguraDispositivo / 4, alturaDispositivo / 2);
			mensagem.draw(batch," Toque para jogar novamente ", larguraDispositivo / 2 - 280, alturaDispositivo / 2 -
			gameover.getHeight()/2);
		}

		batch.end();

		dinoCirculo.set(120 + dinos[0].getWidth() / 2, posicaoInicialVertical + dinos[0].getHeight() /2,
				dinos[0].getWidth()/3);
		retanguloCanoBaixo = new Rectangle(
				posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandom,
				canoBaixo.getWidth(), canoTopo.getHeight()
		);
		retanguloCanoTopo = new Rectangle(
				posicaoMovimentoCanoHorizontal,alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandom,
				canoTopo.getWidth(), canoTopo.getHeight()
		);

		/*Desenhando formas
		shape.begin(ShapeRenderer.ShapeType.Filled );
		shape.circle(dinoCirculo.x, dinoCirculo.y, dinoCirculo.radius);
		shape.rect(retanguloCanoBaixo.x, retanguloCanoBaixo.y, retanguloCanoBaixo.width, retanguloCanoBaixo.height);
		shape.rect(retanguloCanoTopo.x, retanguloCanoTopo.y, retanguloCanoTopo.width, retanguloCanoTopo.height);
		shape.end();*/

		//teste de colisão
		if( Intersector.overlaps(dinoCirculo, retanguloCanoBaixo) || Intersector.overlaps(dinoCirculo, retanguloCanoTopo) ||
				 posicaoInicialVertical <= 0 || posicaoInicialVertical >= alturaDispositivo){
			estadoJogo = 2;
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
}
