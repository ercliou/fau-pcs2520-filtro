package fitro;

import guicomponents.GAlign;
import guicomponents.GButton;
import guicomponents.GCScheme;
import guicomponents.GComponent;
import guicomponents.GFont;
import guicomponents.GLabel;

import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics2D;
import processing.core.PImage;

public class MaskSetupScreen extends PApplet {

	private static final long serialVersionUID = 4984442284518242701L;

	private PImage originalImg;
	private PImage img;
	private PImage filtered;
	private PFont font;

	GLabel labelMatrixSize;
	// GTextField matrixW, matrixH;
	GButton btnClearMatrix, blurFilter, negativeFilter, edgesFilter, sharpenFilter, blackAndWhiteFilter, thresholdFilter, embossFilter, dilateFilter,
			erosionFilter, browseButton, saveButton, bitsFilter, multiFocalFilter, smudgeFilter, brightFilter, saturationFilter;
	
	PGraphics2D setupScreen = new PGraphics2D();
	PGraphics2D imageScreen = new PGraphics2D();

	static final int MAX_W = 11;
	static final int MAX_H = 11;
	static final int MAX_IMG_W = 575;
	static final int MAX_IMG_H = 430;
	static final int WINDOW_W = 950;
	static final int WINDOW_H = 650;
	static final int WINDOW_MARGIN = 20;
	
	static final String BLUR_TITLE = "BLUR";
	static final String SHARPEN_TITLE = "SHARPEN";
	static final String BRIGHTNESS_TITLE = "BRIGHTNESS";
	static final String EDGES_TITLE = "EDGE DETECTION";
	static final String EMBOSS_TITLE = "EMBOSS";
	static final String BLACKWHITE_TITLE = "BLACK & WHITE";
	static final String NEGATIVE_TITLE = "NEGATIVE";
	static final String SATURATION_TITLE = "SATURATION";
	static final String SMUDGE_TITLE = "SMUDGE";
	static final String THRESHOLD_TITLE = "THRESHOLD";
	static final String DISPLAY_TITLE = "PROCESSAMENTO DE IMAGEM";
	static final String COLORS_TITLE = "64 COLORS";
	static final String EROSION_TITLE = "EROSION";
	static final String DILATION_TITLE = "DILATION";
	static final String MULTIFOCAL_TITLE = "MULTI FOCAL";

	static final String BLUR_DESC = "Suavização da imagem é feita com a média dos valores dos pixels. \nA matriz de Convolução representa uma média aritmética dos\n pixels ao redor do pixel que será calculado.";
	static final String SHARPEN_DESC = "Aumenta nitidez da imagem destacando as bordas.\nA matriz de Convolução é similar a de detecção de bordas reforçando o pixel que será calculado.\n";
	static final String EMBOSS_DESC = "Cria relevo na imagem\n Um lado do pixel causa influência positiva e o outro negativa";
	static final String EDGES_DESC = "Detecção das bordas.\nA matriz de convolução calcula a diferença entre pixel central e os ao redor dele.";
	static final String BLACKWHITE_DESC = "Muda cores da imagem para escala de cinza";
	static final String NEGATIVE_DESC = "Muda as cores para suas complementares";
	static final String THRESHOLD_DESC = "Se o pixel tem valor maior que um ponto pré determinado\nmuda seu valor para branco, caso contrário para preto";
	static final String DILATION_DESC = "Utiliza o maior valor de cada subpixel(RGB)\n de uma área ao redor do pixel que será calculado\nOs valores da matriz apenas representam a área que\ndelimita os pixels utilizados no cálculo da cor de cada pixel";
	static final String EROSION_DESC = "Utiliza o menor valor de cada subpixel(RGB)\n de uma área ao redor do pixel que será calculado\nOs valores da matriz apenas representam a área que\ndelimita os pixels utilizados no cálculo da cor de cada pixel";
	static final String DISPLAY_TEXT = "Operações pontuais: utilizam apenas o próprio pixel para cálculo do seu novo valor\n" +
			"							Filtros: utilizam os pixels em torno do primeiro para calculo do seu novo valor";
	static final String COLORS_DESC = "Deixa a imagem com até 64 cores\nDefinindo a cor de cada pixel para o valor válido mais próximo.";
	static final String BRIGHTNESS_DESC = "Aumenta brilho da imagem.\nBrilho (HSB) de cada pixel é aumentado em 50%.";
	static final String SATURATION_DESC = "Aumenta saturação da imagem. \nSaturação (HSB) de cada pixel é aumentado em 50%. ";
	static final String SMUDGE_DESC = "Deslocamento da imagem na diagonal.\nA imagem é borrada diagonalmente com sobreposições da imagem original.";
	static final String MULTIFOCAL_DESC = "A matriz de Convolução representa a sobreposição de 4 imagens do original.";
	
