# Sistema para restaurante

Proyecto #8 del curso de Programación Orientada a Objetos: aplicación de escritorio en **Java + Swing** con base de datos **MySQL** (JDBC), organizada en capas.

## Funciones

- Inicio de sesión con menús distintos según el rol del usuario.
- Catálogo de platillos (consultar y agregar).
- Mesas: consultar, agregar, ocupar y liberar.
- Pedidos: el mesero registra un pedido con varios platillos (se guarda en una sola transacción y la mesa pasa a "Ocupada").
- Cocina: lista de pedidos en proceso que se actualiza sola cada 10 segundos; permite marcarlos como completados.
- Caja: cobro de pedidos.
- Reportes: ventas por día, platillos más vendidos, ventas por mesero, ingresos por método de pago y pedidos por estado.
- Usuarios y roles: el Super Administrador agrega, edita y elimina usuarios y les asigna un rol.


## Arquitectura por capas

```
Vista (Swing)  →  Servicio (reglas)  →  DAO (SQL)  →  MySQL
                         ↘ Modelo (clases del dominio) ↙
```

| Paquete | Qué contiene |
|---|---|
| `modelo` | Clases del dominio con sus validaciones (Platillo, Mesa, Pedido, Usuario, etc.) |
| `dao` | Acceso a datos: el único lugar donde se escribe SQL |
| `servicio` | Reglas de negocio y permisos por rol |
| `vista` | Ventanas Swing; no contienen SQL ni reglas |
| `conexion` | `ConexionBD`, que entrega la conexión JDBC |

Estructura de carpetas:

```
sistema_restaurante
├── lib        (mysql-connector-j y junit-platform-console-standalone)
├── src        (código fuente: conexion, dao, modelo, servicio, vista, App.java)
├── test       (pruebas JUnit: modelo y servicio)
├── .vscode    (configuración de VS Code)
└── sistema_restaurante_mysql.sql   (base de datos con datos de ejemplo)
```

## Requisitos

- JDK 17 o superior.
- MySQL Server 8 y MySQL Workbench (o la consola de MySQL).
- Visual Studio Code con la extensión **Extension Pack for Java**.

Los drivers (`lib`) ya están incluidos en el repositorio; no hay que instalarlos.

## Instalación paso a paso

1. **Clonar el repositorio** en una carpeta fuera de OneDrive, por ejemplo `C:\Proyectos`:
   ```
   git clone https://github.com/issacm508/sistema_restaurante.git
   ```
2. **Crear la base de datos.** En MySQL Workbench abre `sistema_restaurante_mysql.sql` y ejecútalo completo (rayo). Crea la base `sistema_restaurante`, sus tablas y los datos de ejemplo.
3. **Crear el usuario de MySQL** (cambia `TU_CONTRASENA` por una contraseña tuya):
   ```sql
   CREATE USER 'restaurante'@'localhost' IDENTIFIED BY 'TU_CONTRASENA';
   GRANT ALL PRIVILEGES ON sistema_restaurante.* TO 'restaurante'@'localhost';
   FLUSH PRIVILEGES;
   ```
4. **Configurar la conexión.** Abre `src/conexion/ConexionBD.java` y revisa estos tres valores:
   - `URL`: `jdbc:mysql://localhost:3306/sistema_restaurante?...`
   - `USUARIO`: `restaurante`
   - `PASSWORD`: la contraseña del paso 3.
5. **Abrir el proyecto** en VS Code (`File > Open Folder` sobre la carpeta `sistema_restaurante`). Espera a que abajo diga **Java: Ready**.
6. **Ejecutar.** Abre `src/App.java` y pulsa **Run**.

## Usuarios de ejemplo

Vienen en la tabla `usuarios` del archivo SQL:

| Rol | Usuario | Contraseña | Qué puede hacer |
|---|---|---|---|
| Super Administrador | Ariadna Cornejo | 123 | Todo |
| Recepcionista | Maria Lopez | 321 | Mesas |
| Mesero | Carlos Vallarta | 098 | Registrar pedidos |
| Cocina | Jessica Cruz | 890 | Ver y completar pedidos |
| Administrador | Juan Perez | 456 | Caja (cobrar) |

## Pruebas unitarias (JUnit 5)

Hay 61 pruebasen `test/modelo` y `test/servicio`. No necesitan base de datos: cubren las validaciones del modelo y las reglas de los servicios (permisos por rol, login, mesas, pedidos y platillos).

Para correrlas en VS Code abre el panel **Testing** (icono del matraz) y pulsa **Run Tests**. El resultado esperado es **61/61 en verde**.

## Notas y limitaciones conocidas

- Las contraseñas se guardan en texto plano en la tabla `usuarios` (datos de ejemplo del curso). Mejora pendiente: guardarlas con hash.
- La contraseña de MySQL está escrita en `ConexionBD.java`; no debe reutilizarse una contraseña personal.
- Los diagramas ER y UML entregados anteriormente no reflejan los últimos cambios de la base de datos.
- Pendiente: edición y borrado de platillos.

## Equipo

- Cornejo Malfavon Ariadna Alejandra
- Cervantes Perez Melanie Alejandra
- Sanchez Mendoza Issac Efren
- Santiago Diaz Acosta

