# Wordle MVC (Java + WindowBuilder)

Estructura base para un TP estilo Wordle usando patron MVC.

## Paquetes
- com.juego.modelo
- com.juego.vista
- com.juego.controlador
- com.juego.aplicacion

## Punto de inicio
Ejecutar: `com.juego.aplicacion.Aplicacion`

## Nota WindowBuilder
La clase `VistaPrincipal` esta preparada para que la abras con WindowBuilder y ajustes componentes visualmente.

## Pruebas JUnit
Se agrego la carpeta de pruebas en `src/test/java` con casos normales y especiales.

Tests incluidos:
- `GameModelTest`
- `WordRepositoryTest`

Para correrlos en Eclipse:
1. Click derecho al proyecto -> Build Path -> Add Libraries -> JUnit -> JUnit 5.
2. Click derecho sobre `src/test/java` o sobre una clase de test -> Run As -> JUnit Test.

Alternativa sin Eclipse (ya lista):
1. Ejecutar en PowerShell: `powershell -ExecutionPolicy Bypass -File run-tests.ps1`
2. El script compila y corre todos los tests con `lib/junit-platform-console-standalone-1.12.2.jar`.
