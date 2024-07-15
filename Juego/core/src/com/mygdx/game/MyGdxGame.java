package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class MyGdxGame extends ApplicationAdapter implements InputProcessor {

	// Objeto que recoge el mapa
	private TiledMap mapa;
	//Capa de tesoros
	private TiledMapTileLayer capaTesoros;
	//Capa de obstáculos
	TiledMapTileLayer capaObstaculos;
	//Capa de profundidad
	TiledMapTileLayer capaProfundidad;
	//Capa de pasos
	TiledMapTileLayer capaPasos;
	//Capa de suelo
	TiledMapTileLayer capaSuelo;
	// Ancho y alto del mapa en tiles
	private int anchoTiles, altoTiles;
	// Arrays bidimensionales de cque contienen los obstáculos y los tesoros del mapa
	private boolean[][] obstaculo, tesoro;
	// Objeto con el que se pinta el mapa de baldosas
	private TiledMapRenderer mapaRenderer;
	//Variables de ancho y alto
	int anchoMapa, altoMapa, anchoCelda, altoCelda;
	// Variable para contar el numero de tesoros
	private int cuentaTesoros, totalTesoros;
	// Cámara que nos da la vista del juego
	private OrthographicCamera camara;
	//Variables para las dimensiones de la pantalla
	private float anchuraPantalla, alturaPantalla;
	//Posicion del jugador en el mapa
	private Vector2 posicionJugador;
	// Este atributo indica el tiempo en segundos transcurridos desde que se inicia la animación,
	// servirá para determinar qué frame se debe representar
	private float stateTime;
	//Booleanos que determinan la dirección de marcha del sprite
	private static boolean izquierda, derecha, arriba, abajo;
	//Dimensiones del sprite
	private int anchoJugador;
	private int altoJugador;
	//Objeto que permite dibujar en el método render() imágenes 2D
	private SpriteBatch sb;
	//Constantes que indican el numero de filas y columnas de la hoja de sprites
	private static final int FRAME_COLS = 3;
	private static final int FRAME_ROWS = 4;
	// Atributo en el que se cargará la imagen del personaje principal.
	private Texture imagenPrincipal;
	//Animacion que se muestra en el metodo render()
	private Animation<TextureRegion> jugador;
	//Animaciones para cada una de las direcciones de mvto. del jugador
	private Animation<TextureRegion> jugadorArriba;
	private Animation<TextureRegion> jugadorDerecha;
	private Animation<TextureRegion> jugadorAbajo;
	private Animation<TextureRegion> jugadorIzquierda;
	//Velocidad de desplazamiento del jugador para cada iteración del bucle de renderizado
	private float velocidadJugador;
	//Celdas inicial y final del recorrido del personaje principal
	private Vector2 celdaInicial, celdaFinal;
	private Vector2 celdaTraslador = new Vector2(21, 9); // Casilla de teletransporte 1
	private Vector2 celdaDestino = new Vector2(0, 0); // Casilla de destino 1
	//variable que controla el avance de tiempo para los npc
	private float stateTimeNPC;
	//Jugadores no principales
	private Texture[] imgNPC;
	//Array de animaciones activas de los npc
	private Animation[] npc;
	//Array de animaciones de los NPC para cada dirección
	private Animation[] npcArriba;
	private Animation[] npcDerecha;
	private Animation[] npcAbajo;
	private Animation[] npcIzquierda;
	//Numero de NPC que hay en el juego
	private static final int numeroNPC = 5;
	//Posiciones de los NPC
	private Vector2[] posicionNPC;
	//Posiciones iniciales
	private Vector2[] origen;
	//Posiciones finales
	private Vector2[] destino;
	//Velocidad de desplazamiento de los NPC
	private float velocidadNPC;
	// Música y sonidos
	//Elementos para la musica
	private Music musicaJuego;
	private Sound tesoroEncontrado;
	private Sound pillado;
	private Sound fracaso;
	private Sound exito;
	private Sound pasos;
	private Sound traslador;
	//private Sound corre;
	private Sound lento;
	private Sound run;
	// Variable para medir velocidad de los pasos
	private int cycle, cycle_ant;
	//Elementos para sobreimpresionar informacion
	private BitmapFont fotnExito;
	private BitmapFont fontTesoros;
	private Texture corazonTexture;
	//Numero de vidas del jugador
	private int nVidas;
	// estados de juego
	private boolean vidaExtra = false;
	private boolean vivo = true;
	private boolean finalMapa = false;
	// Usado para capturar y usar marcador de tiempo
	private float lapso = 0.0f;

	@Override
	public void create () {
		// CARGA DE ELEMENTOS GRÁFICOS DEL JUEGO////////////////////////////////////////////
		// Carga del mapa
		mapa = new TmxMapLoader().load("mapa4.tmx");
		mapaRenderer = new OrthogonalTiledMapRenderer(mapa);
		//Inicializamos las variables de dirección del jugador
		stateTime = 0f;
		// Carga de la imagen del jugador principal
		imagenPrincipal = new Texture(Gdx.files.internal("RecursosGraficos/player/per_ppal.png"));
		//Array de imágenes para cada npc
		imgNPC = new Texture[numeroNPC];
		//Imágenes de cada npc
		imgNPC[0] = new Texture(Gdx.files.internal("RecursosGraficos/pnjs/pnj_uno.png"));
		imgNPC[1] = new Texture(Gdx.files.internal("RecursosGraficos/pnjs/pnj_dos.png"));
		imgNPC[2] = new Texture(Gdx.files.internal("RecursosGraficos/pnjs/pnj_tres.png"));
		imgNPC[3] = new Texture(Gdx.files.internal("RecursosGraficos/pnjs/pnj_cuatro.png"));
		imgNPC[4] = new Texture(Gdx.files.internal("RecursosGraficos/pnjs/pnj_cinco.png"));
		//Creamos el objeto SpriteBatch que nos permitirá crear animaciones dentro del método render()
		// Imagen del corazón para las vidas
		corazonTexture = new Texture(Gdx.files.internal("RecursosGraficos/vida.png"));
		sb = new SpriteBatch();
		//Determinamos el alto y ancho del mapa de baldosas. Para ello necesitamos extraer la capa
		//base del mapa y, a partir de ella, determinamos el número de celdas a lo ancho y alto,
		//así como el tamaño de la celda, que multiplicando por el número de celdas a lo alto y
		//ancho, da como resultado el alto y ancho en pixeles del mapa.
		TiledMapTileLayer capa = (TiledMapTileLayer) mapa.getLayers().get(0);
		//determinamos el ancho y alto de cada celda
		anchoCelda = (int) capa.getTileWidth();
		altoCelda = (int) capa.getTileHeight();
		//determinamos el ancho y alto del mapa completo
		anchoMapa = capa.getWidth() * anchoCelda;
		altoMapa = capa.getHeight() * altoCelda;
		//Cargamos las capas de los obstáculos y las de los pasos en el TiledMap.
		capaSuelo = (TiledMapTileLayer) mapa.getLayers().get(0);
		capaObstaculos = (TiledMapTileLayer) mapa.getLayers().get(1);
		capaPasos = (TiledMapTileLayer) mapa.getLayers().get(2);
		capaTesoros = (TiledMapTileLayer) mapa.getLayers().get(3);
		capaProfundidad = (TiledMapTileLayer) mapa.getLayers().get(4);
		//El numero de tiles es igual en todas las capas. Lo tomamos de la capa Suelo
		anchoTiles = capaSuelo.getWidth();
		altoTiles = capaSuelo.getHeight();
		//Creamos un array bidimensional de booleanos para obstáculos y tesoros
		obstaculo = new boolean[anchoTiles][altoTiles];
		tesoro = new boolean[anchoTiles][altoTiles];
		//Rellenamos los valores recorriendo el mapa
		for (int x = 0; x < anchoTiles; x++) {
			for (int y = 0; y < altoTiles; y++) {
				//rellenamos el array bidimensional de los obstaculos
				obstaculo[x][y] = ((capaObstaculos.getCell(x, y) != null) //obstaculos de la capa Obstaculos
						&& (capaPasos.getCell(x, y) == null)); //que no sean pasos permitidos de la capa Pasos
				//rellenamos el array bidimensional de los tesoros
				tesoro[x][y] = (capaTesoros.getCell(x, y) != null);
				//contabilizamos cuántos tesoros se han incluido en el mapa
				if (tesoro[x][y]) totalTesoros++;
			}
		}
		//Extraemos los frames de la imagen del jugador principal
		TextureRegion[][] tmp = TextureRegion.split(imagenPrincipal, imagenPrincipal.getWidth() / FRAME_COLS, imagenPrincipal.getHeight() / FRAME_ROWS);
		//Posiciones actuales, origen y destino de los npc
		posicionNPC = new Vector2[numeroNPC];
		origen = new Vector2[numeroNPC];
		destino = new Vector2[numeroNPC];
		// Variables para las animaciones direccionales del jugador
		float frameJugador = 0.15f;
		//Creamos las distintas animaciones, teniendo en cuenta el tiempo entre frames y le indicamos que está en bucle
		jugadorAbajo = new Animation<>(frameJugador, tmp[0]); //Fila 0, dirección abajo
		jugadorAbajo.setPlayMode(Animation.PlayMode.LOOP);
		jugadorIzquierda = new Animation<>(frameJugador, tmp[1]); //Fila 1, dirección izquierda
		jugadorIzquierda.setPlayMode(Animation.PlayMode.LOOP);
		jugadorDerecha = new Animation<>(frameJugador, tmp[2]); //Fila 2, dirección derecha
		jugadorDerecha.setPlayMode(Animation.PlayMode.LOOP);
		jugadorArriba = new Animation<>(frameJugador, tmp[3]); //Fila 3, dirección arriba
		jugadorArriba.setPlayMode(Animation.PlayMode.LOOP);
		//En principio se utiliza la animación en la dirección abajo para el jugador principal
		jugador = jugadorAbajo;
		//Ponemos a cero el atributo stateTimeNPC, que marca el tiempo de ejecución de los npc
		stateTimeNPC = 0f;
		//Velocidad de los NPC
		velocidadNPC = 0.75f; //Vale cualquier múltiplo de 0.25f
		//Creamos arrays de animaciones para los NPC
		//Las animaciones activas
		npc = new Animation[numeroNPC];
		//Las animaciones direccionales
		npcAbajo = new Animation[numeroNPC];
		npcIzquierda = new Animation[numeroNPC];
		npcDerecha = new Animation[numeroNPC];
		npcArriba = new Animation[numeroNPC];
		//Inicializamos las animaciones de los NPC
		for (int i = 0; i < numeroNPC; i++) {
			//Sacamos los frames de img en un array de TextureRegion
			tmp = TextureRegion.split(imgNPC[i], imgNPC[i].getWidth() / FRAME_COLS, imgNPC[i].getHeight() / FRAME_ROWS);

			//Creamos las distintas animaciones en bucle, teniendo en cuenta que el timepo entre frames será 150 milisegundos
			float frameNPC = 0.15f;
			//Creamos las distintas animaciones, teniendo en cuenta el tiempo entre frames y le indicamos que está en bucle
			// Creamos los arrays (filas) de imágenes para cada npc extrayéndolos
			//de las imágenes png de los distintos sprites
			npcAbajo[i] = new Animation<>(frameNPC, tmp[0]);
			npcAbajo[i].setPlayMode(Animation.PlayMode.LOOP);
			npcIzquierda[i] = new Animation<>(frameNPC, tmp[1]);
			npcIzquierda[i].setPlayMode(Animation.PlayMode.LOOP);
			npcDerecha[i] = new Animation<>(frameNPC, tmp[2]);
			npcDerecha[i].setPlayMode(Animation.PlayMode.LOOP);
			npcArriba[i] = new Animation<>(frameNPC, tmp[3]);
			npcArriba[i].setPlayMode(Animation.PlayMode.LOOP);
			//Las animaciones activas iniciales de todos los npc las seteamos en dirección abajo
			npc[i] = npcAbajo[i];
		}
		//RECORRIDO DE LOS NPC. Indicamos las baldosas de inicio y fin de su recorrido y  usamos
		//la funcion posicionaMapa para traducirlo a puntos del mapa.
		origen[0] = posicionaMapa(new Vector2(8, 14));
		destino[0] = posicionaMapa(new Vector2(23, 14));
		origen[1] = posicionaMapa(new Vector2(18, 12));
		destino[1] = posicionaMapa(new Vector2(23, 12));
		origen[2] = posicionaMapa(new Vector2(0, 8));
		destino[2] = posicionaMapa(new Vector2(16, 8));
		origen[3] = posicionaMapa(new Vector2(3, 4));
		destino[3] = posicionaMapa(new Vector2(9, 4));
		origen[4] = posicionaMapa(new Vector2(11, 1));
		destino[4] = posicionaMapa(new Vector2(23, 1));
		//POSICION INICIAL DE LOS NPC
		for (int i = 0; i < numeroNPC; i++) {
			posicionNPC[i] = new Vector2();
			posicionNPC[i].set(origen[i]);
		}
		//Posiciones inicial y final del recorrido del jugador principal
		celdaInicial = new Vector2(0, 10);
		celdaFinal = new Vector2(24, 1);
		//Inicializamos la cámara del juego
		anchuraPantalla = Gdx.graphics.getWidth();
		alturaPantalla = Gdx.graphics.getHeight();

		// INICIACIÓN DE LA MÚSICA Y SONIDOS DEL JUEGO////////////////////////////////////////////
		musicaJuego = Gdx.audio.newMusic(Gdx.files.internal("sonidos/musica_fondo.mp3"));
		musicaJuego.setLooping(true);
		tesoroEncontrado = Gdx.audio.newSound(Gdx.files.internal("sonidos/tesoros.mp3"));
		pillado = Gdx.audio.newSound(Gdx.files.internal("sonidos/pillado.mp3"));
		fracaso = Gdx.audio.newSound(Gdx.files.internal("sonidos/muerte.mp3"));
		exito = Gdx.audio.newSound(Gdx.files.internal("sonidos/final.mp3"));
		pasos = Gdx.audio.newSound(Gdx.files.internal("sonidos/pasos.mp3"));
		traslador = Gdx.audio.newSound(Gdx.files.internal("sonidos/traslador.mp3"));
		//corre = Gdx.audio.newSound(Gdx.files.internal("sonidos/corre.mp3"));
		lento = Gdx.audio.newSound(Gdx.files.internal("sonidos/lento.mp3"));
		run = Gdx.audio.newSound(Gdx.files.internal("sonidos/run.mp3"));
		//Resto de inicializaciones////////////////////////////////////////////
		//Inicializamos las variables de control de los pasos
		//Sirven para controlar los ciclos de reproduccion del sonido pasos
		cycle = 0;
		cycle_ant = 0;
		//fuentes de texto para mostrar información en pantalla
		fotnExito = new BitmapFont(Gdx.files.internal("ui/crang.fnt"));
		fontTesoros = new BitmapFont(Gdx.files.internal("ui/crang.fnt"));
		//Inicializamos las vidas del jugador y los tesoros recogidos
		cuentaTesoros = 0;
		nVidas = 3;
		//Dimensiones del jugador
		anchoJugador = tmp[0][0].getRegionWidth();
		altoJugador = tmp[0][0].getRegionHeight();
		//Velocidad del jugador (puede hacerse un menú de configuración para cambiar la dificultad del juego)
		velocidadJugador = 0.75f;//0.75f;
		//Creamos una cámara que mostrará una zona del mapa (igual en todas las plataformas)
		int anchoCamara = 400, altoCamara = 240;
		camara = new OrthographicCamera(anchoCamara, altoCamara);
		posicionJugador = new Vector2(posicionaMapa(celdaInicial));
		//Actualizamos la posición de la cámara
		camara.update();
	}

	@Override
	public void render () {
		//ponemos a la escucha de eventos la propia clase del juego
		Gdx.input.setInputProcessor(this);
		//Para borrar la pantalla
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//Vinculamos el objeto que dibuja el mapa con la cámara del juego
		//Centramos la camara en el jugador principal
		camara.position.set(posicionJugador, 0);
		//Comprobamos que la cámara no se salga de los límites del mapa de baldosas con el método MathUtils.clamp
		camara.position.x = MathUtils.clamp(camara.position.x,
				camara.viewportWidth / 2f,
				anchoMapa - camara.viewportWidth / 2f);
		camara.position.y = MathUtils.clamp(camara.position.y,
				camara.viewportHeight / 2f,
				altoMapa - camara.viewportHeight / 2f);
		//Actualizamos la cámara del juego
		camara.update();
		//Vinculamos el objeto que dibuja el mapa con la cámara del juego
		mapaRenderer.setView(camara);
		//Dibujamos las capas del mapa
		//Posteriormente quitaremos la capa de profundidad para intercalar a los personajes
		int[] capas = {0, 1, 2, 3, 4};
		mapaRenderer.render(capas);
		//ANIMACION DEL JUGADOR
		//En este método actualizaremos la posición del jugador principal
		actualizaPosicionJugador();
		// Indicamos al SpriteBatch que se muestre en el sistema de coordenadas específicas de la cámara.
		sb.setProjectionMatrix(camara.combined);
		//sonidos del juego
		//Reproducimos la musica del juego
		if (!musicaJuego.isPlaying())
			musicaJuego.play();
		//Inicializamos el objeto SpriteBatch para dibujar en pantalla
		sb.begin();
		//cuadroActual contendrá el frame que se va a mostrar en cada momento.
		TextureRegion cuadroActual = jugador.getKeyFrame(stateTime);
		sb.draw(cuadroActual, posicionJugador.x, posicionJugador.y);
		//Deteccion de colisiones con NPC
		detectaColisiones();
		// Actualizamos el estado de los NPC
		stateTimeNPC += Gdx.graphics.getDeltaTime();
		//Dibujamos las animaciones de los NPC
		for (int i = 0; i < numeroNPC; i++) {
			actualizaPosicionNPC(i);
			cuadroActual = (TextureRegion) npc[i].getKeyFrame(stateTimeNPC);
			sb.draw(cuadroActual, posicionNPC[i].x, posicionNPC[i].y);
		}
		//Finalizamos el objeto SpriteBatch
		sb.end();
		// Capturo en un string con formato de 00:00 minutos trascurridos a traves del stateTmeNPC para mistrarlo posteriormente en el hud
		String tiempo = String.format("%02d:%02d", (int) stateTimeNPC / 60, (int) stateTimeNPC % 60);
		//Pintamos la capa de profundidad del mapa de baldosas.
		capas = new int[1];
		capas[0] = 4; //Número de la capa de profundidad
		mapaRenderer.render(capas);
		// Texto de información en pantalla
		String infoTesoros = "TESOROS: " + cuentaTesoros;
		String infoexito = "¡¡¡HAS GANADO!!!";
		if(stateTimeNPC>lapso){
			vidaExtra=false;
		}
		//Dibujamos la información en la pantalla////////////////////////////////////////////////
		sb.begin();
		fontTesoros.draw(sb, infoTesoros+"/10", camara.position.x - camara.viewportWidth / 2+250, camara.position.y - camara.viewportHeight / 2 + 240);
		//fontVidas.draw(sb, infoVidas, camara.position.x - camara.viewportWidth / 2, camara.position.y - camara.viewportHeight / 2 + 30);
		for (int i = 0; i < nVidas; i++) {
			sb.draw(corazonTexture, camara.position.x - camara.viewportWidth / 2 + i*20, camara.position.y - camara.viewportHeight / 2 + 220); // Ajusta la posición según sea necesario

		}
		//Dibujamos el tiempo en pantalla
		fontTesoros.draw(sb, "Tiempo: "+tiempo, camara.position.x - camara.viewportWidth / 2 + 110, camara.position.y - camara.viewportHeight / 2 + 240);
		//Si el jugador ha recogido todos los tesoros, se muestra un mensaje de éxito
		if (cuentaTesoros == totalTesoros&&celdaActual(posicionJugador).epsilonEquals(celdaFinal,0.25f)) {
			fotnExito.draw(sb, infoexito, camara.position.x - camara.viewportWidth / 2 + 100, camara.position.y - camara.viewportHeight / 2 + 30);
		}
		//si el jugador no ha recogido todos los tesoros y llega al final del mapa, se muestra un mensaje de fracaso
		if (cuentaTesoros < totalTesoros&&celdaActual(posicionJugador).epsilonEquals(celdaFinal,0.25f)) {
			fotnExito.draw(sb, "¡¡¡HAS PERDIDO!!!", camara.position.x - camara.viewportWidth / 2 + 100, camara.position.y - camara.viewportHeight / 2 + 30);
		}
		// si el jugador ha perdido todas las vidas, se muestra un mensaje de fracaso
		if (!vivo) {
			fotnExito.draw(sb, "¡¡¡FIN DE JUEGO!!!", camara.position.x - camara.viewportWidth / 2 + 100, camara.position.y - camara.viewportHeight / 2 + 30);
			//meto en un float el estado del tiempo
		}
		// aviso de la vida extra
		if (vidaExtra) {
			fotnExito.draw(sb, "¡¡¡VIDA EXTRA!!!", camara.position.x - camara.viewportWidth / 2 + 100, camara.position.y - camara.viewportHeight / 2 + 30);
		}
		sb.end();
	}
	
	@Override
	public void dispose () {
		// Liberamos los recursos utilizados por el juego
		// Mapa
		mapa.dispose();
		// Imagen del jugador principal
		imagenPrincipal.dispose();
		// Imágenes de los NPC
		imgNPC[0].dispose();
		imgNPC[1].dispose();
		imgNPC[2].dispose();
		imgNPC[3].dispose();
		imgNPC[4].dispose();
		// Sonidos
		musicaJuego.dispose();
		tesoroEncontrado.dispose();
		pillado.dispose();
		exito.dispose();
		fracaso.dispose();
		pasos.dispose();
		traslador.dispose();
		run.dispose();
		lento.dispose();
		//Elementos para sobreimpresionar informacion
		fotnExito.dispose();
		fontTesoros.dispose();
		corazonTexture.dispose();
		//SpriteBatch
		if (sb.isDrawing())
			sb.dispose();
	}
	//Método que traduce una celda del mapa a una posición
	private Vector2 posicionaMapa(Vector2 celda) {
		Vector2 res = new Vector2();
		if (celda.x + 1 > anchoTiles ||
				celda.y + 1 > altoTiles) {  //Si la peticion esta mal, situamos en el origen del mapa
			res.set(0, 0);
		}
		res.x = celda.x * anchoCelda;
		res.y = (altoTiles - 1 - celda.y) * altoCelda;
		return res;
	}
	//Método que actualiza la posición del jugador principal
	private void actualizaPosicionJugador() {
		//Guardamos la posicion del jugador por si encontramos algun obstaculo
		Vector2 posicionAnterior = new Vector2();
		posicionAnterior.set(posicionJugador);
		//Deteccion de tesoros: calculamos la celda en la que se encuentran los límites de la zona de contacto.
		int limIzq = (int) ((posicionJugador.x + 0.25 * anchoJugador) / anchoCelda);
		int limDrcha = (int) ((posicionJugador.x + 0.75 * anchoJugador) / anchoCelda);
		int limSup = (int) ((posicionJugador.y + 0.25 * altoJugador) / altoCelda);
		int limInf = (int) ((posicionJugador.y) / altoCelda);
		//Los booleanos izquierda, derecha, arriba y abajo recogen la dirección del personaje,
		//para permitir direcciones oblícuas no deben ser excluyentes.
		//Pero sí debemos excluir la simultaneidad entre arriba/abajo e izquierda/derecha
		//para no tener direcciones contradictorias
		if (izquierda) {
			posicionJugador.x -= velocidadJugador;
			jugador = jugadorIzquierda;
		}
		if (derecha) {
			posicionJugador.x += velocidadJugador;
			jugador = jugadorDerecha;
		}
		if (arriba) {
			posicionJugador.y += velocidadJugador;
			jugador = jugadorArriba;
		}
		if (abajo) {
			posicionJugador.y -= velocidadJugador;
			jugador = jugadorAbajo;
		}
		//Avanzamos el stateTime del jugador principal cuando hay algún estado de movimiento activo
		if (izquierda || derecha || arriba || abajo) {
			stateTime += Gdx.graphics.getDeltaTime();
			cycle = (int) (stateTime / 0.5f);
			if (cycle != cycle_ant)
				pasos.play(1f);
			cycle_ant = cycle;
		}
		//Limites en el mapa para el jugador
		posicionJugador.x = MathUtils.clamp(posicionJugador.x, 0, anchoMapa - anchoJugador);
		posicionJugador.y = MathUtils.clamp(posicionJugador.y, 0, altoMapa - altoJugador);

		//Detección de obstáculos///////////////////////////////////////////////////
		if (obstaculo(posicionJugador))
			posicionJugador.set(posicionAnterior);
		//Detección de teletransporte///////////////////////////////////////////////////
		if (celdaActual(posicionJugador).epsilonEquals(celdaTraslador,0.25f)) {
			posicionJugador = posicionaMapa(celdaDestino);
			traslador.play(1f);
		}
		//Detección de tesoros///////////////////////////////////////////////////
		//Límite inferior izquierdo
		if (tesoro[limIzq][limInf]) {
			TiledMapTileLayer.Cell celda = capaTesoros.getCell(limIzq, limInf);
			celda.setTile(null);
			tesoro[limIzq][limInf] = false;
			tesoroencontrado();
		} //Límite superior derecho
		else if (tesoro[limDrcha][limSup]) {
			TiledMapTileLayer.Cell celda = capaTesoros.getCell(limDrcha, limSup);
			celda.setTile(null);
			tesoro[limDrcha][limSup] = false;
			tesoroencontrado();
		}
		//Deteccion de fin del mapa///////////////////////////////////////////////////
		if (celdaActual(posicionJugador).epsilonEquals(celdaFinal,0.25f)&&!finalMapa) {
			//Paralizamos el juego 1 segundo para reproducir algún efecto sonoro
			try {
				//Pausamos la música
				musicaJuego.pause();
				//Reproducimos el sonido de éxito
				exito.play();
				Thread.sleep(3000);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//reiniciarJuego();
			velocidadJugador = 0.0f;
			velocidadNPC = 0.0f;
			finalMapa = true;
		}
	}
	// Método que controla las acciones cuando encuentra tesoro
	private void tesoroencontrado() {
		tesoroEncontrado.play();
		cuentaTesoros++;
		// Defino la nueva posición de inicio después de "morir" cada dos tesoros
		if (cuentaTesoros % 2 == 0) {
			celdaInicial = celdaActual(posicionJugador);
		}
		// si llega a 4 tesoros subo la velocidad del jugador
		if (cuentaTesoros == 4) {
			velocidadJugador = 1.75f;
			run.play();
		}
		// si llega a 6 tesoros vuelvo a la velocidad inicial
		if (cuentaTesoros == 6) {
			velocidadJugador = 0.75f;
			lento.play();
		}
		// si llega a 7 tesoros, cambio estado de vida extra
		if (cuentaTesoros == 7) {
			vidaExtra = true;
			nVidas++;
			// capturo el momento en lapso y le añado 5 segundos para mostrar el mensaje de vida extra
			lapso = stateTimeNPC + 5;
		}
	}
	//Método que actualiza la posición de los NPC
	private void actualizaPosicionNPC(int i) {

		if (posicionNPC[i].y < destino[i].y) {
			posicionNPC[i].y += velocidadNPC;
			npc[i] = npcArriba[i];
		}
		if (posicionNPC[i].y > destino[i].y) {
			posicionNPC[i].y -= velocidadNPC;
			npc[i] = npcAbajo[i];
		}
		if (posicionNPC[i].x < destino[i].x) {
			posicionNPC[i].x += velocidadNPC;
			npc[i] = npcDerecha[i];
		}
		if (posicionNPC[i].x > destino[i].x) {
			posicionNPC[i].x -= velocidadNPC;
			npc[i] = npcIzquierda[i];
		}
		posicionNPC[i].x = MathUtils.clamp(posicionNPC[i].x, 0, anchoMapa - anchoJugador);
		posicionNPC[i].y = MathUtils.clamp(posicionNPC[i].y, 0, altoMapa - altoJugador);
		//Dar la vuelta al NPC cuando llega a un extremo
		if (posicionNPC[i].epsilonEquals(destino[i], 0.50f)) {
			destino[i].set(origen[i]);
			origen[i].set(posicionNPC[i]);
		}
	}
	//Metodo que detecta si hay un obstaculo en una determinada posicion
	private boolean obstaculo(Vector2 posicion) {
		int limIzq = (int) ((posicion.x + 0.25 * anchoJugador) / anchoCelda);
		int limDrcha = (int) ((posicion.x + 0.75 * anchoJugador) / anchoCelda);
		int limSup = (int) ((posicion.y + 0.25 * altoJugador) / altoCelda);
		int limInf = (int) ((posicion.y) / altoCelda);

		return obstaculo[limIzq][limInf] || obstaculo[limDrcha][limSup];
	}
	//Método que detecta si se producen colisiones usando rectángulos
	private void detectaColisiones() {
		//Vamos a comprobar que el rectángulo de contacto del jugador
		//no se solape con el rectángulo de contacto del npc
		Rectangle rJugador = new Rectangle((float) (posicionJugador.x + 0.25 * anchoJugador), (float) (posicionJugador.y + 0.25 * altoJugador),
				(float) (0.5 * anchoJugador), (float) (0.5 * altoJugador));
		Rectangle rNPC;
		//Ahora recorremos el array de NPC, para cada uno generamos su rectángulo de contacto
		for (int i = 0; i < numeroNPC; i++) {
			rNPC = new Rectangle((float) (posicionNPC[i].x + 0.1 * anchoJugador), (float) (posicionNPC[i].y + 0.1 * altoJugador),
					(float) (0.8 * anchoJugador), (float) (0.8 * altoJugador));
			//Si hay colision
			if (rJugador.overlaps(rNPC)) {
				// si el nº de vidas es 3
				if (nVidas == 3) {
					//Código de fin de partida
					//pausamos la musica, guardando el instante actual de reproducción
					float posicionMusica = musicaJuego.getPosition();
					musicaJuego.pause();
					//quitamos una vida al jugador
					nVidas--;
					//reproducimos el sonido "pillado"
					pillado.play();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					//reiniciamos la música
					musicaJuego.setPosition(posicionMusica);
					musicaJuego.play();
					posicionJugador.set(posicionaMapa(celdaInicial));
					return; //Acabamos el bucle si hay una sola colisión
				}else if (nVidas == 2) {
					//Código de fin de partida
					//pausamos la musica, guardando el instante actual de reproducción
					float posicionMusica = musicaJuego.getPosition();
					musicaJuego.pause();
					//quitamos una vida al jugador
					nVidas--;
					//reproducimos el sonido "pillado"
					pillado.play();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//reiniciamos la música
					musicaJuego.setPosition(posicionMusica);
					musicaJuego.play();
					posicionJugador.set(posicionaMapa(celdaInicial));
					return; //Acabamos el bucle si hay una sola colisión
				}else if (nVidas == 1) {
					//Código de fin de partida
					//pausamos la musica, guardando el instante actual de reproducción
					float posicionMusica = musicaJuego.getPosition();
					musicaJuego.pause();
					//quitamos una vida al jugador
					nVidas--;
					//reproducimos el sonido "pillado"
					fracaso.play();
					vivo = false;
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//paro a todos los jugadores
					velocidadJugador = 0;
					velocidadNPC = 0;
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//reiniciarJuego();

					return; //Acabamos el bucle si hay una sola colisión
				}
			}
		}//Si no hay colisión no se hace nada
	}
	//Método que devuelve la celda actual en la que se encuentra un objeto
	private Vector2 celdaActual(Vector2 posicion) {
		return new Vector2((int) (posicion.x / anchoCelda), (altoTiles - 1 - (int) (posicion.y / altoCelda)));
	}
	//Con estos setters se impide la situacion de direcciones contradictorias pero no las
	//direcciones compuestas que permiten movimientos oblícuos
	private void setIzquierda(boolean izq) {
		if (derecha && izq) derecha = false;
		izquierda = izq;
	}
	private void setDerecha(boolean der) {
		if (izquierda && der) izquierda = false;
		derecha = der;
	}
	private void setArriba(boolean arr) {
		if (abajo && arr) abajo = false;
		arriba = arr;
	}
	private void setAbajo(boolean abj) {
		if (arriba && abj) arriba = false;
		abajo = abj;
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
			case Input.Keys.LEFT:
				setIzquierda(true);
				break;
			case Input.Keys.RIGHT:
				setDerecha(true);
				break;
			case Input.Keys.UP:
				setArriba(true);
				break;
			case Input.Keys.DOWN:
				setAbajo(true);
				break;
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
			case Input.Keys.LEFT:
				setIzquierda(false);
				break;
			case Input.Keys.RIGHT:
				setDerecha(false);
				break;
			case Input.Keys.UP:
				setArriba(false);
				break;
			case Input.Keys.DOWN:
				setAbajo(false);
				break;
		}
		//Para ocultar/mostrar las distintas capas pulsamos desde el 1 en adelante...
		int codigoCapa = keycode - Input.Keys.NUM_1;
		if (codigoCapa <= 4)
			mapa.getLayers().get(codigoCapa).setVisible(!mapa.getLayers().get(codigoCapa).isVisible());

		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector3 clickCoordinates = new Vector3(screenX, screenY, 0f);
		//Transformamos las coordenadas del vector a coordenadas de nuestra camara
		Vector3 pulsacion3d = camara.unproject(clickCoordinates);
		Vector2 pulsacion = new Vector2(pulsacion3d.x, pulsacion3d.y);
		//Calculamos la diferencia entre la pulsacion y el centro del jugador
		Vector2 centroJugador = new Vector2(posicionJugador).add((float) anchoJugador / 2, (float) altoJugador / 2);
		Vector2 diferencia = new Vector2(pulsacion.sub(centroJugador));
		//Vamos a determinar la intencion del usuario para mover al personaje en funcion del
		//angulo entre la pulsacion y la posicion del jugador
		float angulo = diferencia.angleDeg();

		if (angulo > 30 && angulo <= 150) setArriba(true);
		if (angulo > 120 && angulo <= 240) setIzquierda(true);
		if (angulo > 210 && angulo <= 330) setAbajo(true);
		if ((angulo > 0 && angulo <= 60) || (angulo > 300 && angulo < 360)) setDerecha(true);

		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		setArriba(false);
		setAbajo(false);
		setIzquierda(false);
		setDerecha(false);

		return true;
	}
	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		return false;
	}
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		//mismo caso que touchDown
		touchDown(screenX,screenY,pointer,0);
		return true;
	}
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}
	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
	//Método que reinicia el juego
	private void reiniciarJuego() {
		//Reiniciamos las variables del juego
		nVidas = 3;
		cuentaTesoros = 0;
		capaTesoros = (TiledMapTileLayer) mapa.getLayers().get(3);
		stateTime = 0.75f;
		stateTimeNPC = 0.75f;
		celdaInicial = new Vector2(0, 10);
		posicionJugador = posicionaMapa(celdaInicial);

	}
}