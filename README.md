# Backend LevelUp

## Configuración de secretos para la publicación de imágenes Docker

Para que el workflow de GitHub Actions pueda autenticar y publicar imágenes en Docker Hub, es necesario configurar los siguientes secretos en los ajustes del repositorio en GitHub:

- `DOCKERHUB_USERNAME`: nombre de usuario de Docker Hub.
- `DOCKERHUB_TOKEN`: token de acceso o contraseña de Docker Hub con permisos para publicar en el repositorio correspondiente.

Una vez configurados, el workflow utilizará estos secretos para iniciar sesión y publicar las imágenes etiquetadas como `latest` y con el SHA del commit.