	static final String TITULO = "TITULO";
	
	int maskWidth = MAX_W;
	int maskHeight = MAX_H;
	int maskXoffset = 0;
	int maskYoffset = 0;

	int maskCenterX = MAX_W / 2;
	int maskCenterY = MAX_H / 2;

	int matrixOffsetX = 40;
	int matrixOffsetY = 320;

	boolean refreshFrame = true;
	boolean toggledFilter = false;

	int cellSize = 25;
	int clickModifier = 1;

	private int[][] mask = new int[MAX_W][MAX_H];
	private boolean isKeyPressed = false;
	private boolean changeMaskCenter = false;
	
	private boolean isNegative = false;
	private boolean isThreshold = false;
	private boolean isWhiteBlack = false;
	private boolean isBits = false;
	private boolean isBright = false;
	private boolean isSaturate = false;
	
	private boolean isDilation = false;
	private boolean isErosion = false;

	private int threshold = 127;

	public static void main(String args[]){
		PApplet.main(new String[] { fitro.MaskSetupScreen.class.getName() });
    }
	
	@Override
	public void setup() {
		GComponent.globalColor = GCScheme.getColor(this, GCScheme.YELLOW_SCHEME);
		GComponent.globalFont = GFont.getFont(this, "Arial", 12);

		blurFilter = new GButton(this, "Blur", 30, 215, 90, 25);
		blurFilter.setTextAlign(GAlign.CENTER | GAlign.MIDDLE);
		sharpenFilter = new GButton(this, "Sharpen", 230, 215, 90, 25);
		sharpenFilter.setTextAlign(GAlign.CENTER | GAlign.MIDDLE);
		embossFilter = new GButton(this, "Emboss", 30, 245, 90, 25);
		embossFilter.setTextAlign(GAlign.CENTER | GAlign.MIDDLE);
		edgesFilter = new GButton(this, "Edges", 130, 215, 90, 25);
		edgesFilter.setTextAlign(GAlign.CENTER | GAlign.MIDDLE);
		smudgeFilter = new GButton(this, "Smudge", 130, 245, 90, 25);
		smudgeFilter.setTextAlign(GAlign.CENTER | GAlign.MIDDLE);
		multiFocalFilter = new GButton(this, "Multi-Focal", 230, 245, 90, 25);
		multiFocalFilter.setTextAlign(GAlign.CENTER | GAlign.MIDDLE);
		
		negativeFilter = new GButton(this, "Negative", 230, 45, 90, 25);
		negativeFilter.setTextAlign(GAlign.CENTER | GAlign.MIDDLE);
		blackAndWhiteFilter = new GButton(this, "Black & White", 30, 45, 90, 25);
		blackAndWhiteFilter.setTextAlign(GAlign.CENTER | GAlign.MIDDLE);
		thresholdFilter = new GButton(this, "Threshold", 130, 45, 90, 25);
		thresholdFilter.setTextAlign(GAlign.CENTER | GAlign.MIDDLE);
		bitsFilter = new GButton(this, "64 Colors", 30, 75, 90, 25);
		bitsFilter.setTextAlign(GAlign.CENTER | GAlign.MIDDLE);
		brightFilter = new GButton(this, "Brightness", 130, 75, 90, 25);
		brightFilter.setTextAlign(GAlign.CENTER | GAlign.MIDDLE);
		saturationFilter = new GButton(this, "Saturation", 230, 75, 90, 25);
		saturationFilter.setTextAlign(GAlign.CENTER | GAlign.MIDDLE);
		
		dilateFilter = new GButton(this, "Dilation", 130, 145, 90, 25);
		dilateFilter.setTextAlign(GAlign.CENTER | GAlign.MIDDLE);
		erosionFilter = new GButton(this, "Erosion", 30, 145, 90, 25);
		erosionFilter.setTextAlign(GAlign.CENTER | GAlign.MIDDLE);

		btnClearMatrix = new GButton(this, "Clear", 40, 603, 90, 25);
		btnClearMatrix.setTextAlign(GAlign.CENTER | GAlign.MIDDLE);
		browseButton = new GButton(this, "Browse...", 740,605, 90, 25);
		browseButton.setTextAlign(GAlign.CENTER | GAlign.MIDDLE);
		saveButton = new GButton(this, "Save", 840, 605, 90, 25);
		saveButton.setTextAlign(GAlign.CENTER | GAlign.MIDDLE);

//		size(WINDOW_W, WINDOW_H);
		size(950,650);
		font = loadFont("../ArialMT-16.vlw");

		textAlign(CENTER);
		textFont(font, 16);
		textLeading(15);
		// img = loadImage("/Users/maryliagutierrez/Downloads/file.jpg");
		img = loadImage("../viena.jpg");
		try {
			originalImg = (PImage) img.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		this.resizeImage(img);
		initializeMatrixCenter();

	}

	@Override
	public void draw() {
		if (refreshFrame) {
			this.initializeLayout();
			calculateMatrixSize(mask, MAX_W, MAX_H);
			filtered = applyConvolution(this.normalizeMatrix(mask, MAX_W, MAX_H), maskWidth, maskHeight, img);
			this.drawImage(filtered);
			this.drawMatrix(mask, MAX_W, MAX_H);
			refreshFrame = false;
		}
		handleButtonEvents();
	}

	private void initializeLayout() {
		background(255);
		fill(255);
		noStroke();
		fill(0xAA, 0xD5, 0xFF);
		rect(WINDOW_MARGIN, 190, 315, 90);
		rect(WINDOW_MARGIN, WINDOW_MARGIN, 315, 90);
		rect(WINDOW_MARGIN, 120, 315, 60);
		stroke(0xAA, 0xD5, 0xFF);
		strokeWeight(2f);
		noFill();
		rect(WINDOW_MARGIN,190,315,445);
		strokeWeight(1f);
		
		fill(255);
		textAlign(CENTER);
		text("Filtros Lineares (Convolução)", 175, 208);
		text("Operações Pontuais", 175, 38);
		text("Filtros Não Lineares", 175, 138);
		fill(0);
		text("Matriz de Convolução", 175, 305);
	}

	private void drawImage(PImage image) {
		noStroke();
		fill(63);
		rect(355, 165, MAX_IMG_W, MAX_IMG_H);
		image(filtered, 355 + (MAX_IMG_W - image.width) / 2, 165 + (MAX_IMG_H - image.height) / 2, image.width, image.height);
	}

	public PImage applyConvolution(float[][] mask, int maskWidth, int maskHeight, PImage img) {

		PImage resultImg = createImage(img.width, img.height, RGB);
		resultImg.loadPixels();

		for (int j = maskCenterY - maskYoffset; j < img.height + maskCenterY - maskYoffset - maskHeight + 1; j++) {
			for (int i = maskCenterX - maskXoffset; i < img.width + maskCenterX - maskXoffset - maskWidth + 1; i++) {
				resultImg.pixels[j * img.width + i] = calculateFilteredPixel(img, mask, i, j, maskWidth, maskHeight, maskCenterX, maskCenterY);
			}
		}
		return resultImg;
	}

	private int calculateFilteredPixel(PImage img, float[][] mask, int x, int y, int maskWidth, int maskHeight, int maskCenterWidth, int maskCenterHeight) {

		int[] original = img.pixels;

		int heightOffset = y - (maskCenterHeight - maskYoffset);
		int widthOffset = x - (maskCenterWidth - maskXoffset);

		float rtotal = 0;
		float gtotal = 0;
		float btotal = 0;

		if (isDilation || isErosion) {
			return dilationCalculation(img, mask, maskWidth, maskHeight, heightOffset, widthOffset);
		}
		
		for (int j = 0; j < maskHeight; j++) {
			for (int i = 0; i < maskWidth; i++) {
				rtotal += red(original[(heightOffset + j) * img.width + (widthOffset + i)]) * mask[i + maskXoffset][j + maskYoffset];
				gtotal += green(original[(heightOffset + j) * img.width + (widthOffset + i)]) * mask[i + maskXoffset][j + maskYoffset];
				btotal += blue(original[(heightOffset + j) * img.width + (widthOffset + i)]) * mask[i + maskXoffset][j + maskYoffset];
			}
		}

		/**** Point Operations ****/
		if (isNegative) {
			rtotal = 255 - rtotal;
			gtotal = 255 - gtotal;
			btotal = 255 - btotal;
		}

		if (isThreshold) {
			if(brightness(color((int) rtotal, (int) gtotal, (int) btotal))> this.threshold){
				rtotal = 255;
				gtotal = 255;
				btotal = 255;
			} else {
				rtotal = 0;
				gtotal = 0;
				btotal = 0;
			}
		}

		if (isWhiteBlack) {
			int gray = color((int) (rtotal * 0.2125 + gtotal * 0.7154 + btotal * 0.072));
			rtotal = red(gray);
			gtotal = green(gray);
			btotal = blue(gray);
		}
		
		if(isBits){
			int value = 64;
			rtotal = (((int) rtotal)/value)*value;
			gtotal = (((int) gtotal)/value)*value;
			btotal = (((int) btotal)/value)*value;
		}
		if(isBright){
			int clr = color((int)rtotal, (int)gtotal, (int)btotal );
			colorMode(HSB);
			float brt = brightness(clr)*1.5f;
			clr = color(hue(clr),saturation(clr),brt<255?brt:255);
			colorMode(RGB);
			rtotal = red(clr);
			gtotal = green(clr);
			btotal = blue(clr);
		}
		if(isSaturate){
			int clr = color((int)rtotal, (int)gtotal, (int)btotal );
			colorMode(HSB);
			float str = saturation(clr)*1.5f;
			clr = color(hue(clr),str<255?str:255,brightness(clr));
			colorMode(RGB);
			rtotal = red(clr);
			gtotal = green(clr);
			btotal = blue(clr);
		}

		return color((int) rtotal, (int) gtotal, (int) btotal);
	}

	private int dilationCalculation(PImage img, float[][] mask, int maskWidth, int maskHeight, int heightOffset, int widthOffset) {

		int[] original = img.pixels;

		float rFinal, gFinal, bFinal;
		float rAux, gAux, bAux;

		if (isErosion) {
			rFinal = gFinal = bFinal = 255;
		} else { // isDilation
			rFinal = gFinal = bFinal = 0;
		}

		for (int j = 0; j < maskHeight; j++) {
			for (int i = 0; i < maskWidth; i++) {
				if (mask[i][j] > 0) {
					rAux = red(original[(heightOffset + j) * img.width + (widthOffset + i)]) + mask[i][j];
					gAux = green(original[(heightOffset + j) * img.width + (widthOffset + i)]) + mask[i][j];
					bAux = blue(original[(heightOffset + j) * img.width + (widthOffset + i)]) + mask[i][j];
					if (isErosion) {
						rFinal = rFinal > rAux ? rAux : rFinal;
						gFinal = gFinal > gAux ? gAux : gFinal;
						bFinal = bFinal > bAux ? bAux : bFinal;
					} else { // isDilation
						rFinal = rFinal < rAux ? rAux : rFinal;
						gFinal = gFinal < gAux ? gAux : gFinal;
						bFinal = bFinal < bAux ? bAux : bFinal;
					}
				}
			}
		}

		return color((int) rFinal, (int) gFinal, (int) bFinal);
	}

	private void drawMatrix(int[][] matrix, int w, int h) {
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				stroke(191);
				fill(255);
				rect(matrixOffsetX + i * cellSize, matrixOffsetY + j * cellSize, cellSize, cellSize);
				fill(191);
				textAlign(CENTER);
				text((int) matrix[i][j], matrixOffsetX + i * cellSize + cellSize / 2, matrixOffsetY + j * cellSize + cellSize / 2 + 7);
			}
		}

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				if (isActiveCell(matrix, w, h, i, j, maskCenterX, maskCenterY)) {
					stroke(0);
					if (i == maskCenterX && j == maskCenterY) {
						fill(240, 240, 60);
					} else if (matrix[i][j] == 0) {
						fill(255);
					} else {
						fill(168, 244, 149);
					}
					rect(matrixOffsetX + i * cellSize, matrixOffsetY + j * cellSize, cellSize, cellSize);
					fill(0);
					text((int) matrix[i][j], matrixOffsetX + i * cellSize + cellSize / 2, matrixOffsetY + j * cellSize + cellSize / 2 + 7);
				}
			}
		}
	}

	private float[][] normalizeMatrix(int[][] matrix, int w, int h) {
		int total = 0;
		float[][] normalized = new float[w][h];

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				total += matrix[i][j];
			}
		}

		if (total == 0) {
			total = 1;
		}

		if (isDilation || isErosion) { // nao normaliza
			total = 1;
		}

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				normalized[i][j] = ((float) matrix[i][j]) / total;
			}
		}
		return normalized;
	}

	public void mouseReleased() {
		if (mouseInsideMatrix()) {
			refreshFrame = true;
			if (changeMaskCenter) {
				maskCenterX = (mouseX - matrixOffsetX) / cellSize;
				maskCenterY = (mouseY - matrixOffsetY) / cellSize;
			} else {

				if (mouseButton == 37) {
					// left click
					mask[(mouseX - matrixOffsetX) / cellSize][(mouseY - matrixOffsetY) / cellSize] += clickModifier;
				} else if (mouseButton == 39) {
					mask[(mouseX - matrixOffsetX) / cellSize][(mouseY - matrixOffsetY) / cellSize] -= clickModifier;
				}
			}
		}

		toggledFilter = false;
	}

	private boolean mouseInsideMatrix() {
		return mouseX < matrixOffsetX + cellSize * MAX_W && mouseX > matrixOffsetX && mouseY < matrixOffsetY + cellSize * MAX_H && mouseY > matrixOffsetY;
	}

	private void handleButtonEvents() {
		
		isOver();
		
		if(anyButtonButNonLinearPressed()){
			isErosion = isDilation = false;
			erosionFilter.setColorScheme(GCScheme.YELLOW_SCHEME);
			dilateFilter.setColorScheme(GCScheme.YELLOW_SCHEME);
		}
		
		if (!toggledFilter) {
			
			// Controle
			if (browseButton.eventType == GButton.PRESSED) {
				PImage oldImage = img;
				img = pickImage();
				if(img != null) {
					try {
						originalImg = (PImage) img.clone();
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
					this.resizeImage(img);
				} else {
					img = oldImage;
				}
				return;
			}

			if (saveButton.eventType == GButton.PRESSED) {
				exportImage();
				return;
			}
			if (btnClearMatrix.eventType == GButton.PRESSED) {
				clearMatrix();
				initializeMatrixCenter();
				refreshFrame = true;
				return;

			}

			// Filtros Lineares
			if (blurFilter.eventType == GButton.PRESSED) {
				clearMatrix();
				maskWidth = 5;
				maskHeight = 5;
				maskCenterX = 5;
				maskCenterY = 5;
				for (int i = 3; i < 3 + maskWidth; i++) {
					for (int j = 3; j < 3 + maskHeight; j++) {
						mask[i][j] = 1;
						if (i == 2 || j == 2) {
							mask[i][j] = 5;
						} else if (i == j || (j == (4 - i))) {
							mask[i][j] = 3;
						}
					}
				}
				refreshFrame = true;
				return;
			}
			if (edgesFilter.eventType == GButton.PRESSED) {
				clearMatrix();
				maskWidth = 3;
				maskHeight = 3;
				maskCenterX = 5;
				maskCenterY = 5;
				for (int i = 4; i < 4+maskWidth; i++) {
					for (int j = 4; j < 4+maskHeight; j++) {
						if (i != 5 || j != 5)
							mask[i][j] = -1;
					}
				}
				mask[5][5] = 8;
				refreshFrame = true;
				return;
			}
			if (multiFocalFilter.eventType == GButton.PRESSED) {
				clearMatrix();
				maskWidth = 11;
				maskHeight = 11;
				maskCenterX = 5;
				maskCenterY = 5;
				mask[0][0]=mask[0][10]=mask[10][0]=mask[10][10] = -1;
				refreshFrame = true;
				return;
			}
			if(smudgeFilter.eventType == GButton.PRESSED){
				clearMatrix();
				maskWidth = 11;
				maskHeight = 11;
				maskCenterX = 5;
				maskCenterY = 5;
				for (int i = 0; i < maskWidth; i++) {
					mask[i][i] = 1;
				}
				refreshFrame = true;
				return;
			}
			if (sharpenFilter.eventType == GButton.PRESSED) {
				clearMatrix();
				maskWidth = 3;
				maskHeight = 3;
				maskCenterX = 5;
				maskCenterY = 5;
				mask[4][5] = mask[5][4] = mask[5][6] = mask[6][5] = -1;
				mask[5][5] = 5;
				refreshFrame = true;
				return;
			}
			if (embossFilter.eventType == GButton.PRESSED) {
				clearMatrix();
				maskWidth = 3;
				maskHeight = 3;
				maskCenterX = 1 + 4;
				maskCenterY = 1 + 4;
				mask[0 + 4][0 + 4] = -2;
				mask[0 + 4][1 + 4] = mask[1+4][0+4] = -1;
				mask[0 + 4][2 + 4] = mask[2+4][0+4] = 0;
				mask[1 + 4][1 + 4] = mask[1+4][2+4] = mask[2+4][1+4] = 1;
				mask[2 + 4][2 + 4] = 2;
				refreshFrame = true;
				return;
			}
			
			// operacao pontual
			if (negativeFilter.eventType == GButton.PRESSED) {
				if (isNegative == true) {
					isNegative = false;
					negativeFilter.setColorScheme(GCScheme.YELLOW_SCHEME);
				} else {
					isNegative = true;
					negativeFilter.setColorScheme(GCScheme.RED_SCHEME);
				}
				refreshFrame = true;
				toggledFilter = true;
				return;
			}
			if (bitsFilter.eventType == GButton.PRESSED) {
				if (isBits == true) {
					isBits = false;
					bitsFilter.setColorScheme(GCScheme.YELLOW_SCHEME);
				} else {
					isBits = true;
					bitsFilter.setColorScheme(GCScheme.RED_SCHEME);
				}
				refreshFrame = true;
				toggledFilter = true;
				return;
			}
			if (blackAndWhiteFilter.eventType == GButton.PRESSED) {
				if (isWhiteBlack == true) {
					isWhiteBlack = false;
					blackAndWhiteFilter.setColorScheme(GCScheme.YELLOW_SCHEME);
				} else {
					isWhiteBlack = true;
					blackAndWhiteFilter.setColorScheme(GCScheme.RED_SCHEME);
				}
				refreshFrame = true;
				toggledFilter = true;
				return;
			}
			if (thresholdFilter.eventType == GButton.PRESSED) {
				if (isThreshold == true) {
					isThreshold = false;
					thresholdFilter.setColorScheme(GCScheme.YELLOW_SCHEME);
				} else {
					isThreshold = true;
					thresholdFilter.setColorScheme(GCScheme.RED_SCHEME);
				}
				refreshFrame = true;
				toggledFilter = true;
				return;
			} 
			
			if (saturationFilter.eventType == GButton.PRESSED) {
				if (isSaturate == true) {
					isSaturate = false;
					saturationFilter.setColorScheme(GCScheme.YELLOW_SCHEME);
				} else {
					isSaturate = true;
					saturationFilter.setColorScheme(GCScheme.RED_SCHEME);
				}
				refreshFrame = true;
				toggledFilter = true;
				return;
			} 
			if (brightFilter.eventType == GButton.PRESSED) {
				if (isBright == true) {
					isBright = false;
					brightFilter.setColorScheme(GCScheme.YELLOW_SCHEME);
				} else {
					isBright = true;
					brightFilter.setColorScheme(GCScheme.RED_SCHEME);
				}
				refreshFrame = true;
				toggledFilter = true;
				return;
			} 

			// Filtros não lineares
			if (dilateFilter.eventType == GButton.PRESSED) { 
				
				maskWidth = 11;
				maskHeight = 11;
				maskCenterX = 5;
				maskCenterY = 5;

				clearMatrix();
				for (int i = 0; i < maskWidth; i++) {
					for (int j = 0; j < maskHeight; j++) {
						if (((i - 5) * (i - 5) + (j - 5) * (j - 5)) < 16)
							mask[i][j] = 1;
					}
				}
				mask[5][5] = 2;
				if (isDilation == true) {
					clearMatrix();
					initializeMatrixCenter();
					isDilation = false;
					dilateFilter.setColorScheme(GCScheme.YELLOW_SCHEME);
				} else {
					clearToggles();
					isDilation = true;
					dilateFilter.setColorScheme(GCScheme.RED_SCHEME);
				}
				refreshFrame = true;
				toggledFilter = true;
				return;
			}

			if (erosionFilter.eventType == GButton.PRESSED) { 
				maskWidth = 11;
				maskHeight = 11;
				maskCenterX = 5;
				maskCenterY = 5;

				clearMatrix();
				
				for (int i = 0; i < maskWidth; i++) {
					for (int j = 0; j < maskHeight; j++) {
						if (((i - 5) * (i - 5) + (j - 5) * (j - 5)) < 16)
							mask[i][j] = 1;
					}
				}
				mask[5][5] = 2;
				if (isErosion == true) {
					clearMatrix();
					initializeMatrixCenter();
					isErosion = false;
					erosionFilter.setColorScheme(GCScheme.YELLOW_SCHEME);
				} else {
					clearToggles();
					isErosion = true;
					erosionFilter.setColorScheme(GCScheme.RED_SCHEME);
				}
				refreshFrame = true;
				toggledFilter = true;
				refreshFrame = true;
				return;
			}
			
		}
	}
	
	private void clearToggles(){
		
		isDilation = isErosion = isNegative = isThreshold = isWhiteBlack = isBits = isBright = isSaturate = false;
		dilateFilter.setColorScheme(GCScheme.YELLOW_SCHEME);
		erosionFilter.setColorScheme(GCScheme.YELLOW_SCHEME);
		negativeFilter.setColorScheme(GCScheme.YELLOW_SCHEME);
		thresholdFilter.setColorScheme(GCScheme.YELLOW_SCHEME);
		blackAndWhiteFilter.setColorScheme(GCScheme.YELLOW_SCHEME);
		bitsFilter.setColorScheme(GCScheme.YELLOW_SCHEME);
		brightFilter.setColorScheme(GCScheme.YELLOW_SCHEME);
		saturationFilter.setColorScheme(GCScheme.YELLOW_SCHEME);
		
	}
	
	
	private boolean anyButtonButNonLinearPressed() {
		return blurFilter.eventType == GButton.PRESSED || edgesFilter.eventType == GButton.PRESSED
				|| sharpenFilter.eventType == GButton.PRESSED || embossFilter.eventType == GButton.PRESSED || blackAndWhiteFilter.eventType == GButton.PRESSED
				|| thresholdFilter.eventType == GButton.PRESSED || negativeFilter.eventType == GButton.PRESSED
				|| bitsFilter.eventType == GButton.PRESSED || brightFilter.eventType == GButton.PRESSED || saturationFilter.eventType == GButton.PRESSED;
	}

	private PImage pickImage() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			if (checkExtension(file.getName())) {
				refreshFrame = true;
				return loadImage(file.getPath());
			}
		}
		return null;
	}

	private boolean checkExtension(String fileName) {
		if (fileName.toUpperCase().endsWith("GIF") || fileName.toUpperCase().endsWith("JPG") || fileName.toUpperCase().endsWith("TGA")
				|| fileName.toUpperCase().endsWith("PNG")) {
			return true;
		}
		return false;
	}

	private void clearMatrix() {
		for (int i = 0; i < MAX_W; i++) {
			for (int j = 0; j < MAX_H; j++) {
				mask[i][j] = 0;
			}
		}
	}

	private void initializeMatrixCenter() {
		mask[maskCenterX][maskCenterY] = 1;
	}

	public void keyPressed() {
		if (keyCode >= 49 && key <= 57 && !isKeyPressed) {
			clickModifier = keyCode - 48;
		} else if (keyCode == KeyEvent.VK_SHIFT) {
			changeMaskCenter = true;
		}
		isKeyPressed = true;
	}

	public void keyReleased() {
		isKeyPressed = false;
		changeMaskCenter = false;
		clickModifier = 1;
	}

	private void resizeImage(PImage image) {
		float ratio = (float) image.width / (float) image.height;
		float fitRatio = (float) MAX_IMG_W / (float) MAX_IMG_H;
		if (ratio > fitRatio) {
			float wratio = (float) MAX_IMG_W / (float) image.width;
			image.resize(MAX_IMG_W, (int) wratio * image.height);
		} else if (ratio < fitRatio) {
			float hratio = (float) MAX_IMG_H / (float) image.height;
			image.resize((int) hratio * image.width, MAX_IMG_H);
		} else {
			image.resize(MAX_IMG_W, MAX_IMG_H);
		}
	}

	private void exportImage() {
		PImage originalFiltered = applyConvolution(this.normalizeMatrix(mask, MAX_W, MAX_H), maskWidth, maskHeight, originalImg);
		originalFiltered.save("../export.png");
	}

	private boolean isActiveCell(int[][] matrix, int matrixSizeX, int matrixSizeY, int posX, int posY, int matrixCenterX, int matrixCenterY) {
		if (isActiveX(matrix, matrixSizeX, matrixSizeY, posX, matrixCenterX) && isActiveY(matrix, matrixSizeX, matrixSizeY, posY, matrixCenterY)) {
			return true;
		}
		return false;
	}

	private boolean isActiveX(int[][] matrix, int matrixSizeX, int matrixSizeY, int posX, int matrixCenterX) {
		if (posX < matrixCenterX) {
			for (int i = 0; i <= posX; i++) {
				for (int j = 0; j < matrixSizeY; j++) {
					if (matrix[i][j] != 0) {
						return true;
					}
				}
			}
		} else if (posX > matrixCenterX) {
			for (int i = posX; i < matrixSizeX; i++) {
				for (int j = 0; j < matrixSizeY; j++) {
					if (matrix[i][j] != 0) {
						return true;
					}
				}
			}
		} else if (posX == matrixCenterX) {
			return true;
		}
		return false;
	}

	private boolean isActiveY(int[][] matrix, int matrixSizeX, int matrixSizeY, int posY, int matrixCenterY) {
		if (posY < matrixCenterY) {
			for (int i = 0; i < matrixSizeX; i++) {
				for (int j = 0; j <= posY; j++) {
					if (matrix[i][j] != 0) {
						return true;
					}
				}
			}
		} else if (posY > matrixCenterY) {
			for (int i = 0; i < matrixSizeX; i++) {
				for (int j = posY; j < matrixSizeY; j++) {
					if (matrix[i][j] != 0) {
						return true;
					}
				}
			}
		} else if (posY == matrixCenterY) {
			return true;
		}
		return false;
	}

	private int findMinX(int[][] matrix, int matrixMaxSizeX, int matrixMaxSizeY) {
		for (int i = 0; i < matrixMaxSizeX; i++) {
			for (int j = 0; j < matrixMaxSizeY; j++) {
				if (matrix[i][j] != 0) {
					return i;
				}
			}
		}
		return 0;
	}

	private int findMinY(int[][] matrix, int matrixMaxSizeX, int matrixMaxSizeY) {
		for (int j = 0; j < matrixMaxSizeY; j++) {
			for (int i = 0; i < matrixMaxSizeX; i++) {
				if (matrix[i][j] != 0) {
					return j;
				}
			}
		}
		return 0;
	}

	private int findMaxX(int[][] matrix, int matrixMaxSizeX, int matrixMaxSizeY) {
		for (int i = matrixMaxSizeX - 1; i >= 0; i--) {
			for (int j = matrixMaxSizeY - 1; j >= 0; j--) {
				if (matrix[i][j] != 0) {
					return i;
				}
			}
		}
		return matrixMaxSizeX - 1;
	}

	private int findMaxY(int[][] matrix, int matrixMaxSizeX, int matrixMaxSizeY) {
		for (int j = matrixMaxSizeY - 1; j >= 0; j--) {
			for (int i = matrixMaxSizeX - 1; i >= 0; i--) {
				if (matrix[i][j] != 0) {
					return j;
				}
			}
		}
		return matrixMaxSizeY - 1;
	}

	private void calculateMatrixSize(int[][] matrix, int matrixMaxSizeX, int matrixMaxSizeY) {
		maskXoffset = findMinX(matrix, matrixMaxSizeX, matrixMaxSizeY);
		maskYoffset = findMinY(matrix, matrixMaxSizeX, matrixMaxSizeY);
		maskWidth = findMaxX(matrix, matrixMaxSizeX, matrixMaxSizeY) - maskXoffset + 1;
		maskHeight = findMaxY(matrix, matrixMaxSizeX, matrixMaxSizeY) - maskYoffset + 1;
		
		if((maskCenterX < maskXoffset || maskCenterX > (maskXoffset + maskWidth - 1)) ||
				(maskCenterY < maskYoffset || maskCenterY > (maskYoffset + maskHeight - 1))){
			maskCenterX = (maskXoffset + (maskXoffset + maskWidth))/2;
			maskCenterY = (maskYoffset + (maskYoffset + maskHeight))/2;
		}
		
	}
	
	private void isOver(){
		String displayText = "";
		String titleText = "";
		if(blurFilter.isOver(mouseX, mouseY)){
			displayText = BLUR_DESC;
			titleText = BLUR_TITLE;
		} else if(sharpenFilter.isOver(mouseX, mouseY)){
			displayText = SHARPEN_DESC;
			titleText = SHARPEN_TITLE;
		} else if(embossFilter.isOver(mouseX, mouseY)){
			displayText = EMBOSS_DESC;
			titleText = EMBOSS_TITLE;
		} else if(edgesFilter.isOver(mouseX, mouseY)){
			displayText = EDGES_DESC;
			titleText = EDGES_TITLE;
		} else if(negativeFilter.isOver(mouseX, mouseY)){
			displayText = NEGATIVE_DESC;
			titleText = NEGATIVE_TITLE;
		} else if(blackAndWhiteFilter.isOver(mouseX, mouseY)){
			displayText = BLACKWHITE_DESC;
			titleText = NEGATIVE_TITLE;
		} else if(thresholdFilter.isOver(mouseX, mouseY)){
			displayText = THRESHOLD_DESC;
			titleText = THRESHOLD_TITLE;
		} else if(erosionFilter.isOver(mouseX, mouseY)){
			displayText = EROSION_DESC;
			titleText = EROSION_TITLE;
		} else if(dilateFilter.isOver(mouseX, mouseY)){
			displayText = DILATION_DESC;
			titleText = DILATION_TITLE;
		} else if(bitsFilter.isOver(mouseX, mouseY)){
			displayText = COLORS_DESC;
			titleText = COLORS_TITLE;
		}else if(brightFilter.isOver(mouseX, mouseY)){
			displayText = BRIGHTNESS_DESC;
			titleText = BRIGHTNESS_TITLE;
		}else if(saturationFilter.isOver(mouseX, mouseY)){
			displayText = SATURATION_DESC;
			titleText = SATURATION_TITLE;
		}else if(smudgeFilter.isOver(mouseX, mouseY)){
			displayText = SMUDGE_DESC;
			titleText = SMUDGE_TITLE;
		}else if(multiFocalFilter.isOver(mouseX, mouseY)){
			displayText = MULTIFOCAL_DESC;
			titleText = MULTIFOCAL_TITLE;
		}else {
			displayText = DISPLAY_TEXT;
			titleText = DISPLAY_TITLE;
		}
		drawDescriptionBox(displayText, titleText);
	}
	
	private void drawDescriptionBox(String displayText, String titleText){
		fill(127);
		noStroke();
		rect(355, WINDOW_MARGIN, MAX_IMG_W, 130);
		fill(255);
		textAlign(PConstants.RIGHT);
		
		textFont(font, 14);
		textLeading(15);
		text(displayText,900,70);
		
		textFont(font, 16);
		textLeading(15);
		text(titleText,900,45);
		
		
	}

}
