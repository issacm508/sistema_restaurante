# Sistema para restaurante — La Comarca Mexicana

Proyecto #8 del curso de Programación Orientada a Objetos: aplicación de escritorio en **Java + Swing** con base de datos **MySQL** (JDBC), organizada en capas.

## Funciones

- Inicio de sesión con menús distintos según el rol del usuario. Las contraseñas se guardan cifradas (hash SHA-256). El login recuerda los nombres de usuario que ya entraron en esa computadora (nunca guarda contraseñas).
- Catálogo de platillos (consultar, agregar, editar y eliminar). No se puede eliminar un platillo que ya aparece en pedidos.
- Mesas: la recepcionista asigna una mesa libre al cliente (la marca "Ocupada"). Una mesa no se puede liberar mientras tenga un pedido sin cobrar (el número de mesa va de 1 a 999).
- Pedidos: el mesero toma el pedido en una mesa que la recepcionista ya asignó (Ocupada). Puede armar pedidos de varias mesas en la misma ventana y se guardan en una sola transacción. En "Mis pedidos" cancela los suyos mientras estén En Proceso.
- Cocina: lista de pedidos en proceso que se actualiza sola cada 10 segundos; permite marcarlos como completados. Cancelar lo hace el mesero en "Mis pedidos" (la mesa sigue ocupada hasta que la recepcionista la libere).
- Caja: cobro de pedidos con efectivo o tarjeta (libera la mesa). Desglosa el IVA (16%, ya incluido en los precios) y genera el ticket, que se puede imprimir o guardar como .txt.
- Reportes: ventas por día, platillos más vendidos, ventas por mesero, ingresos por método de pago y pedidos por estado.
- Usuarios y roles: el Super Administrador agrega, edita y elimina usuarios y les asigna un rol.
- El Super Administrador tiene acceso a todos los módulos. Si todavía no está registrado como mesero, se registra solo la primera vez que toma un pedido.
- Interfaz con barra lateral: al elegir un módulo, su contenido aparece a la derecha dentro de la misma ventana. Tema negro y dorado con el logo del restaurante.

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
| `vista` | Ventanas Swing y el tema visual (`Tema`); no contienen SQL ni reglas |
| `conexion` | `ConexionBD`, que entrega la conexión JDBC |

Estructura de carpetas:

```
sistema_restaurante
├── lib        (mysql-connector-j y junit-platform-console-standalone)
├── src        (código fuente: conexion, dao, modelo, servicio, vista, App.java)
├── test       (pruebas JUnit: modelo y servicio)
├── recursos   (logo del restaurante: logo.png)
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
   - `URL`: `jdbc:mysql://localhost:3306/sistema_restaurante?...` (si tu MySQL usa otro puerto, por ejemplo 3307, cambia el número 3306 por el tuyo; en MySQL Workbench aparece en los datos de la conexión).
   - `USUARIO`: `restaurante`
   - `PASSWORD`: la contraseña del paso 3.
5. **Abrir el proyecto** en VS Code (`File > Open Folder` sobre la carpeta `sistema_restaurante`, la que contiene `src` y `lib`). Espera a que abajo diga **Java: Ready**.
6. **Ejecutar.** Abre `src/App.java` y pulsa **Run**.

## Usuarios de ejemplo

Vienen en la tabla `usuarios` del archivo SQL:

| Rol | Usuario | Contraseña | Qué puede hacer |
|---|---|---|---|
| Super Administrador | Ariadna Cornejo | 123 | Todo (incluye tomar pedidos y cobrar) |
| Recepcionista | Maria Lopez | 321 | Asignar y liberar mesas |
| Mesero | Carlos Vallarta | 098 | Registrar y cancelar sus pedidos |
| Cocina | Jessica Cruz | 890 | Ver pedidos y marcarlos listos |
| Administrador | Juan Perez | 456 | Caja (cobrar y ticket) |

## Pruebas unitarias (JUnit 5)

Hay 87 pruebas en `test/modelo` y `test/servicio`. No necesitan base de datos: cubren las validaciones del modelo y las reglas de los servicios (permisos por rol, login, mesas, pedidos, platillos, usuarios, cifrado de contraseñas, IVA y ticket, y usuarios recordados).

Para correrlas en VS Code abre el panel **Testing** (icono del matraz) y pulsa **Run Tests**. El resultado esperado es **87/87 en verde**.

## Notas y limitaciones conocidas

- Las contraseñas se guardan cifradas con hash SHA-256 en la tabla `usuarios` (nunca en texto plano). Mejora pendiente: agregar una "sal" aleatoria por usuario.
- La contraseña de MySQL está escrita en `ConexionBD.java`; no debe reutilizarse una contraseña personal.
- Los usuarios recordados del login se guardan en un archivo de la carpeta personal de Windows (`.la_comarca_usuarios.txt`), fuera del proyecto. Solo contiene nombres de usuario.
- Los diagramas ER y UML entregados anteriormente no reflejan los últimos cambios de la base de datos.

## Equipo

- Cornejo Malfavon Ariadna Alejandra
- Cervantes Perez Melanie Alejandra
- Sanchez Mendoza Issac Efren
- Santiago Diaz Acosta