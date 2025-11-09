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
El proyecto utiliza una base de datos Oracle; por lo tanto, debes definir las variables de entorno antes de iniciar la aplicación:

```bash
export BD_URL="jdbc:oracle:thin:@//<host>:<puerto>/<service_name>"
export BD_USER="usuario"
export BD_PASSWORD="contraseña"
# Solo requerido si la conexión utiliza Oracle Wallet
export WALLET_B64="$(base64 -w0 wallet.zip)"
```

Estas variables son consumidas en `src/main/resources/application.yml` para configurar el `datasource` y las propiedades de Hibernate.

`WALLET_B64` es opcional y únicamente debe proporcionarse cuando la base de datos requiere un Oracle Wallet para autenticarse. Su valor debe contener el archivo ZIP del wallet codificado en Base64, tal como se muestra en el ejemplo anterior. Si no necesitas wallet, puedes omitir la variable y la aplicación se conectará utilizando únicamente las credenciales.

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
  -e BD_URL="jdbc:oracle:thin:@//<host>:<puerto>/<service_name>" \
  -e BD_USER="usuario" \
  -e BD_PASSWORD="contraseña" \
  # Opcional: solo si requieres wallet codificado en Base64
  -e WALLET_B64="$(base64 -w0 wallet.zip)" \
  --name backend-levelup levelup/backend
```

## Configuración de secretos para publicar imágenes
Para que el workflow de GitHub Actions pueda autenticar y publicar imágenes en Docker Hub, es necesario configurar los siguientes secretos en los ajustes del repositorio en GitHub:

- `DOCKERHUB_USERNAME`: nombre de usuario de Docker Hub.
- `DOCKERHUB_TOKEN`: token de acceso o contraseña de Docker Hub con permisos para publicar en el repositorio correspondiente.

Una vez configurados, el workflow utilizará estos secretos para iniciar sesión y publicar las imágenes etiquetadas como `latest` y con el SHA del commit.
