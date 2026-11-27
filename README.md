#
#    _   _   _ _   _     __ _  _  _  ___ __ __  ___  _  _  _  _     _  __  ___  __
#   | | / \ | | | / \   / _/ \| \| || __/ _/ _||_ _|| |/ \| \| |   | ||  \| o )/ _|
# n_| || o || V || o | ( (( o ) \\ || _( (( (_  | | | ( o ) \\ | n_| || o ) o ( (_
# \__/ |_n_| \_/ |_n_|  \__\_/|_|\_||___\__\__| |_| |_|\_/|_|\_| \__/ |__/|___/\__|
#
# 👾 Sistema de Gestión de Restaurante — Java JDBC & PostgreSQL

Aplicación desarrollada en Java con arquitectura por capas conectada a una base de datos relacional en **PostgreSQL** mediante **JDBC**. El sistema implementa control transaccional explícito (`commit` / `rollback`), consultas parametrizadas (`PreparedStatement`), ejecución de funciones y procedimientos almacenados (`CallableStatement`), manejo estandarizado de errores (`SQLState`) y soporte dual de ejecución tanto en **Consola** como en **Interfaz Gráfica (Swing Glassmorphism)**.

> **Modos de Ejecución Disponibles:** El proyecto cuenta con dos puntos de entrada independientes según cómo desees probar el sistema:
> * Ejecuta **`Main.java`** para interactuar únicamente por **consola (terminal)**.
> * Ejecuta **`MainGUI.java`** para utilizar la **interfaz gráfica** responsiva (Java Swing con estilo Glassmorphism).

---

## Requisitos Previos

Antes de clonar y ejecutar el proyecto, asegúrate de contar con las siguientes herramientas instaladas:

- **Java Development Kit (JDK):** Versión 17 o superior.
- **Apache Maven:** Versión 3.8 o superior (o soporte integrado en tu IDE).
- **PostgreSQL / Docker:** Motor PostgreSQL 15+ ejecutándose en el puerto `5433` (o contenedor Docker activo con la base de datos `taller_bd` y el esquema `restaurante`).
- **IDE recomendado:** Visual Studio Code (con *Extension Pack for Java*) o IntelliJ IDEA.

---

## Arquitectura del Proyecto

El código fuente sigue el patrón de separación de responsabilidades organizado en los siguientes paquetes dentro de `src/main/java/mx/tecnm/restaurante/`:

| Paquete | Clases | Responsabilidad |
| :--- | :--- | :--- |
| **`ui`** | `MenuConsola`, `MenuGrafico` | Interfaz de usuario en consola e interfaz gráfica responsiva. Captura entradas y muestra mensajes amigables ocultando trazas técnicas. |
| **`service`** | `PedidoService` | Capa de lógica de negocio. Valida reglas previas y coordina operaciones compuestas antes de acceder a los datos. |
| **`dao`** | `ProductoDAO`, `ClienteDAO`, `PedidoDAO` | Acceso a datos mediante SQL parametrizado, mapeo de `ResultSet` y control explícito de transacciones (`setAutoCommit`, `commit`, `rollback`). |
| **`model`** | `Producto`, `Cliente`, `Pedido`, `DetallePedido` | Entidades de dominio que representan las tablas del esquema `restaurante`. |
| **`database`** | `ConexionDB` | Administración centralizada de la conexión JDBC leyendo credenciales desde variables de entorno. |

---

## Variables de Entorno

Por seguridad, el proyecto no contiene credenciales escritas en el código Java. Para establecer la conexión con PostgreSQL debes definir las siguientes variables de entorno en tu sistema o en el archivo `.vscode/launch.json`:

| Variable | Descripción | Valor por defecto del proyecto |
| :--- | :--- | :--- |
| `DB_URL` | Cadena de conexión JDBC hacia PostgreSQL | `jdbc:postgresql://localhost:5433/taller_bd` |
| `DB_USER` | Usuario de aplicación con permisos sobre el esquema `restaurante` | `usuario_app` |
| `DB_PASSWORD` | Contraseña del usuario de base de datos | `App2026!` |

---

## Guía de Instalación y Ejecución

Sigue estos pasos en orden para poner en marcha el proyecto sin modificar su configuración:

### 1. Clonar el repositorio
```bash
git clone <URL_DEL_REPOSITORIO>
cd restaurante-jdbc
```
### 👾 Integrantes del Equipo
[Mendoza Martinez Eduardo Yael]
[Antonio Peña Jose Angel]
[Ruiz Chavez Youri Jorkaeff]