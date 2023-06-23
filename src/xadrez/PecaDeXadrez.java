package xadrez;

import jogoDeTabuleiro.Peca;
import jogoDeTabuleiro.Tabuleiro;

public abstract class PecaDeXadrez extends Peca{

	private Cor cor;

	public PecaDeXadrez(Tabuleiro tabuleiro, Cor cor) {
		super(tabuleiro);
		this.cor = cor;
	}

	public Cor getCor() {
		return cor;
	}

}
