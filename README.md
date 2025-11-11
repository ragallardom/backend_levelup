# Backend LevelUp

API REST construida con Spring Boot 3 para gestionar productos de LevelUp. Expone un catálogo con operaciones para listar y crear productos y un endpoint de saludo de verificación. El proyecto está preparado para conectarse a una base de datos Oracle y empaquetarse como imagen Docker para su despliegue continuo.

## Tabla de contenido
- [Tecnologías](#tecnologías)
- [Requisitos previos](#requisitos-previos)
- [Variables de entorno](#variables-de-entorno)
- [Ejecución local](#ejecución-local)
- [Colección de endpoints](#colección-de-endpoints)
- [Estrategia de pruebas](#estrategia-de-pruebas)
- [Construcción y despliegue con Docker](#construcción-y-despliegue-con-docker)
- [Configuración de secretos para publicar imágenes](#configuración-de-secretos-para-publicar-imágenes)
- [Publicación automatizada en Docker Hub](#publicación-automatizada-en-docker-hub)

## Tecnologías
- Java 21
- Spring Boot 3.5 (web, data-jpa, validation)
- Oracle JDBC Driver (ojdbc11)
- Maven Wrapper (`mvnw`)
- Docker (opcional para despliegue)

## Requisitos previos
Asegúrate de contar con las siguientes herramientas instaladas:

- [JDK 21](https://adoptium.net/) o compatible con la versión definida en `pom.xml`.
- [Maven 3.9+](https://maven.apache.org/) (opcional si utilizas el wrapper incluido `./mvnw`).
- Docker Engine 24+ si deseas construir y ejecutar la imagen del servicio.

## Variables de entorno
El proyecto utiliza una base de datos Oracle; por lo tanto, debes definir las variables de entorno antes de iniciar la aplicación. Puedes exportarlas manualmente o almacenarlas en un archivo `.env`.

```bash
# Define BD_URL usando uno de los dos formatos válidos:
#   SID dedicado: jdbc:oracle:thin:@<host>:<puerto>:<sid>
#   Service-name: jdbc:oracle:thin:@//<host>:<puerto>/<service_name>
export BD_URL="<descriptor>"

export BD_USER="usuario"
export BD_PASSWORD="contraseña"

# Opcional: define la ruta donde se montará el Oracle Wallet.
# De manera predeterminada la imagen apunta a /oracle/wallet
export TNS_ADMIN="/oracle/wallet"

# Opcional: personaliza el puerto del servicio Spring Boot.
export SERVER_PORT="8080"
```

Usa el descriptor que corresponda al modo en que tu instancia Oracle publica la conexión (SID dedicado vs. service-name). Estas variables son consumidas en `src/main/resources/application.yml` para configurar el `datasource` y las propiedades de Hibernate.

`TNS_ADMIN` es opcional y solo debe configurarse si deseas que la aplicación lea un Oracle Wallet desde un directorio específico. Si no necesitas wallet, puedes omitir la variable y la aplicación se conectará utilizando únicamente las credenciales.

### Archivo `.env` para despliegues automatizados

El workflow de despliegue remoto espera encontrar un archivo `/opt/backend-levelup/.env` en la máquina destino. La carpeta se crea automáticamente durante el job, pero tú debes generar el archivo con las credenciales correctas:

```bash
sudo mkdir -p /opt/backend-levelup
sudo tee /opt/backend-levelup/.env >/dev/null <<'EOF'
BD_URL=jdbc:oracle:thin:@//db-host:1521/LEVELUP
BD_USER=levelup_app
BD_PASSWORD=contraseña-super-secreta
# Opcional: apunta al directorio del Oracle Wallet si aplica
TNS_ADMIN=/oracle/wallet
# Opcional: reemplaza si necesitas exponer otro puerto
SERVER_PORT=8080
EOF

sudo chmod 600 /opt/backend-levelup/.env
sudo chown <usuario-deploy>:<grupo> /opt/backend-levelup/.env
```

> **Importante:** el archivo vive fuera del repositorio y nunca debe versionarse. GitHub Actions se conectará por SSH, leerá el `.env` y exportará las variables antes de ejecutar `docker compose`.

#### Rotación segura de credenciales

1. Edita el archivo `/opt/backend-levelup/.env` con los nuevos valores (`sudo nano /opt/backend-levelup/.env` o una herramienta similar).
2. Guarda los cambios y verifica los permisos (`chmod 600`) para evitar accesos no autorizados.
3. Ejecuta nuevamente el workflow de despliegue (o lanza `docker compose` manualmente) para que los contenedores reciban las nuevas credenciales.
4. Revoca las credenciales antiguas en la base de datos para completar la rotación.

Este procedimiento evita exponer secretos en Git y mantiene el servidor siempre alineado con los valores vigentes.

## Ejecución local
1. Clona el repositorio y accede a la carpeta del proyecto.
2. Verifica que las variables de entorno estén definidas.
3. Inicia la aplicación con el wrapper de Maven:

```bash
./mvnw spring-boot:run
```

La API quedará disponible en `http://localhost:8080`.

Para generar el artefacto `jar` ejecuta:

```bash
./mvnw clean package
```

## Colección de endpoints
| Método | Ruta                      | Descripción                                      |
|--------|---------------------------|--------------------------------------------------|
| GET    | `/api/v1/hola`            | Devuelve el saludo de verificación del servicio. |
| GET    | `/api/v1/products`        | Lista todos los productos registrados.           |
| POST   | `/api/v1/products`        | Crea un nuevo producto.                          |

### Ejemplo de solicitud para crear producto
```http
POST /api/v1/products HTTP/1.1
Content-Type: application/json

{
  "code": "SKU-001",
  "name": "Camiseta LevelUp",
  "description": "Camiseta oficial talla M",
  "price": 18990,
  "imagePath": "https://cdn.example.com/products/sku-001.jpg",
  "stock": 25
}
```

#### Respuesta exitosa
```json
{
  "id": 1,
  "code": "SKU-001",
  "name": "Camiseta LevelUp",
  "description": "Camiseta oficial talla M",
  "price": 18990,
  "imagePath": "https://cdn.example.com/products/sku-001.jpg",
  "stock": 25
}
```

Si intentas crear un producto con un `code` repetido, la API responderá con HTTP `409 Conflict`.

## Estrategia de pruebas
Ejecuta la suite de pruebas (unitarias y de integración) con:

```bash
./mvnw test
```

> Nota: actualmente no se incluyen pruebas predefinidas en `src/test`, por lo que el comando finalizará sin ejecutar casos.

## Construcción y despliegue con Docker
Construye la imagen localmente utilizando el `Dockerfile` incluido:

```bash
docker build -t levelup/backend .
```

Para ejecutar el contenedor exponiendo el puerto 8080 y pasando las variables de entorno requeridas:

```bash
docker run -d \
  -p 8080:8080 \
  # Reemplaza <descriptor> por el formato que use tu instancia:
  #   SID: jdbc:oracle:thin:@<host>:<puerto>:<sid>
  #   Service-name: jdbc:oracle:thin:@//<host>:<puerto>/<service_name>
  -e BD_URL="<descriptor>" \
  -e BD_USER="usuario" \
  -e BD_PASSWORD="contraseña" \
  -v /oracle/wallet:/oracle/wallet:ro \
  --name backend-levelup levelup/backend
```

Si necesitas exponer otro puerto diferente a `8080`, establece la variable `SERVER_PORT` antes de arrancar el contenedor para que Spring Boot utilice ese valor.

## Configuración de secretos para publicar imágenes
Para que el workflow de GitHub Actions pueda autenticar y publicar imágenes en Docker Hub, es necesario configurar los siguientes secretos en los ajustes del repositorio en GitHub:

- `DOCKERHUB_USERNAME`: nombre de usuario de Docker Hub.
- `DOCKERHUB_TOKEN`: token de acceso o contraseña de Docker Hub con permisos para publicar en el repositorio correspondiente.

Una vez configurados, el workflow utilizará estos secretos para iniciar sesión y publicar las imágenes etiquetadas como `latest` y con el SHA del commit.

## Publicación automatizada en Docker Hub
El repositorio incluye un workflow de GitHub Actions en `.github/workflows/build-and-publish.yml` que automatiza la creación y publicación de la imagen Docker del proyecto. El flujo se activa automáticamente al hacer `push` en la rama `main` y también puede ejecutarse manualmente mediante la opción **Run workflow** (`workflow_dispatch`).

El job realiza las siguientes tareas:

1. Clona el repositorio usando `actions/checkout@v4`.
2. Compila el proyecto con `./mvnw -B clean package` para asegurar que el artefacto se genere correctamente.
3. Inicia sesión en Docker Hub mediante `docker/login-action@v3` utilizando los secretos `DOCKERHUB_USERNAME` y `DOCKERHUB_TOKEN` configurados previamente.
4. Construye y publica la imagen con `docker/build-push-action@v5`, apuntando al `Dockerfile` de la raíz del repositorio y subiendo las etiquetas `ragallardom/backend-levelup:latest` y `ragallardom/backend-levelup:<SHA del commit>`.

Con esta configuración, cada cambio fusionado en `main` queda disponible automáticamente en Docker Hub con una etiqueta fija (`latest`) y otra inmutable que permite identificar exactamente el commit (`ragallardom/backend-levelup:<sha>`). Para lanzar la publicación manualmente (por ejemplo, tras un hotfix), dirígete a la pestaña **Actions**, selecciona _Build and Publish Docker image_ y pulsa en **Run workflow**.
