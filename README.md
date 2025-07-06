# 📱 Asgard Gym – App móvil para gimnasios

![Banner Asgard](https://github.com/mariarosete/asgardGym/blob/main/bannerAsgard.png?raw=true)

**Asgard Gym** es una aplicación Android desarrollada para mejorar la experiencia de gestión en centros deportivos. Permite a los usuarios consultar y reservar actividades fácilmente, mientras el personal puede gestionar usuarios y actividades de forma eficiente.

---

## 🛠 Tecnologías utilizadas

Kotlin · Android Studio · SQLite · Material Design · GitHub

---

## 🚀 Funcionalidades destacadas

### 👤 Para usuarios del gimnasio:

- **Inicio de sesión rápido** mediante DNI y correo electrónico.
- **Agenda semanal visual** con navegación por semanas, colores por tipo de actividad y filtros por nombre, monitor o tipo.
- **Reserva de actividades en tiempo real** con validaciones automáticas.
- **Consulta y cancelación de reservas activas**, con actualización automática de plazas.
- **Perfil personal editable**, incluyendo estadísticas (número de reservas y última fecha).

### 🧑‍💼 Para personal del gimnasio (panel administración):

- Gestión de usuarios: alta, edición y baja.
- Administración de actividades: creación, edición, eliminación y asignación de plazas.
- Registro histórico de reservas mediante triggers automáticos.

---

## 💻 Cómo ejecutar el proyecto

### 1. Clonar el repositorio

```bash
git clone https://github.com/mariarosete/asgardGym.git
```

### 2. Abrir en Android Studio

1. Abre Android Studio y selecciona **File > Open**.
2. Navega hasta la carpeta del proyecto y ábrela.
3. Espera a que Gradle sincronice automáticamente.

### 3. Ejecutar la aplicación

- Conecta un dispositivo Android o inicia un emulador.
- Haz clic en ▶️ (Run).
- La app se instalará y ejecutará automáticamente.

📌 **Nota**: Si no aparecen datos precargados, incrementa la constante `DATABASE_VERSION` en la clase `DatabaseHelper` para forzar la reinicialización de la base de datos.

---
### 📦 Instalación directa (APK)

También puedes instalar la aplicación sin compilarla, descargando directamente el archivo `.apk`:

📥 **[Descargar Asgard Gym (APK)](https://github.com/mariarosete/asgardGym/raw/main/appAsgardGym/APK/app-debug.apk)**

🔸 El archivo está en la ruta: `appAsgardGym/APK/app-debug.apk`  
🔸 Solo necesitas transferirlo a tu dispositivo Android y abrirlo para instalar.  
🔸 Puede ser necesario habilitar la opción **"Instalar apps de orígenes desconocidos"** en tu móvil.

---

## 🔐 Inicio de sesión de prueba

Puedes acceder con los siguientes usuarios precargados:

| DNI         | Correo electrónico         |
|-------------|----------------------------|
| 12345678A   | admin@asgardgym.com        |
| 12345678B   | maria@email.com            |

> No se requiere contraseña.

---

## 📸 Capturas de pantalla

| 🔐 Iniciar sesión | 🏋️ Inicio y navegación |
|-------------------|------------------------|
| ![Login](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Login.png?raw=true) | ![Inicio](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Pantalla_Principal.png?raw=true) |

| 📅 Agenda de actividades | 🔎 Filtrado de actividades |
|--------------------------|----------------------------|
| ![Agenda](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Agenda.png?raw=true) | ![Filtro](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Filtro.png?raw=true) |
| ![Actividades](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Agenda_Actividades.png?raw=true) | |

| 📋 Detalles y reservas | 📚 Mis reservas |
|------------------------|-----------------|
| ![Detalles](https://github.com/mariarosete/asgardGym/blob/main/screenshots/DEtalles.png?raw=true) | ![Reservas](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Reservas.png?raw=true) |

| 👤 Perfil de usuario | ❓ Ayuda |
|----------------------|---------|
| ![Usuario](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Usuario.png?raw=true) | ![Ayuda](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Ayuda.png?raw=true) |
| ![Detalles usuario](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Detalles_Usuario.png?raw=true) | |

| 👤 Panel de administración | 📜 Histórico |
|----------------------------|--------------|
| ![Admin](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Panel_Admin.png?raw=true) | ![Histórico](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Historico.png?raw=true) |
| ![Crud](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Crud_Actividades.png?raw=true) | ![Actividad](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Actividad.png?raw=true)
| ![Actividad](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Actividad.png?raw=true) | ![Crear usuario](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Crear_Usuario.png?raw=true)

---

## 🔮 Próximas mejoras

- Sincronización de datos mediante API REST.
- Panel de administración en la nube.
- Análisis de hábitos saludables y planes personalizados.

---

## 📩 Contacto

<p align="center">
  <a href="mailto:marlarosete89@gmail.com">
    <img src="https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white" />
  </a>
  <a href="https://linkedin.com/in/mariarosetesuarez">
    <img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" />
  </a>
  <a href="https://github.com/mariarosete">
    <img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white" />
  </a>
</p>
