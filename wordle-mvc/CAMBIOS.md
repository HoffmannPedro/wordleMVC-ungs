# Resumen de Cambios: Refactorización del Proyecto Wordle

Acá les dejo el resumen de los problemas que teníamos y cómo los fuimos solucionando.

## 1. Arquitectura y Código Limpio 🧹

* **MVC Real (Separación de responsabilidades):** * *El problema:* La Vista le prestaba sus componentes (botones, cajas de texto) directamente al Controlador. El Controlador terminaba cambiando colores y modificando la UI, y además hacía cálculos matemáticos que no le correspondían (como el sistema de pistas). Era un código espagueti.
    * *La solución:* Aislamos todo. Ahora el Controlador es solo un director de orquesta. La lógica pesada (las pistas, evaluar si ganaste) se mudó 100% al `ModeloJuego`. Por su parte, la `VistaPrincipal` ahora solo expone métodos simples (ej. `mostrarMensaje()`, `pintarCelda()`) y maneja sus propios eventos gráficos. Si mañana queremos cambiar Swing por una interfaz web, el Controlador y el Modelo ni se enteran.
* **Chau a los "Valores Mágicos" y colores repetidos (Principios DRY y Tipado Fuerte):** * *El problema:* Estábamos usando letras sueltas (`'G'`, `'Y'`, `'B'`) para el estado de los aciertos y creando los mismos objetos `Color` con códigos RGB desparramados por toda la vista. Si queríamos cambiar un tono de verde, había que buscar línea por línea.
    * *La solución:* Creamos un `Enum` llamado `EstadoLetra` (`CORRECTA`, `PRESENTE`, `AUSENTE`) para que el código sea semántico y seguro. Además, centralizamos los colores en constantes estáticas al principio de la vista. Se cambia en un solo lugar y se actualiza todo.
* **Código más legible (Principio de Nivel Único de Abstracción):** * Agrupamos y ordenamos los métodos en todas las clases. Rompimos los métodos gigantes (como `procesarIntento` o el constructor de la vista) en funciones auxiliares más chicas y específicas. Ahora el código se lee casi como un índice.

## 2. Manejo de Datos y Diccionarios 📚

* **El "Doble Diccionario" y Validación de Palabras:** * *El problema:* Las palabras estaban "quemadas" en el código en un arreglo. Peor aún, el juego te dejaba ingresar palabras inventadas como "AAAAA" y te descontaba un intento.
    * *La solución:* Sacamos los datos a archivos `.txt`. Implementamos un sistema profesional de dos listas: una lista corta (`List`) para elegir la palabra secreta (palabras comunes que todos conocen), y un diccionario masivo (`HashSet`) que tiene todo el vocabulario del idioma. Este último se usa súper rápido (tiempo O(1)) solo para validar que la palabra que ingresaste realmente exista antes de descontarte el turno.
* **Idiomas sin tocar el código (Internacionalización):** * Sacamos todos los textos (`Map<String, String>`) que estaban metidos a la fuerza en el controlador y armamos archivos `.properties`. Ahora usamos la herramienta nativa `ResourceBundle` de Java. Agregar un idioma nuevo es sumar un archivo de texto, no requiere volver a compilar nada.
* **Falla rápido y hace ruido (Patrón Fail-Fast):** * Antes, si borrabas el archivo del diccionario, el juego se tragaba el error, le ponía "ERROR" como palabra secreta y te dejaba jugar una partida rota. Ahora, si falta un recurso vital, el juego explota a propósito (`RuntimeException`) y no arranca, avisando exactamente qué archivo falta.

## 3. Interfaz Gráfica y Experiencia de Usuario (UI/UX) 🎨

* **Nuevo Diseño (Modo Oscuro Moderno):** * Swing suele verse viejo, y las grillas se deformaban si cambiabas el tamaño de la ventana. Rediseñamos todo con una paleta plana hexadecimal (modo oscuro estilo Wordle original), le dimos márgenes a los paneles para que el diseño respire (`EmptyBorder`) y bloqueamos el tamaño de las celdas y teclas para que la estructura gráfica no se rompa nunca.
* **Jugar con la tecla "Enter":** * Tener que agarrar el mouse para hacer clic en "Probar" en cada turno era muy molesto. Le agregamos un `ActionListener` a la caja de texto. Ahora podés escribir y apretar *Enter* de corrido.
* **Arreglo del "Tablero Fantasma":** * Si pasabas de Dificultad Fácil (6 intentos) a Difícil (4 intentos) a mitad de camino, quedaban letras de la partida anterior flotando abajo. Arreglamos el método `limpiarTablero()` para que la Vista borre siempre la matriz visual completa, independientemente de lo que diga el Modelo en ese momento.
* **Popups anclados:** * Los mensajes emergentes (`JOptionPane`) salían perdidos en el centro del monitor. Ahora se lanzan desde la Vista y están anclados a la ventana del juego.
* **Cronómetro moderno:** * Dejamos de usar el viejo `System.currentTimeMillis()` y pasamos a usar la API `java.time` (`Instant` y `Duration`) que es la manera correcta y moderna de calcular el tiempo transcurrido en Java.