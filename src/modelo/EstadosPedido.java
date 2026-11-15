package modelo;

//Ids de los estados de un pedido, tal como estan en la tabla estados_pedido 

public final class EstadosPedido {

    public static final int EN_PROCESO = 1;
    public static final int COMPLETADO = 2;
    public static final int CANCELADO = 3;

    private EstadosPedido() {
    }
}