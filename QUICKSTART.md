# 🚀 Guía Rápida de Arranque

## Opción A — Arrancar todo el stack localmente

### 1. PostgreSQL
```bash
# Crear base de datos
createdb -U postgres nexo_criminal

# Aplicar schema
psql -U postgres -d nexo_criminal -f database/01-schema.sql

# (Opcional) Datos de ejemplo mínimos
psql -U postgres -d nexo_criminal -f database/02-datos-ejemplo.sql
```

### 2. Backend (Terminal 1)
```bash
cd backend
mvn spring-boot:run
```
Arranca en `http://localhost:8080` y crea el usuario `admin / admin123`.

### 3. Frontend (Terminal 2)
```bash
cd frontend
npm install
npm run dev
```
Abre `http://localhost:5173` en el navegador.

### 4. Cargar datos masivos (opcional, Terminal 3)
```bash
cd scripts
pip install -r requirements.txt
python generar_datos.py
```

### 5. Probar el sistema
1. Abre `http://localhost:5173`.
2. Login con `admin / admin123`.
3. Click en **"▶ Ejecutar motor completo"** desde el Dashboard.
4. Navega a **Alertas** para ver las alertas generadas.
5. Navega a **Grafo** para ver el Hilo Rojo.

---

## Demo de 5 minutos

1. Login → Dashboard.
2. Mostrar que hay 0 alertas.
3. Click en Ejecutar motor → aparecen alertas de Nodo Logístico, Modus Operandi, etc.
4. Ir a Alertas, mostrar los 4 tipos.
5. Ir a Grafo, mostrar los hilos rojos conectando ubicaciones con vehículos.
6. Explicar el plot twist: los sucesos con mismo modus operandi aparecen enhebrados entre sí.

---

## Troubleshooting

**El backend no conecta a la base de datos**
- Verifica `backend/src/main/resources/application.properties` con tu usuario/clave.
- Asegúrate de que PostgreSQL está corriendo.

**El frontend no ve el backend**
- Verifica que el backend corre en `:8080`.
- El proxy está configurado en `frontend/vite.config.ts`.

**El motor no genera alertas**
- Necesitas datos primero. Ejecuta `generar_datos.py` o el SQL de ejemplo.
- La regla de Nodo Logístico requiere al menos 3 robos cerca de un taller.
