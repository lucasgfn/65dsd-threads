package util;

public enum Directions {
    VAZIO(0, 0, 0,0,0),
    ESTRADA_CIMA(1, 1, 0,0,0),
    ESTRADA_DIREITA(2, 0,0,1,0 ),
    ESTRADA_BAIXO(3, 0,1,0,0 ),
    ESTRADA_ESQUERDA(4, 0,0,0,1),
    CRUZAMENTO_CIMA(5, 1, 0, 0, 0),
    CRUZAMENTO_DIREITA(6, 0, 0, 1, 0),
    CRUZAMENTO_BAIXO(7, 0, 1, 0, 0),
    CRUZAMENTO_ESQUERDA(8, 0, 0, 0, 1),
    CRUZAMENTO_CIMA_DIREITA(9, 1, 0, 1, 0),
    CRUZAMENTO_CIMA_ESQUERDA(10, 1, 0, 0, 1),
    CRUZAMENTO_BAIXO_DIREITA(11, 0, 1, 1, 0),
    CRUZAMENTO_BAIXO_ESQUERDA(12, 0, 1, 0, 1);

    public final int codigo;
    public final int CIMA;
    public final int BAIXO;
    public final int DIREITA;
    public final int ESQUERDA;


    Directions(int codigo, int CIMA, int BAIXO, int DIREITA, int ESQUERDA) {
        this.codigo = codigo;
        this.CIMA = CIMA;
        this.BAIXO = BAIXO;
        this.DIREITA = DIREITA;
        this.ESQUERDA = ESQUERDA;
    }


    public static Directions existeDirecao(int codigo){
        for(Directions d : values()){
            if(d.codigo == codigo){
                return d;
            }
        }
        throw new IllegalArgumentException("Codigo da direção não existe: " + codigo);
    }
}
