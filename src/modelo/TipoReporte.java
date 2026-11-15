package modelo;

 // Los reportes que ofrece el sistema.Es un enum: un conjunto fijo de opciones, mas seguro que usar textos sueltos.
public enum TipoReporte {

    VENTAS_POR_DIA("Ventas por dia"),
    PLATILLOS_MAS_VENDIDOS("Platillos mas vendidos"),
    VENTAS_POR_MESERO("Ventas por mesero"),
    INGRESOS_POR_METODO_PAGO("Ingresos por metodo de pago"),
    PEDIDOS_POR_ESTADO("Pedidos por estado");

    private final String nombre;

    TipoReporte(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    // Lo que se muestra cuando el reporte aparece en un JComboBox
    @Override
    public String toString() {
        return nombre;
    }
}