package xadrez;

import jogoDeTabuleiro.Peca;
import jogoDeTabuleiro.Posicao;
import jogoDeTabuleiro.Tabuleiro;

public abstract class PecaDeXadrez extends Peca{

	private Cor cor;
	private int contadorDeMovimento;

	public PecaDeXadrez(Tabuleiro tabuleiro, Cor cor) {
		super(tabuleiro);
		this.cor = cor;
	}

	public Cor getCor() {
		return cor;
	}
	
	public int getContadorDeMovimento() {
		return contadorDeMovimento;
	}
	
	public void acrescentaContadorDeMovimento() {
		contadorDeMovimento++;
	}
	
	public void decrementaContadorDeMovimento() {
		contadorDeMovimento--;
	}

	public PosicaoDoXadrez getPosicaoDoXadrez() {
		return PosicaoDoXadrez.fromPosicao(posicao);
	}
	
	protected boolean haUmaPecaOponente(Posicao posicao) {
		PecaDeXadrez p = (PecaDeXadrez)getTabuleiro().peca(posicao);
		return p != null && p.getCor() != cor;
	}
}
